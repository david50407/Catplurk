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
import tw.catcafe.catplurk.android.adapter.iface.ResponsesAdapter;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.Response;
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
public class ResponseViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ResponsesAdapter adapter;

    @Bind(R.id.profile_image)
    CircleImageView profileImage;
    @Bind(R.id.profile_nickname)
    TextView profileName;
    @Bind(R.id.qualifier)
    TextView qualifier;
    @Bind(R.id.content)
    TextView content;
//    @Bind(R.id.time)
//    TextView mTime;

    public ResponseViewHolder(@NonNull final ResponsesAdapter adapter, @NonNull final View itemView) {
        super(itemView);
        this.adapter = adapter;
        ButterKnife.bind(this, itemView);
    }

    public void displayResponse(final Context context, final Response response) {
        final Application application = Application.getInstance(context);
        final User user = findUserInDatabase(context, response.getAccountId(), response.getUserId());
        final int qualifierColorResource = QualifierUtil.getQualifierColorResource(response.getQualifier());
        final int qualifierColor = context.getResources().getColor(qualifierColorResource);
        bindProfile(user, application, qualifierColorResource);
        bindPlurk(response, qualifierColor);
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

    protected void bindPlurk(final Response response, final int qualifierColor) {
        // TODO: Configurable displaying local qualifier or poster's qualifier
        final String qualifierDisplayed = StringUtils.isEmpty(response.getQualifierTranslated()) ?
                response.getQualifier() : response.getQualifierTranslated();
        qualifier.setText(qualifierDisplayed);
        qualifier.setTextColor(qualifierColor);
        // TODO: Rich-ful Content
        content.setText(response.getContentRaw());
//        mTime.setText(DateFormat.format("hh:mm", response.getPosted()));
    }
}
