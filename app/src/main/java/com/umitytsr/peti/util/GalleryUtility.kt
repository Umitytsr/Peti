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
import com.umitytsr.peti.R

class GalleryUtility(private val fragment: Fragment) {
    private var onImageSelected: ((Uri?) -> Unit)? = null

    private val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private val activityResultLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            onImageSelected?.invoke(selectedImageUri)
        }
    }

    private val permissionLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            openGallery()
        } else {
            showPermissionDeniedSnackbar()
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
        return storagePermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        permissionLauncher.launch(storagePermissions)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent)
    }

    private fun showPermissionDeniedSnackbar() {
        Snackbar.make(fragment.requireView(), R.string.permission_gallery, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.permission_allow) {
                requestPermission()
            }.show()
    }
}