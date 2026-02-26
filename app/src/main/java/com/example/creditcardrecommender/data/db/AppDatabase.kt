package com.example.creditcardrecommender.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.creditcardrecommender.data.dao.AppDao
import com.example.creditcardrecommender.data.entity.CardReward
import com.example.creditcardrecommender.data.entity.CreditCard
import com.example.creditcardrecommender.data.entity.GeofenceLocation
import com.example.creditcardrecommender.data.entity.RewardCategory
import com.example.creditcardrecommender.data.entity.Transaction
import com.example.creditcardrecommender.data.entity.Budget

@Database(
    entities = [
        CreditCard::class,
        RewardCategory::class,
        CardReward::class,
        GeofenceLocation::class,
        Transaction::class,
        Budget::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "credit_card_recommender_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
