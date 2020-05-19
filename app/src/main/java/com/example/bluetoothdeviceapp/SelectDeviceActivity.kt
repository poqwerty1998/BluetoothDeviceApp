package com.example.bluetoothdeviceapp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.select_device_layout.*

class SelectDeviceActivity : AppCompatActivity() {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        const val EXTRA_NAME: String = "Device Name"
        const val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "this device doesn't support bluetooth", Toast.LENGTH_LONG).show()
            return
        }
        if(!mBluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_refresh.setOnClickListener{ pairedDeviceList() }

    }

    private fun pairedDeviceList() {
        mPairedDevices = mBluetoothAdapter!!.bondedDevices
        val list : ArrayList<String> = ArrayList()

        if (mPairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in mPairedDevices) {
                list.add(device.name + "\n" + device.address)
                Log.i("device", ""+device.name)
                Log.i("device", "  $device")
            }
        } else {
            Toast.makeText(this, "no paired bluetooth devices found", Toast.LENGTH_LONG).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val deviceInfo = list[position].split('\n')
            val deviceName = deviceInfo[0]
            val deviceAddress: String = deviceInfo[1]

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_NAME, deviceName)
            intent.putExtra(EXTRA_ADDRESS, deviceAddress)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (mBluetoothAdapter!!.isEnabled) {
                    Toast.makeText(this, "Bluetooth has been enabled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Bluetooth has been disabled", Toast.LENGTH_LONG).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth enabling has been canceled", Toast.LENGTH_LONG).show()
            }
        }
    }
}
