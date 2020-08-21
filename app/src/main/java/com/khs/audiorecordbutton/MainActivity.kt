package com.khs.audiorecordbutton

import android.Manifest.permission
import android.animation.LayoutTransition
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.method.MovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khs.audiorecorder.AudioListener
import com.khs.audiorecorder.AudioRecordButton
import com.khs.audiorecorder.AudioRecording
import com.khs.audiorecorder.RecordingItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mAudioRecordButton: AudioRecordButton? = null
    private var audioRecording: AudioRecording? = null
    private var mLayoutChronomter: LinearLayout?=null
    private var mChronometer: Chronometer?=null
    private var mBtnRecord: FloatingActionButton?=null
    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        audioRecording = AudioRecording(baseContext)
        initView()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                permission.WRITE_EXTERNAL_STORAGE,
                permission.RECORD_AUDIO,
                permission.READ_EXTERNAL_STORAGE
            ),
            0
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.WRITE_EXTERNAL_STORAGE),
            0
        )
/*

        mBtnRecord?.setOnTouchListener { p0, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN ->{
                    mLayoutChronomter?.visibility = View.VISIBLE
                    changeImageView()
                    true
                }
                MotionEvent.ACTION_UP ->{
                    mChronometer!!.stop()
                    mLayoutChronomter?.visibility = View.GONE
                    true
                }
                else -> false
            }
        }
*/

        mAudioRecordButton?.setOnAudioListener(object : AudioListener {
            override fun onStop(recordingItem: RecordingItem?) {
                Toast.makeText(baseContext, "Audio..", Toast.LENGTH_SHORT).show()
                if (recordingItem != null) {
                    audioRecording?.play(recordingItem)
                }
                mChronometer!!.stop()
                mLayoutChronomter?.visibility = View.GONE
            }

            override fun onStart() {
                mLayoutChronomter?.visibility = View.VISIBLE
                changeImageView()
            }

            override fun onError(e: Exception?) {
                Log.d("MainActivity", "Error: " + e?.message)
            }
        })
    }

    private fun initView() {
        mAudioRecordButton = findViewById<View>(R.id.audio_record_button) as AudioRecordButton
        mLayoutChronomter = findViewById<View>(R.id.lyt_chronomter) as LinearLayout
        mChronometer = findViewById<View>(R.id.test_chronomter) as Chronometer
        // mBtnRecord = findViewById<View>(R.id.btn_record) as FloatingActionButton
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun changeImageView() {
        mChronometer!!.base = SystemClock.elapsedRealtime()
        mChronometer!!.start()
    }
}