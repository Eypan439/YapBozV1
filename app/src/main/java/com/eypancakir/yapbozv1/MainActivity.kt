package com.eypancakir.yapbozv1

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var gridLayout: GridLayout
    private lateinit var buttonShuffle: Button
    private lateinit var selectedImageUri: Uri
    private lateinit var boardView: PuzzleBoardView

    private val REQUEST_IMAGE_CAPTURE = 1
    private val pieces = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        gridLayout = findViewById(R.id.gridLayout)
        buttonShuffle = findViewById(R.id.buttonShuffle)

        // Yapboz tahtasını oluştur
        val board = createPuzzleBoard()

        // PuzzleBoardView'i bul
        boardView = findViewById(R.id.board_view)

        // PuzzleBoardView'e tahtayı ayarla ve güncelle
        boardView.setBoard(board)
        boardView.invalidate()

        // PuzzleBoardView nesnesini oluştur
        puzzleBoardView = PuzzleBoardView(this, null)

        // ConstraintLayout içine PuzzleBoardView nesnesini yerleştir
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint_layout)
        constraintLayout.addView(puzzleBoardView)

        // Bitmap'i PuzzleBoard'a dönüştür ve PuzzleBoardView'a ayarla
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.my_image)
        val board = PuzzleBoard.fromBitmap(bitmap, 10)
        puzzleBoardView.setBoard(board)

        buttonShuffle.setOnClickListener { shuffle() }

        dispatchTakePictureIntent()


    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            splitImage(imageBitmap)
        }
    }

    private fun splitImage(image: Bitmap) {
        val width = image.width / 5
        val height = image.height / 2

        for (i in 0 until 10) {
            val piece = ImageView(this)
            piece.layoutParams = GridLayout.LayoutParams().apply {
                width = width
                height = height
            }
            piece.setImageBitmap(Bitmap.createBitmap(image, (i % 5) * width, (i / 5) * height, width, height))
            pieces.add(piece)
        }

        pieces.shuffle()

        for (piece in pieces) {
            gridLayout.addView(piece)
        }
    }

    private fun shuffle() {
        pieces.shuffle()
        gridLayout.removeAllViews()
        for (piece in pieces) {
            gridLayout.addView(piece)
        }

        shuffle()
        checkGameOver()

    }

    private fun checkGameOver() {
        var gameOver = true
        for (i in 0 until 10) {
            val piece = gridLayout.getChildAt(i) as ImageView
            if (piece.tag != i) {
                gameOver = false
                break
            }
        }
        if (gameOver) {
            Toast.makeText(this, "Congratulations! You solved the puzzle!", Toast.LENGTH_LONG).show()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            val puzzle = Puzzle(this, bitmap)
            gridLayout.columnCount = puzzle.columns
            gridLayout.rowCount = puzzle.rows
            for (row in 0 until puzzle.rows) {
                for (col in 0 until puzzle.columns) {
                    val piece = ImageView(this)
                    piece.setImageBitmap(puzzle.getPiece(row, col))
                    piece.tag = puzzle.getPieceId(row, col)
                    piece.setOnTouchListener(PieceTouchListener())
                    gridLayout.addView(piece)
                }
            }
            shuffle()
        }
    }

    inner class PieceTouchListener : View.OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val piece = view as ImageView
                val dragData = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(piece)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    piece.startDragAndDrop(dragData, shadowBuilder, piece, 0)
                } else {
                    piece.startDrag(dragData, shadowBuilder, piece, 0)
                }
                piece.visibility = View.INVISIBLE
                return true
            } else {
                return false
            }
        }
    }

    inner class PieceDragListener : View.OnDragListener {
        override fun onDrag(view: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    return true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.alpha = 0.5f
                    return true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    return true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.alpha = 1.0f
                    return true
                }
                DragEvent.ACTION_DROP -> {
                    val droppedPiece = event.localState as ImageView
                    val droppedId = droppedPiece.tag as Int
                    val dropTarget = view as ImageView
                    val tempTag = dropTarget.tag
                    val tempBitmap = (dropTarget.drawable as BitmapDrawable).bitmap
                    dropTarget.setImageBitmap((droppedPiece.drawable as BitmapDrawable).bitmap)
                    dropTarget.tag = droppedId
                    droppedPiece.setImageBitmap(tempBitmap)
                    droppedPiece.tag = tempTag
                    droppedPiece.visibility = View.VISIBLE
                    checkGameOver()
                    return true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val piece = view as ImageView
                    piece.visibility = View.VISIBLE
                    view.alpha = 1.0f
                    return true
                }
                else -> {
                    return false
                }
            }
        }
    }

}
