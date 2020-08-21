package com.khs.audiorecorder

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File
import java.io.IOException

/**
 * @author netodevel
 */
class AudioRecording {
    private var mFileName: String? = null
    private var mContext: Context? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var audioListener: AudioListener? = null
    private var mRecorder: MediaRecorder
    private var mStartingTimeMillis: Long = 0
    private var mElapsedMillis: Long = 0

    constructor(context: Context?) {
        mRecorder = MediaRecorder()
        mContext = context
    }

    constructor() {
        mRecorder = MediaRecorder()
    }

    fun setNameFile(nameFile: String?): AudioRecording {
        mFileName = nameFile
        return this
    }

    fun start(audioListener: AudioListener?): AudioRecording {
        this.audioListener = audioListener
        try {
            mRecorder.reset()
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mRecorder.setOutputFile(mContext!!.cacheDir.toString() + mFileName)
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mRecorder.prepare()
            mRecorder.start()
            mStartingTimeMillis = System.currentTimeMillis()
        } catch (e: IOException) {
            this.audioListener!!.onError(e)
        }
        return this
    }

    fun stop(cancel: Boolean) {
        try {
            mRecorder.stop()
        } catch (e: RuntimeException) {
            deleteOutput()
        }
        mRecorder.release()
        mElapsedMillis = System.currentTimeMillis() - mStartingTimeMillis
        val recordingItem = RecordingItem()
        recordingItem.filePath = mContext!!.cacheDir.toString() + mFileName
        recordingItem.name = mFileName
        recordingItem.length = mElapsedMillis.toInt()
        recordingItem.time = System.currentTimeMillis()
        if (!cancel) {
            audioListener!!.onStop(recordingItem)
        }
    }

    private fun deleteOutput() {
        val file = File(mContext!!.cacheDir.toString() + mFileName)
        if (file.exists()) {
            file.delete()
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