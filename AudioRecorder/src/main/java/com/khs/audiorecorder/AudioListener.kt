package com.khs.audiorecorder

/**
 * @author netodevel
 */
interface AudioListener {
    fun onStop(recordingItem: RecordingItem?)
    fun onCancel()
    fun onError(e: Exception?)
}