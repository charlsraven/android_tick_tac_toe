package my.tick_tac_toe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import my.tick_tac_toe.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private var currentSound = 0
    private var currentLvl = 0
    private var currentRules = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}