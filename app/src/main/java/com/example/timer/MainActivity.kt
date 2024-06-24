package com.example.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var seekBar: SeekBar
    private lateinit var startButton: Button
    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false
    companion object {
        private const val KEY_TIME_REMAINING = "time_remaining"
        private const val KEY_IS_RUNNING = "is_running"
        private const val KEY_SEEK_BAR_PROGRESS = "seek_bar_progress"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        timerText = findViewById(R.id.timer_text)
        progressBar = findViewById(R.id.progress_bar)
        seekBar = findViewById(R.id.seek_bar)
        startButton = findViewById(R.id.start_button)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState != null) {
            val timeRemaining = savedInstanceState.getInt(KEY_TIME_REMAINING)
            isRunning = savedInstanceState.getBoolean(KEY_IS_RUNNING)
            val seekBarProgress = savedInstanceState.getInt(KEY_SEEK_BAR_PROGRESS)

            seekBar.progress = seekBarProgress
            if (isRunning) {
                startTimer(timeRemaining)
            } else {
                updateTimerText(timeRemaining)
            }
            seekBar.isEnabled = !isRunning
            startButton.text = if (isRunning) "Stop" else "Start"
        }

        startButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer(seekBar.progress)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTimerText(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        countDownTimer?.cancel()  // Остановим таймер, чтобы избежать утечек

        outState.putInt(KEY_TIME_REMAINING, progressBar.max - progressBar.progress)
        outState.putBoolean(KEY_IS_RUNNING, isRunning)
        outState.putInt(KEY_SEEK_BAR_PROGRESS, seekBar.progress)
    }

    private fun startTimer(timeInSeconds: Int) {
        isRunning = true
        startButton.text = "Stop"
        seekBar.isEnabled = false
        progressBar.max = timeInSeconds

        countDownTimer = object : CountDownTimer(timeInSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                updateTimerText(secondsRemaining)
                progressBar.progress = timeInSeconds - secondsRemaining
            }

            override fun onFinish() {
                stopTimer()
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false
        startButton.text = "Start"
        seekBar.isEnabled = true
        updateTimerText(seekBar.progress)
        progressBar.progress = 0
    }

    private fun updateTimerText(seconds: Int) {
        val minutes = seconds / 60
        val secondsRemaining = seconds % 60
        timerText.text = String.format("%02d:%02d", minutes, secondsRemaining)
    }
}




