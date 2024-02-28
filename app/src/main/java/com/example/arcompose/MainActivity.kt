package com.example.arcompose

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.arcompose.ui.theme.ARComposeTheme
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.arcompose.model.Venue
import com.example.arcompose.utils.PermissionUtils
import com.example.arcompose.viewModel.LocationViewModel
import com.example.arcompose.viewModel.MainViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ViewRenderable
import dagger.hilt.android.AndroidEntryPoint
//import com.xperiencelabs.armenu.ui.theme.ARMenuTheme
//import com.xperiencelabs.armenu.ui.theme.Translucent

import org.json.JSONArray
import java.util.concurrent.CompletableFuture

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val locationViewModel: LocationViewModel by viewModels()
    val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
                    ARScreen("Android",locationViewModel,mainViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (!PermissionUtils.hasLocationAndCameraPermissions(this)) {
            PermissionUtils.requestCameraAndLocationPermissions(this)
        } else {

        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ARComposeTheme {
        Greeting("Android")
    }
}

fun fetchVenues(deviceLatitude: Double, deviceLongitude: Double ) {

//    loadingDialog.dismiss()
//    userGeolocation = Geolocation(deviceLatitude.toString(), deviceLongitude.toString())
//    currentLatitude=deviceLatitude
//    currentLongitude=deviceLongitude



    var venuesSet: MutableSet<Venue> = mutableSetOf()

    val locJson = ("[{'name': 'Asahi','lat':51.322810473750415, 'lon':-0.5539164021334745,'altitude': 0},"
            + "{'name': 'abb','lat':51.323723556338045,'lon':-0.5525103973853058,'altitude': 0}, "
            + "{'name': 'idbs','lat':51.32191182068472,'lon':-0.5546657758925518,'altitude': 0}]")

    venuesSet.clear()

    val jsonArray = JSONArray(locJson)

    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        val name = jsonObject.getString("name")
        val lat = jsonObject.getString("lat")
        val lon = jsonObject.getString("lon")
        val altitude = jsonObject.getDouble("altitude")
        //    val icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        Log.e("name", name + "")

        val vLatLng = LatLng(lat.toDouble(), lon.toDouble())
        val dLatLng = LatLng(deviceLatitude, deviceLongitude)
//        val distance = SphericalUtil.computeDistanceBetween(vLatLng, dLatLng);
        //   if (distance < 2000) // filter on basis of distance from current location
        venuesSet.add(Venue(name, name, lat, lon, ""))

    }
    Log.e("venuesSet", venuesSet.toString())



    //  venuesSet.addAll(venueWrapper.venueList)
//    areAllMarkersLoaded = false
//    locationScene!!.clearMarkers()
//
//    renderVenues(venuesSet)
//    setUpMaps(deviceLatitude, deviceLongitude)
}



@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ARScreen(model:String,
             locationViewModel: LocationViewModel = hiltViewModel(),
             mainViewModel: MainViewModel = hiltViewModel()) {
    val nodes = remember {
        mutableListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    val placeModelButton = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    locationViewModel.getRealTimeLocation(context )
    Box(modifier = Modifier.fillMaxSize()){
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = {arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false

//                modelNode.value = ArModelNode(arSceneView.engine,PlacementMode.INSTANT).apply {
//                    loadModelGlbAsync(
//                        glbFileLocation = "models/${model}.glb",
//                        scaleToUnits = 0.8f
//                    ){
//
//                    }
//                    onAnchorChanged = {
//                        placeModelButton.value = !isAnchored
//                    }
//                    onHitResult = {node, hitResult ->
//                        placeModelButton.value = node.isTracking
//                    }
//
//                }
//                nodes.add(modelNode.value!!)
            },
            onSessionCreate = {
                planeRenderer.isVisible = false
                fetchVenues( locationViewModel.latLocation.value.toDouble(),locationViewModel.lngLocation.value.toDouble())
            }
        )
        if(placeModelButton.value){
            Button(onClick = {
                modelNode.value?.anchor()
            }, modifier = Modifier.align(Alignment.Center)) {
                Text(text = "Place It")
            }
        }
    }


    LaunchedEffect(key1 = model){
        modelNode.value?.loadModelGlbAsync(
            glbFileLocation = "models/${model}.glb",
            scaleToUnits = 0.8f
        )
        Log.e("errorloading","ERROR LOADING MODEL")
    }


    LaunchedEffect(key1 = LocalContext.current) {
        locationViewModel.trackerEvent.collect { event ->
            when (event) {
                is LocationViewModel.LocationUpdates.OnLocationTracker -> {
                    mainViewModel.currentLocation.value = LatLng(
                        event.currentLocation.latitude,
                        event.currentLocation.longitude
                    )
                }
            }
        }
    }
}