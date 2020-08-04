package com.kronos.netdata.Activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;
import com.kronos.netdata.Widget.NetDataWidgetProvider;
import com.kronos.netdata.Widget.NetDataWidgetService;

public class SettingsActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        setTitle(R.string.title_activity_settings);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.app_preferences, rootKey);
            ListPreference theme = findPreference("theme");
            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(preference.getKey().equals("theme")){
                        if (newValue.equals("MODE_NIGHT_NO")) {
                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_NO);
                        } else if (newValue.equals("MODE_NIGHT_YES")) {
                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_YES);
                        } else if (newValue.equals("MODE_NIGHT_FOLLOW_SYSTEM")) {
                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        } else if (newValue.equals("MODE_NIGHT_AUTO_BATTERY")) {
                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                        }
                    }
                    return true;
                }
            });

            ListPreference widgetPqt = findPreference("widget_paquete");
            widgetPqt.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    GeneralUtility.updateWidget(getContext());
                    return true;
                }
            });

        }
    }
}