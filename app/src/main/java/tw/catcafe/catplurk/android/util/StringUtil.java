package tw.catcafe.catplurk.android.util;

/**
 * Created by Davy on 2015/7/16.
 */
public class StringUtil {
    public static String implode(String separator, Object... data) {
        StringBuilder sb = new StringBuilder();
        String str;
        for (int i = 0; i < data.length - 1; i++) {
            str = data[i].toString();
            if (!str.matches(" *")) { //empty string are ""; " "; "  "; and so on
                sb.append(str);
                sb.append(separator);
            }
        }
        sb.append(data[data.length - 1].toString().trim());
        return sb.toString();
    }
}
