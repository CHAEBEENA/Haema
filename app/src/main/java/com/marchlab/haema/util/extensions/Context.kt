package com.marchlab.haema.util.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.math.roundToInt

fun Context.convertDpToPx(dp: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)

fun Context.convertDpToPx(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun Context.convertSpToPx(sp: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), resources.displayMetrics)

fun Context.isGranted(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.toast(length: Int = Toast.LENGTH_LONG, message:() -> String) = Toast.makeText(this, message(), length).show()

fun Context.freeTrialPeriod(): Long {
    val left = 30 - (Calendar.getInstance().timeInMillis - this.packageManager.getPackageInfo(this.packageName, 0).firstInstallTime) / (24 * 60 * 60 * 1000)
    return if(left > 0) left else 0
}


