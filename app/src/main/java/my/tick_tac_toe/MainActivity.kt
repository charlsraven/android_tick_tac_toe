package my.tick_tac_toe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import my.tick_tac_toe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyTickTacToe)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toNewGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        binding.toContinueGame.setOnClickListener {
            val data = getInfoAboutLastGame()
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra(EXTRA_TIME, data.time)
                putExtra(EXTRA_GAME_FIELD, data.gameField)
            }
            startActivity(intent)
        }
        binding.toSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        setContentView(binding.root)
    }

    private fun getInfoAboutLastGame(): InfoGame {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)) {
            val time = getLong(GameActivity.PREF_TIME, 0L)
            val gameField = getString(GameActivity.PREF_GAME_FIELD, "")
            return if (gameField != null)
                InfoGame(time, gameField)
            else
                InfoGame(0L, "")
        }
    }

    data class InfoGame(val time: Long, val gameField: String)

    companion object {
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_GAME_FIELD = "extra_game_field"
    }
}