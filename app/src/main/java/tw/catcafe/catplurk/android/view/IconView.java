package tw.catcafe.catplurk.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author Davy
 */
public class IconView extends ImageView {

    private final int mDefaultColor;

    public IconView(Context context) {
        this(context, null);
    }

    public IconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.color,
                android.R.attr.colorForeground});
        if (a.hasValue(0)) {
            mDefaultColor = a.getColor(0, 0);
        } else {
            mDefaultColor = a.getColor(1, 0);
        }
        setColorFilter(mDefaultColor, PorterDuff.Mode.SRC_ATOP);
        a.recycle();
    }

    public void setColor(int color) {
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public int getDefaultColor() {
        return mDefaultColor;
    }
}
