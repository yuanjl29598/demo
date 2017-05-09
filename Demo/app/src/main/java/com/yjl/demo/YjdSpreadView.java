package com.yjl.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.Random;

@SuppressLint("DrawAllocation")
public class YjdSpreadView extends SurfaceView {
    private float mRectFWidth; // 波浪的的宽度，由波浪的个数决定
    private int mWidth;
    private int mHeight;
    private Canvas drawCanvas;
    private Drawable drawable = null;
    private Bitmap bitmap;// 背景图

    private Path path; // 波浪线path
    private int size = DensityUtils.dip2px(getContext(), 10);// 波浪线的高
    private Paint paintRect; // 波浪线的paint
    private int count; // 每一次向右移动的偏移
    private float scaleW = 1.0f; // 图片宽度的缩放比
    // 平移开始点与移动点
    private Paint textPaint;
    private int dX = 0;
    private int dY = 0;
    private int offsetY = DensityUtils.dip2px(getContext(), 3);// 向下移动的偏移度
    private int alphaDelt = 3;// 透明度
    private int textAlpha = 255;
    private String drawText = "+0.2%";
    private int maxNum = 100;
    private Random random;
    private Handler mHandler = new Handler(getContext().getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x23:
                    count += DensityUtils.dip2px(getContext(), 1);// 波浪线波动的速率
                    if (count >= 2 * mRectFWidth) {
                        count = 0;
                    }
                    postInvalidate();
                    sendEmptyMessageDelayed(0x23, 25);
                    // sendEmptyMessage(0x23);
                    break;
                case 0x25:
                    if (dY >= mHeight || textAlpha <= 80 && random != null) {
                        dX = random.nextInt(maxNum);
                        dY = 0;
                        textAlpha = 255;
                    } else {
                        dY = dY + offsetY;
                        textAlpha -= alphaDelt;
                    }
                    postInvalidate();
                    sendEmptyMessageDelayed(0x25, 0);
                    break;
            }

        }
    };

    public YjdSpreadView(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    public YjdSpreadView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public YjdSpreadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public YjdSpreadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        random = new Random();
        path = new Path();
        paintRect = new Paint();
        paintRect.setStrokeWidth(DensityUtils.dip2px(getContext(), 2));
        paintRect.setColor(Color.WHITE);
        paintRect.setAntiAlias(true);
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
        paintRect.setStyle(Paint.Style.FILL_AND_STROKE);
        paintRect.setXfermode(mode);

        textPaint = new Paint();
        textPaint.setStrokeWidth(DensityUtils.dip2px(getContext(), 1));
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(DensityUtils.sp2px(getContext(), 16));
        mHandler.sendEmptyMessageDelayed(0x23, 25);
        mHandler.sendEmptyMessageDelayed(0x25, 0);
    }

    /**
     * 设置背景图的drawable
     * 
     * @param drawable
     */
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawable == null) {
            // 拿到Drawable
            drawable = getContext().getResources().getDrawable(R.mipmap.bg_invite_friend);
        }
        // 获取图片宽度和高度

        // mHeight = getMeasuredHeight();
        mHeight = drawable.getIntrinsicHeight();

        setMeasuredDimension(widthMeasureSpec, mHeight);
        mWidth = getMeasuredWidth();
        mRectFWidth = mWidth / 5f;
        maxNum = drawable.getIntrinsicWidth() - DensityUtils.dip2px(getContext(), 25);
        if (maxNum <= 0) {
            maxNum = 100;
        }
        mRectFWidth = mRectFWidth - mRectFWidth % 1;// 去掉小数部分

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取drawable的宽和高
        if (drawable != null) {
            int dWidth = drawable.getIntrinsicWidth();
            int dHeight = drawable.getIntrinsicHeight();
            // 创建bitmap
            if (bitmap == null || bitmap.isRecycled()) {
                bitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
            }

            // float scaleH = 1.0f;
            // 创建画布
            if (drawCanvas == null) {
                drawCanvas = new Canvas(bitmap);
                scaleW = getWidth() * 1.0F / dWidth;
                // scaleH = getHeight() * 1.0f / dHeight;
                // 根据缩放比例，设置bounds，相当于缩放图片了
                // 横向全屏，纵向自然高
                drawable.setBounds(0, 0, (int) (scaleW * dWidth), dHeight);
            }
            canvas.save();
            drawable.draw(drawCanvas);
            path.reset();
            path.moveTo(count - mRectFWidth * 2,
                    mHeight - size - DensityUtils.dip2px(getContext(), 15));// 控制从左向右滚动
            for (int i = 0; i < 20; i++) {
                path.rQuadTo(mRectFWidth / 2, size, mRectFWidth, 0);
                path.rQuadTo(mRectFWidth / 2, -size, mRectFWidth, 0);
            }
            path.lineTo(0, mHeight + size + DensityUtils.dip2px(getContext(), 15));
            path.close();
            drawCanvas.drawPath(path, paintRect);
            canvas.drawBitmap(bitmap, 0, 0, null);

            textPaint.setAlpha(textAlpha);
            textPaint.setAntiAlias(false);
            canvas.translate(dX, dY);
            canvas.drawText(drawText, 0, 0, textPaint);
            canvas.restore();
        }
    }

    public void onRelease() {
        if (mHandler != null) {
            mHandler = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (drawCanvas != null) {
            drawCanvas = null;
        }

    }
}
