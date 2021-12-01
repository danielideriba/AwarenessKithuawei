package com.ml.awarenesskithuawei

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.status.BeaconStatus
import com.huawei.hms.kit.awareness.status.BluetoothStatus
import com.huawei.hms.kit.awareness.status.HeadsetStatus
import java.util.*


class CaptureActivity: AppCompatActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

    }

    private fun getTimeCategories() {
        // Use getTimeCategories() to get the information about the current time of the user location.
        // Time information includes whether the current day is a workday or a holiday, and whether the current day is in the morning, afternoon, or evening, or at the night.
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Awareness.getCaptureClient(this).timeCategories
            .addOnSuccessListener { timeCategoriesResponse ->
                val timeCategories = timeCategoriesResponse.timeCategories
                val stringBuilder = StringBuilder()
                for (timeCode in timeCategories.timeCategories) {
//                    stringBuilder.append(Constant.TIME_DESCRIPTION_MAP.get(timeCode))
                    Log.i(TAG, "---$timeCode")
                }
                Log.e(TAG,
                    "This device supports the following awareness capabilities$stringBuilder.toString()"
                )
//                scrollToBottom()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get time categories.", e)
            }
    }

    private fun getHeadsetStatus() {
        // Use the getHeadsetStatus API to get headset connection status.
        Awareness.getCaptureClient(this)
            .headsetStatus
            .addOnSuccessListener { headsetStatusResponse ->
                val headsetStatus = headsetStatusResponse.headsetStatus
                val status = headsetStatus.status
                val stateStr = "Headsets are " +
                        if (status == HeadsetStatus.CONNECTED) "connected" else "disconnected"
                Log.e(TAG, stateStr)

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get the headset capture.", e)
            }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Awareness.getCaptureClient(this).location
            .addOnSuccessListener { locationResponse ->
                val location: Location = locationResponse.location
                Log.e(TAG,
                    "Longitude:" + location.getLongitude()
                        .toString() + ",Latitude:" + location.getLatitude()
                )
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get the location.", e)
            }
    }

    private fun getBehaviorStatus() {
        Awareness.getCaptureClient(this).behavior
            .addOnSuccessListener { behaviorResponse ->
                val behaviorStatus = behaviorResponse.behaviorStatus
                val mostLikelyBehavior = behaviorStatus.mostLikelyBehavior
                val str = "Most likely behavior is " +
                        mostLikelyBehavior.type +
                        ",the confidence is " + mostLikelyBehavior.confidence
                Log.e(TAG, "Log - $str")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get the behavior.", e)
            }
    }

    private fun getLightIntensity() {
        Awareness.getCaptureClient(this).lightIntensity
            .addOnSuccessListener { ambientLightResponse ->
                val ambientLightStatus = ambientLightResponse.ambientLightStatus
                Log.e(TAG, "Light intensity is " + ambientLightStatus.lightIntensity + " lux")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get the light intensity.", e)
            }
    }

    private fun getWeatherStatus() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Awareness.getCaptureClient(this).weatherByDevice
            .addOnSuccessListener { weatherStatusResponse ->
                val weatherStatus = weatherStatusResponse.weatherStatus
                val weatherSituation = weatherStatus.weatherSituation
                val situation = weatherSituation.situation
                // For more weather information, please refer to the development guide.
                val weatherInfoStr = """
            City:${weatherSituation.city.name}
            Weather id is ${situation.weatherId}
            CN Weather id is ${situation.cnWeatherId}
            Temperature is ${situation.temperatureC}℃,${situation.temperatureF}℉
            Wind speed is ${situation.windSpeed}km/h
            Wind direction is ${situation.windDir}
            Humidity is ${situation.humidity}%
            """.trimIndent()
                Log.i(TAG, "---$weatherInfoStr")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to get weather information.")
            }
    }

    private fun getBluetoothStatus() {
        val deviceType = 0 // Value 0 indicates a Bluetooth car stereo.
        Awareness.getCaptureClient(this).getBluetoothStatus(deviceType)
            .addOnSuccessListener { bluetoothStatusResponse ->
                val bluetoothStatus = bluetoothStatusResponse.bluetoothStatus
                val status = bluetoothStatus.status
                val stateStr = "The Bluetooth car stereo is " +
                        if (status == BluetoothStatus.CONNECTED) "connected" else "disconnected"
                Log.i(TAG, "---$stateStr")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get Bluetooth status.", e)
            }
    }

    private fun getBeaconStatus() {
        val namespace = "sample namespace"
        val type = "sample type"
        val content = byteArrayOf(
            's'.toByte(),
            'a'.toByte(),
            'm'.toByte(),
            'p'.toByte(),
            'l'.toByte(),
            'e'.toByte()
        )
        val filter = BeaconStatus.Filter.match(namespace, type, content)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Awareness.getCaptureClient(this).getBeaconStatus(filter)
            .addOnSuccessListener { beaconStatusResponse ->
                val beaconDataList = beaconStatusResponse
                    .beaconStatus.beaconData
                if (beaconDataList != null && beaconDataList.size != 0) {
                    var i = 1
                    val builder = StringBuilder()
                    for (beaconData in beaconDataList) {
                        builder.append("Beacon Data ").append(i)
                        builder.append(" namespace:").append(beaconData.namespace)
                        builder.append(",type:").append(beaconData.type)
                        builder.append(",content:").append(Arrays.toString(beaconData.content))
                        builder.append(". ")
                        i++
                    }
                    Log.i(TAG, "---$builder")
                } else {
                    Log.i(TAG, "No beacon matches filters nearby.")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get beacon status.", e)
            }
    }
}