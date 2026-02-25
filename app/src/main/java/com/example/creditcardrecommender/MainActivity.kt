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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val cards by viewModel.cards.collectAsState()
    val locations by viewModel.locations.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Credit Card Recommender", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = {
            viewModel.addCategory("Grocery")
            // In a real app, IDs would be fetched. Assuming Grocery is ID 1 for this test.
            viewModel.addCard("Chase Sapphire", "1234", "Chase")
            viewModel.linkCardToCategory(1, 1, 5.0) // 5% on Groceries
            viewModel.addLocation("Local Target", 37.422, -122.084, 100f, 1) // Googleplex
        }) {
            Text("Inject Mock Data & Geofence (Googleplex)")
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
