package com.umitytsr.peti.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.umitytsr.peti.R

class GalleryUtility(private val fragment: Fragment) {
    private var onImageSelected: ((Uri?) -> Unit)? = null
    private val activityResultLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                onImageSelected?.invoke(selectedImageUri)
            }
        }

    private val permissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(
                    fragment.requireContext(),
                    fragment.getString(R.string.permission_needed_for_gallery),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun selectImageFromGallery(onImageSelected: (Uri?) -> Unit) {
        this.onImageSelected = onImageSelected
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }
        activityResultLauncher.launch(intent)
    }
}



