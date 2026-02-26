package com.example.creditcardrecommender.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.creditcardrecommender.data.entity.CardReward
import com.example.creditcardrecommender.data.entity.CreditCard
import com.example.creditcardrecommender.data.entity.GeofenceLocation
import com.example.creditcardrecommender.data.entity.RewardCategory
import com.example.creditcardrecommender.data.entity.Transaction
import com.example.creditcardrecommender.data.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CreditCard): Long

    @Query("SELECT * FROM credit_cards")
    fun getAllCards(): Flow<List<CreditCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: RewardCategory): Long

    @Query("SELECT * FROM reward_categories")
    fun getAllCategories(): Flow<List<RewardCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardReward(reward: CardReward)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: GeofenceLocation): Long

    @Query("SELECT * FROM geofenced_locations")
    fun getAllLocations(): Flow<List<GeofenceLocation>>
    
    @Query("SELECT * FROM geofenced_locations WHERE id = :id")
    suspend fun getLocationById(id: Int): GeofenceLocation?

    @Transaction
    @Query("""
        SELECT c.* FROM credit_cards c
        INNER JOIN card_rewards cr ON c.id = cr.cardId
        WHERE cr.categoryId = :categoryId
        ORDER BY cr.cashbackPercentage DESC
        LIMIT 1
    """)
    suspend fun getBestCardForCategory(categoryId: Int): CreditCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT SUM(amount) FROM transactions WHERE category = :category AND date >= :startDate")
    fun getSpentInCategory(category: String, startDate: Long): Flow<Double?>
}
