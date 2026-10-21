package com.amanuel.afm.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    CBE("Commercial Bank of Ethiopia (CBE)"),
    CBE_BIRR("CBE Birr"),
    TELEBIRR("Telebirr"),
    AWASH_BANK("Awash Bank"),
    OTHER("Other")
}

enum class Category(val displayName: String) {
    FOOD("Food"),
    RENT("Rent"),
    TRANSPORT("Transport"),
    EDUCATION("Education"),
    HEALTHCARE("Healthcare"),
    INTERNET("Internet"),
    UTILITIES("Utilities"),
    SHOPPING("Shopping"),
    SALARY("Salary"),
    BUSINESS("Business"),
    FREELANCING("Freelancing"),
    GIFTS("Gifts"),
    OTHER("Other")
}

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["referenceNumber"], unique = false),
        Index(value = ["date"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val category: Category,
    val paymentMethod: PaymentMethod,
    val date: String, // YYYY-MM-DD
    val notes: String? = null,
    val referenceNumber: String? = null, // e.g. TB20261019, FT26292MKL9
    val source: String = "MANUAL", // MANUAL or SMS_IMPORT
    val createdAt: Long = System.currentTimeMillis()
)
