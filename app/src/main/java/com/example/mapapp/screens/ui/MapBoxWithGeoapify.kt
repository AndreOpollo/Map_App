package com.example.mapapp.screens.ui


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import com.example.mapapp.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationInteractionsState
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.rememberPointAnnotationInteractionsState
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenWithRoute(
    viewModel: GeolocationViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value

    // Starting point (Nairobi)
    val start = Point.fromLngLat(36.8263840993416, -1.30326415)

    // State for UI
    val cityInput = remember { mutableStateOf("") }
    val animatedPoints = remember { mutableStateOf<List<Point>>(listOf(start)) }
    val showCompleteRoute = remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember{  mutableStateOf(false)}
    val pointAnnotationInteractionsState = rememberPointAnnotationInteractionsState {
        PointAnnotationInteractionsState().onClicked{
            showBottomSheet = true
                    Log.d("MapScreen", "Destination marker clicked, showing bottom sheet")
        true // Consume the click
    }


    }


    // Map viewport state
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0)
            center(start)
        }
    }

    // Request route when location is available
    LaunchedEffect(uiState.location) {
        uiState.location?.let { location ->
            val end = Point.fromLngLat(location.lon, location.lat)
            val points = "${start.latitude()},${start.longitude()}|${end.latitude()},${end.longitude()}"
            viewModel.getPoints(points)

            // Reset animation state
            animatedPoints.value = listOf(start)
            showCompleteRoute.value = false
        }
    }

    // Animate through the route points
    LaunchedEffect(uiState.routes) {
        val routePoints = uiState.routes
        if (!routePoints.isNullOrEmpty()) {
            // Reset animation
            animatedPoints.value = listOf(start)

            // Animate through each point
            for (i in routePoints.indices) {
                val point = routePoints[i]
                animatedPoints.value = animatedPoints.value + point

                // Follow camera along the route
                if (i % 5 == 0) { // Update camera less frequently for smoother animation
                    mapViewportState.easeTo(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(12.0)
                            .build(),
                        mapAnimationOptions { duration(100) }
                    )
                }

                delay(15) // Controls animation speed
            }

            // When animation completes, show the complete route
            showCompleteRoute.value = true

            // Zoom out to show the entire route
            val boundingPoints = listOf(start) + (uiState.routes ?: emptyList())
            if (boundingPoints.size > 1) {
                // Calculate route center for better camera position
                val avgLat = boundingPoints.map { it.latitude() }.average()
                val avgLng = boundingPoints.map { it.longitude() }.average()
                val center = Point.fromLngLat(avgLng, avgLat)

                // Adjust zoom level based on route distance
                val zoomLevel = calculateZoomLevel(boundingPoints)

                mapViewportState.easeTo(
                    CameraOptions.Builder()
                        .center(center)
                        .zoom(zoomLevel)
                        .build(),
                    mapAnimationOptions { duration(600) }
                )
            }
        }
        Log.d("Routes",uiState.routes.toString())
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = cityInput.value,
                onValueChange = { cityInput.value = it },
                label = { Text(text = "Enter city") },
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    viewModel.getLocation(cityInput.value)
                }) {
                    Text("Go")
                }
            }
        }

        // Show error message if any
        uiState.errorMsg?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        MapboxMap(
            modifier = Modifier.weight(1f),
            mapViewportState = mapViewportState,
            logo = {},
            compass = {},
            attribution = {},
            scaleBar = {},
            onMapClickListener = {
                pointAnnotationInteractionsState
                false},

        ) {
            // Draw animated route progress
            PolylineAnnotation(points = animatedPoints.value) {
                lineColor = Color(0xFF4285F4) // Google Maps blue
                lineWidth = 5.0
            }

            // Draw complete route once animation finishes
            if (showCompleteRoute.value && !uiState.routes.isNullOrEmpty()) {
                PolylineAnnotation(points = uiState.routes) {
                    lineColor = Color(0xFF4285F4) // Same color for consistency
                    lineWidth = 5.0
                }
            }

            // Draw start marker
            CircleAnnotation(start) {
                circleColor = Color(0xFFEA4335) // Red marker
                circleRadius = 8.0
                circleStrokeWidth = 2.0
                circleStrokeColor = Color.White
            }

            // Draw destination marker if available
            uiState.location?.let { location ->
                val end = Point.fromLngLat(location.lon, location.lat)
                val image = rememberIconImage(resourceId = R.drawable.placeholder)
                var interactionState = rememberPointAnnotationInteractionsState()

                PointAnnotation(
                    point = end,
                    onClick = {
                        showBottomSheet = true
                        true
                    }){
                    iconImage = image
                    iconSize = 0.2

                }
            }
        }
    }
    if(showBottomSheet){
        ModalBottomSheet(onDismissRequest = {
            showBottomSheet = false
        },
            sheetState = bottomSheetState) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(cityInput.value.capitalize(), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(16.dp))
                Text("Take Photo", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Blue)
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(16.dp))
                Text("Sign In", fontSize = 20.sp, fontWeight = FontWeight.Medium,color=Color.Blue)

            }
        }
    }
}

// Helper function to calculate appropriate zoom level based on route points
private fun calculateZoomLevel(points: List<Point>): Double {
    if (points.size < 2) return 10.0

    // Find min/max coordinates
    val minLat = points.minOf { it.latitude() }
    val maxLat = points.maxOf { it.latitude() }
    val minLng = points.minOf { it.longitude() }
    val maxLng = points.maxOf { it.longitude() }

    // Calculate the distance
    val latDistance = maxLat - minLat
    val lngDistance = maxLng - minLng

    // Simple logarithmic zoom calculation (higher value = more zoomed out)
    val maxDistance = maxOf(latDistance, lngDistance)

    return when {
        maxDistance > 10.0 -> 3.0
        maxDistance > 5.0 -> 5.0
        maxDistance > 1.0 -> 7.0
        maxDistance > 0.5 -> 9.0
        maxDistance > 0.1 -> 11.0
        else -> 13.0
    }
}