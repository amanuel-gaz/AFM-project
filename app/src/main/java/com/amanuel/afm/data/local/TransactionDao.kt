package com.amanuel.afm.data.local

import androidx.room.*
import com.amanuel.afm.data.model.TransactionEntity
import com.amanuel.afm.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE referenceNumber = :ref LIMIT 1")
    suspend fun findByReference(ref: String): TransactionEntity?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    fun getTotalByType(type: TransactionType): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
