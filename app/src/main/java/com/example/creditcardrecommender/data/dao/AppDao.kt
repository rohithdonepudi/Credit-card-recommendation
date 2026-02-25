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
}
