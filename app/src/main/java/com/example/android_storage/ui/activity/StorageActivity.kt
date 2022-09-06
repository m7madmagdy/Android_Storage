package com.example.android_storage.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity.CENTER
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.android_storage.BuildConfig
import com.example.android_storage.R
import com.example.android_storage.data.Data
import com.example.android_storage.util.Constants.CAMERA
import com.example.android_storage.util.Constants.EMPTY
import com.example.android_storage.util.Constants.GALLERY
import com.example.android_storage.util.Constants.MY_FILE
import com.example.android_storage.util.Constants.SAVED
import com.example.android_storage.databinding.ActivityMainBinding
import com.example.android_storage.databinding.DialogCustomImageSelectionBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File


@Suppress("DEPRECATION")
class StorageActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Request Launcher Permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        title = getString(R.string.storage_title)
        binding.dataClass = Data(
            getString(R.string.read_and_write_external_storage),
            getString(R.string.save_to_file),
            getString(R.string.get_from_file),
            getString(R.string.another_storage),
            "RecyclerView",
            R.drawable.ic_baseline_storage_24
        )
        binding.activity = this
    }

    fun saveBtn() {
        val edtText = binding.edtTxt.text.toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        val checkPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (checkPermission == PackageManager.PERMISSION_GRANTED) {
            saveTextToFile(edtText)
        }
    }

    fun getBtn() {
        readTextToFile()
    }

    //Saved to cache directory
    fun saveTextToFile(text: Int) {
        val path = applicationInfo.dataDir
        val fileName = MY_FILE
        val file = File("$path/$fileName")
        file.writeText(text.toString())
    }

    //Saved to data (Android) package directory
    fun saveTextToFile(text: Char) {
        val path = getExternalFilesDir(null)?.path.toString()
        val fileName = MY_FILE
        val file = File("$path/$fileName")
        file.writeText(text.toString())
    }

    //Saved to external storage directory
    //Using Permission WRITE_EXTERNAL_STORAGE
    private fun saveTextToFile(text: String) {
        if (binding.edtTxt.text.isEmpty()) {
            toastView(EMPTY, android.R.color.holo_red_light)
        } else {
            val path = Environment.getExternalStorageDirectory().path
            val fileName = MY_FILE
            val file = File("$path/$fileName")
            file.writeText(text)
            toastView(SAVED, android.R.color.holo_green_light)
        }
    }

    private fun readTextToFile() {
        val path = Environment.getExternalStorageDirectory().path
        val fileName = MY_FILE
        val file = File("$path/$fileName")
        val text = file.readText()
        binding.edtTxt.setText(text)
    }

    fun anotherStorageBtn() {
        startActivity(Intent(this, AnotherPermissionActivity::class.java))
    }

    fun recyclerActivity() {
        startActivity(Intent(this, RecyclerActivity::class.java))
    }

    fun addImage() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
            dialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(galleryIntent, GALLERY)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(
                            this@StorageActivity,
                            "Permission Denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread().check()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail: Bitmap =
                        data.extras!!.get("data") as Bitmap
                    binding.imageContainer.setImageBitmap(thumbnail)
                    replaceImage()
                }
            } else if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data
                    binding.imageContainer.setImageURI(selectedPhotoUri)
                    replaceImage()
                }
            }

        }
    }

    private fun replaceImage() {
        binding.addImage.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_vector_edit
            )
        )
    }

    @SuppressLint("InflateParams")
    private fun toastView(message: String, bg: Int) {
        val inflater = layoutInflater
        val toastLayout = inflater.inflate(R.layout.toast_view, null)
        val toastMsg = toastLayout.findViewById(R.id.toastMsg) as TextView
        val linearBg = toastLayout.findViewById(R.id.linearBg) as LinearLayout
        linearBg.background = ContextCompat.getDrawable(this, bg)
        toastMsg.text = message
        val toast = Toast(this)
        toast.setGravity(CENTER, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = toastLayout
        toast.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}