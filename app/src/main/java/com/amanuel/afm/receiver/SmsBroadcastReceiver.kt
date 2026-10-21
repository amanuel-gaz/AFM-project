package com.amanuel.afm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.amanuel.afm.data.local.AFMDatabase
import com.amanuel.afm.data.model.TransactionEntity
import com.amanuel.afm.parser.EthiopianSmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return

        for (sms in messages) {
            val body = sms.messageBody ?: continue
            val sender = sms.originatingAddress ?: "Unknown"

            val parsed = EthiopianSmsParser.parse(body) ?: continue
            if (!parsed.isConfident) continue

            // Save asynchronously into Room database with deduplication
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AFMDatabase.getDatabase(context)
                    val dao = db.transactionDao()

                    // Deduplicate by reference number if present
                    if (parsed.referenceNumber != null) {
                        val existing = dao.findByReference(parsed.referenceNumber)
                        if (existing != null) {
                            Log.d("AFM_SMS", "Skipping duplicate reference: ${parsed.referenceNumber}")
                            return@launch
                        }
                    }

                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val entity = TransactionEntity(
                        type = parsed.type,
                        amount = parsed.amount,
                        description = "${parsed.provider.displayName} SMS Alert",
                        category = parsed.suggestedCategory,
                        paymentMethod = parsed.provider,
                        date = today,
                        notes = "Imported from SMS sender: $sender",
                        referenceNumber = parsed.referenceNumber,
                        source = "SMS_IMPORT"
                    )
                    dao.insertTransaction(entity)
                    Log.d("AFM_SMS", "Successfully imported ETB ${parsed.amount} from ${parsed.provider.displayName}")
                } catch (e: Exception) {
                    Log.e("AFM_SMS", "Error inserting SMS transaction", e)
                }
            }
        }
    }
}
