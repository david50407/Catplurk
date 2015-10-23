package tw.catcafe.catplurk.android.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;

/**
 * @author Davy
 */
public class DateViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.content_layout)
    View mLayout;
    @Bind(R.id.title)
    TextView mDate;

    public DateViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final Plurk plurk) {
        if (plurk == null || plurk.getPosted() == null) {
            mLayout.setVisibility(View.GONE);
            mLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } else {
            mLayout.setVisibility(View.VISIBLE);
            mLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(plurk.getPosted());
            calendar.setTimeZone(TimeZone.getDefault());
            mDate.setText(DateFormat.getDateInstance().format(calendar.getTime()));
        }
    }
}
