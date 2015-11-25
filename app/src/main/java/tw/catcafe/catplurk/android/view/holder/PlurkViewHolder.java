package tw.catcafe.catplurk.android.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
public class PlurkViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final PlurksAdapter adapter;

    @Bind(R.id.replurk_container)
    LinearLayout replurkContainer;
    @Bind(R.id.replurker)
    TextView mReplurker;
    @Bind(R.id.profile_image)
    CircleImageView profileImage;
    @Bind(R.id.profile_nickname)
    TextView profileName;
    @Bind(R.id.qualifier)
    TextView qualifier;
    @Bind(R.id.content)
    TextView content;
    @Bind(R.id.response_count)
    TextView responseCount;
    @Bind(R.id.response_count_ribbon)
    CardView responseCountRibbon;
    @Bind(R.id.response_count_ribbon_under)
    IconView responseCountRibbonUnder;
    @Bind(R.id.time)
    TextView mTime;

    @Bind(R.id.action_favorite)
    IconTextView mActionFavorite;
    @Bind(R.id.action_replurk)
    IconTextView mActionReplurk;

    @BindString(R.string.plurk_has_been_replurked)
    String template_plurk_has_been_replurked;

    @BindColor(R.color.plurk_read_text)
    int colorPlurkReadText;
    @BindColor(R.color.plurk_read_ribbon)
    int colorPlurkReadRibbon;
    @BindColor(R.color.plurk_read_ribbon_under)
    int colorPlurkReadRibbonUnder;
    @BindColor(R.color.plurk_unread_text)
    int colorPlurkUnreadText;
    @BindColor(R.color.plurk_unread_ribbon)
    int colorPlurkUnreadRibbon;
    @BindColor(R.color.plurk_unread_ribbon_under)
    int colorPlurkUnreadRibbonUnder;
    @BindColor(R.color.plurk_muted_text)
    int colorPlurkMutedText;
    @BindColor(R.color.plurk_muted_ribbon)
    int colorPlurkMutedRibbon;
    @BindColor(R.color.plurk_muted_ribbon_under)
    int colorPlurkMutedRibbonUnder;

    public PlurkViewHolder(@NonNull final PlurksAdapter adapter, @NonNull final View itemView) {
        super(itemView);
        this.adapter = adapter;
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
        final User user = findUserInDatabase(context, plurk.getAccountId(), plurk.getOwnerId());
        final int qualifierColorResource = QualifierUtil.getQualifierColorResource(plurk.getQualifier());
        final int qualifierColor = context.getResources().getColor(qualifierColorResource);
        bindProfile(user, application, qualifierColorResource);
        bindPlurk(plurk, qualifierColor, context);
        bindActionButtons(plurk);
    }

    protected void bindProfile(final User user, final Application application, final int qualifierColorResource) {
        profileImage.setBorderColorResource(qualifierColorResource);
        if (user != null) {
            application.getImageLoaderWrapper().displayImage(profileImage,
                    UserProfileImageHelper.getUrl(user),
                    new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.dummy_profile_image)
                            .build());
            profileName.setText(user.getDisplayName());
            profileName.setTextColor(Color.parseColor(user.getNameColor() == null ? "#000000" : ("#" + user.getNameColor())));
        }
    }

    protected void bindPlurk(final Plurk plurk, final int qualifierColor, final Context context) {
        // TODO: Configurable displaying local qualifier or poster's qualifier
        final String qualifierDisplayed = StringUtils.isEmpty(plurk.getQualifierTranslated()) ?
                plurk.getQualifier() : plurk.getQualifierTranslated();
        qualifier.setText(qualifierDisplayed);
        qualifier.setTextColor(qualifierColor);
        // TODO: Rich-ful Content
        content.setText(plurk.getContentRaw());
        responseCount.setText(ParseUtil.parseString(plurk.getResponseCount()));
        switch (plurk.isUnread()) {
            case READ:
                responseCount.setTextColor(colorPlurkReadText);
                responseCountRibbon.setCardBackgroundColor(colorPlurkReadRibbon);
                responseCountRibbonUnder.setColor(colorPlurkReadRibbonUnder);
                break;
            case UNREAD:
                responseCount.setTextColor(colorPlurkUnreadText);
                responseCountRibbon.setCardBackgroundColor(colorPlurkUnreadRibbon);
                responseCountRibbonUnder.setColor(colorPlurkUnreadRibbonUnder);
                break;
            case MUTED:
                responseCount.setTextColor(colorPlurkMutedText);
                responseCountRibbon.setCardBackgroundColor(colorPlurkMutedRibbon);
                responseCountRibbonUnder.setColor(colorPlurkMutedRibbonUnder);
                break;
        }
        mTime.setText(DateUtils.formatDateTime(context, plurk.getPosted().getTime(), DateUtils.FORMAT_SHOW_TIME));
    }

    protected void bindActionButtons(final Plurk plurk) {
        mActionFavorite.setText(plurk.getFavoriteCount() > 0 ? "" + plurk.getFavoriteCount() : "");
        mActionReplurk.setText(plurk.getReplurkersCount() > 0 ? "" + plurk.getReplurkersCount() : "");
        mActionReplurk.setVisibility(plurk.getReplurkable() ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.card)
    public void onClick(View v) {
        adapter.onCardClick(this, this.getAdapterPosition());
    }

    public interface PlurkClickListener extends ContentCardClickListener {
        void onUserProfileClick(PlurkViewHolder holder, int position);
    }
}
