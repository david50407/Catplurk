package tw.catcafe.catplurk.android;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import tw.catcafe.catplurk.android.model.ParcelablePlurk;

/**
 * A fragment representing a single Plurk detail screen.
 * This fragment is either contained in a {@link PlurkListActivity}
 * in two-pane mode (on tablets) or a {@link PlurkDetailActivity}
 * on handsets.
 */
public class PlurkDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private ParcelablePlurk mItem = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlurkDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = new Select()
//                    .from(Plurk.class)
//                    .where("plurkId = ?", getArguments().getLong(ARG_ITEM_ID))
//                    .executeSingle();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plurk_detail, container, false);


        return rootView;
    }
}
