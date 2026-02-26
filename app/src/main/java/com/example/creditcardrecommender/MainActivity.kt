package com.example.creditcardrecommender

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.creditcardrecommender.data.db.AppDatabase
import com.example.creditcardrecommender.data.repository.AppRepository
import com.example.creditcardrecommender.ui.MainViewModel
import com.example.creditcardrecommender.ui.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { AppRepository(db.appDao()) }
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(repository) }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle Permission granted/rejected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        requestPermissions()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreditCardRecommenderApp(viewModel)
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            requestPermissionLauncher.launch(notGranted.toTypedArray())
        }
    }
}

@Composable
fun CreditCardRecommenderApp(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Cards") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Money") },
                    label = { Text("Money") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> CardRecommendationsScreen(viewModel)
                1 -> MoneyManagementScreen(viewModel)
            }
        }
    }
}

@Composable
fun CardRecommendationsScreen(viewModel: MainViewModel) {
    val cards by viewModel.cards.collectAsState()
    val locations by viewModel.locations.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Credit Card Recommender", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = {
            viewModel.addCategory("Grocery")
            viewModel.addCard("Chase Sapphire", "1234", "Chase")
            viewModel.linkCardToCategory(1, 1, 5.0) 
            viewModel.addLocation("Local Target", 37.422, -122.084, 100f, 1) 
        }) {
            Text("Inject Card Mock Data")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Your Cards:", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(cards) { card ->
                Text("- ${card.name} ending in ${card.last4}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Monitored Locations:", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(locations) { loc ->
                Text("- ${loc.name} (Lat: ${loc.latitude}, Lng: ${loc.longitude})")
            }
        }
    }
}

@Composable
fun MoneyManagementScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val budgets by viewModel.budgets.collectAsState()

    // Calculate total spend
    val totalSpend = transactions.sumOf { it.amount }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Financial Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Total Spending Card
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Spent This Month", style = MaterialTheme.typography.titleMedium)
                Text(String.format("\$%.2f", totalSpend), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.addBudget("Groceries", 500.0)
            viewModel.addBudget("Dining", 300.0)
            viewModel.addTransaction(65.40, "Trader Joe's", "Groceries")
            viewModel.addTransaction(120.00, "Whole Foods", "Groceries")
            viewModel.addTransaction(45.00, "Starbucks", "Dining")
        }) {
            Text("Inject Rocket Money Mock Data")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Budgets
        Text("Budgets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(budgets) { budget ->
                val spent = transactions.filter { it.category == budget.category }.sumOf { it.amount }
                val progress = (spent / budget.monthlyLimit).toFloat().coerceIn(0f, 1f)
                val color = if (progress >= 1f) Color.Red else MaterialTheme.colorScheme.primary

                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(budget.category)
                        Text(String.format("\$%.0f / \$%.0f", spent, budget.monthlyLimit))
                    }
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Transactions
        Text("Recent Transactions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(transactions) { tx ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(tx.merchantName, fontWeight = FontWeight.Bold)
                        Text(tx.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Text(String.format("\$%.2f", tx.amount), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
