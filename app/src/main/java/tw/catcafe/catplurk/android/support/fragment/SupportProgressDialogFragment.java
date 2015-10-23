package tw.catcafe.catplurk.android.support.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import tw.catcafe.catplurk.android.R;

/**
 * @author Davy
 */
public class SupportProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.please_wait));
        return dialog;
    }

    public static SupportProgressDialogFragment show(final FragmentActivity activity, final String tag) {
        if (activity == null) return null;
        final SupportProgressDialogFragment f = new SupportProgressDialogFragment();
        f.show(activity.getSupportFragmentManager(), tag);
        return f;
    }

}
