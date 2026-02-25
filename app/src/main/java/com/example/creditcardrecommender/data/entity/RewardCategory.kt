package com.example.creditcardrecommender.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_categories")
data class RewardCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String // e.g., "Dining", "Gas Station", "Grocery"
)
