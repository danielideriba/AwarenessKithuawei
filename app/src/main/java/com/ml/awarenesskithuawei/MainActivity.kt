package com.ml.awarenesskithuawei

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hms.kit.awareness.Awareness
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 940
    private val mPermissionsOnHigherVersion = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION
    )
    private val mPermissionsOnLowerVersion = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION
    )

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()
        queryDeviceSupportingCapabilities()

        awareness_capture.setOnClickListener {
            val snapshotIntent = Intent(this, CaptureActivity::class.java)
            startActivity(snapshotIntent)
        }

//        awareness_barrier.setOnClickListener {
//            val barrierIntent = Intent(this, BarrierActivity::class.java)
//            startActivity(barrierIntent)
//        }
    }

    private fun queryDeviceSupportingCapabilities() {
        // Use querySupportingCapabilities to query awareness capabilities supported by the current device.
        Awareness.getCaptureClient(this).querySupportingCapabilities()
            .addOnSuccessListener { capabilityResponse ->
                val status = capabilityResponse.capabilityStatus
                val capabilities = status.capabilities
                Log.i(TAG, "capabilities code :" + Arrays.toString(capabilities))
                val deviceSupportingStr = StringBuilder()
                .append("This device supports the following awareness capabilities:\n")
                for (capability in capabilities) {
                    Log.i("----", "---$capability")
//                    deviceSupportingStr.append(Constant.CAPABILITIES_DESCRIPTION_MAP.get(capability))
//                    deviceSupportingStr.append("\n")
                }

                Log.e(TAG,
                    "This device supports the following awareness capabilities$deviceSupportingStr"
                )
//                mLogView.printLog(deviceSupportingStr.toString())
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get supported capabilities.", e)
            }
    }

    private fun checkAndRequestPermissions() {
        val permissionsDoNotGrant: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (permission in mPermissionsOnHigherVersion) {
                if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsDoNotGrant.add(permission)
                }
            }
        } else {
            for (permission in mPermissionsOnLowerVersion) {
                if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsDoNotGrant.add(permission)
                }
            }
        }
        if (permissionsDoNotGrant.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsDoNotGrant.toTypedArray(), PERMISSION_REQUEST_CODE
            )
        }
    }
}