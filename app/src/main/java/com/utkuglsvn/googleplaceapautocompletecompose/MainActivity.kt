package com.utkuglsvn.googleplaceapautocompletecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.Places
import com.utkuglsvn.googleplaceapautocompletecompose.ui.theme.GooglePlaceApAutoCompleteComposeTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<PlacesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val apiKey = BuildConfig.PLACES_API_KEY
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

            viewModel.initializePlacesClient(this)
            GooglePlaceApAutoCompleteComposeTheme {

                PlacesAutocomplete(viewModel)
            }
        }
    }
}

@Composable
fun PlacesAutocomplete(viewModel: PlacesViewModel) {
    val query = remember { mutableStateOf("") }
    val suggestions by viewModel.suggestions.observeAsState(emptyList())

    Column {
        OutlinedTextField(
            value = query.value,
            onValueChange = {
                query.value = it
                if (query.value.isNotEmpty()) {
                    viewModel.getAutocompleteSuggestions(query.value)
                }
            },
            label = { Text("Search Places") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
        LazyColumn {
            items(suggestions) { suggestion ->
                Text(
                    text = suggestion.suggestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            query.value = suggestion.suggestion
                            viewModel.getAutocompleteSuggestions("")
                            viewModel.getPlaceDetails(suggestion.id)
                        }
                        .padding(16.dp)
                )
                Divider()
            }
        }
    }
}