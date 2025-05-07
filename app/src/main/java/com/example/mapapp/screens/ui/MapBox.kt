package com.example.mapapp.screens.ui

import android.R.attr.end
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotationInteractionsState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationInteractionsState
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

@Composable
fun MapScreen(
    viewModel: GeolocationViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0)
            center(Point.fromLngLat(36.8263840993416, -1.30326415))
        }
    }
    val coroutineScope = rememberCoroutineScope()

    val start = Point.fromLngLat(36.8263840993416, -1.30326415)
    val cityInput = remember { mutableStateOf("") }

    // Points list that updates with animation
    val animatedPoints = remember { mutableStateOf<List<Point>>(listOf(start)) }

    LaunchedEffect(uiState.location) {
        uiState.location?.let { location ->
            val start = Point.fromLngLat(36.8263840993416, -1.30326415)
            val end = Point.fromLngLat(location.lon, location.lat)
            val points = "${start.longitude()},${start.latitude()}|${end.longitude()},${end.latitude()}"
            viewModel.getPoints(points)
        }
    }


    LaunchedEffect(uiState.location) {

        uiState.location?.let {
            location->
            val end = Point.fromLngLat(location.lon,location.lat)
            // Animate between points
            val steps = 100
            val delayPerStep = 10L

            val latDiff = (end.latitude() - start.latitude()) / steps
            val lngDiff = (end.longitude() - start.longitude()) / steps
            animatedPoints.value = listOf(start)
            for (i in 1..steps) {
                val newPoint = Point.fromLngLat(
                    start.longitude() + lngDiff * i,
                    start.latitude() + latDiff * i
                )
                animatedPoints.value = animatedPoints.value + newPoint
                mapViewportState.easeTo(
                    CameraOptions.Builder()
                        .center(newPoint)
                        .zoom(10.0)
                        .build(),
                    mapAnimationOptions {
                        duration(500)
                    }
                )
                delay(delayPerStep)

            }
            val pointsString = "${start.longitude()},${start.latitude()}|${end.longitude()},${end.latitude()}"
            viewModel.getPoints(pointsString)

        }

        Log.d("location",uiState.location.toString())


    }

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Row(modifier = Modifier.padding(32.dp)) {
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
        Spacer(modifier = Modifier.height(8.dp))
        MapboxMap(
            modifier = Modifier.weight(1f),
            mapViewportState = mapViewportState,
            logo = {},
            compass = {},
            attribution = {},
            scaleBar = {}
        ) {
            PolylineAnnotation(points = animatedPoints.value) {
                lineColor = Color(0xff000000)
                lineWidth = 5.0
            }
            uiState.routes?.let { routePoints ->
                PolylineAnnotation(points = routePoints) {
                    lineColor = Color.Blue
                    lineWidth = 4.0
                }
            }

            CircleAnnotation(start) {
                CircleAnnotationInteractionsState().onClicked {
                    Log.d("Clicked","Destination clicked")
                    true
                }
                circleColor = Color.Red
                circleRadius = 6.0
            }


            uiState.location?.let {
                    location->
                val end = Point.fromLngLat(location.lon,location.lat)
                CircleAnnotation(end, onClick ={
                    false
                } ) {
                    PointAnnotationInteractionsState().onClicked {
                        Log.d("Clicked","Destination clicked")
                        true
                    }
                    circleColor = Color.Green
                    circleRadius = 6.0
                }
            }

        }
    }


}