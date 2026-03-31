package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var isPlaying: Boolean = false
    private val drawRect = RectF()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView).use { typedArray ->
            val playResId = typedArray.getResourceId(R.styleable.PlaybackButtonView_playIcon, 0)
            val pauseResId = typedArray.getResourceId(R.styleable.PlaybackButtonView_pauseIcon, 0)
            if (playResId != 0) playBitmap = drawableToBitmap(playResId)
            if (pauseResId != 0) pauseBitmap = drawableToBitmap(pauseResId)
        }
    }

    private fun drawableToBitmap(resId: Int): Bitmap? {
        val drawable = AppCompatResources.getDrawable(context, resId) ?: return null
        val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: 100
        val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: 100
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap?.let { canvas.drawBitmap(it, null, drawRect, null) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                isPlaying = !isPlaying
                invalidate()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setIsPlaying(isPlaying: Boolean) {
        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying
            invalidate()
        }
    }
}
