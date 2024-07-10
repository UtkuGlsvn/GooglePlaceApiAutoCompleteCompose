package com.utkuglsvn.googleplaceapautocompletecompose

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.utkuglsvn.googleplaceapautocompletecompose.ui.theme.PlaceData

class PlacesViewModel : ViewModel() {

    private val _suggestions = MutableLiveData<List<PlaceData>>()
    val suggestions: LiveData<List<PlaceData>> = _suggestions


    private lateinit var placesClient: PlacesClient

    fun initializePlacesClient(context: Context) {
        placesClient = Places.createClient(context)
    }

    fun getAutocompleteSuggestions(query: String) {
        val sessionToken = AutocompleteSessionToken.newInstance()

        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .setTypesFilter(listOf(PlaceTypes.LOCALITY))
            .build()


        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val suggestionsList =
                    response.autocompletePredictions.map {
                        PlaceData(
                            it.placeId, it.getFullText(null).toString()
                        )
                    }
                _suggestions.value = suggestionsList

            }
            .addOnFailureListener { exception ->
                _suggestions.value = emptyList()
                Log.e("PlacesViewModel", "Autocomplete query failed", exception)
            }
    }

    fun getPlaceDetails(placeId: String) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val latLng = place.latLng
            if (latLng != null) {
                val latitude = latLng.latitude
                val longitude = latLng.longitude
                Log.e("PlacesViewModel places:", "lat:$latitude - long: $longitude")
            }
        }.addOnFailureListener { exception ->
            Log.e("PlacesViewModel", "Place details query failed", exception)
        }
    }
}
