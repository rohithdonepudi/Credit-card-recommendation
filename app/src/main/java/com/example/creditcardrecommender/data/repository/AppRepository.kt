package com.example.creditcardrecommender.data.repository

import com.example.creditcardrecommender.data.dao.AppDao
import com.example.creditcardrecommender.data.entity.CardReward
import com.example.creditcardrecommender.data.entity.CreditCard
import com.example.creditcardrecommender.data.entity.GeofenceLocation
import com.example.creditcardrecommender.data.entity.RewardCategory

class AppRepository(private val dao: AppDao) {
    val allCards = dao.getAllCards()
    val allCategories = dao.getAllCategories()
    val allLocations = dao.getAllLocations()

    suspend fun addCard(card: CreditCard): Long = dao.insertCard(card)
    
    suspend fun addCategory(category: RewardCategory): Long = dao.insertCategory(category)
    
    suspend fun addLocation(location: GeofenceLocation): Long = dao.insertLocation(location)
    
    suspend fun addCardReward(reward: CardReward) = dao.insertCardReward(reward)
}
