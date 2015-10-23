package tw.catcafe.catplurk.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by Davy on 2015/7/20.
 */
public class IconButton extends ImageButton {
    private final int mDefaultColor;

    public IconButton(Context context) {
        this(context, null);
    }
    public IconButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
    }
    public IconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, new int[] {
                android.R.attr.color,
                android.R.attr.colorForeground
        });
        if (a.hasValue(0)) {
            mDefaultColor = a.getColor(0, 0);
        } else {
            mDefaultColor = a.getColor(1, 0);
        }
        setColorFilter(mDefaultColor, PorterDuff.Mode.SRC_ATOP);
        a.recycle();
    }
    public int getDefaultColor() {
        return mDefaultColor;
    }

}
