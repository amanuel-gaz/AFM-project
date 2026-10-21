package com.amanuel.afm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amanuel.afm.data.local.AFMDatabase
import com.amanuel.afm.data.model.Category
import com.amanuel.afm.data.model.PaymentMethod
import com.amanuel.afm.data.model.TransactionEntity
import com.amanuel.afm.data.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalIncome: Double,
    val totalExpense: Double,
    val totalBalance: Double
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AFMDatabase.getDatabase(application).transactionDao()

    val allTransactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val metrics: StateFlow<DashboardMetrics> = allTransactions.map { list ->
        val income = list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        DashboardMetrics(
            totalIncome = income,
            totalExpense = expense,
            totalBalance = income - expense
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics(0.0, 0.0, 0.0))

    fun addTransaction(
        type: TransactionType,
        amount: Double,
        description: String,
        category: Category,
        paymentMethod: PaymentMethod,
        date: String,
        notes: String? = null,
        referenceNumber: String? = null
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                type = type,
                amount = amount,
                description = description,
                category = category,
                paymentMethod = paymentMethod,
                date = date,
                notes = notes,
                referenceNumber = referenceNumber,
                source = "MANUAL"
            )
            dao.insertTransaction(entity)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
        }
    }
}
