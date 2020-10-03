package com.marchlab.haema.util.extensions

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.delay

fun Fragment.toast(length: Int = Toast.LENGTH_SHORT, message: () -> String) = Toast.makeText(requireContext(), message(), length).show()

fun Fragment.isGranted(permission: String) = ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

val Fragment.supportFragmentManager: FragmentManager get() = requireActivity().supportFragmentManager

fun FragmentManager.replace(containerViewId: Int,  fragmentClass: Class<out Fragment>, args: Bundle? = null, tag: String = fragmentClass.simpleName)
        = commit { replace(containerViewId, fragmentClass, args, tag) }

suspend fun FragmentManager.commitDelayed(timeInMillis: Long, body: FragmentTransaction.() -> Unit) { delay(timeInMillis); commit { body() } }

