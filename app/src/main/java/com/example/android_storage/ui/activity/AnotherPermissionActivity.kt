package com.example.android_storage.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android_storage.R
import com.example.android_storage.util.Constants.GRANTED
import com.example.android_storage.databinding.ActivityAnotherStorageBinding

class AnotherPermissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnotherStorageBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnotherStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.camera_permission)

        binding.camera.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        val checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (checkPermission == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, GRANTED, Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

}