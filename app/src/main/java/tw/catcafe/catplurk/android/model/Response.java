package tw.catcafe.catplurk.android.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Davy on 2015/7/16.
 */
public class Response {
    private int id, plurk_id;
    private String qualifier, qualifier_translated;
    private int user_id;
    private Date posted;
    private String content, content_raw;

    public Response(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
