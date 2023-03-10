package my.tick_tac_toe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import my.tick_tac_toe.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private var currentSoundValue = 0
    private var currentLvl = 0
    private var currentRules = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val currentSettings = getCurrentSettings()
        currentSoundValue = currentSettings.soundValue
        currentLvl = currentSettings.lvl
        currentRules = currentSettings.rules
        if (currentLvl == 0) binding.prevLvl.visibility = View.INVISIBLE
        else if (currentLvl == 2) binding.nextLvl.visibility = View.INVISIBLE
        binding.infoLevel.text = resources.getStringArray(R.array.game_level)[currentLvl]
        binding.soundBar.progress = currentSoundValue
        when (currentRules) {
            1 -> binding.checkBoxVertical.isChecked = true
            2 -> binding.checkBoxHorizontal.isChecked = true
            3 -> {
                binding.checkBoxVertical.isChecked = true
                binding.checkBoxHorizontal.isChecked = true
            }
            4 -> binding.checkBoxDiagonal.isChecked = true
            5 -> {
                binding.checkBoxVertical.isChecked = true
                binding.checkBoxDiagonal.isChecked = true
            }
            6 -> {
                binding.checkBoxHorizontal.isChecked = true
                binding.checkBoxDiagonal.isChecked = true
            }
            7 -> {
                binding.checkBoxVertical.isChecked = true
                binding.checkBoxHorizontal.isChecked = true
                binding.checkBoxDiagonal.isChecked = true
            }
        }
        binding.prevLvl.setOnClickListener {
            currentLvl--
            if (currentLvl == 0) binding.prevLvl.visibility = View.INVISIBLE
            else if (currentLvl == 1) binding.nextLvl.visibility = View.VISIBLE
            updateLvl(currentLvl)
            binding.infoLevel.text = resources.getStringArray(R.array.game_level)[currentLvl]
        }
        binding.nextLvl.setOnClickListener {
            currentLvl++
            if (currentLvl == 2) binding.prevLvl.visibility = View.INVISIBLE
            else if (currentLvl == 1) binding.nextLvl.visibility = View.VISIBLE
            updateLvl(currentLvl)
            binding.infoLevel.text = resources.getStringArray(R.array.game_level)[currentLvl]
        }
        binding.soundBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                currentSoundValue = value
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                updateSoundValue(currentSoundValue)
            }
        })
        binding.checkBoxVertical.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) currentRules++
            else currentRules--
            updateRules(currentRules)
        }
        binding.checkBoxHorizontal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) currentRules += 2
            else currentRules -= 2
            updateRules(currentRules)
        }
        binding.checkBoxDiagonal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) currentRules += 4
            else currentRules -= 4
            updateRules(currentRules)
        }
        binding.toBack.setOnClickListener {
            setResult(RESULT_OK)
            onBackPressed()
        }
        setContentView(binding.root)
    }

    private fun updateSoundValue(volume: Int) {
        getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit().apply {
            putInt(PREF_SOUND_VALUE, volume)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun updateLvl(lvl: Int) {
        getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit().apply {
            putInt(PREF_LVL, lvl)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun updateRules(rules: Int) {
        getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit().apply {
            putInt(PREF_RULES, rules)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun getCurrentSettings(): SettingsInfo {
        getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).apply {
            val soundValue = getInt(PREF_SOUND_VALUE, 50)
            val lvl = getInt(PREF_LVL, 0)
            val rules = getInt(PREF_RULES, 7)
            return SettingsInfo(soundValue, lvl, rules)
        }
    }

    data class SettingsInfo(val soundValue: Int, val lvl: Int, val rules: Int)

    companion object {
        const val PREF_SOUND_VALUE = "pref_sound_value"
        const val PREF_LVL = "pref_lvl"
        const val PREF_RULES = "pref_rules"
    }
}