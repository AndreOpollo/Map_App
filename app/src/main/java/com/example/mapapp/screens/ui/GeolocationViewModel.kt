package com.example.mapapp.screens.ui



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.domain.GeoLocationRepository
import com.example.mapapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeolocationViewModel @Inject constructor(
    private val repository: GeoLocationRepository
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
            repository.getLocationByCity(cityName).collectLatest {
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
}