package com.darklycoder.lib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout

/**
 * 阴影view 2018/10/10
 */
class ShadowView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    //<editor-fold desc="常量定义">

    companion object {
        const val ALL = 0x1111
        const val LEFT = 0x0001
        const val TOP = 0x0010
        const val RIGHT = 0x0100
        const val BOTTOM = 0x1000

        const val SHAPE_RECTANGLE = 0x0001
        const val SHAPE_OVAL = 0x0010
        const val SHAPE_ROUND = 0x0100
    }

    //</editor-fold>

    //<editor-fold desc="属性变量">

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRectF = RectF()
    // 阴影的颜色
    private var mShadowColor = Color.TRANSPARENT
    // 阴影的大小范围
    private var mShadowRadius = 0f
    // 阴影 x 轴的偏移量
    private var mShadowDx = 0f
    // 阴影 y 轴的偏移量
    private var mShadowDy = 0f
    // 阴影显示的边界
    private var mShadowSide = ALL
    // 圆角大小
    private var mRadius = 0f
    // 阴影的形状，圆形/矩形
    private var mShadowShape = SHAPE_RECTANGLE
    private var isOpen = true
    //</editor-fold>

    init {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null)  // 关闭硬件加速
        this.setWillNotDraw(false)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowView)
        try {
            if (typedArray != null) {
                mShadowColor = typedArray.getColor(R.styleable.ShadowView_shadowColor, mShadowColor)
                mShadowRadius = typedArray.getDimension(R.styleable.ShadowView_shadowRadius, mShadowRadius)
                mShadowDx = typedArray.getDimension(R.styleable.ShadowView_shadowDx, mShadowDx)
                mShadowDy = typedArray.getDimension(R.styleable.ShadowView_shadowDy, mShadowDy)
                mRadius = typedArray.getDimension(R.styleable.ShadowView_radius, mRadius)
                mShadowSide = typedArray.getInt(R.styleable.ShadowView_shadowSide, ALL)
                mShadowShape = typedArray.getInt(R.styleable.ShadowView_shadowShape, SHAPE_RECTANGLE)
            }

        } finally {
            typedArray?.recycle()
        }

        setUpShadowPaint()
    }

    //<editor-fold desc="内部方法">

    private fun setUpShadowPaint() {
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.color = Color.TRANSPARENT
        mPaint.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!isOpen || childCount != 1) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            //指定大小
            if (BuildConfig.DEBUG) {
                Log.e("ShadowView", "指定大小：widthSize:$widthSize|heightSize:$heightSize")
            }

            measureSpecExactly()
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        } else {
            //手动设置大小
            if (BuildConfig.DEBUG) {
                Log.e("ShadowView", "手动设置大小：widthSize:$widthSize|heightSize:$heightSize")
            }

            //测量子view
            val childView = getChildAt(0)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            val size = measureSpecOther(childView)

            this.setMeasuredDimension(size.first, size.second)
        }
    }

    private fun measureSpecExactly() {
        val effect = mShadowRadius
        var rectLeft = 0f
        var rectTop = 0f
        var rectRight = measuredWidth.toFloat()
        var rectBottom = measuredHeight.toFloat()

        var paddingLeft = 0
        var paddingTop = 0
        var paddingRight = 0
        var paddingBottom = 0

        when (mShadowSide) {
            LEFT   -> {
                rectLeft = effect
                paddingLeft = effect.toInt()
            }

            TOP    -> {
                rectTop = effect
                paddingTop = effect.toInt()
            }

            RIGHT  -> {
                rectRight -= effect
                paddingRight = effect.toInt()
            }

            BOTTOM -> {
                rectBottom -= effect
                paddingBottom = effect.toInt()
            }

            ALL    -> {
                rectLeft = effect
                paddingLeft = effect.toInt()

                rectTop = effect
                paddingTop = effect.toInt()

                rectRight -= effect
                paddingRight = effect.toInt()

                rectBottom -= effect
                paddingBottom = effect.toInt()
            }
        }

        if (mShadowDy != 0.0f) {
            rectBottom -= mShadowDy
            paddingBottom += mShadowDy.toInt()
        }

        if (mShadowDx != 0.0f) {
            rectRight -= mShadowDx
            paddingRight += mShadowDx.toInt()
        }

        mRectF.set(rectLeft, rectTop, rectRight, rectBottom)

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        if (BuildConfig.DEBUG) {
            Log.e("ShadowView", "measuredWidth:$measuredWidth|measuredHeight:$measuredHeight")
            Log.e("ShadowView", "rectLeft:$rectLeft|rectTop:$rectTop|rectRight:$rectRight|rectBottom:$rectBottom")
            Log.e("ShadowView", "paddingLeft:$paddingLeft|paddingTop:$paddingTop|paddingRight:$paddingRight|paddingBottom:$paddingBottom")
        }
    }

    private fun measureSpecOther(view: View): Pair<Int, Int> {
        var width = view.measuredWidth
        var height = view.measuredHeight

        val effect = mShadowRadius
        var rectLeft = 0f
        var rectTop = 0f
        var rectRight = view.measuredWidth.toFloat()
        var rectBottom = view.measuredHeight.toFloat()

        var paddingLeft = 0
        var paddingTop = 0
        var paddingRight = 0
        var paddingBottom = 0

        when (mShadowSide) {
            LEFT   -> {
                rectLeft = effect
                paddingLeft = effect.toInt()
            }

            TOP    -> {
                rectTop = effect
                paddingTop = effect.toInt()
            }

            RIGHT  -> {
                rectRight -= effect
                paddingRight = effect.toInt()
            }

            BOTTOM -> {
                rectBottom -= effect
                paddingBottom = effect.toInt()
            }

            ALL    -> {
                rectLeft = effect
                paddingLeft = effect.toInt()

                rectTop = effect
                paddingTop = effect.toInt()

                rectRight -= effect
                paddingRight = effect.toInt()

                rectBottom -= effect
                paddingBottom = effect.toInt()
            }
        }

        if (mShadowDy != 0.0f) {
            rectBottom -= mShadowDy
            paddingBottom += mShadowDy.toInt()
        }

        if (mShadowDx != 0.0f) {
            rectRight -= mShadowDx
            paddingRight += mShadowDx.toInt()
        }

        if (BuildConfig.DEBUG) {
            Log.e("ShadowView", "width:$width|height:$height")
        }

        //重新计算rect，补足右、下
        rectRight += (paddingLeft + paddingRight)
        rectBottom += (paddingTop + paddingBottom)

        mRectF.set(rectLeft, rectTop, rectRight, rectBottom)

        width += (paddingLeft + paddingRight)
        height += (paddingTop + paddingBottom)

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        if (BuildConfig.DEBUG) {
            Log.e("ShadowView", "measuredWidth:$width|measuredHeight:$height")
            Log.e("ShadowView", "rectLeft:$rectLeft|rectTop:$rectTop|rectRight:$rectRight|rectBottom:$rectBottom")
            Log.e("ShadowView", "paddingLeft:$paddingLeft|paddingTop:$paddingTop|paddingRight:$paddingRight|paddingBottom:$paddingBottom")
        }

        return Pair(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!isOpen) {
            return
        }

        when (mShadowShape) {
            SHAPE_RECTANGLE -> canvas?.drawRect(mRectF, mPaint)

            SHAPE_OVAL      -> canvas?.drawCircle(mRectF.centerX(), mRectF.centerY(), Math.max(mRectF.width(), mRectF.height()) / 2, mPaint)

            SHAPE_ROUND     -> canvas?.drawRoundRect(mRectF, mRadius, mRadius, mPaint)
        }

    }

    //</editor-fold>

    //<editor-fold desc="开放接口">

    fun setShadowColor(color: Int): ShadowView {
        this.mShadowColor = color

        return this
    }

    fun setShadowRadius(shadowRadius: Float): ShadowView {
        this.mShadowRadius = shadowRadius

        return this
    }

    fun setShadowDx(shadowDx: Float): ShadowView {
        this.mShadowDx = shadowDx

        return this
    }

    fun setShadowDy(shadowDy: Float): ShadowView {
        this.mShadowDy = shadowDy

        return this
    }

    fun setShadowSide(side: Int): ShadowView {
        this.mShadowSide = side

        return this
    }

    fun setRadius(radius: Float): ShadowView {
        this.mRadius = radius

        return this
    }

    fun setShadowShape(shadowShape: Int): ShadowView {
        this.mShadowShape = shadowShape

        return this
    }

    fun openShadow(open: Boolean) {
        this.isOpen = open

        setUpShadowPaint()

        requestLayout()
        postInvalidate()
    }

    //</editor-fold>

}