package my.tick_tac_toe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import my.tick_tac_toe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameField: Array<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        binding.toPopupMenu.setOnClickListener {

        }
        binding.toGameClose.setOnClickListener {

        }
        binding.cell11.setOnClickListener {

        }
        binding.cell12.setOnClickListener {

        }
        binding.cell13.setOnClickListener {

        }
        binding.cell21.setOnClickListener {

        }
        binding.cell22.setOnClickListener {

        }
        binding.cell23.setOnClickListener {

        }
        binding.cell31.setOnClickListener {

        }
        binding.cell32.setOnClickListener {

        }
        binding.cell33.setOnClickListener {

        }
        setContentView(binding.root)
        initGameField()
    }

    private fun initGameField() {
        gameField = Array(3) { Array(3) { " " } }
    }

    private fun makeStep(row: Int, column: Int, symbol: String) {
        gameField[row][column] = symbol
        makeStepUI("$row$column", symbol)
    }

    private fun makeStepUI(position: String, symbol: String) {
        val resId = when (symbol) {
            "0" -> R.drawable.ic_zero
            "X" -> R.drawable.ic_cross
            else -> return
        }
        when (position) {
            "11" -> binding.cell11.setImageResource(resId)
            "12" -> binding.cell12.setImageResource(resId)
            "13" -> binding.cell13.setImageResource(resId)
            "21" -> binding.cell21.setImageResource(resId)
            "22" -> binding.cell22.setImageResource(resId)
            "23" -> binding.cell23.setImageResource(resId)
            "31" -> binding.cell31.setImageResource(resId)
            "32" -> binding.cell32.setImageResource(resId)
            "33" -> binding.cell33.setImageResource(resId)
        }
    }
}