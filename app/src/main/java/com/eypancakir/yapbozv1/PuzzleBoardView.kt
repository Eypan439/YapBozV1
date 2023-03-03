package com.eypancakir.yapbozv1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PuzzleBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var board: PuzzleBoard? = null

    fun setBoard(board: PuzzleBoard) {
        this.board = board
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Tahta boşsa veya PuzzleBoard yoksa hiçbir şey yapma
        if (board == null) return

        // Her parça için çizim koordinatlarını hesapla ve resmi çiz
        for (piece in board!!.pieces) {
            val left = piece.column * piece.width
            val top = piece.row * piece.height
            val right = left + piece.width
            val bottom = top + piece.height
            canvas.drawBitmap(piece.bitmap, null, Rect(left, top, right, bottom), null)
        }
    }
}

