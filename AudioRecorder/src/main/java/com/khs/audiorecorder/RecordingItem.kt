package com.khs.audiorecorder

import android.os.Parcel
import android.os.Parcelable

class RecordingItem : Parcelable {
    var name: String? = null
    var filePath: String? = null
    var id = 0
    var length = 0
    var time: Long = 0

    constructor() {}
    constructor(`in`: Parcel) {
        name = `in`.readString()
        filePath = `in`.readString()
        id = `in`.readInt()
        length = `in`.readInt()
        time = `in`.readLong()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeInt(length)
        dest.writeLong(time)
        dest.writeString(filePath)
        dest.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<RecordingItem?> =
            object : Parcelable.Creator<RecordingItem?> {
                override fun createFromParcel(`in`: Parcel): RecordingItem? {
                    return RecordingItem(`in`)
                }

                override fun newArray(size: Int): Array<RecordingItem?> {
                    return arrayOfNulls(size)
                }
            }
    }
}