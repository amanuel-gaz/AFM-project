package com.amanuel.afm

import android.content.Context

data class Transaction(
    val id: Long,
    val date: Long,
    val source: String,
    val type: String,
    val amount: Double,
    val reason: String,
    val rawText: String,
    val confirmed: Boolean
)

object Store {
    private val list = mutableListOf<Transaction>()

    fun all(context: Context): List<Transaction> {
        return list
    }

    fun add(context: Context, t: Transaction) {
        list.add(t)
    }

    fun update(context: Context, id: Long, reason: String) {
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = list[index]
            list[index] = old.copy(reason = reason, confirmed = true)
        }
    }
}
