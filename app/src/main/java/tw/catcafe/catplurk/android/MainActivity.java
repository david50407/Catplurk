package tw.catcafe.catplurk.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Entry from the Launcher
 *
 * @author Davy
 */
public class MainActivity extends Activity implements Constants {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, TimelineActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
