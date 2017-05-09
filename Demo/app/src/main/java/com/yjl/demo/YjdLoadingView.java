package com.yjl.demo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

/**
 * 三个弹球的动画
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class YjdLoadingView extends SurfaceView {

    private Paint leftPain;
    private RectF mleftRectF;

    private Paint midPain;
    private RectF midRectF;

    private Paint rightPain;
    private RectF mrightRectF;

    private int color = Color.parseColor("#197dff");
    private int background = Color.parseColor("#0f000000");
    private float mX = Float.valueOf(getWidth() / 2);
    private float mY = Float.valueOf(getHeight() / 2);

    private float mleftY = Float.valueOf(getHeight() / 2);
    private float midY = Float.valueOf(getHeight() / 2);
    private float rightY = Float.valueOf(getHeight() / 2);

    private int limitY = DensityUtils.dip2px(getContext(), 10f); // 球的弹跳高度
    private int mRadius = DensityUtils.dip2px(getContext(), 8f);
    ;// 球的半径

    private final int DIREC_LEFT = 1;
    private final int DIREC_MID = 2;
    private final int DIREC_RIGHT = 3;

    private int[] leftNum = {0, 0, 0, 0};// 左边球
    private int[] midNum = {0, 0, 0, 0}; // 中间球
    private int[] rightNum = {0, 0, 0, 0}; // 右边球

    private int numL = 0;
    private int numM = 0;
    private int numR = 0;
    private Paint bgPaint;// 背景
    private int offsetX; // x的偏移量
    private int offsetY; // y的偏移量
    private Handler mHandler = new Handler(getContext().getMainLooper());

    // public volatile boolean started = true;

    public YjdLoadingView(Context context) {
        super(context);
        init(context);
    }

    // 构造器
    public YjdLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("NewApi")
    public YjdLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public YjdLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // RefWatcher watcher = YjdApplication.getRefWatcher(getContext());
        // watcher.watch(this, getClass().getSimpleName());
        leftPain = new Paint();
        leftPain.setAntiAlias(true);
        leftPain.setStyle(Style.FILL);
        mleftRectF = new RectF();

        midPain = new Paint();
        midPain.setAntiAlias(true);
        midPain.setStyle(Style.FILL);
        midRectF = new RectF();

        rightPain = new Paint();

        rightPain.setAntiAlias(true);
        rightPain.setStyle(Style.FILL);
        mrightRectF = new RectF();
        bgPaint = new Paint();
        bgPaint.setColor(this.background);
        /**
         * 偏移量由于椭圆公式比较麻烦，简化为每次改变只改变1单位值
         */
        offsetX = DensityUtils.dip2px(context, 1f);// x的偏移量
        offsetY = DensityUtils.dip2px(context, 1f);// y的偏移量
    }

    /**
     * 设置补间画笔颜色.
     *
     * @param color 颜色
     */

    public void setPaintColor(int color) {
        this.color = color;
        leftPain.setColor(this.color);
        midPain.setColor(this.color);
        rightPain.setColor(this.color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.save();
        canvas.drawPaint(bgPaint);
        // 左边的圆
        leftPain.setColor(this.color);
        mleftRectF.set(mX - 4 * mRadius + leftNum[0] + DensityUtils.dip2px(getContext(), 2f),
                mleftY - mRadius + leftNum[1] - DensityUtils.dip2px(getContext(), 4f), mX - 2
                        * mRadius - leftNum[2] - DensityUtils.dip2px(getContext(), 2f), mleftY
                        + mRadius + leftNum[3]);
        // canvas.clipRect(mleftRectF);
        canvas.drawArc(mleftRectF, 0, 360, true, leftPain);
        // 中间的圆
        midPain.setColor(this.color);
        midRectF.set(mX - mRadius + midNum[0] + DensityUtils.dip2px(getContext(), 2f), midY
                - mRadius + midNum[1] - DensityUtils.dip2px(getContext(), 4f), mX + mRadius
                - midNum[2] - DensityUtils.dip2px(getContext(), 2f), midY + mRadius + midNum[3]);
        // canvas.clipRect(midRectF);
        canvas.drawArc(midRectF, 0, 360, true, midPain);

        // 右边的圆
        rightPain.setColor(this.color);
        mrightRectF.set(mX + 2 * mRadius + rightNum[0] + DensityUtils.dip2px(getContext(), 2f),
                rightY - mRadius + rightNum[1] - DensityUtils.dip2px(getContext(), 4f), mX + 4
                        * mRadius - rightNum[2] - DensityUtils.dip2px(getContext(), 2f), rightY
                        + mRadius + rightNum[3]);

        canvas.drawArc(mrightRectF, 0, 360, true, rightPain);
        canvas.clipRect(mleftRectF);
        canvas.clipRect(midRectF);
        canvas.clipRect(mrightRectF);
        canvas.restore();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(DensityUtils.dip2px(getContext(), 90),
                DensityUtils.dip2px(getContext(), 60));
        // setBackgroundColor(this.background);
        setBackgroundResource(R.drawable.bg_loading_view_corner);
        mX = Float.valueOf(getMeasuredWidth() / 2);
        // mX = Float.valueOf(getWidth() / 2);
        mY = Float.valueOf(getMeasuredHeight() / 2);
        setY();
    }

    private void setY() {
        mY = getMeasuredHeight() / 2;
        double deltLeft = (limitY * Math.sin(Math.toRadians(offset)));
        mleftY = (float) (mY + deltLeft);
        double deltMid = (limitY * Math.sin(Math.toRadians(offset + 90)));
        midY = (float) (mY + deltMid);
        double deltRight = (limitY * Math.sin(Math.toRadians(offset + 180)));
        rightY = (float) (mY + deltRight);
    }

    /**
     * 弹跳高度为两个圆直径
     * <p>
     * 0-90纵向拉伸(Y增大)，横向(X减小)压缩,至顶点
     * <p>
     * 90-180纵向还原(Y减小至半径大小)，横向增加(X增大半径大小)
     * <p>
     * 180-270保持圆形
     * <p>
     * 当在270度的时候，先纵向压缩(Y减小,X增大)，
     * <p>
     * 270-360再还原（Y增大至半径，X减小至半径）
     *
     * @param offset 偏移角度
     */
    private void setXandY(int offs, int diraction, int num, int[] dirNum) {

        /**
         * 椭圆a^2+b^2=c^2
         */

        if (offs >= 0 && offs <= 90) {
            // 纵向向上拉伸
            dirNum[1] = dirNum[1] + offsetY;
            dirNum[3] = 0;// 底部不变
            // 横向对称压缩

            dirNum[0] = dirNum[0] - (offsetX / 2);
            dirNum[2] = dirNum[2] - (offsetX / 2);
        } else if (offs > 90 && offs <= 180) {
            // 上部反弹还原成圆形
            dirNum[1] = dirNum[1] - offsetY;
            dirNum[3] = 0;
            dirNum[0] = dirNum[0] + (offsetX / 2);
            dirNum[2] = dirNum[2] + (offsetX / 2);
        } else if (offs > 180 && offs < 270) {
            dirNum[1] = 0;
            dirNum[3] = 0;
            dirNum[0] = 0;
            dirNum[2] = 0;
        } else if (offs == 270) {
            // 先纵向压缩再还原
            switch (num) {
                case 1:
                case 2:
                    dirNum[1] = dirNum[1] - offsetY;
                    dirNum[3] = 0;
                    dirNum[0] = dirNum[0] + (offsetX / 2);
                    dirNum[2] = dirNum[2] + (offsetX / 2);
                    break;
                case 3:
                case 4:
                    dirNum[1] = dirNum[1] + offsetY;
                    dirNum[3] = 0;
                    dirNum[0] = dirNum[0] - (offsetX / 2);
                    dirNum[2] = dirNum[2] - (offsetX / 2);
                    break;
            }
        }

        switch (diraction) {
            case 1:
                leftNum = dirNum;
                break;
            case 2:
                midNum = dirNum;
                break;
            case 3:
                rightNum = dirNum;
                break;
            default:
                break;
        }
    }

    public void stop() {
        // started = false;
        mHandler.removeCallbacks(mRunnable);
        // removeCallbacks(mRunnable);
    }

    private int offset = 0;

    public void start() {
        // stop();
        // started = true;
        // if (started) {
        mHandler.postDelayed(mRunnable, 0);
        // postDelayed(mRunnable, 20);
        // }
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // 左边
            offset = offset + 15;
            if (offset >= 360) {
                offset = 0;
            }
            int offsetL = offset;
            if (offsetL == 270 && numL >= 0 && numL < 3) {
                setXandY(offsetL, DIREC_LEFT, numL, leftNum);
                numL++;
            } else {
                numL = 0;
                setXandY(offsetL, DIREC_LEFT, numL, leftNum);
            }

            int offsetM = offset + 90;
            if (offsetM > 360) {
                offsetM = offsetM - 360;
            }
            if (offsetM == 270 && numM >= 0 && numM < 3) {
                setXandY(offsetM, DIREC_MID, numM, midNum);
                numM++;
            } else {
                numM = 0;
                setXandY(offsetM, DIREC_MID, numM, midNum);
            }

            int offsetR = offset + 180;
            if (offsetR > 360) {
                offsetR = offsetR - 360;
            }
            if (offsetR + 180 == 270 && numR >= 0 && numR < 3) {
                setXandY(offsetR, DIREC_RIGHT, numR, rightNum);
                numR++;
            } else {
                numR = 0;
                setXandY(offsetR, DIREC_RIGHT, numR, rightNum);
            }
            setY();
            postInvalidate();
            mHandler.post(mRunnable);
        }
    };

    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            start();
        } else {
            stop();
        }
    }
}
