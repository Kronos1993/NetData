package com.kronos.netdata.Activities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kronos.netdata.Activities.Fragments.ChartHistoryFragment;
import com.kronos.netdata.Activities.Fragments.ListHistoryFragment;
import com.kronos.netdata.DB.Connection;

import java.sql.SQLException;
import java.util.ArrayList;

import com.kronos.netdata.R;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Util.GeneralUtility;


public class HistorialActivity extends AppCompatActivity {

    private Context context = this;
    private boolean showingList = false;
    private ArrayList historials = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GeneralUtility.setTheme(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));


    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        try {
            QueryBuilder<Historial, Integer> queryBuilder = null;
            queryBuilder = Connection.getConnection(context).getHistorialDao().queryBuilder();
            queryBuilder.orderBy(Historial.DATE_FIELD_NAME, false);
            PreparedQuery<Historial> preparedQuery = queryBuilder.prepare();
            ArrayList historials = (ArrayList<Historial>) Connection.getConnection(context).getHistorialDao().query(preparedQuery);
            if (historials.size() > 0) {
                showChartFragment();
            } else {
                Toast.makeText(context, R.string.db_empty, Toast.LENGTH_SHORT).show();
                GeneralUtility.navigate(context, MainActivityDrawer.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.error_db, Toast.LENGTH_LONG).show();
            GeneralUtility.navigate(context, MainActivityDrawer.class);
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        try {
            QueryBuilder<Historial, Integer> queryBuilder = null;
            queryBuilder = Connection.getConnection(context).getHistorialDao().queryBuilder();
            queryBuilder.orderBy(Historial.DATE_FIELD_NAME, false);
            PreparedQuery<Historial> preparedQuery = queryBuilder.prepare();
            historials = (ArrayList<Historial>) Connection.getConnection(context).getHistorialDao().query(preparedQuery);
            if (historials.size() > 0) {
                showChartFragment();
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.error_db, Toast.LENGTH_LONG).show();
            GeneralUtility.navigate(context, MainActivityDrawer.class);
        }
    }

    public void showChartFragment() {
        showingList = false;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ChartHistoryFragment fragmentRequest = ChartHistoryFragment.newInstance();
        ft.replace(R.id.fragment_history_layout, fragmentRequest);
        ft.commit();
    }

    public void showListFragment() {
        showingList = true;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ListHistoryFragment fragmentRequest = ListHistoryFragment.newInstance();
        ft.replace(R.id.fragment_history_layout, fragmentRequest);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            GeneralUtility.navigate(context, SettingsActivity.class);
        } else if (id == R.id.action_show_list) {
            if (historials.size() > 0) {
                if (showingList) {
                    showChartFragment();
                    changeMenuIcon(showingList, item);
                } else {
                    showListFragment();
                    changeMenuIcon(showingList, item);
                }
            } else {
                Toast.makeText(context, R.string.db_empty, Toast.LENGTH_SHORT).show();
            }
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeMenuIcon(boolean showingList, MenuItem item) {
        if (showingList) {
            item.setIcon(R.drawable.ic_show_chart);
        } else {
            item.setIcon(R.drawable.ic_list);
        }
    }

}
