package tw.catcafe.catplurk.android.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.adapter.iface.ContentCardClickListener;
import tw.catcafe.catplurk.android.adapter.iface.PlurksAdapter;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.util.ParseUtil;
import tw.catcafe.catplurk.android.util.QualifierUtil;
import tw.catcafe.catplurk.android.util.imageloader.UserProfileImageHelper;
import tw.catcafe.catplurk.android.view.IconTextView;
import tw.catcafe.catplurk.android.view.IconView;

import static tw.catcafe.catplurk.android.util.DatabaseUtils.findUserInDatabase;

/**
 * Created by Davy on 2015/7/19.
 */
public class ResponsePlurkViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.replurk_container)
    LinearLayout replurkContainer;
    @Bind(R.id.replurker)
    TextView mReplurker;
    @Bind(R.id.content)
    TextView content;
    // TODO: Show time
//    @Bind(R.id.time)
//    TextView mTime;

    @Bind(R.id.action_favorite)
    IconTextView mActionFavorite;
    @Bind(R.id.action_replurk)
    IconTextView mActionReplurk;

    @BindString(R.string.plurk_has_been_replurked)
    String template_plurk_has_been_replurked;

    public ResponsePlurkViewHolder(@NonNull final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void displayPlurk(final Context context, final Plurk plurk) {
        final Application application = Application.getInstance(context);
        if (plurk.getReplurkerId() > 0) {
            replurkContainer.setVisibility(View.VISIBLE);
            User replurker = findUserInDatabase(context, plurk.getAccountId(), plurk.getReplurkerId());
            if (replurker != null) {
                mReplurker.setText(String.format(template_plurk_has_been_replurked, replurker.getDisplayName()));
            }
        } else {
            replurkContainer.setVisibility(View.GONE);
        }
        bindPlurk(plurk);
        bindActionButtons(plurk);
    }

    protected void bindPlurk(final Plurk plurk) {
        // TODO: Rich-ful Content
        content.setText(plurk.getContentRaw());
//        mTime.setText(DateFormat.format("hh:mm", plurk.getPosted()));
    }

    protected void bindActionButtons(final Plurk plurk) {
        mActionFavorite.setText(plurk.getFavoriteCount() > 0 ? "" + plurk.getFavoriteCount() : "");
        mActionReplurk.setText(plurk.getReplurkersCount() > 0 ? "" + plurk.getReplurkersCount() : "");
        mActionReplurk.setVisibility(plurk.getReplurkable() ? View.VISIBLE : View.GONE);
    }
}
