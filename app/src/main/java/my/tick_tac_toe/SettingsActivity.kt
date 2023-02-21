package my.tick_tac_toe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import my.tick_tac_toe.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private var currentSound = 0
    private var currentLvl = 0
    private var currentRules = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        binding.prevLvl.setOnClickListener {
            currentLvl--
        }
        binding.nextLvl.setOnClickListener {
            currentLvl++
        }
        binding.soundBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                currentSound = value
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
        binding.checkBoxVertical.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                currentRules++
            else
                currentRules--
        }
        binding.checkBoxHorizontal.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                currentRules+=2
            else
                currentRules-=2
        }
        binding.checkBoxDiagonal.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                currentRules+=4
            else
                currentRules-=4
        }
        setContentView(binding.root)
    }
}