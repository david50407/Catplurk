package tw.catcafe.catplurk.android.support.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.util.AsyncPlurkWrapper;

/**
 * @author Davy
 */
public class BaseSupportFragment extends Fragment implements Constants {
    public BaseSupportFragment() {}

    public Application getApplication() {
        final Activity activity = getActivity();
        if (activity != null) return (Application) activity.getApplication();
        return null;
    }

    public AsyncPlurkWrapper getPlurkWrapper() {
        if (getApplication() != null) return getApplication().getPlurkWrapper();
        return null;
    }
}
