package com.umitytsr.peti.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class GalleryUtility(private val fragment: Fragment) {
    private var onImageSelected: ((Uri?) -> Unit)? = null
    private val activityResultLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            onImageSelected?.invoke(selectedImageUri)
        }
    }

    private val permissionLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Snackbar.make(fragment.requireView(), "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                .setAction("Give Permission") {
                    requestPermission()
                }.show()
        }
    }

    fun selectImageFromGallery(onImageSelected: (Uri?) -> Unit) {
        this.onImageSelected = onImageSelected
        if (hasStoragePermission()) {
            openGallery()
        } else {
            requestPermission()
        }
    }

    private fun hasStoragePermission(): Boolean {
        val context = fragment.requireContext()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Scoped storage is used, no need for READ_EXTERNAL_STORAGE permission
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // Android 10 and above, scoped storage is used.
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }
}