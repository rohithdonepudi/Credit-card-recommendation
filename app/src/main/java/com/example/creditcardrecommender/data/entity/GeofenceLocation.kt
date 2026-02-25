package com.example.creditcardrecommender.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "geofenced_locations",
    foreignKeys = [
        ForeignKey(
            entity = RewardCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class GeofenceLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "Costco", "Shell Station"
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 100f,
    val categoryId: Int? = null
)
