package com.udacity.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize: Int = 0
    private var heightSize: Int = 0
    private val buttonTextSize = resources.getDimension(R.dimen.default_text_size)
    private val circleXOffset = buttonTextSize / 2

    private var duration = 2000
    private var progressWidth = 0f
    private var progressCircle = 0f

    private var textButton = ""

    private var valueAnimator = ValueAnimator()

    /**
     * The button behaviour and the animation was inspired from here:
     * https://knowledge.udacity.com/questions/911544?fbclid=IwAR1rbb0VESf-tEti0yLTXIPqklxPhw_LZb6wjzafseRsecVO8rK78R1sBes
     */
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                invalidate()
            }
            ButtonState.Loading -> {
                textButton = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                valueAnimator.duration = duration.toLong()
                valueAnimator.addUpdateListener { animation ->
                    progressWidth = widthSize * animation.animatedValue as Float
                    progressCircle = 360 * animation.animatedValue as Float
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        progressWidth = 0f
                        if (buttonState == ButtonState.Loading) {
                            buttonState = ButtonState.Loading
                        }
                    }
                })
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                progressWidth = 0f
                progressCircle = 0f
                valueAnimator.cancel()
                textButton = resources.getString(R.string.button_name)
                invalidate()
            }
        }

    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            widthSize = getDimension(R.styleable.LoadingButton_widthSize, 0f).toInt()
            heightSize = getDimension(R.styleable.LoadingButton_widthSize, 0f).toInt()
            textButton = getString(R.styleable.LoadingButton_textButton).toString()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = buttonTextSize
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val loadingPaint =  Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    @SuppressLint("ResourceAsColor")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawText(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawBackground(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        if (buttonState == ButtonState.Loading) {
            loadingPaint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas.drawRect(0f, 0f,progressWidth, height.toFloat(), loadingPaint)
        }
    }

    private fun drawText(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.white)
        textButton.length
        val textWidth = paint.measureText(textButton)

        canvas.drawText(
            textButton,
            (widthSize / 2).toFloat() ,
            heightSize / 2 - (paint.descent() + paint.ascent()) / 2,
            paint)

        canvas.save()
        canvas.translate(
            widthSize / 2 + textWidth / 2 + circleXOffset,
            heightSize / 2 - buttonTextSize / 2
        )
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        canvas.drawArc(RectF(0f, 0f, buttonTextSize, buttonTextSize), 0F, progressCircle, true, paint)
        canvas.restore()
    }

    fun changeState(state: ButtonState) {
        buttonState = state
    }
}