package com.eypancakir.yapbozv1

import android.graphics.Bitmap
import android.graphics.RectF

class PuzzlePiece(var bitmap: Bitmap, var x: Float, var y: Float, var index: Int) {
    var rectF: RectF = RectF(x, y, x + bitmap.width, y + bitmap.height)
    var isInRightPlace: Boolean = false
    private var originalRectF: RectF = RectF(rectF)

    fun checkIsInRightPlace(originalRectF: RectF) {
        isInRightPlace = rectF.contains(originalRectF)
    }

    fun reset() {
        isInRightPlace = false
        rectF = RectF(originalRectF)
    }
}

