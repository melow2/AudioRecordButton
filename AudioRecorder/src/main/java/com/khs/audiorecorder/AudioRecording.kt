package com.khs.audiorecorder

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author netodevel
 */
class AudioRecording {
    private var mFileName: String? = null
    private var mContext: Context? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var audioListener: AudioListener? = null
    private var mRecorder: MediaRecorder?=null
    private var mStartingTimeMillis: Long = 0
    private var mElapsedMillis: Long = 0
    private lateinit var filePath:String

    constructor(context: Context?) {
        mContext = context
        filePath = mContext?.externalCacheDir.toString()
    }

    fun setNameFile(nameFile: String?): AudioRecording {
        mFileName = nameFile
        return this
    }

    fun start(audioListener: AudioListener?): AudioRecording {
        mRecorder = MediaRecorder()
        this.audioListener = audioListener
        try {
            mRecorder?.reset()
            mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mRecorder?.setOutputFile(filePath+ mFileName)
            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mRecorder?.prepare()
            mRecorder?.start()
            mStartingTimeMillis = System.currentTimeMillis()
        } catch (e: IOException) {
            this.audioListener!!.onError(e)
        }
        return this
    }

    fun stop(cancel: Boolean) {
        try {
          if(mRecorder!=null){
              mRecorder?.release()
              mRecorder = null
          }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            deleteOutput()
        }
        mElapsedMillis = System.currentTimeMillis() - mStartingTimeMillis
        val recordingItem = RecordingItem()
        recordingItem.filePath = filePath + mFileName
        recordingItem.name = mFileName
        recordingItem.length = mElapsedMillis.toInt()
        recordingItem.time = System.currentTimeMillis()
        if (!cancel) {
            audioListener!!.onStop(recordingItem)
        }
    }

    fun deleteOutput() {
        val file = File(filePath + mFileName)
        if (file.exists()) {
            file.delete()
        }
    }

    fun clearCacheData() {
        val cache = mContext?.externalCacheDir
        if (cache != null) {
            if (cache.isDirectory) {
                val children: Array<String> = cache.list()
                for (i in children.indices) {
                    File(cache, children[i]).delete()
                }
            }
        }
    }


    fun play(recordingItem: RecordingItem) {
        try {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.setDataSource(recordingItem.filePath)
            mMediaPlayer!!.prepare()
            mMediaPlayer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}