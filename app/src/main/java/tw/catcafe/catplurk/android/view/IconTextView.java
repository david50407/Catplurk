package tw.catcafe.catplurk.android.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsTextView;

import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.dummy.DummyDrawable;

/**
 * Created by Davy on 2015/7/20.
 */
public class IconTextView extends TextView {
    private final int mIconWidth, mIconHeight;
    private int mDisabledColor, mActivatedColor;

    public IconTextView(Context context) {
        this(context, null);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconTextView);
        mDisabledColor = a.getColor(R.styleable.IconTextView_itvDisabledColor, 0);
        mActivatedColor = a.getColor(R.styleable.IconTextView_itvActivatedColor, 0);
        mIconWidth = a.getDimensionPixelSize(R.styleable.IconTextView_itvIconWidth, 0);
        mIconHeight = a.getDimensionPixelSize(R.styleable.IconTextView_itvIconHeight, 0);
        String mIivIcon = a.getString(R.styleable.IconTextView_itvIconicsIcon);
        a.recycle();

        if (mIivIcon != null) {
            if (!isInEditMode()) {
                setCompoundDrawables(new IconicsDrawable(getContext(), mIivIcon)
                                .sizePxX(mIconWidth)
                                .sizePxY(mIconHeight).colorRes(android.R.color.white),
                        null,
                        null,
                        null);
            } else {
                setCompoundDrawables(
                        new DummyDrawable()
                                .setSizePxX(mIconWidth)
                                .setSizePxY(mIconHeight),
                        null,
                        null,
                        null);
            }
        }
        updateCompoundDrawables();
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        updateCompoundDrawables();
    }
    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        updateCompoundDrawables();
    }
    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        updateCompoundDrawables();
    }
    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        updateCompoundDrawables();
    }

    public int getActivatedColor() {
        if (mActivatedColor != 0) return mActivatedColor;
        final ColorStateList colors = getLinkTextColors();
        if (colors != null) return colors.getDefaultColor();
        return getCurrentTextColor();
    }
    public int getColor() {
        final ColorStateList colors = getTextColors();
        if (colors != null) return colors.getDefaultColor();
        return getCurrentTextColor();
    }

    public int getDisabledColor() {
        if (mDisabledColor != 0) return mDisabledColor;
        final ColorStateList colors = getTextColors();
        if (colors != null) return colors.getColorForState(new int[0], colors.getDefaultColor());
        return getCurrentTextColor();
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateCompoundDrawables();
    }

    private void updateCompoundDrawables() {
        updateCompoundDrawables(getCompoundDrawables());
        updateCompoundDrawables(getCompoundDrawablesRelative());
    }

    private void updateCompoundDrawables(Drawable[] drawables) {
        if (drawables == null) return;
        for (Drawable d : drawables) {
            if (d == null) continue;
            d.mutate();

            final int color;
            if (isActivated())
                color = getActivatedColor();
            else if (isEnabled())
                color = getColor();
            else
                color = getDisabledColor();

            if (mIconWidth > 0 && mIconHeight > 0)
                d.setBounds(0, 0, mIconWidth, mIconHeight);
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
