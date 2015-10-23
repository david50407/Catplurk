package tw.catcafe.catplurk.android.loader;

import android.content.Context;
import android.os.Bundle;

import java.util.Collections;
import java.util.List;

import tw.catcafe.catplurk.android.constant.IntentConstant;
import tw.catcafe.catplurk.android.model.ParcelablePlurk;

/**
 * @author Davy
 */
public class IntentExtasPlurksLoader extends BasePlurksLoader
        implements IntentConstant{
    private final Bundle mExtras;

    public IntentExtasPlurksLoader(final Context context, final Bundle extras,
                                   final List<ParcelablePlurk> data, final boolean fromUser) {
        super(context, data, fromUser);
        mExtras = extras;
    }

    @Override
    public List<ParcelablePlurk> loadInBackground() {
        final List<ParcelablePlurk> data = getData();
        if (mExtras != null && mExtras.containsKey(EXTRA_PLURKS)) {
            final List<ParcelablePlurk> plurks = mExtras.getParcelableArrayList(EXTRA_PLURKS);
            data.addAll(plurks);
            Collections.sort(data);
        }
        return data;
    }
}
