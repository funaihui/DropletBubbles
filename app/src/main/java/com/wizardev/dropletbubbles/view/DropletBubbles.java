package com.wizardev.dropletbubbles.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wizardev.dropletbubbles.R;


/**
 * @describe 自定义气泡
 * @anthor 符乃辉 QQ:1151631351
 * @time 2018/4/13 上午9:36
 */
public class DropletBubbles extends View {
    private Paint mBackgroundPaint;//背景画笔
    private Paint mBubblesPaint;//气泡画笔
    private Path mBackgroundPath;
    private Path mBubblesPath;
    private int mInnerRadius;
    private int mOutRadius;
    private int mResultWidth;
    private int mResultHeight;
    private static final String TAG = "DropletBubbles";

    private float baseLine = 0;// 基线，用于控制水位上涨的
    private int waveHeight;// 波浪的最高度
    private int waveWidth;//波长
    private float offset = 0f;//偏移量
    private int width = 0;
    private int height = 0;
    private int mBackgroundColor;
    private int mWaveColor;

    public DropletBubbles(Context context) {
        this(context, null);
    }

    public DropletBubbles(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropletBubbles(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DropletBubbles, 0, defStyleAttr);
        mInnerRadius = typedArray.getDimensionPixelSize(R.styleable.DropletBubbles_innerRadius, 10);
        mOutRadius = typedArray.getDimensionPixelSize(R.styleable.DropletBubbles_outRadius, 12);
        mBackgroundColor = typedArray.getColor(R.styleable.DropletBubbles_backgroundColor, getResources().getColor(R.color.colorPrimary));
        mWaveColor = typedArray.getColor(R.styleable.DropletBubbles_waveColor, getResources().getColor(R.color.colorAccent));
        typedArray.recycle();

        init();//初始化画笔

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mResultWidth = widthSize;
        mResultHeight = heightSize;
        if (widthMode == MeasureSpec.AT_MOST) {
            int contentWidth = mOutRadius * 2+ getPaddingLeft() + getPaddingRight();
            mResultWidth = (contentWidth < widthSize) ? contentWidth : mResultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = mOutRadius * 2 + getPaddingTop() + getPaddingBottom() + mOutRadius / 4;
            mResultHeight = (contentHeight < heightSize) ? contentHeight : mResultHeight;
        }

        setMeasuredDimension(mResultWidth, mResultHeight);
    }

    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setShadowLayer(10, 0, 0, Color.GRAY);


        mBubblesPaint = new Paint();
        mBubblesPaint.setAntiAlias(true);
        mBubblesPaint.setColor(mWaveColor);
        mBubblesPaint.setStyle(Paint.Style.FILL);


        mBackgroundPath = new Path();
        mBubblesPath = new Path();
        waveHeight = dp2px(getContext(), 4);

    }



    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBubblesPath.reset();

        //设置渐变色
        Shader shader = new LinearGradient(mResultWidth / 2, mResultWidth / 2 - mInnerRadius, mResultWidth / 2, mResultWidth / 2 + mInnerRadius, Color.parseColor("#9592FB"),
                Color.parseColor("#3831D4"), Shader.TileMode.CLAMP);
        mBubblesPaint.setShader(shader);

        //此处代码是下部尖角的path
        mBackgroundPath.moveTo(mResultWidth / 2 - mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2);
        mBackgroundPath.lineTo(mResultWidth / 2, mResultWidth / 2 + mOutRadius + mOutRadius / 4);
        mBackgroundPath.lineTo(mResultWidth / 2 + mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2);


        //内部气泡的尖角
        mBubblesPath.moveTo(mResultWidth / 2 - mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2 - dp2px(getContext(), 5));
        mBubblesPath.lineTo(mResultWidth / 2, mResultWidth / 2 + mOutRadius + mOutRadius / 4 - dp2px(getContext(), 5));
        mBubblesPath.lineTo(mResultWidth / 2 + mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2 - dp2px(getContext(), 5));
        //画外部背景
        canvas.drawPath(mBackgroundPath, mBackgroundPaint);
        canvas.drawCircle(mResultWidth / 2, mResultWidth / 2, mOutRadius, mBackgroundPaint);
        Log.d(TAG, "cx: " + mResultWidth / 2);
        //切割画布，画水波
        canvas.save();
        mBubblesPath.addCircle(mResultWidth / 2, mResultWidth / 2, mInnerRadius, Path.Direction.CCW);
        //将画布裁剪成内部气泡的样子
        canvas.clipPath(mBubblesPath);

        canvas.drawPath(getPath(), mBubblesPaint);
        canvas.restore();

    }


    /**
     * 不断的更新偏移量，并且循环。
     */
    public void updateXControl() {
        //设置一个波长的偏移
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, waveWidth);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                offset = animatorValue;//不断的设置偏移量，并重画
                postInvalidate();
            }
        });
        mAnimator.setDuration(1800);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = mResultWidth;
        height = mResultHeight;//获取视图高度
        waveWidth = width;
//        setBaseLine(height/2);
        if (mCallback != null) {
            mCallback.measureHeightResult(height);
        }
        updateXControl();
    }

    public void setBaseLine(float value) {

        baseLine = height - value * height;
        invalidate();

    }

    private OnMeasureBaseLineCallback mCallback;


    public interface OnMeasureBaseLineCallback {

        void measureHeightResult(int height);
    }

    public void setOnMeasureBaseLineCallback(OnMeasureBaseLineCallback measureBaseLineCallback) {
        mCallback = measureBaseLineCallback;
    }


    /**
     * 核心代码，计算path
     *
     * @return
     */
    private Path getPath() {
        int itemWidth = waveWidth / 2;//半个波长
        Path mPath = new Path();
        mPath.moveTo(-itemWidth * 3, baseLine);//起始坐标
        Log.d(TAG, "getPath: " + baseLine);

        //核心的代码就是这里
        for (int i = -3; i < 2; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(
                    startX + itemWidth / 2 + offset,//控制点的X,（起始点X + itemWidth/2 + offset)
                    getWaveHeight(i),//控制点的Y
                    startX + itemWidth + offset,//结束点的X
                    baseLine//结束点的Y
            );//只需要处理完半个波长，剩下的有for循环自已就添加了。
        }
        Log.d(TAG, "getPath: ");
        //下面这三句话很重要，它是形成了一封闭区间，让曲线以下的面积填充一种颜色，大家可以把这3句话注释了看看效果。
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        mPath.close();
        return mPath;
    }

    //奇数峰值是正的，偶数峰值是负数
    private float getWaveHeight(int num) {
        if (num % 2 == 0) {
            return baseLine + waveHeight;
        }
        return baseLine - waveHeight;
    }

}
