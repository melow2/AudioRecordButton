package com.khs.audiorecorder

import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import java.util.*

class AudioRecordButton : RelativeLayout {
    private val DEFAULT_ICON_SIZE =
        Math.round(resources.getDimension(R.dimen.default_icon_size))
    private val DEFAULT_REMOVE_ICON_SIZE =
        Math.round(resources.getDimension(R.dimen.default_icon_remove_size))
    private var mContext: Context? = null
    private var mLayoutTimer: RelativeLayout? = null
    private var mLayoutVoice: RelativeLayout? = null
    private var mChronometer: Chronometer? = null
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
    val viewParams: WindowManager.LayoutParams? = null

    constructor(context: Context) : super(context) {
        setupLayout(context, null, -1, -1)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        setupLayout(context, attrs, -1, -1)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        setupLayout(context, attrs, defStyleAttr, -1)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setupLayout(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (!isPlaying && !isPausing) {
                isPlaying = true
                initialTouchX = event.rawX
                changeImageView()
                if (initialX == 0f) {
                    initialX = mImageView!!.x
                }
                mLayoutTimer!!.visibility = View.VISIBLE
                startRecord()
                return true
            }
            MotionEvent.ACTION_UP -> if (isPlaying && !isPausing) {
                isPausing = true
                moveImageToBack()
                mLayoutTimer!!.visibility = View.INVISIBLE
                if (mImageView!!.x < DEFAULT_REMOVE_ICON_SIZE - 10) {
                    stopRecord(true)
                } else {
                    stopRecord(false)
                }
            }
            else -> return false
        }
        return true
    }

    private fun moveImageToBack() {
        mImageButton!!.alpha = 0.5f
        val positionAnimator =
            ValueAnimator.ofFloat(mImageView!!.x, initialX)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener { animation ->
            val x = animation.animatedValue as Float
            mImageView!!.x = x
            if (mImageView!!.x > DEFAULT_REMOVE_ICON_SIZE) {
                unRevealSizeToRemove()
            }
        }
        positionAnimator.duration = 200
        positionAnimator.start()
    }

    private fun startRecord() {
        if (mAudioListener != null) {
            val audioListener: AudioListener = object : AudioListener {
                override fun onStop(recordingItem: RecordingItem?) {
                    mAudioListener!!.onStop(recordingItem)
                }

                override fun onCancel() {
                    mAudioListener!!.onCancel()
                }

                override fun onError(e: Exception?) {
                    mAudioListener!!.onError(e)
                }
            }
            mAudioRecording = AudioRecording(mContext)
                .setNameFile("/" + UUID.randomUUID() + "-audio.ogg")
                .start(audioListener)
        }
    }

    private fun stopRecord(cancel: Boolean) {
        if (mAudioListener != null) {
            val handler = Handler()
            handler.postDelayed({
                mAudioRecording!!.stop(cancel)
                unRevealImageView()
                isPlaying = false
                isPausing = false
            }, 300)
        }
    }

    fun setOnAudioListener(audioListener: AudioListener?) {
        mAudioListener = audioListener
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
         * layout to chronometer
         */
        mLayoutTimer = RelativeLayout(context)
        mLayoutTimer!!.id = 9 + 1
        mLayoutTimer!!.visibility = View.INVISIBLE
        mLayoutTimer!!.background = ContextCompat.getDrawable(context, R.drawable.shape_event)
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(ALIGN_PARENT_TOP, TRUE)
        layoutParams.addRule(CENTER_HORIZONTAL, TRUE)
        val margin =
            Math.round(resources.getDimension(R.dimen.chronometer_margin))
        layoutParams.setMargins(margin, margin, margin, margin)
        addView(mLayoutTimer, layoutParams)
        /**
         * chronometer
         */
        mChronometer = Chronometer(context)
        mChronometer!!.setTextColor(Color.WHITE)
        val layoutParamsChronometer = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParamsChronometer.addRule(CENTER_IN_PARENT, TRUE)
        mLayoutTimer!!.addView(mChronometer, layoutParamsChronometer)
        /**
         * Layout to voice and cancel audio
         */
        mLayoutVoice = RelativeLayout(context)
        val layoutVoiceParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutVoiceParams.addRule(BELOW, 9 + 1)
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
        layoutParamImage.addRule(CENTER_IN_PARENT, TRUE)
        mLayoutVoice!!.addView(mImageView, layoutParamImage)
        /**
         * Image Button
         */
        mImageButton = ImageButton(context)
        mImageButton!!.visibility = View.INVISIBLE
        mImageButton!!.alpha = 0.5f
        mImageButton!!.setImageDrawable(
            if (drawableRemoveButton != null) drawableRemoveButton else ContextCompat.getDrawable(
                context,
                R.drawable.ic_close
            )
        )
        mImageButton!!.background = ContextCompat.getDrawable(context, R.drawable.shape_circle)
        mImageButton!!.setColorFilter(Color.WHITE)
        val layoutParamImageButton = LayoutParams(
            if (removeImageWidth > 0 && removeImageWidth < DEFAULT_REMOVE_ICON_SIZE) removeImageWidth else DEFAULT_REMOVE_ICON_SIZE,
            if (removeImageHeight > 0 && removeImageHeight < DEFAULT_REMOVE_ICON_SIZE) removeImageHeight else DEFAULT_REMOVE_ICON_SIZE
        )
        layoutParamImageButton.addRule(CENTER_VERTICAL, TRUE)
        mLayoutVoice!!.addView(mImageButton, layoutParamImageButton)
        initialXImageButton = mImageButton!!.x
    }

    fun changeImageView() {
        val transition = LayoutTransition()
        transition.setDuration(600)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.CHANGING)
        }
        mLayoutTimer!!.layoutTransition = transition
        this.layoutTransition = transition
        mChronometer!!.base = SystemClock.elapsedRealtime()
        mChronometer!!.start()
        mImageView!!.scaleX = 0.8f
        mImageView!!.scaleY = 0.8f
        requestLayout()
    }

    fun changeSizeToRemove() {
        if (mImageButton!!.layoutParams.width != mImageView!!.width) {
            mImageButton!!.layoutParams.width = mImageView!!.width
            mImageButton!!.layoutParams.height = mImageView!!.height
            mImageButton!!.requestLayout()
            mImageButton!!.x = 0f
        }
    }

    fun unRevealSizeToRemove() {
        mImageButton!!.layoutParams.width =
            if (removeImageWidth > 0 && removeImageWidth < DEFAULT_REMOVE_ICON_SIZE) removeImageWidth else DEFAULT_REMOVE_ICON_SIZE
        mImageButton!!.layoutParams.height =
            if (removeImageHeight > 0 && removeImageHeight < DEFAULT_REMOVE_ICON_SIZE) removeImageHeight else DEFAULT_REMOVE_ICON_SIZE
        mImageButton!!.requestLayout()
    }

    fun unRevealImageView() {
        mChronometer!!.stop()
        mImageView!!.scaleX = 1f
        mImageView!!.scaleY = 1f
        requestLayout()
    }
}