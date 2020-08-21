package com.khs.audiorecordbutton

import android.Manifest.permission
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.khs.audiorecorder.AudioListener
import com.khs.audiorecorder.AudioRecordButton
import com.khs.audiorecorder.AudioRecording
import com.khs.audiorecorder.RecordingItem

class MainActivity : AppCompatActivity() {

    private var mAudioRecordButton: AudioRecordButton? = null
    private var audioRecording: AudioRecording? = null

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
        mAudioRecordButton?.setOnAudioListener(object : AudioListener {
            override fun onStop(recordingItem: RecordingItem?) {
                Toast.makeText(baseContext, "Audio..", Toast.LENGTH_SHORT).show()
                if (recordingItem != null) {
                    audioRecording?.play(recordingItem)
                }
            }

            override fun onCancel() {
                Toast.makeText(baseContext, "Cancel", Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: Exception?) {
                Log.d("MainActivity", "Error: " + e?.message)
            }
        })
    }

    private fun initView() {
        mAudioRecordButton =
            findViewById<View>(R.id.audio_record_button) as AudioRecordButton
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}