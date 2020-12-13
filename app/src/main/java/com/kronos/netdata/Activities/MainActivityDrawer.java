package com.kronos.netdata.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.android.material.textfield.TextInputLayout;
import com.kronos.netdata.Activities.Adapters.PaqueteInternetAdapterGridOrList;
import com.kronos.netdata.Activities.GridViews.GridViewWithHeaderAndFooter;
import com.kronos.netdata.Domain.PaqueteInternet;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Util.GeneralUtility;
import com.kronos.netdata.Domain.USSDCode;
import com.kronos.netdata.R;

import org.joda.time.DateMidnight;
import org.joda.time.Days;


public class MainActivityDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] appPermissions = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS};
    private static final int PERMISSIONS_REQUEST_CODE = 1240;
    private ArrayList<PaqueteInternet> actionList = new ArrayList<>();
    private ArrayList<PaqueteInternet> actionList4G = new ArrayList<>();
    private PaqueteInternetAdapterGridOrList adapterGridOrList;
    private GridViewWithHeaderAndFooter gridViewActions;
    private Context context = this;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesSetings;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView textViewLastTimeConsult, textViewNoDataGrid, textViewTraficoRed, textViewMegasRestantes, textViewBonos, textViewDaysLeft, textViewConsumedLastTime;
    private Handler handler = new Handler();
    private Long mStartRX, mStartTX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(context, R.xml.app_preferences, false);
        PreferenceManager.setDefaultValues(context, R.xml.widget_preferences, false);

        sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        sharedPreferencesSetings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        GeneralUtility.setTheme(sharedPreferencesSetings);

        setContentView(R.layout.activity_main_activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewLastTimeConsult = findViewById(R.id.textViewLastTimeConsult);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        if (checkAndRequestPermisions()) {
            initViews();
        }
    }

    public boolean checkAndRequestPermisions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (!GeneralUtility.validatePermiso(context, perm)) {
                listPermissionsNeeded.add(perm);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            HashMap<String, Integer> permissionResult = new HashMap<>();
            int deniedCount = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    deniedCount++;
                    permissionResult.put(permissions[i], grantResults[i]);
                }
            }
            if (deniedCount == 0) {
                initViews();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResult.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        showDialogPermissionRequest();
                    }
                }
            }
        }
    }

    private void showDialogPermissionRequest() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(getString(R.string.title_dialog_permission_request));
        builder.setMessage(getString(R.string.message_dialog_permissions_request));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                checkAndRequestPermisions();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
            }
        });
        alertDialog.show();

        /*BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetRoundCorners);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_confirm, this.findViewById(R.id.bottom_confirm_dialog));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(35, 0, 35, 300);
        ConstraintLayout layout = view.findViewById(R.id.bottom_confirm_dialog);
        layout.setLayoutParams(layoutParams);

        TextView textViewTitle = view.findViewById(R.id.bottom_sheet_title);
        TextView textViewBody = view.findViewById(R.id.bottom_sheet_body);

        textViewTitle.setText(R.string.title_dialog_permission_request);
        textViewBody.setText(R.string.message_dialog_permissions_request);

        Button ok = view.findViewById(R.id.bottom_sheet_button_ok);
        Button cancel = view.findViewById(R.id.bottom_sheet_button_cancel);

        ok.setOnClickListener(view1 -> {
            checkAndRequestPermisions();
            bottomSheetDialog.dismiss();
        });
        cancel.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            ok.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
            cancel.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();*/
    }

    private void initViews() {
        TextView textViewVersion = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewVersion);
        textViewTraficoRed = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewTraficoRed);
        textViewMegasRestantes = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewMegas);
        textViewBonos = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewMegasBonos);
        textViewDaysLeft = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewDaysLeft);
        textViewConsumedLastTime = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewConsumedLastTime);
        textViewVersion.setText(String.format(getString(R.string.version), GeneralUtility.getVersionName(context)));
        textViewNoDataGrid = (TextView) findViewById(R.id.textViewNoDataMainGrid);

        Long last_consult = 0l;
        try {
            last_consult = sharedPreferencesSetings.getLong("last_consult", 0l);
        } catch (Exception e) {

        }

        int days_last_consult = Math.round(Days.daysBetween(new DateMidnight(last_consult), new DateMidnight(Calendar.getInstance().getTimeInMillis())).getDays());

        if (days_last_consult == 0) {
            textViewLastTimeConsult.setText(R.string.last_time_check_today);
        } else if (days_last_consult == 1) {
            textViewLastTimeConsult.setText(R.string.last_time_check_yesterday);
        } else {
            textViewLastTimeConsult.setText(String.format(context.getString(R.string.last_time_check_days), days_last_consult));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!sharedPreferencesSetings.contains("megas")) {
                sharedPreferencesSetings.edit().putString("megas", "0").apply();
            }
            if (!sharedPreferencesSetings.contains("bonos")) {
                sharedPreferencesSetings.edit().putString("bonos", "0").apply();
            }
            if (!sharedPreferencesSetings.contains("last_consult")) {
                sharedPreferencesSetings.edit().putString("last_consult", getString(R.string.package_not_consult_msg)).apply();
            }
            String megas, bonos, days, consumed;
            megas = sharedPreferencesSetings.getString("megas", getString(R.string.package_not_consult));
            bonos = sharedPreferencesSetings.getString("bonos", getString(R.string.bono_not_consult));
            days = sharedPreferencesSetings.getString("days", getString(R.string.days_left_not_consult));
            consumed = sharedPreferencesSetings.getString("consumes", "0MB");

            textViewMegasRestantes.setText(String.format(getString(R.string.drawer_packages), megas));
            textViewBonos.setText(String.format(getString(R.string.drawer_bonos), bonos));
            textViewConsumedLastTime.setText(consumed);
            textViewDaysLeft.setText(String.format(getString(R.string.drawer_days_left), days));
            textViewMegasRestantes.setVisibility(View.VISIBLE);
            textViewBonos.setVisibility(View.VISIBLE);
            textViewConsumedLastTime.setVisibility(View.GONE);
            textViewDaysLeft.setVisibility(View.VISIBLE);
        } else {
            textViewMegasRestantes.setVisibility(View.GONE);
            textViewBonos.setVisibility(View.GONE);
            textViewConsumedLastTime.setVisibility(View.GONE);
            textViewDaysLeft.setVisibility(View.GONE);
        }

        getTraficoRed();

        if (!sharedPreferences.getBoolean("init", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("list", true);
            editor.putBoolean("init", true);
            editor.commit();
            editor.apply();
        }

        actionList.clear();
        actionList4G.clear();
        Set<String> actionsActive = sharedPreferencesSetings.getStringSet("action_list", null);
        if (actionsActive != null) {
            Iterator iterator = actionsActive.iterator();
            while (iterator.hasNext()) {
                String val = (String) iterator.next();
                if (val.equals("0")) {
                    PaqueteInternet internet400 = new PaqueteInternet(125, "400MB", "500MB");
                    if (actionList.size() > 0) {
                        actionList.add(0, internet400);
                    } else {
                        actionList.add(internet400);
                    }
                } else if (val.equals("1")) {
                    PaqueteInternet internet600MB3G = new PaqueteInternet(175, "600MB", "800");
                    if (actionList.size() > 1) {
                        actionList.add(1, internet600MB3G);
                    } else {
                        actionList.add(internet600MB3G);
                    }
                } else if (val.equals("2")) {

                    PaqueteInternet internet1Gb3G = new PaqueteInternet(250, "1GB", "1.5GB");
                    if (actionList.size() > 2) {
                        actionList.add(2, internet1Gb3G);
                    } else {
                        actionList.add(internet1Gb3G);
                    }
                } else if (val.equals("3")) {
                    PaqueteInternet internet2Gb3G = new PaqueteInternet(500, "2.5GB", "3GB");
                    if (actionList.size() > 3) {
                        actionList.add(3, internet2Gb3G);
                    } else {
                        actionList.add(internet2Gb3G);
                    }
                } else if (val.equals("4")) {
                    PaqueteInternet internet4Gb3G = new PaqueteInternet(750, "4GB", "5GB");
                    ;
                    if (actionList.size() > 4) {
                        actionList.add(4, internet4Gb3G);
                    } else {
                        actionList.add(internet4Gb3G);
                    }
                    // aqui paquetes 4G
                } else if (val.equals("5")) {
                    PaqueteInternet pqt1GB4G = new PaqueteInternet(100, "1GB LTE", "");
                    if (actionList4G.size() > 0) {
                        actionList4G.add(0, pqt1GB4G);
                    } else {
                        actionList4G.add(pqt1GB4G);
                    }
                } else if (val.equals("6")) {
                    PaqueteInternet pqt2_5GB4G = new PaqueteInternet(200, "2.5GB LTE", "");
                    if (actionList4G.size() > 1) {
                        actionList4G.add(1, pqt2_5GB4G);
                    } else {
                        actionList4G.add(pqt2_5GB4G);
                    }
                }/*else if(val.equals("7")){
                    PaqueteInternet pqt6GB4G = new PaqueteInternet(35,"6.5GB LTE","");
                    if(actionList4G.size()>2){
                        actionList4G.add(2,pqt6GB4G);
                    }else{
                        actionList4G.add(pqt6GB4G);
                    }
                }*/ else if (val.equals("8")) {
                    PaqueteInternet pqt14GB4G = new PaqueteInternet(1125, "14GB LTE", "");
                    if (actionList4G.size() > 3) {
                        actionList4G.add(3, pqt14GB4G);
                    } else {
                        actionList4G.add(pqt14GB4G);
                    }
                } else if (val.equals("9")) {
                    PaqueteInternet pqt200MB4G = new PaqueteInternet(25, "200MB LTE", "");
                    if (actionList4G.size() > 4) {
                        actionList4G.add(4, pqt200MB4G);
                    } else {
                        actionList4G.add(pqt200MB4G);
                    }
                }
            }
        } else {
            PaqueteInternet internet400MB3G = new PaqueteInternet(125, "400MB", "500MB");
            PaqueteInternet internet600MB3G = new PaqueteInternet(175, "600MB", "800");
            PaqueteInternet internet1Gb3G = new PaqueteInternet(250, "1GB", "1.5GB");
            PaqueteInternet internet2Gb3G = new PaqueteInternet(500, "2.5GB", "3GB");
            PaqueteInternet internet4Gb3G = new PaqueteInternet(750, "4GB", "5GB");
            actionList.add(internet400MB3G);
            actionList.add(internet600MB3G);
            actionList.add(internet1Gb3G);
            actionList.add(internet2Gb3G);
            actionList.add(internet4Gb3G);
            PaqueteInternet pqt1GB4G = new PaqueteInternet(100, "1GB LTE", "");
            PaqueteInternet pqt2_5GB4G = new PaqueteInternet(200, "2.5GB LTE", "");
            //PaqueteInternet pqt6GB4G = new PaqueteInternet(35,"6.5GB LTE","");
            PaqueteInternet pqt10GB4G = new PaqueteInternet(1125, "14GB LTE", "");
            PaqueteInternet pqt200MB4G = new PaqueteInternet(25, "200MB LTE", "");
            actionList4G.add(pqt1GB4G);
            actionList4G.add(pqt2_5GB4G);
            //actionList4G.add(pqt6GB4G);
            actionList4G.add(pqt10GB4G);
            actionList4G.add(pqt200MB4G);
        }

        gridViewActions = (GridViewWithHeaderAndFooter) findViewById(R.id.gridview);
        set3Gor4G();
        if (sharedPreferences.getBoolean("list", true)) {
            if (is3GOr4G(navigationView.getMenu())) {
                if (actionList.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList, true, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(1);
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            } else {
                if (actionList4G.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList4G, true, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(1);
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            }
        } else {
            if (is3GOr4G(navigationView.getMenu())) {
                if (actionList.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList, false, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            } else {
                if (actionList4G.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList4G, false, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            }
        }
        if (gridViewActions.getHeaderViewCount() == 0) {
            gridViewActions.addHeaderView(initGridHeader(context));
        }
        if (adapterGridOrList != null) {
            gridViewActions.setAdapter(adapterGridOrList);
        } else {
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void getTraficoRed() {
        mStartRX = TrafficStats.getMobileRxBytes();
        mStartTX = TrafficStats.getMobileTxBytes();
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
            alert.setTitle(R.string.uh_oh);
            alert.setMessage(R.string.no_access_net_traffic_error);
            alert.show();
        } else {
            handler.postDelayed(runner, 1500);
        }
    }

    private final Runnable runner = new Runnable() {
        public void run() {
            long total = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
            long Mb = (long) (total * 0.000001);
            if (Mb >= 1024) {
                Mb = Mb / 1024;
                textViewTraficoRed.setText(getString(R.string.consumed) + Mb + " GB");
            } else {
                textViewTraficoRed.setText(getString(R.string.consumed) + Mb + " MB");
            }
            handler.postDelayed(runner, 1500);
        }
    };

    private void set3Gor4G() {
        String defaultOption = sharedPreferencesSetings.getString("red", "3G");
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            if (menuItem.isCheckable()) {
                if (menuItem.getTitle().toString().contains(defaultOption)) {
                    menuItem.setChecked(true);
                }
            }
        }
    }

    public View initGridHeader(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.grid_header, null);

        final ImageView imageView = (ImageView) v.findViewById(R.id.imageViewPaquete);
        TextView nombrePlan = (TextView) v.findViewById(R.id.label_paquete_name);
        TextView internet = (TextView) v.findViewById(R.id.internet);
        TextView bono = (TextView) v.findViewById(R.id.bono);

        imageView.setImageResource(R.drawable.internet_card);
        nombrePlan.setText(R.string.paquete_consultar);

        bono.setOnClickListener(view -> {
            GeneralUtility.makeCallUSSD(context, "*222*266#", textViewBonos, textViewDaysLeft, textViewLastTimeConsult);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.rotate);
            imageView.startAnimation(animation);
        });

        internet.setOnClickListener(view -> {
            GeneralUtility.makeCallUSSD(context, "*222*328#", textViewMegasRestantes, textViewDaysLeft, textViewLastTimeConsult);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.rotate);
            imageView.startAnimation(animation);
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConsultDialog(context, imageView);
            }
        });

        return v;
    }

    private void showDialog(String s) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(getString(R.string.dialog_etecsa_solicitud_title));
        if (s.equalsIgnoreCase("4G")) {
            builder.setMessage(getString(R.string.message_dialog_etecsa_4G));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GeneralUtility.sendSMS(context, "2266", "4GLTE");
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (s.equalsIgnoreCase("imei")) {
            builder.setMessage(getString(R.string.message_dialog_etecsa_imei));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String imei = GeneralUtility.obtenerIMEI(context);
                    GeneralUtility.sendSMS(context, "2266", imei.substring(0, 8));
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
            }
        });
        alertDialog.show();

        /*BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetRoundCorners);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_confirm, this.findViewById(R.id.bottom_confirm_dialog));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(35, 0, 35, 300);
        ConstraintLayout layout = view.findViewById(R.id.bottom_confirm_dialog);
        layout.setLayoutParams(layoutParams);

        TextView textViewTitle = view.findViewById(R.id.bottom_sheet_title);
        TextView textViewBody = view.findViewById(R.id.bottom_sheet_body);
        textViewTitle.setText(R.string.dialog_etecsa_solicitud_title);

        Button ok = view.findViewById(R.id.bottom_sheet_button_ok);
        Button cancel = view.findViewById(R.id.bottom_sheet_button_cancel);

        if(s.equalsIgnoreCase("4G")){
            textViewBody.setText(R.string.message_dialog_etecsa_4G);
            ok.setOnClickListener(view1 -> {
                checkAndRequestPermisions();
                bottomSheetDialog.dismiss();
            });
            cancel.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        }else if(s.equalsIgnoreCase("imei")){
            textViewBody.setText(R.string.message_dialog_etecsa_imei);
            ok.setOnClickListener(view1 -> {
                checkAndRequestPermisions();
                bottomSheetDialog.dismiss();
            });
            cancel.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        }

        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            ok.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
            cancel.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();*/
    }

    private void showAs(String as) {
        if (as.equalsIgnoreCase("list")) {
            if (is3GOr4G(navigationView.getMenu())) {
                if (actionList.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList, true, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(1);
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            } else {
                if (actionList4G.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList4G, true, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(1);
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            }
        } else if (as.equalsIgnoreCase("grid")) {
            if (is3GOr4G(navigationView.getMenu())) {
                if (actionList.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList, false, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            } else {
                if (actionList4G.isEmpty()) {
                    gridViewActions.setVisibility(View.GONE);
                    textViewNoDataGrid.setVisibility(View.VISIBLE);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList4G, false, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setVisibility(View.VISIBLE);
                    textViewNoDataGrid.setVisibility(View.GONE);
                }
            }
        }
        if (adapterGridOrList != null) {
            gridViewActions.setAdapter(adapterGridOrList);
        } else {
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean is3GOr4G(Menu menu) {
        boolean is3G = true;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isCheckable()) {
                if (menuItem.isChecked()) {
                    if (menuItem.getTitle().toString().contains("3G")) {
                        is3G = true;
                    } else if (menuItem.getTitle().toString().contains("4G")) {
                        is3G = false;
                    }
                }
            }
        }
        return is3G;
    }

    public void createConsultDialog(final Context context, final ImageView imageView) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(getString(R.string.select));
        builder.setPositiveButton(getString(R.string.paquete_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GeneralUtility.makeCallUSSD(context, "*222*328#", textViewMegasRestantes, textViewDaysLeft, textViewLastTimeConsult);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.rotate);
                imageView.startAnimation(animation);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.paquete_nacional), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GeneralUtility.makeCallUSSD(context, "*222*266#", textViewBonos, textViewDaysLeft, textViewLastTimeConsult);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.rotate);
                imageView.startAnimation(animation);
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
            }
        });
        alertDialog.show();

        /*BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetRoundCorners);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_confirm, this.findViewById(R.id.bottom_confirm_dialog));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(35, 0, 35, 300);
        ConstraintLayout layout = view.findViewById(R.id.bottom_confirm_dialog);
        layout.setLayoutParams(layoutParams);

        TextView textViewTitle = view.findViewById(R.id.bottom_sheet_title);
        TextView textViewBody = view.findViewById(R.id.bottom_sheet_body);

        textViewTitle.setText(R.string.select);
        textViewBody.setText("");

        Button ok = view.findViewById(R.id.bottom_sheet_button_ok);
        Button cancel = view.findViewById(R.id.bottom_sheet_button_cancel);

        ok.setText(getString(R.string.paquete_internet));
        ok.setOnClickListener(view1 -> {
            GeneralUtility.makeCallUSSD(context,"*222*328#", textViewMegasRestantes,textViewDaysLeft,textViewLastTimeConsult);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.rotate);
            imageView.startAnimation(animation);
            bottomSheetDialog.dismiss();
        });
        cancel.setText(getString(R.string.paquete_nacional));
        cancel.setOnClickListener(view1 -> {
            GeneralUtility.makeCallUSSD(context,"*222*266#", textViewBonos,textViewDaysLeft,textViewLastTimeConsult);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.rotate);
            imageView.startAnimation(animation);
            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            ok.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
            cancel.setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();*/
    }

    public void createTarifaDialog(final Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.confirm_tarifa_consumo_dialog);
        builder.setIcon(R.drawable.tarifa_por_consumo);
        String body = context.getString(R.string.confirm_tarifa_consumo_dialog_body_part);
        builder.setMessage(body);
        builder.setPositiveButton(context.getString(R.string.activate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GeneralUtility.makeCallUSSD(context, USSDCode.switchTarifa(true));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(context.getString(R.string.desactivate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GeneralUtility.makeCallUSSD(context, USSDCode.switchTarifa(false));
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
            }
        });
        alertDialog.show();
    }

    public void createBolsaDialog(final Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.confirm_tarifa_consumo_dialog);
        builder.setIcon(R.drawable.bolsa_nauta);
        String body = String.format(context.getString(R.string.confirm_paquete_dialog_body_part1), "Bolsa Nauta") + String.format(context.getString(R.string.confirm_paquete_dialog_body_part2), 1);
        builder.setMessage(body);
        builder.setPositiveButton(context.getString(R.string.action_paquete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Historial historial = new Historial();
                historial.setId_paquete(1);
                historial.setPaquete("Bolsa Nauta");
                historial.setDate(new Date().getTime());
                historial.setMonth_name(GeneralUtility.getMonthName(new Date().getMonth(), context));
                GeneralUtility.makeCallUSSD(context, USSDCode.buy(0), historial);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorDialogButton));
            }
        });
        alertDialog.show();
    }

    public void showDialogRecargarSaldo(final Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.recargar_saldo_dialog, null);
        builder.setView(v);
        final TextInputLayout textInputLayoutRecargar = (TextInputLayout) v.findViewById(R.id.textLayoutRecargar);
        final EditText editTextRecargar = v.findViewById(R.id.editTextRecargar);
        final TextView textViewRecargar = v.findViewById(R.id.textViewActionRecargar);
        Button btnCod = v.findViewById(R.id.btn_code);
        Button btnBr = v.findViewById(R.id.btn_br_code);

        btnCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayoutRecargar.setVisibility(View.VISIBLE);
                textViewRecargar.setVisibility(View.VISIBLE);
            }
        });

        btnBr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
                textInputLayoutRecargar.setVisibility(View.VISIBLE);
                textViewRecargar.setVisibility(View.VISIBLE);
            }
        });

        textViewRecargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextRecargar.getText().toString();
                if (!code.isEmpty()) {
                    String ussd = "*662*" + code + "#";
                    if (GeneralUtility.validatePermiso(context, Manifest.permission.CALL_PHONE)) {
                        GeneralUtility.makeCallUSSD(context, ussd);
                    } else {
                        checkAndRequestPermisions();
                    }
                } else {
                    textInputLayoutRecargar.setError(getString(R.string.inserte_cod_recarga));
                }
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            if (sharedPreferences.getBoolean("list", true)) {
                item.setIcon(R.drawable.ic_list);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("list", false);
                editor.commit();
                editor.apply();
                showAs("grid");
            } else {
                item.setIcon(R.drawable.ic_grid);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("list", true);
                editor.commit();
                editor.apply();
                showAs("list");
            }
        } else if (id == R.id.action_solicitar_4G) {
            showDialog("4G");
        } else if (id == R.id.action_verificar_bandas) {
            showDialog("imei");
        } else if (id == R.id.action_settings) {
            GeneralUtility.navigate(context, SettingsActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.pqt_3g) {
            setPaqt3Gor4G("3");
        } else if (id == R.id.pqt_4g) {
            setPaqt3Gor4G("4");
        } else if (id == R.id.check_balance) {
            GeneralUtility.makeCallUSSD(context, "*222#", textViewNoDataGrid, textViewDaysLeft, textViewLastTimeConsult);
        } else if (id == R.id.tarifa) {
            createTarifaDialog(context);
        } else if (id == R.id.bolsa_nauta) {
            createBolsaDialog(context);
        }/* else if(id==R.id.action_share){
            GeneralUtility.shareAPK(this);
        }*/ else if (id == R.id.action_history) {
            GeneralUtility.navigate(context, HistorialActivity.class);
        } else if (id == R.id.about) {
            GeneralUtility.navigate(context, AboutActivity.class);
        } else if (id == R.id.action_recargar_saldo) {
            showDialogRecargarSaldo(context);
        } else if (id == R.id.donate) {
            GeneralUtility.navigate(context, DonateActivity.class);
        }/*else if(id==R.id.action_verificar_bandas){
            showDialog("imei");
        }else if(id==R.id.action_settings){
            GeneralUtility.navigate(context,SettingsActivity.class);
        }*/

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setPaqt3Gor4G(String pqt) {
        if (pqt.equals("3")) {
            if (!actionList.isEmpty()) {
                if (sharedPreferences.getBoolean("list", true)) {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList, true, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(1);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList, false, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                }
                if (gridViewActions.getHeaderViewCount() == 0) {
                    gridViewActions.addHeaderView(initGridHeader(context));
                }
                gridViewActions.setAdapter(adapterGridOrList);
                gridViewActions.setVisibility(View.VISIBLE);
                textViewNoDataGrid.setVisibility(View.GONE);
            } else {
                gridViewActions.setVisibility(View.GONE);
                textViewNoDataGrid.setVisibility(View.VISIBLE);
            }
        } else {
            if (!actionList4G.isEmpty()) {
                if (sharedPreferences.getBoolean("list", true)) {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList4G, true, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(1);
                } else {
                    adapterGridOrList = new PaqueteInternetAdapterGridOrList(this, context, actionList4G, false, Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                    gridViewActions.setNumColumns(Integer.parseInt(sharedPreferencesSetings.getString("row_list", "2")));
                }
                if (gridViewActions.getHeaderViewCount() == 0) {
                    gridViewActions.addHeaderView(initGridHeader(context));
                }
                gridViewActions.setAdapter(adapterGridOrList);
                gridViewActions.setVisibility(View.VISIBLE);
                textViewNoDataGrid.setVisibility(View.GONE);
            } else {
                gridViewActions.setVisibility(View.GONE);
                textViewNoDataGrid.setVisibility(View.VISIBLE);
            }
        }
    }

}
