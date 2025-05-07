package com.example.mapapp.screens.ui



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.domain.geoapify.GeoapifyRepository
import com.example.mapapp.domain.geolocation.GeoLocationRepository
import com.example.mapapp.util.Resource
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeolocationViewModel @Inject constructor(
    private val geoLocationRepo: GeoLocationRepository,
    private val geoapifyRepo: GeoapifyRepository
): ViewModel(){
    private val _uiState = MutableStateFlow(GeolocationState())
    var uiState = _uiState.asStateFlow()

    fun getLocation(cityName: String){
        viewModelScope.launch {
            if(cityName.isBlank()){
                _uiState.update {
                    it.copy(errorMsg = "Please enter city")
                }
            }
            _uiState.update {
                it.copy(isLoading = true, location = null, errorMsg = null, successMsg = null)
            }
            geoLocationRepo.getLocationByCity(cityName).collectLatest {
                result->
                when(result){
                    is Resource.Error<*> -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false, errorMsg = result.message, location = null
                            )
                        }
                    }
                    is Resource.Loading<*> -> {
                        _uiState.update {
                            it.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                    is Resource.Success<*> -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                location = result.data,
                                errorMsg = null,
                                successMsg = "Successful location"
                            )
                        }

                    }
                }
            }
        }



    }
    fun getPoints(points:String){
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true,
                    routes = null,
                    errorMsg = null,
                    successMsg = null)
            }
            geoapifyRepo.getRoutePoints(points).collectLatest {
                result->
                when(result){
                    is Resource.Error<*> -> {
                        _uiState.update {
                            it.copy(isLoading = false,
                                errorMsg = result.message)
                        }
                    }
                    is Resource.Loading<*> -> {
                        _uiState.update {
                            it.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                    is Resource.Success<*> -> {
                        val geometryList = result.data
                        val routePoints = geometryList?.flatMap {
                            geometry->
                            geometry.coordinates.flatten().mapNotNull {
                                coord->
                                if(coord.size>=2) Point.fromLngLat(coord[0],coord[1])
                                else
                                    null
                            }
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                successMsg = "Successful route points",
                                routes = routePoints
                            )
                        }
                    }
                }
            }
        }

    }
}