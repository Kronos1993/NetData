package com.kronos.netdata.Activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.kronos.netdata.Activities.Adapters.AboutPage.AboutPage;
import com.kronos.netdata.Activities.Adapters.AboutPage.Element;
import com.kronos.netdata.R;

import java.util.Calendar;

/**
 * Created by froilan on 3/24/2020.
 */
public class AboutActivity extends AppCompatActivity {

    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //simulateDayNight(/* DAY */ 3);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.app_icon_drawer)
                .addEmail("mguerral1993@gmail.com")
                //.addWebsite("http://medyo.github.io/")
                //.addFacebook("the.medy")
                .addTelegram("")
                .addTwitter("MarcosOctavio93")
                //.addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.beesource.etecsainternet")
                //.addInstagram("medyo80")
                .addGitHub("Kronos1993")
                .addItem(getVersionElement())
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);

        setTitle(R.string.about_us);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(R.color.colorBlack);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    Element getVersionElement() {
        Element versionElement = new Element();
        final String version="Versión: "+getVersionName(context);
        versionElement.setTitle(version);
        versionElement.setIconDrawable(R.drawable.ic_version_icon);
        versionElement.setIconTint(R.color.colorPrimary);
        versionElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, version, Toast.LENGTH_SHORT).show();
            }
        });
        return versionElement;
    }

    public static String getVersionName(Context ctx) {
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
