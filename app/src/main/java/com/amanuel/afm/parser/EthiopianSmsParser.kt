package com.amanuel.afm.parser

import com.amanuel.afm.data.model.Category
import com.amanuel.afm.data.model.PaymentMethod
import com.amanuel.afm.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

data class ParsedFinancialSms(
    val provider: PaymentMethod,
    val type: TransactionType,
    val amount: Double,
    val referenceNumber: String?,
    val senderOrReceiver: String?,
    val suggestedCategory: Category,
    val rawText: String,
    val isConfident: Boolean
)

object EthiopianSmsParser {

    fun parse(smsBody: String): ParsedFinancialSms? {
        val lower = smsBody.lowercase(Locale.ROOT)

        // Security check: Ignore OTPs, passwords, and verification pins
        if (lower.contains("otp") || lower.contains("verification code") || lower.contains("secret pin")) {
            return null
        }

        // 1. Identify Ethiopian Banking / Wallet Provider
        val provider = when {
            lower.contains("telebirr") -> PaymentMethod.TELEBIRR
            lower.contains("cbe birr") || lower.contains("cbebirr") -> PaymentMethod.CBE_BIRR
            lower.contains("commercial bank of ethiopia") || lower.contains("cbe") || lower.contains("1000") -> PaymentMethod.CBE
            lower.contains("awash") -> PaymentMethod.AWASH_BANK
            else -> PaymentMethod.OTHER
        }

        // 2. Identify Transaction Type
        val type = when {
            lower.contains("credited") || lower.contains("received") || lower.contains("deposited") -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }

        // 3. Extract Amount in ETB / Birr
        var amount = 0.0
        val amountPattern = Pattern.compile("""(?i)(?:etb|birr|br.?)s*([0-9]{1,3}(?:,[0-9]{3})*(?:.[0-9]{1,2})?|[0-9]+(?:.[0-9]{1,2})?)""")
        val amountMatcher = amountPattern.matcher(smsBody)
        if (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)?.replace(",", "")
            amount = amountStr?.toDoubleOrNull() ?: 0.0
        }

        // 4. Extract Reference Number / Txn ID
        var referenceNumber: String? = null
        val refPattern = Pattern.compile("""(?i)(?:ref|txns*id|transactions*number|reference|txnid)[s:]*([A-Za-z0-9_-]+)""")
        val refMatcher = refPattern.matcher(smsBody)
        if (refMatcher.find()) {
            referenceNumber = refMatcher.group(1)?.trim()
        }

        // 5. Intelligent default category
        val category = when {
            type == TransactionType.INCOME && lower.contains("salary") -> Category.SALARY
            type == TransactionType.INCOME -> Category.BUSINESS
            lower.contains("supermarket") || lower.contains("restaurant") || lower.contains("cafe") -> Category.FOOD
            lower.contains("electric") || lower.contains("water") || lower.contains("eeu") -> Category.UTILITIES
            lower.contains("telecom") || lower.contains("internet") || lower.contains("fiber") -> Category.INTERNET
            lower.contains("ride") || lower.contains("fテレ") || lower.contains("taxi") || lower.contains("transport") -> Category.TRANSPORT
            else -> Category.OTHER
        }

        if (amount <= 0.0 && provider == PaymentMethod.OTHER) {
            return null
        }

        return ParsedFinancialSms(
            provider = provider,
            type = type,
            amount = amount,
            referenceNumber = referenceNumber,
            senderOrReceiver = null,
            suggestedCategory = category,
            rawText = smsBody,
            isConfident = amount > 0.0 && provider != PaymentMethod.OTHER
        )
    }
}
