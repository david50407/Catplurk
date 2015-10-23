package tw.catcafe.catplurk.android.util;

import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;

import tw.catcafe.catplurk.android.R;

/**
 * @author Davy
 */
public class QualifierUtil {
    static HashMap<String, Triple<Integer, Integer, Integer>> QUALIFIER_MAPPER = new HashMap<>();

    static {
        QUALIFIER_MAPPER.put(":", Triple.of(
                        R.string.qualifier_freestyle,
                        R.color.qualifier_freestyle,
                        R.style.AppTheme_Qualifier_freestyle));
        QUALIFIER_MAPPER.put("freestyle", Triple.of(
                        R.string.qualifier_freestyle,
                        R.color.qualifier_freestyle,
                        R.style.AppTheme_Qualifier_freestyle));
        QUALIFIER_MAPPER.put("loves", Triple.of(
                        R.string.qualifier_loves,
                        R.color.qualifier_loves,
                        R.style.AppTheme_Qualifier_loves));
        QUALIFIER_MAPPER.put("likes", Triple.of(
                        R.string.qualifier_likes,
                        R.color.qualifier_likes,
                        R.style.AppTheme_Qualifier_likes));
        QUALIFIER_MAPPER.put("shares", Triple.of(
                        R.string.qualifier_shares,
                        R.color.qualifier_shares,
                        R.style.AppTheme_Qualifier_shares));
        QUALIFIER_MAPPER.put("gives", Triple.of(
                        R.string.qualifier_gives,
                        R.color.qualifier_gives,
                        R.style.AppTheme_Qualifier_gives));
        QUALIFIER_MAPPER.put("hates", Triple.of(
                        R.string.qualifier_hates,
                        R.color.qualifier_hates,
                        R.style.AppTheme_Qualifier_hates));
        QUALIFIER_MAPPER.put("wants", Triple.of(
                        R.string.qualifier_wants,
                        R.color.qualifier_wants,
                        R.style.AppTheme_Qualifier_wants));
        QUALIFIER_MAPPER.put("wishes", Triple.of(
                        R.string.qualifier_wishes,
                        R.color.qualifier_wishes,
                        R.style.AppTheme_Qualifier_wishes));
        QUALIFIER_MAPPER.put("needs", Triple.of(
                        R.string.qualifier_needs,
                        R.color.qualifier_needs,
                        R.style.AppTheme_Qualifier_needs));
        QUALIFIER_MAPPER.put("will", Triple.of(
                        R.string.qualifier_will,
                        R.color.qualifier_will,
                        R.style.AppTheme_Qualifier_will));
        QUALIFIER_MAPPER.put("hopes", Triple.of(
                        R.string.qualifier_hopes,
                        R.color.qualifier_hopes,
                        R.style.AppTheme_Qualifier_hopes));
        QUALIFIER_MAPPER.put("asks", Triple.of(
                        R.string.qualifier_asks,
                        R.color.qualifier_asks,
                        R.style.AppTheme_Qualifier_asks));
        QUALIFIER_MAPPER.put("has", Triple.of(
                        R.string.qualifier_has,
                        R.color.qualifier_has,
                        R.style.AppTheme_Qualifier_has));
        QUALIFIER_MAPPER.put("was", Triple.of(
                        R.string.qualifier_was,
                        R.color.qualifier_was,
                        R.style.AppTheme_Qualifier_was));
        QUALIFIER_MAPPER.put("wonders", Triple.of(
                        R.string.qualifier_wonders,
                        R.color.qualifier_wonders,
                        R.style.AppTheme_Qualifier_wonders));
        QUALIFIER_MAPPER.put("feels", Triple.of(
                        R.string.qualifier_feels,
                        R.color.qualifier_feels,
                        R.style.AppTheme_Qualifier_feels));
        QUALIFIER_MAPPER.put("thinks", Triple.of(
                        R.string.qualifier_thinks,
                        R.color.qualifier_thinks,
                        R.style.AppTheme_Qualifier_thinks));
        QUALIFIER_MAPPER.put("says", Triple.of(
                        R.string.qualifier_says,
                        R.color.qualifier_says,
                        R.style.AppTheme_Qualifier_says));
        QUALIFIER_MAPPER.put("is", Triple.of(
                        R.string.qualifier_is,
                        R.color.qualifier_is,
                        R.style.AppTheme_Qualifier_is));
        QUALIFIER_MAPPER.put("whispers", Triple.of(
                        R.string.qualifier_whispers,
                        R.color.qualifier_whispers,
                        R.style.AppTheme_Qualifier_whispers));
    }

    public static int getQualifierStringResource(final String qualifier) {
        return QUALIFIER_MAPPER.get(qualifier).getLeft();
    }

    public static int getQualifierColorResource(final String qualifier) {
        return QUALIFIER_MAPPER.get(qualifier).getMiddle();
    }
    public static int getQualifierThemeResource(final String qualifier) {
        return QUALIFIER_MAPPER.get(qualifier).getRight();
    }
}
