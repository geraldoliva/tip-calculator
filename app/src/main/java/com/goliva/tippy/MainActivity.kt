package com.goliva.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.goliva.tippy.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_PERSON_COUNT = 1

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBarTip.progress = INITIAL_TIP_PERCENT
        binding.tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        binding.seekBarPersonCount.progress = INITIAL_PERSON_COUNT
        binding.tvSplitByLabel.text = "Split by $INITIAL_PERSON_COUNT"

        binding.seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1")
                binding.tvTipPercentLabel.text = "$p1%"
                computeTipAndTotal()
                updateTipDescription(p1)
                computePerPersonTotal()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() == ".") {
                    binding.etBaseAmount.setText("0.")
                    binding.etBaseAmount.setSelection(2)
                }
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
                computePerPersonTotal()
            }
        })

        binding.seekBarPersonCount.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onPersonCountChanged $p1")
                binding.tvSplitByLabel.text = "Split by $p1"
                computePerPersonTotal()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
    }

    private fun computePerPersonTotal() {
        if (binding.etBaseAmount.text.isEmpty()) {
            binding.tvPerPersonTotalAmount.text = ""
            return
        }
        // Computer per person amount
        val perPersonAmount = binding.tvTotalAmount.text.toString().toDouble() / binding.seekBarPersonCount.progress
        binding.tvPerPersonTotalAmount.text = "%.2f".format(perPersonAmount)
    }

    private fun updateTipDescription(tipPercent: Int) {
        val  tipDescription = when (tipPercent) {
            in 0..9 ->"Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        binding.tvTipDescription.text = tipDescription
        // Update the color based on the tip
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / binding.seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        binding.tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (binding.etBaseAmount.text.isEmpty()) {
            binding.tvTipAmount.text = ""
            binding.tvTotalAmount.text = ""
            return
        }
        // Get the value of the base and tip percent
        val baseAmount = binding.etBaseAmount.text.toString().toDouble()
        val tipPercent = binding.seekBarTip.progress
        // Compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        // Update the UI
        binding.tvTipAmount.text = "%.2f".format(tipAmount)
        binding.tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}