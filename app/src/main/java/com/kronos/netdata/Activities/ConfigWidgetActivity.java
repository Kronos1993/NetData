package com.kronos.netdata.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;

public class ConfigWidgetActivity extends AppCompatActivity {

    private Context context = this;
    private static int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_widget);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_wiget, new WidgetSettingsFragment())
                .commit();

        GeneralUtility.setTheme(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras!=null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        setResult(RESULT_CANCELED,result);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:onBackPressed();break;
            default:onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class WidgetSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.widget_preferences, rootKey);

            ListPreference widgetPqt = findPreference("widget_paquete");
            widgetPqt.setOnPreferenceChangeListener((preference, newValue) -> {
                GeneralUtility.updateWidget(getContext());
                Intent result = new Intent();
                result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
                getActivity().setResult(RESULT_OK,result);
                getActivity().finish();
                return true;
            });
        }
    }
}

