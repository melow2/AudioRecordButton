package com.khs.audiorecorder

import android.animation.LayoutTransition
import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import java.util.*

class AudioRecordButton : LinearLayout {
    private val DEFAULT_ICON_SIZE = Math.round(resources.getDimension(R.dimen.default_icon_size))
    private val DEFAULT_REMOVE_ICON_SIZE = Math.round(resources.getDimension(R.dimen.default_icon_remove_size))
    private var mContext: Context? = null
    private var mLayoutVoice: RelativeLayout? = null
    private var mImageView: ImageView? = null
    private var mImageButton: ImageButton? = null
    private var mAudioListener: AudioListener? = null
    private var mAudioRecording: AudioRecording? = null
    private var initialX = 0f
    private var initialXImageButton = 0f
    private var initialTouchX = 0f
    private var recorderImageWidth = 0
    private var recorderImageHeight = 0
    private var removeImageWidth = 0
    private var removeImageHeight = 0
    private var drawableMicVoice: Drawable? = null
    private var drawableRemoveButton: Drawable? = null
    private var isPlaying = false
    private var isPausing = false

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        setupLayout(context, attrs, -1, -1)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (!isPlaying && !isPausing) {
                startRecord()
                isPlaying = true
                initialTouchX = event.rawX
                changeImageView()
                if (initialX == 0f) {
                    initialX = mImageView!!.x
                }
                mAudioListener!!.onStart()
                return true
            }
            MotionEvent.ACTION_UP -> if (isPlaying && !isPausing) {
                isPausing = true
                stopRecord(false)
            }
            else -> return false
        }
        return true
    }

    private fun startRecord() {
        if (mAudioListener != null) {
            val audioListener: AudioListener = object : AudioListener {
                override fun onStart() {
                    mAudioListener!!.onStart()
                }
                override fun onStop(recordingItem: RecordingItem?) {
                    mAudioListener!!.onStop(recordingItem)
                }
                override fun onError(e: Exception?) {
                    mAudioListener!!.onError(e)
                }
            }
            mAudioRecording = AudioRecording(mContext)
                .setNameFile("/" + UUID.randomUUID() + "-audio.mp3")
                .start(audioListener)
        }
    }

    private fun stopRecord(cancel: Boolean) {
        if (mAudioListener != null) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                mAudioRecording!!.stop(cancel)
                unRevealImageView()
                isPlaying = false
                isPausing = false
            }, 700)
        }
    }

    fun setOnAudioListener(audioListener: AudioListener?) {
        mAudioListener = audioListener
    }

    fun setupLayout(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        mContext = context
        /**
         * Component Attributes
         */
        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            val typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.AudioButton,
                defStyleAttr, defStyleRes
            )
            recorderImageWidth = typedArray.getDimension(
                R.styleable.AudioButton_recorder_image_size,
                ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
            ).toInt()
            recorderImageHeight = typedArray.getDimension(
                R.styleable.AudioButton_recorder_image_size,
                ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
            ).toInt()
            removeImageWidth = typedArray.getDimension(
                R.styleable.AudioButton_remove_image_size,
                ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
            ).toInt()
            removeImageHeight = typedArray.getDimension(
                R.styleable.AudioButton_remove_image_size,
                ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
            ).toInt()
            drawableMicVoice = typedArray.getDrawable(R.styleable.AudioButton_recorder_image)
            drawableRemoveButton = typedArray.getDrawable(R.styleable.AudioButton_remove_image)
        }
        /**
         * Layout to voice and cancel audio
         */
        mLayoutVoice = RelativeLayout(context)
        val layoutVoiceParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(mLayoutVoice, layoutVoiceParams)
        /**
         * Image voice
         */
        mImageView = ImageView(context)
        mImageView!!.background =
            if (drawableMicVoice != null) drawableMicVoice else ContextCompat.getDrawable(
                context,
                R.drawable.mic_shape
            )
        val layoutParamImage = LayoutParams(
            if (recorderImageWidth > 0) recorderImageWidth else DEFAULT_ICON_SIZE,
            if (recorderImageHeight > 0) recorderImageHeight else DEFAULT_ICON_SIZE
        )
        mLayoutVoice!!.addView(mImageView, layoutParamImage)
    }

    fun changeImageView() {
        val transition = LayoutTransition()
        transition.setDuration(600)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.CHANGING)
        }
        this.layoutTransition = transition
        mImageView!!.scaleX = 0.8f
        mImageView!!.scaleY = 0.8f
        requestLayout()
    }

    fun unRevealImageView() {
        mImageView!!.scaleX = 1f
        mImageView!!.scaleY = 1f
        requestLayout()
    }
}