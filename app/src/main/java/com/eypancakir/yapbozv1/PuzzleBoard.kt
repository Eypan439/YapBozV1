package com.eypancakir.yapbozv1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class PuzzleBoard(context: Context, var width: Int, var height: Int, var piecesCount: Int) {
    private var pieces: MutableList<PuzzlePiece> = mutableListOf()
    private var originalPieces: MutableList<PuzzlePiece> = mutableListOf()
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.original_image)

    init {
        splitBitmap()
    }

    private fun splitBitmap() {
        val blockSize = minOf(width, height) / piecesCount
        for (y in 0 until piecesCount) {
            for (x in 0 until piecesCount) {
                val startX = x * blockSize
                val startY = y * blockSize
                val pieceBitmap = Bitmap.createBitmap(bitmap, startX, startY, blockSize, blockSize)
                val puzzlePiece = PuzzlePiece(pieceBitmap, startX.toFloat(), startY.toFloat(), y * piecesCount + x)
                pieces.add(puzzlePiece)
                originalPieces.add(PuzzlePiece(pieceBitmap, startX.toFloat(), startY.toFloat(), y * piecesCount + x))
            }
        }
    }

    fun shuffle() {
        pieces.shuffle()
    }

    fun reset() {
        for (i in 0 until piecesCount * piecesCount) {
            pieces[i].reset()
            pieces[i].checkIsInRightPlace(originalPieces[i].rectF)
        }
        shuffle()
    }
}
