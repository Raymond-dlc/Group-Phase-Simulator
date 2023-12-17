package com.oceanscurse.groupstagesimulator.utilities

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * Created by Raymond de la Croix on 17-12-2023.
 */
class ResourceExtensions {}

/**
 * Converts this integer assuming it is a pixel value to the dp equivalent.
 */
fun Int.toDp(resources: Resources): Int {
    return (this / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

/**
 * Converts this integer assuming it is a dp value to the pixel equivalent.
 */
fun Int.toPixels(resources: Resources): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics).toInt()
}
