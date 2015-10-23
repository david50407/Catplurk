package tw.catcafe.catplurk.android.dummy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by Davy on 2015/7/20.
 */
public class DummyDrawable extends Drawable {
    private int mWidth, mHeight;
    private Paint mPaint = new Paint();

    public DummyDrawable setSizePxX(int width) {
        return setSizePx(width, mHeight);
    }

    public DummyDrawable setSizePxY(int height) {
        return setSizePx(mWidth, height);
    }

    public DummyDrawable setSizePx(int width, int height) {
        mWidth = width;
        mHeight = height;
        this.setBounds(0, 0, mWidth, mHeight);
        this.invalidateSelf();
        return this;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        canvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 0.5f, 0.5f, mPaint);
    }

    @Override
    public void setAlpha(int alpha) { }
    @Override
    public void setColorFilter(ColorFilter cf) { }
    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
