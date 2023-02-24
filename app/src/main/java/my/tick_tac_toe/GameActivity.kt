package my.tick_tac_toe

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import my.tick_tac_toe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameField: Array<Array<String>>
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        binding.toPopupMenu.setOnClickListener {
            showPopupMenu()
        }
        binding.toGameClose.setOnClickListener {
            onBackPressed()
        }
        binding.cell00.setOnClickListener {
            makeStepToUser(0, 0)
        }
        binding.cell01.setOnClickListener {
            makeStepToUser(0, 1)
        }
        binding.cell02.setOnClickListener {
            makeStepToUser(0, 2)
        }
        binding.cell10.setOnClickListener {
            makeStepToUser(1, 0)
        }
        binding.cell11.setOnClickListener {
            makeStepToUser(1, 1)
        }
        binding.cell12.setOnClickListener {
            makeStepToUser(1, 2)
        }
        binding.cell20.setOnClickListener {
            makeStepToUser(2, 0)
        }
        binding.cell21.setOnClickListener {
            makeStepToUser(2, 1)
        }
        binding.cell22.setOnClickListener {
            makeStepToUser(2, 2)
        }
        setContentView(binding.root)
        val time = intent.getLongExtra(MainActivity.EXTRA_TIME, 0)
        val gameField = intent.getStringExtra(MainActivity.EXTRA_GAME_FIELD)
        if (time != 0L && gameField != null && gameField != "") restartGame(time, gameField)
        else initGameField()
        mediaPlayer = MediaPlayer.create(this, R.raw.test)
        mediaPlayer.isLooping = true
        val settingsInfo = getCurrentSettings()
        setVolumeMediaPlayer(settingsInfo.soundValue)
        mediaPlayer.start()
        binding.chronometer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

    private fun setVolumeMediaPlayer(soundValue: Int) {
        val volume = soundValue / 100.0
        mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
    }

    private fun initGameField() {
        gameField = Array(3) { Array(3) { EMPTY_SYMBOL } }
    }

    private fun makeStep(row: Int, column: Int, symbol: String) {
        gameField[row][column] = symbol
        makeGameFieldUI("$row$column", symbol)
    }

    private fun makeGameFieldUI(position: String, symbol: String) {
        val resId = when (symbol) {
            BOT_SYMBOL -> R.drawable.ic_zero
            PLAYER_SYMBOL -> R.drawable.ic_cross
            else -> return
        }
        when (position) {
            "00" -> binding.cell00.setImageResource(resId)
            "01" -> binding.cell01.setImageResource(resId)
            "02" -> binding.cell02.setImageResource(resId)
            "10" -> binding.cell10.setImageResource(resId)
            "11" -> binding.cell11.setImageResource(resId)
            "12" -> binding.cell12.setImageResource(resId)
            "20" -> binding.cell20.setImageResource(resId)
            "21" -> binding.cell21.setImageResource(resId)
            "22" -> binding.cell22.setImageResource(resId)
        }
    }

    private fun makeStepToUser(row: Int, column: Int) {
        if (isEmptyField(row, column)) {
            makeStep(row, column, PLAYER_SYMBOL)
            if (checkGameField(row, column, PLAYER_SYMBOL)) {
                showGameStatus(STATUS_PLAYER_WIN)
                return
            }
            if (!isFilledGameField()) {
                val resultCell = makeStepToAI()
                if (checkGameField(resultCell.row, resultCell.column, BOT_SYMBOL)) {
                    showGameStatus(STATUS_PLAYER_LOSE)
                    return
                }
                if (isFilledGameField()) {
                    showGameStatus(STATUS_PLAYER_DRAW)
                    return
                }
            } else {
                showGameStatus(STATUS_PLAYER_DRAW)
                return
            }
        } else Toast.makeText(this, "This field is already filled", Toast.LENGTH_SHORT).show()
    }

    private fun isEmptyField(row: Int, column: Int): Boolean {
        return gameField[row][column] == EMPTY_SYMBOL
    }

    private fun makeStepToAI(): CellGameField {
        val settingsInfo = getCurrentSettings()
        return when (settingsInfo.lvl) {
            0 -> makeStepOfAIEasyLvl()
            1 -> makeStepOfAIMediumLvl()
            2 -> makeStepOfAIHardLvl()
            else -> CellGameField(0, 0)
        }
    }

    private fun makeStepOfAIEasyLvl(): CellGameField {
        var randRow = 0
        var randColumn = 0
        do {
            randRow = (0..2).random()
            randColumn = (0..2).random()
        } while (!isEmptyField(randRow, randColumn))
        makeStep(randRow, randColumn, BOT_SYMBOL)
        return CellGameField(randRow, randColumn)
    }

    private fun makeStepOfAIMediumLvl(): CellGameField {
        val rand = (0..1).random()
        return if (rand == 1) makeStepOfAIHardLvl() else makeStepOfAIEasyLvl()
    }

    private fun makeStepOfAIHardLvl(): CellGameField {
        var bestScore = Double.NEGATIVE_INFINITY
        var move = CellGameField(0, 0)
        val board = gameField.map { it.clone() }.toTypedArray()
        board.forEachIndexed { indexRows, columns ->
            columns.forEachIndexed { indexCols, _ ->
                if (board[indexRows][indexCols] == EMPTY_SYMBOL) {
                    board[indexRows][indexCols] = BOT_SYMBOL
                    val score = minimax(board, false)
                    board[indexRows][indexCols] = EMPTY_SYMBOL
                    if (score > bestScore) {
                        bestScore = score
                        move = CellGameField(indexRows, indexCols)
                    }
                }
            }
        }
        makeStep(move.row, move.column, BOT_SYMBOL)
        return move
    }

    private fun minimax(board: Array<Array<String>>, isMaximizinig: Boolean): Double {
        val result = checkWinner(board)
        result?.let {
            return scores[result]!!
        }
        if (isMaximizinig) {
            var bestScore = Double.NEGATIVE_INFINITY
            board.forEachIndexed { indexRows, columns ->
                columns.forEachIndexed { indexCols, _ ->
                    if (board[indexRows][indexCols] == EMPTY_SYMBOL) {
                        board[indexRows][indexCols] = BOT_SYMBOL
                        val score = minimax(board, false)
                        board[indexRows][indexCols] = EMPTY_SYMBOL
                        if (score > bestScore) {
                            bestScore = score
                        }
                    }
                }
            }
            return bestScore
        } else {
            var bestScore = Double.POSITIVE_INFINITY
            board.forEachIndexed { indexRows, columns ->
                columns.forEachIndexed { indexCols, _ ->
                    if (board[indexRows][indexCols] == EMPTY_SYMBOL) {
                        board[indexRows][indexCols] = PLAYER_SYMBOL
                        val score = minimax(board, true)
                        board[indexRows][indexCols] = EMPTY_SYMBOL
                        if (score < bestScore) {
                            bestScore = score
                        }
                    }
                }
            }
            return bestScore
        }
    }

    private fun checkWinner(board: Array<Array<String>>): Int? {
        var countRowsHuman = 0
        var countRowsAI = 0
        var countLeftDiagonalHuman = 0
        var countLeftDiagonalAI = 0
        var countRightDiagonalHuman = 0
        var countRightDiagonalAI = 0
        board.forEachIndexed { indexRows, columns ->
            if (columns.all { it == PLAYER_SYMBOL }) return STATUS_PLAYER_WIN
            else if (columns.all { it == BOT_SYMBOL }) return STATUS_PLAYER_LOSE
            countRowsHuman = 0
            countRowsAI = 0
            columns.forEachIndexed { indexCols, _ ->
                if (board[indexCols][indexRows] == PLAYER_SYMBOL) countRowsHuman++
                else if (board[indexCols][indexRows] == BOT_SYMBOL) countRowsAI++
                if (indexRows == indexCols && board[indexRows][indexCols] == PLAYER_SYMBOL) countLeftDiagonalHuman++
                else if (indexRows == indexCols && board[indexRows][indexCols] == BOT_SYMBOL) countLeftDiagonalAI++
                if (indexRows == 2 - indexCols && board[indexRows][indexCols] == PLAYER_SYMBOL) countRightDiagonalHuman++
                else if (indexRows == 2 - indexCols && board[indexRows][indexCols] == BOT_SYMBOL) countRightDiagonalAI++
            }
            if (countRowsHuman == 3 || countLeftDiagonalHuman == 3 || countRightDiagonalHuman == 3) return STATUS_PLAYER_WIN
            else if (countRowsAI == 3 || countLeftDiagonalAI == 3 || countRightDiagonalAI == 3) return STATUS_PLAYER_LOSE
        }
        board.forEach {
            if (it.find { it == EMPTY_SYMBOL } != null) return null
        }
        return STATUS_PLAYER_DRAW
    }

    data class CellGameField(val row: Int, val column: Int)

    private fun checkGameField(x: Int, y: Int, symbol: String): Boolean {
        var row = 0
        var column = 0
        var leftDiagonal = 0
        var rightDiagonal = 0
        val n = gameField.size
        for (i in 0..2) {
            if (gameField[x][i] == symbol) column++
            if (gameField[i][y] == symbol) row++
            if (gameField[i][i] == symbol) leftDiagonal++
            if (gameField[i][n - i - 1] == symbol) rightDiagonal++
        }
        val settingsInfo = getCurrentSettings()
        return when (settingsInfo.rules) {
            1 -> {
                column == n
            }
            2 -> {
                row == n
            }
            3 -> {
                row == n || column == n
            }
            4 -> {
                leftDiagonal == n || rightDiagonal == n
            }
            5 -> {
                column == n || leftDiagonal == n || rightDiagonal == n
            }
            6 -> {
                row == n || leftDiagonal == n || rightDiagonal == n
            }
            7 -> {
                row == n || column == n || leftDiagonal == n || rightDiagonal == n
            }
            else -> false
        }
    }

    private fun showGameStatus(status: Int) {
        binding.chronometer.stop()
        val dialog = Dialog(this, R.style.Theme_MyTickTacToe)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
            setContentView(R.layout.dialog_popup_status_game)
            setCancelable(true)
        }
        val image = dialog.findViewById<ImageView>(R.id.dialog_image)
        val text = dialog.findViewById<TextView>(R.id.dialog_text)
        when (status) {
            STATUS_PLAYER_WIN -> {
                image.setImageResource(R.drawable.ic_win)
                text.text = getString(R.string.dialog_status_win)
            }
            STATUS_PLAYER_LOSE -> {
                image.setImageResource(R.drawable.ic_lose)
                text.text = getString(R.string.dialog_status_lose)
            }
            STATUS_PLAYER_DRAW -> {
                image.setImageResource(R.drawable.ic_draw)
                text.text = getString(R.string.dialog_status_draw)
            }
        }
        val button = dialog.findViewById<TextView>(R.id.dialog_ok)
        button.setOnClickListener {
            dialog.hide()
            onBackPressed()
        }
        dialog.show()
    }

    private fun showPopupMenu() {
        binding.chronometer.stop()
        val elapsedMillis = SystemClock.elapsedRealtime() - binding.chronometer.base
        val dialog = Dialog(this, R.style.Theme_MyTickTacToe)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
            setContentView(R.layout.dialog_popup_menu)
            setCancelable(true)
        }
        val toContinue = dialog.findViewById<ImageView>(R.id.dialog_continue)
        toContinue.setOnClickListener {
            dialog.hide()
            binding.chronometer.base = SystemClock.elapsedRealtime() - elapsedMillis
            binding.chronometer.start()
        }
        val toSettings = dialog.findViewById<TextView>(R.id.dialog_settings)
        toSettings.setOnClickListener {
            dialog.hide()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, POPUP_MENU)
        }
        val toExit = dialog.findViewById<TextView>(R.id.dialog_exit)
        toExit.setOnClickListener {
            saveGame(elapsedMillis, convertGameFieldToString())
            dialog.hide()
            onBackPressed()
        }
        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == POPUP_MENU) {
            if (resultCode == RESULT_OK) {
                mediaPlayer = MediaPlayer.create(this, R.raw.test)
                mediaPlayer.isLooping = true
                val settingsInfo = getCurrentSettings()
                setVolumeMediaPlayer(settingsInfo.soundValue)
                mediaPlayer.start()
                binding.chronometer.start()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun isFilledGameField(): Boolean {
        gameField.forEach { strings ->
            if (strings.find { it == EMPTY_SYMBOL } != null) return false
        }
        return true
    }

    private fun convertGameFieldToString(): String {
        val tempArray = arrayListOf<String>()
        gameField.forEach { tempArray.add(it.joinToString(";")) }
        return tempArray.joinToString("\n")
    }

    private fun saveGame(time: Long, gameField: String) {
        getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit().apply() {
            putLong(PREF_TIME, time)
            putString(PREF_GAME_FIELD, gameField)
            apply()
        }
    }

    private fun restartGame(time: Long, gameField: String) {
        binding.chronometer.base = SystemClock.elapsedRealtime() - time
        this.gameField = arrayOf()
        val rows = gameField.split("\n")
        rows.forEach {
            val columns = it.split(";")
            this.gameField += columns.toTypedArray()
        }
        this.gameField.forEachIndexed { indexRow, strings ->
            strings.forEachIndexed { indexColumn, cell ->
                makeGameFieldUI("$indexRow$indexColumn", cell)
            }
        }
    }

    private fun getCurrentSettings(): SettingsActivity.SettingsInfo {
        this.getSharedPreferences("game", MODE_PRIVATE).apply {
            val soundValue = getInt(SettingsActivity.PREF_SOUND_VALUE, 50)
            val lvl = getInt(SettingsActivity.PREF_LVL, 0)
            val rules = getInt(SettingsActivity.PREF_RULES, 7)
            return SettingsActivity.SettingsInfo(soundValue, lvl, rules)
        }
    }

    companion object {
        const val STATUS_PLAYER_WIN = 1
        const val STATUS_PLAYER_LOSE = 2
        const val STATUS_PLAYER_DRAW = 3
        const val PREF_TIME = "pref_time"
        const val PREF_GAME_FIELD = "pref_game_field"
        const val POPUP_MENU = 123
        val scores = hashMapOf(
            Pair(STATUS_PLAYER_WIN, -1.0),
            Pair(STATUS_PLAYER_DRAW, 0.0),
            Pair(STATUS_PLAYER_LOSE, 1.0)
        )
        const val PLAYER_SYMBOL = "X"
        const val BOT_SYMBOL = "0"
        const val EMPTY_SYMBOL = " "
    }

}