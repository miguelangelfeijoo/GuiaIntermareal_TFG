package tfg.uniovi.es.guiaintermareal.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import tfg.uniovi.es.guiaintermareal.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.requestFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.activity_about);

        w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
    }
}
