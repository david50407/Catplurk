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

        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
            String str = "這裡會放很多回覆" + "\n" +
                    "\n" +
                    "分風西好下化前命市：多跑和愛的時一畫學事想：合雜如素制邊策共成時：言費了東子樓叫時魚生人灣部司感中制天。目縣不星以牛家日們！衣的家風、是學出媽自那爸那種支，通醫因子女公的形過課國比天一上報？\n" +
                    "\n" +
                    "集隨其一性性們和會名是大表期轉可得長、為後供寫個其這民……情心氣等。正創車年我年……是片型發先重：後願了太同次無想得世麼心笑樂組：友只了果興國故廠例產錯之羅對標才全據產大古活海而，輕一長出民調況也上友兒為直不道軍明民種屋這。不功著主五。平的或法？她想張廣特法人大！名熱城個長中服評人萬起況省是，這心統在電不：品毒當會中。\n" +
                    "\n" +
                    "連亮指年……保呢我其室上推文要神庭蘭不人灣民裡過麗市國勢！我到步著其：一我量達路家國新容未國理冷官。大才門童關路來麼約稱檢分臺後心現室給要本要人改員名確間職滿。\n" +
                    "\n" +
                    "想片行方家團座大這快流吸股也！際單人輕草吃：場兩生男他或基、醫從演覺血古受數關到三建身分光山作聯最飛廠，發客的即答，此自技無三了人、訴個意色也差冷學體的現球醫邊們有。";
            ((TextView) rootView.findViewById(R.id.plurk_detail)).setText(str);
//        }

        return rootView;
    }
}
