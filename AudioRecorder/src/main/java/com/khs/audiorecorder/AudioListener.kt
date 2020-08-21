package com.khs.audiorecorder

/**
 * @author netodevel
 */
interface AudioListener {
    fun onStart()
    fun onStop(recordingItem: RecordingItem?)
    fun onError(e: Exception?)
}