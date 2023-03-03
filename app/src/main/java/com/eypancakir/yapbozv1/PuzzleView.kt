package com.eypancakir.yapbozv1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.SurfaceHolder
import android.view.SurfaceView

class PuzzleView(context: Context, var puzzlePieces: List<PuzzlePiece>) : SurfaceView(context), SurfaceHolder.Callback {
    private var surfaceHolder: SurfaceHolder = holder
    private var canvas: Canvas? = null

    init {
        surfaceHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Canvas'ı başlat
        canvas = holder.lockCanvas()
        drawPuzzlePieces()
        surfaceHolder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private fun drawPuzzlePieces() {
        // Parçaları çiz
        canvas?.drawColor(Color.WHITE)
        for (piece in puzzlePieces) {
            canvas?.drawBitmap(piece.bitmap, null, piece.rectF, null)
        }
    }
}
