package com.umitytsr.peti.util

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView

fun MaterialAutoCompleteTextView?.setSimpleItem(context: Context,items: List<String>) {
    val adapter = ArrayAdapter(context, R.layout.simple_spinner_dropdown_item, items)
    this?.setAdapter(adapter)
}