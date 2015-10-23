package tw.catcafe.catplurk.android.plurkapi.model;

import org.mariotaku.restfu.http.SimpleValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Davy
 */
public class Paging extends SimpleValueMap {
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    //region Setter
    public void setOffset(String offset) {
        put("offset", offset);
    }
    public void setOffset(Date offset) {
        setOffset(sdf.format(offset));
    }
    public void setOffset(long offset) {
        setOffset(sdf.format(offset));
    }

    public void setLimit(long limit) {
        put("offset", limit);
    }
    //endregion Setter

    //region Builder
    public Paging offset(String offset) {
        setOffset(offset);
        return this;
    }
    public Paging offset(Date offset) {
        setOffset(offset);
        return this;
    }
    public Paging offset(long offset) {
        setOffset(offset);
        return this;
    }

    public Paging limit(long limit) {
        setLimit(limit);
        return this;
    }
    //endregion Builder
}
