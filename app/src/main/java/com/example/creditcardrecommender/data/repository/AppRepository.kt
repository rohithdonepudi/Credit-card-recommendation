package com.example.creditcardrecommender.data.repository

import com.example.creditcardrecommender.data.dao.AppDao
import com.example.creditcardrecommender.data.entity.CardReward
import com.example.creditcardrecommender.data.entity.CreditCard
import com.example.creditcardrecommender.data.entity.GeofenceLocation
import com.example.creditcardrecommender.data.entity.RewardCategory
import com.example.creditcardrecommender.data.entity.Transaction
import com.example.creditcardrecommender.data.entity.Budget

class AppRepository(private val dao: AppDao) {
    val allCards = dao.getAllCards()
    val allCategories = dao.getAllCategories()
    val allLocations = dao.getAllLocations()

    suspend fun addCard(card: CreditCard): Long = dao.insertCard(card)
    
    suspend fun addCategory(category: RewardCategory): Long = dao.insertCategory(category)
    
    suspend fun addLocation(location: GeofenceLocation): Long = dao.insertLocation(location)
    
    suspend fun addCardReward(reward: CardReward) = dao.insertCardReward(reward)
    
    val allTransactions = dao.getAllTransactions()
    val allBudgets = dao.getAllBudgets()
    
    suspend fun addTransaction(transaction: Transaction): Long = dao.insertTransaction(transaction)
    
    suspend fun addBudget(budget: Budget): Long = dao.insertBudget(budget)
    
    fun getSpentInCategory(category: String, startDate: Long) = dao.getSpentInCategory(category, startDate)
}
