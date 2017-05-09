package com.yjl.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

/**
 * 新版推荐标样式
 *
 * @author yx
 */
public class NewHoloCircularProgressBar extends View {
    /**
     * TAG constant for logging
     */
    private static final String TAG = NewHoloCircularProgressBar.class
            .getSimpleName();
    /**
     * used to save the super state on configuration change
     */
    private static final String INSTANCE_STATE_SAVEDSTATE = "saved_state";
    /**
     * used to save the progress on configuration changes
     */
    private static final String INSTANCE_STATE_PROGRESS = "progress";
    /**
     * used to save the marker progress on configuration changes
     */
    private static final String INSTANCE_STATE_MARKER_PROGRESS = "marker_progress";
    /**
     * used to save the background color of the progress
     */
    private static final String INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR = "progress_background_color";
    /**
     * used to save the color of the progress
     */
    private static final String INSTANCE_STATE_PROGRESS_COLOR = "progress_color";
    /**
     * used to save and restore the visibility of the thumb in this instance
     */
    private static final String INSTANCE_STATE_THUMB_VISIBLE = "thumb_visible";
    /**
     * used to save and restore the visibility of the marker in this instance
     */
    private static final String INSTANCE_STATE_MARKER_VISIBLE = "marker_visible";
    private static final String INSTANCE_STATE_CIRCLE_LEFT_OR_RIGHT = "circle_left";
    /**
     * The rectangle enclosing the circle.
     */
    private final RectF mCircleBounds = new RectF();
    /**
     * the rect for the thumb square
     */
    private final RectF mSquareRect = new RectF();
    /**
     * the paint for the background.
     */
    private Paint mBackgroundColorPaint = new Paint();
    /**
     * The stroke width used to paint the circle.
     */
    private int mCircleStrokeWidth = DensityUtils.dip2px(getContext(), 3f);
    /**
     * The gravity of the view. Where should the Circle be drawn within the
     * given bounds
     * <p>
     * {@link #computeInsets(int, int)}
     */
    private int mGravity = Gravity.CENTER;
    /**
     * The Horizontal inset calcualted in {@link #computeInsets(int, int)}
     * depends on {@link #mGravity}.
     */
    private int mHorizontalInset = 0;
    /**
     * true if not all properties are set. then the view isn't drawn and there
     * are no errors in the LayoutEditor
     */
    private boolean mIsInitializing = true;
    /**
     * flag if the marker should be visible
     */
    private boolean mIsMarkerEnabled = false;
    /**
     * indicates if the thumb is visible
     */
    private boolean mIsThumbEnabled = true;
    /**
     * The Marker color paint.
     */
    private Paint mMarkerColorPaint;
    /**
     * The Marker progress.
     */
    private float mMarkerProgress = 0.0f;
    /**
     * the overdraw is true if the progress is over 1.0.
     */
    private boolean mOverrdraw = false;
    /**
     * The current progress.
     */
    private float mProgress = 0.3f;
    /**
     * The color of the progress background.
     */
    private int mProgressBackgroundColor;
    /**
     * the color of the progress.
     */
    private int mProgressColor;
    /**
     * paint for the progress.
     */
    private Paint mProgressColorPaint;
    /**
     * Radius of the circle
     * <p>
     * <p>
     * Note: (Re)calculated in {@link #onMeasure(int, int)}.
     * </p>
     */
    private float mRadius;
    /**
     * The Thumb color paint.
     */
    private Paint mThumbColorPaint = new Paint();
    /**
     * The Thumb pos x.
     * <p>
     * Care. the position is not the position of the rotated thumb. The position
     * is only calculated in {@link #onMeasure(int, int)}
     */
    private float mThumbPosX;
    /**
     * The Thumb pos y.
     * <p>
     * Care. the position is not the position of the rotated thumb. The position
     * is only calculated in {@link #onMeasure(int, int)}
     */
    private float mThumbPosY;
    /**
     * The pointer width (in pixels).
     */
    private int mThumbRadius = DensityUtils.dip2px(getContext(), 7f);
    /**
     * The Translation offset x which gives us the ability to use our own
     * coordinates system.
     */
    private float mTranslationOffsetX;
    /**
     * The Translation offset y which gives us the ability to use our own
     * coordinates system.
     */
    private float mTranslationOffsetY;
    /**
     * The Vertical inset calcualted in {@link #computeInsets(int, int)} depends
     * on {@link #mGravity}..
     */
    private int mVerticalInset = 0;
    // TODO　
    // 中间的圆环
    private Paint mMidCirclePaint;
    private final RectF mMidCircleBounds = new RectF();
    private float mMidRadius;
    private float mUpThumbPosX;// 上面圆的x坐标
    private float mUpThumbPosY;// 上面圆的y坐标
    private float mDownThumbPosX;// 下面圆的x的坐标
    private float mDownThumbPosY;// 下面圆的y坐标
    // 外层圆环
    private Paint mProCirclePaint;
    private final RectF mProCircleBounds = new RectF();
    private float mProRadius;
    // 圆环上的圆及渐隐效果
    private Paint mCirclePaint = new Paint(); // 绘制圆的画笔
    private float circleRadius = mThumbRadius / 2; // 中间圆环上圆半径
    private Paint mSolidPaint;// 绘制渐隐效果的画笔
    private float floatRadius; // 变化的半径
    private float maxRadius = DensityUtils.dip2px(getContext(), 30); // 外层渐隐圆最大半径
    private boolean isUpleftOrRight = true; // true在左上右下对齐，false右上左下对齐
    // 中间背景色
    private Paint midBackgroundPiant = new Paint();
    private int midBackgroundColor;
    private float progress;// 进度条
    // 绘制进度条的加速度
    private int mAcceleration = 0;
    private int delatRediu = DensityUtils.dip2px(getContext(), 1);

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context the context
     */
    public NewHoloCircularProgressBar(final Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public NewHoloCircularProgressBar(final Context context,
                                      final AttributeSet attrs) {
        this(context, attrs, R.attr.circularProgressBarStyle);
    }

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public NewHoloCircularProgressBar(final Context context,
                                      final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        // load the styled attributes and set their properties
        final TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.HoloCircularProgressBar, defStyle, 0);
        if (attributes != null) {
            try {
                setProgressColor(0xff0779FF);
                // setProgressColor(attributes
                // .getColor(R.styleable.HoloCircularProgressBar_progress_color,
                // Color.BLUE));
                setProgressBackgroundColor(attributes
                        .getColor(
                                R.styleable.HoloCircularProgressBar_progress_background_color,
                                Color.LTGRAY));
                setProgress(attributes.getFloat(
                        R.styleable.HoloCircularProgressBar_progress, 0.0f));
                setMarkerProgress(attributes.getFloat(
                        R.styleable.HoloCircularProgressBar_marker_progress,
                        0.0f));
                setWheelSize((int) attributes.getDimension(
                        R.styleable.HoloCircularProgressBar_stroke_width, 5));
                setThumbEnabled(attributes
                        .getBoolean(
                                R.styleable.HoloCircularProgressBar_thumb_visible,
                                true));
                mGravity = attributes.getInt(
                        R.styleable.HoloCircularProgressBar_android_gravity,
                        Gravity.CENTER);
            } finally {
                // make sure recycle is always called.
                attributes.recycle();
            }
        }
        mThumbRadius = (int) (mCircleStrokeWidth * 1.5);
        updateBackgroundColor();
        updateMarkerColor();
        // 进度条及外层圆环和圆环上的圆和渐隐效果颜色
        updateProgressColor();
        // 中间的背景色
        updateMidBackgroundColor();
        // the view has now all properties and can be drawn
        mIsInitializing = false;
        // start();
        // updateMidCircleColor();
        // updateProCircleColor();
        // init();
    }

    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        // LogUtil.e("onWindowVisibilityChanged --- visibility = " +
        // visibility);
        if (visibility == View.VISIBLE) {
            floatRadius = circleRadius;
            start();
        } else {
            stop();
        }
    }

    private void stop() {
        // if (mThread != null) {
        // mThread.started = false;
        // mThread.interrupt();
        // mThread = null;
        // }

        removeCallbacks(mRunnable);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    DrawThread mThread = null;

    private void start() {
        stop();
        // mThread = new DrawThread();
        // mThread.started = true;
        // mThread.start();

        postDelayed(mRunnable, 60);
    }

    Handler mHandler = new Handler();

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            floatRadius = 4F + floatRadius;
            if (floatRadius > maxRadius) {
                floatRadius = circleRadius;
            }
            if (progress < getCurrentRotation() / 2) {
                // progress = (mAcceleration ++) + progress;
                progress = (mAcceleration = (mAcceleration + delatRediu))
                        + progress;
            } else if (progress >= getCurrentRotation() / 2
                    && progress <= getCurrentRotation()) {
                if ((mAcceleration = (mAcceleration - delatRediu)) < 1) {
                    mAcceleration = delatRediu;
                }
                progress = mAcceleration + progress;
                if (progress > getCurrentRotation()) {
                    progress = getCurrentRotation();
                }
            }

            postInvalidate();

            start();
        }
    };

    public class DrawThread extends Thread {
        public volatile boolean started = false;

        public void run() {
            while (started) {
                try {
                    mHandler.post(mRunnable);
                    Thread.sleep(60L);
                } catch (Exception e) {
                    e.printStackTrace();
                    started = false;
                    Log.e("yjl", "run  e = " + e);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.translate(mTranslationOffsetX, mTranslationOffsetY);
        // final float progressRotation = getCurrentRotation();
        if (!mOverrdraw) {
            canvas.drawArc(mCircleBounds, 270, -(360 - progress), false,
                    mBackgroundColorPaint);
        }
        // 绘制进度 条
        // canvas.drawArc(mCircleBounds, 270, mOverrdraw ? 360 :
        // progressRotation,
        // false, mProgressColorPaint);
        canvas.drawArc(mCircleBounds, 270, mOverrdraw ? 360 : progress, false,
                mProgressColorPaint);
        canvas.drawArc(mCircleBounds, 0, 360, false, midBackgroundPiant);
        if (isThumbEnabled() && progress >= getCurrentRotation()) {
            canvas.save();
            canvas.rotate(progress - 90);
            canvas.rotate(45, mThumbPosX, mThumbPosY);
            float r = 0;
            while (true) {
                int alpha = (int) (100F * (r / mThumbRadius));
                if (alpha >= 120 || r >= mThumbRadius) {
                    break;
                }
                mThumbColorPaint.setAlpha(alpha);
                canvas.drawCircle(mThumbPosX, mThumbPosY, r, mThumbColorPaint);
                r = 0.1f + r;
            }
            canvas.restore();
            // 画菱形
            // mSquareRect.left = mThumbPosX - mThumbRadius / 3;
            // mSquareRect.right = mThumbPosX + mThumbRadius / 3;
            // mSquareRect.top = mThumbPosY - mThumbRadius / 3;
            // mSquareRect.bottom = mThumbPosY + mThumbRadius / 3;
            // canvas.drawRect(mSquareRect, mThumbColorPaint);
        }
        canvas.drawArc(mMidCircleBounds, 0, 360, false, mMidCirclePaint);
        canvas.drawArc(mProCircleBounds, 0, 360, false, mProCirclePaint);
        drawCircle(canvas);
    }

    /**
     * 绘制中间圆环上圆的和渐隐效果
     *
     * @param canvas
     */
    private void drawCircle(final Canvas canvas) {
        if (isCircleLeftOrRight()) {
            upLeftCircle();
        } else {
            // 右上左下
            upRightCircle();
        }
        // 中间圆环上的下方的圆
        canvas.drawCircle(mUpThumbPosX, mUpThumbPosY, circleRadius,
                mCirclePaint);
        canvas.drawCircle(mDownThumbPosX + circleRadius / 2, mDownThumbPosY,
                circleRadius, mCirclePaint);
        if (maxRadius <= 0.0F) {
            return;
        }
        float radius = floatRadius;
        mSolidPaint.setStrokeWidth(floatRadius);
        while (true) {
            int alpha = (int) (120.0F * (1.0F - radius / maxRadius));
            if (alpha <= 0) {
                break;
            }
            mSolidPaint.setAlpha(alpha >> 2);
            canvas.drawCircle(mUpThumbPosX, mUpThumbPosY, circleRadius,
                    mSolidPaint);
            canvas.drawCircle(mDownThumbPosX + circleRadius / 2,
                    mDownThumbPosY, circleRadius, mSolidPaint);
            radius = radius + DensityUtils.dip2px(getContext(), 1f);
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec,
                             final int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight()
                + getPaddingTop() + getPaddingBottom(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth()
                + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);
        final int diameter;
        if (heightMeasureSpec == MeasureSpec.UNSPECIFIED) {
            // ScrollView
            diameter = width;
            computeInsets(0, 0);
        } else if (widthMeasureSpec == MeasureSpec.UNSPECIFIED) {
            // HorizontalScrollView
            diameter = height;
            computeInsets(0, 0);
        } else {
            // Default
            diameter = Math.min(width, height);
            computeInsets(width - diameter, height - diameter);
        }
        setMeasuredDimension(diameter, diameter);
        final float halfWidth = diameter * 0.5f;
        // width of the drawed circle (+ the drawedThumb)
        final float drawedWith;
        if (isThumbEnabled()) {
            drawedWith = mThumbRadius * (5f / 6f);
        } else if (isMarkerEnabled()) {
            drawedWith = mCircleStrokeWidth * 1.4f;
        } else {
            drawedWith = mCircleStrokeWidth / 2f;
        }
        mRadius = halfWidth - drawedWith
                - DensityUtils.dip2px(getContext(), 20f);
        mCircleBounds.set(-mRadius, -mRadius, mRadius, mRadius);
        mThumbPosX = (float) (mRadius * Math.cos(0));
        mThumbPosY = (float) (mRadius * Math.sin(0));
        mTranslationOffsetX = halfWidth + mHorizontalInset;
        mTranslationOffsetY = halfWidth + mVerticalInset;
        // 中间圆环
        mMidRadius = halfWidth - drawedWith
                - DensityUtils.dip2px(getContext(), 12);
        mMidCircleBounds.set(-mMidRadius, -mMidRadius, mMidRadius, mMidRadius);
        // 最外层圆环
        mProRadius = halfWidth - drawedWith
                - DensityUtils.dip2px(getContext(), 5);
        mProCircleBounds.set(-mProRadius, -mProRadius, mProRadius, mProRadius);
        // 左上右下对齐的
        if (isCircleLeftOrRight()) {
            upLeftCircle();
        } else {
            // 右上左下
            upRightCircle();
        }
    }

    // 右上角
    // mUpThumbPosX = (float) (mMidRadius * Math.cos(150));
    // mUpThumbPosY = (float) (mMidRadius * Math.sin(150));
    // 左上角
    // mUpThumbPosX = (float) (mMidRadius * Math.sin(150));
    // mUpThumbPosY = (float) (mMidRadius * Math.sin(150));
    // 右边正中间
    // mUpThumbPosX = (float) (mMidRadius * Math.cos(270));
    // mUpThumbPosY = (float) (mMidRadius * Math.sin(270));
    // 右下角
    // mDownThumbPosX = (float) (mMidRadius * Math.cos(150));
    // mDownThumbPosY = (float) (mMidRadius * Math.cos(150));
    // 左下角
    // mDownThumbPosX = (float) (mMidRadius * Math.sin(150));
    // mDownThumbPosY = (float) (mMidRadius * Math.sin(-150));
    private void upLeftCircle() {
        mUpThumbPosX = (float) (mMidRadius * Math.sin(150));
        mUpThumbPosY = (float) (mMidRadius * Math.sin(150));
        mDownThumbPosX = (float) (mMidRadius * Math.cos(150));
        mDownThumbPosY = (float) (mMidRadius * Math.cos(150));
    }

    private void upRightCircle() {
        mUpThumbPosX = (float) (mMidRadius * Math.cos(150));
        mUpThumbPosY = (float) (mMidRadius * Math.sin(150));
        mDownThumbPosX = (float) (mMidRadius * Math.sin(150));
        mDownThumbPosY = (float) (mMidRadius * Math.sin(-150));
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            setProgress(bundle.getFloat(INSTANCE_STATE_PROGRESS));
            setMarkerProgress(bundle.getFloat(INSTANCE_STATE_MARKER_PROGRESS));
            final int progressColor = bundle
                    .getInt(INSTANCE_STATE_PROGRESS_COLOR);
            if (progressColor != mProgressColor) {
                mProgressColor = progressColor;
                updateProgressColor();
            }
            final int progressBackgroundColor = bundle
                    .getInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR);
            if (progressBackgroundColor != mProgressBackgroundColor) {
                mProgressBackgroundColor = progressBackgroundColor;
                updateBackgroundColor();
            }
            mIsThumbEnabled = bundle.getBoolean(INSTANCE_STATE_THUMB_VISIBLE);
            mIsMarkerEnabled = bundle.getBoolean(INSTANCE_STATE_MARKER_VISIBLE);
            isUpleftOrRight = bundle
                    .getBoolean(INSTANCE_STATE_CIRCLE_LEFT_OR_RIGHT);
            super.onRestoreInstanceState(bundle
                    .getParcelable(INSTANCE_STATE_SAVEDSTATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE_SAVEDSTATE,
                super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STATE_PROGRESS, mProgress);
        bundle.putFloat(INSTANCE_STATE_MARKER_PROGRESS, mMarkerProgress);
        bundle.putInt(INSTANCE_STATE_PROGRESS_COLOR, mProgressColor);
        bundle.putInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR,
                mProgressBackgroundColor);
        bundle.putBoolean(INSTANCE_STATE_THUMB_VISIBLE, mIsThumbEnabled);
        bundle.putBoolean(INSTANCE_STATE_MARKER_VISIBLE, mIsMarkerEnabled);
        bundle.putBoolean(INSTANCE_STATE_CIRCLE_LEFT_OR_RIGHT, isUpleftOrRight);
        return bundle;
    }

    public int getCircleStrokeWidth() {
        return mCircleStrokeWidth;
    }

    /**
     * similar to {@link #getProgress}
     */
    public float getMarkerProgress() {
        return mMarkerProgress;
    }

    /**
     * gives the current progress of the ProgressBar. Value between 0..1 if you
     * set the progress to >1 you'll get progress % 1 as return value
     *
     * @return the progress
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * Gets the progress color.
     *
     * @return the progress color
     */
    public int getProgressColor() {
        return mProgressColor;
    }

    /**
     * @return true if the marker is visible
     */
    public boolean isMarkerEnabled() {
        return mIsMarkerEnabled;
    }

    /**
     * @return true if the marker is visible
     */
    public boolean isThumbEnabled() {
        return mIsThumbEnabled;
    }

    /**
     * Sets the marker enabled.
     *
     * @param enabled the new marker enabled
     */
    public void setMarkerEnabled(final boolean enabled) {
        mIsMarkerEnabled = enabled;
    }

    /**
     * Sets the marker progress.
     *
     * @param progress the new marker progress
     */
    public void setMarkerProgress(final float progress) {
        mIsMarkerEnabled = true;
        mMarkerProgress = progress;
    }

    /**
     * Sets the progress.
     *
     * @param progress the new progress
     */
    public void setProgress(final float progress) {
        // if (progress == mProgress) {
        // return;
        // }
        if (progress == 1) {
            mOverrdraw = false;
            mProgress = 1;
        } else {
            if (progress >= 1) {
                mOverrdraw = true;
            } else {
                mOverrdraw = false;
            }
            mProgress = progress % 1.0f;
        }
        if (!mIsInitializing) {
            invalidate();
        }
    }

    /**
     * Sets the progress background color.
     *
     * @param color the new progress background color
     */
    public void setProgressBackgroundColor(final int color) {
        mProgressBackgroundColor = color;
        updateMarkerColor();
        updateBackgroundColor();
    }

    /**
     * Sets the progress color.
     *
     * @param color the new progress color
     */
    public void setProgressColor(final int color) {
        mProgressColor = color;
        updateProgressColor();
    }

    /**
     * shows or hides the thumb of the progress bar
     *
     * @param enabled true to show the thumb
     */
    public void setThumbEnabled(final boolean enabled) {
        mIsThumbEnabled = enabled;
    }

    /**
     * Sets the wheel size.
     *
     * @param dimension the new wheel size
     */
    public void setWheelSize(final int dimension) {
        mCircleStrokeWidth = dimension;
        // update the paints
        updateBackgroundColor();
        updateMarkerColor();
        updateProgressColor();
    }

    /**
     * Compute insets.
     * <p>
     * <pre>
     *  ______________________
     * |_________dx/2_________|
     * |......| /'''''\|......|
     * |-dx/2-|| View ||-dx/2-|
     * |______| \_____/|______|
     * |________ dx/2_________|
     * </pre>
     *
     * @param dx the dx the horizontal unfilled space
     * @param dy the dy the horizontal unfilled space
     */
    @SuppressLint("NewApi")
    private void computeInsets(final int dx, final int dy) {
        int absoluteGravity = mGravity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            absoluteGravity = Gravity.getAbsoluteGravity(mGravity,
                    getLayoutDirection());
        }
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                mHorizontalInset = 0;
                break;
            case Gravity.RIGHT:
                mHorizontalInset = dx;
                break;
            case Gravity.CENTER_HORIZONTAL:
            default:
                mHorizontalInset = dx / 2;
                break;
        }
        switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                mVerticalInset = 0;
                break;
            case Gravity.BOTTOM:
                mVerticalInset = dy;
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                mVerticalInset = dy / 2;
                break;
        }
    }

    /**
     * Gets the current rotation.
     *
     * @return the current rotation
     */
    private float getCurrentRotation() {
        return 360 * mProgress;
    }

    /**
     * Gets the marker rotation.
     *
     * @return the marker rotation
     */
    private float getMarkerRotation() {
        return 360 * mMarkerProgress;
    }

    /**
     * updates the paint of the background
     */
    private void updateBackgroundColor() {
        mBackgroundColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundColorPaint.setColor(mProgressBackgroundColor);
        mBackgroundColorPaint.setStyle(Style.STROKE);
        mBackgroundColorPaint.setStrokeWidth(mCircleStrokeWidth);
        invalidate();
    }

    /**
     * updates the paint of the marker
     */
    private void updateMarkerColor() {
        mMarkerColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkerColorPaint.setColor(mProgressBackgroundColor);
        mMarkerColorPaint.setStyle(Style.STROKE);
        mMarkerColorPaint.setStrokeWidth(mCircleStrokeWidth / 2);
        invalidate();
    }

    /**
     * updates the paint of the progress and the thumb to give them a new visual
     * style
     */
    private void updateProgressColor() {
        mProgressColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressColorPaint.setColor(mProgressColor);
        mProgressColorPaint.setStyle(Style.STROKE);
        mProgressColorPaint.setStrokeWidth(mCircleStrokeWidth);
        mThumbColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbColorPaint.setColor(mProgressColor);
        mThumbColorPaint.setStyle(Style.FILL);
        // 同时更新外层圆环及圆环上圆的颜色
        updateMidCircleColor();
        updateProCircleColor();
        updateUpCircleColor();
        invalidate();
    }

    /**
     * 更新中间圆环的颜色
     */
    private void updateMidCircleColor() {
        mMidCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMidCirclePaint.setColor(mProgressColor);
        mMidCirclePaint.setAlpha(90);
        mMidCirclePaint.setStyle(Style.STROKE);
        mMidCirclePaint.setStrokeWidth(mCircleStrokeWidth / 3);
        invalidate();
    }

    /**
     * 更新外层圆环的颜色
     */
    private void updateProCircleColor() {
        mProCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProCirclePaint.setColor(mProgressColor);
        mProCirclePaint.setAlpha(40);
        mProCirclePaint.setStyle(Style.STROKE);
        mProCirclePaint.setStrokeWidth(mCircleStrokeWidth / 3);
        invalidate();
    }

    private void updateUpCircleColor() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Style.FILL);
        mCirclePaint.setColor(mProgressColor);
        mCirclePaint.setAntiAlias(true);
        mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidPaint.setStyle(Style.FILL_AND_STROKE);
        mSolidPaint.setColor(mProgressColor);
        mSolidPaint.setAntiAlias(true);
        invalidate();
    }

    public void setMidBackground(final int color) {
        midBackgroundColor = color;
        updateMidBackgroundColor();
        // Color.parseColor("#edf5ff")
    }

    private void updateMidBackgroundColor() {
        midBackgroundPiant = new Paint(Paint.ANTI_ALIAS_FLAG);
        midBackgroundPiant.setColor(mProgressBackgroundColor);
        midBackgroundPiant.setStyle(Style.FILL);
        midBackgroundPiant.setColor(midBackgroundColor);
        invalidate();
    }

    public void setCircleLeftOrRight(boolean isUpLeft) {
        isUpleftOrRight = isUpLeft;
    }

    public boolean isCircleLeftOrRight() {
        return isUpleftOrRight;
    }
}
