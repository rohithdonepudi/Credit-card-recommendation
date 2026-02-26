package com.example.creditcardrecommender.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creditcardrecommender.data.entity.CardReward
import com.example.creditcardrecommender.data.entity.CreditCard
import com.example.creditcardrecommender.data.entity.GeofenceLocation
import com.example.creditcardrecommender.data.entity.RewardCategory
import com.example.creditcardrecommender.data.entity.Transaction
import com.example.creditcardrecommender.data.entity.Budget
import com.example.creditcardrecommender.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {
    val cards = repository.allCards.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val categories = repository.allCategories.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val locations = repository.allLocations.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val transactions = repository.allTransactions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val budgets = repository.allBudgets.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addCard(name: String, last4: String, bank: String) {
        viewModelScope.launch {
            repository.addCard(CreditCard(name = name, last4 = last4, bank = bank))
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.addCategory(RewardCategory(name = name))
        }
    }

    fun addLocation(name: String, lat: Double, lng: Double, radius: Float, categoryId: Int) {
        viewModelScope.launch {
            repository.addLocation(
                GeofenceLocation(
                    name = name,
                    latitude = lat,
                    longitude = lng,
                    radiusMeters = radius,
                    categoryId = categoryId
                )
            )
        }
    }
    
    fun linkCardToCategory(cardId: Int, categoryId: Int, percentage: Double) {
        viewModelScope.launch {
            repository.addCardReward(
                CardReward(cardId = cardId, categoryId = categoryId, cashbackPercentage = percentage)
            )
        }
    }

    fun addTransaction(amount: Double, merchant: String, category: String) {
        viewModelScope.launch {
            repository.addTransaction(
                Transaction(
                    amount = amount,
                    merchantName = merchant,
                    category = category,
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    fun addBudget(category: String, limit: Double) {
        viewModelScope.launch {
            repository.addBudget(
                Budget(category = category, monthlyLimit = limit)
            )
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
