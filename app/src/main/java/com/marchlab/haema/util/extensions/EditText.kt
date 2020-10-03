package com.marchlab.haema.util.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

val Context.inputMethodManager get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun EditText.showKeyboard() = context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)

fun EditText.hideKeyboard() = context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)