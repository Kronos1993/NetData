package com.kronos.netdata.Util;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kronos.netdata.Activities.Notifications.NetDataNotification;
import com.kronos.netdata.Activities.Notifications.NotificationsId;
import com.kronos.netdata.DB.Connection;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.PaqueteInternet;
import com.kronos.netdata.R;
import com.kronos.netdata.Widget.NetDataWidgetProvider;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marcos Octavio on 22/01/2017.
 */
public class GeneralUtility {


    /*Generic method for navigate from any class to anywhere*/
    public static void navigate(Context context, Class clazz) {
        Intent intentHome = new Intent(context, clazz);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intentHome);
    }

    public static void navigateWithExtra(Context context, Class clazz, String key, String extra) {
        Intent intentHome = new Intent(context, clazz);
        intentHome.putExtra(key, extra);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intentHome);
    }

    public static void navigateWithExtra(Context context, Class clazz, Bundle extra) {
        Intent intentHome = new Intent(context, clazz);
        intentHome.putExtras(extra);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intentHome);
    }

    public static void navigateFlagNewTask(Context context, Class clazz) {
        Intent intentHome = new Intent(context, clazz);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentHome);
    }

    public static void rateApp(Context context, View view) {
        try{
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://id?" + context.getPackageName())));
        }catch (Exception e){
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/sttore/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void sendSMS(Context context, String number, String sms) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, sms, null, null);
    }

    //set response in the view
    public static void makeCallUSSD(final Context context, final String code, final TextView view, final TextView textViewDays, final TextView consultDate) {

        final SharedPreferences sharedPreferencesSetings = PreferenceManager.getDefaultSharedPreferences(context);

        Date date = Calendar.getInstance().getTime();
/*        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("dd/MMM/yyyy");
        String last_consult = formatter.format(date);*/
        consultDate.setText(R.string.last_time_check_today);

        sharedPreferencesSetings.edit().putLong("last_consult",date.getTime()).apply();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, R.string.permiso_no_obtenido, Toast.LENGTH_LONG).show();
        } else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
                    @Override
                    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                        super.onReceiveUssdResponse(telephonyManager, request, response);
                        String responseUssd;
                        String daysLeft;
                        /*double current = Double.parseDouble(sharedPreferencesSetings.getString("megas","0"));
                        double lastConsult;*/
                        String sresponse=String.valueOf(response);
                        if(view!=null){
                            if(sresponse.contains("Paquetes: ")){
                                if(sresponse.contains("Paquetes: ") && sresponse.contains("validos")){
                                    responseUssd = sresponse.substring(sresponse.indexOf("Paquetes: ")+10,sresponse.indexOf("validos"));
                                    responseUssd = responseUssd.replace("solo","");
                                    daysLeft = sresponse.substring(sresponse.indexOf("validos ")+8,sresponse.indexOf("dia"));
                                    if(view.getId()==R.id.textViewMegas){
                                        view.setText(String.format(context.getString(R.string.drawer_packages),responseUssd));
                                        sharedPreferencesSetings.edit().putString("megas",responseUssd).apply();
                                    }
                                    if (textViewDays!=null && textViewDays.getId()==R.id.textViewDaysLeft){
                                        textViewDays.setText(String.format(context.getString(R.string.drawer_days_left),daysLeft));
                                        sharedPreferencesSetings.edit().putString("days",daysLeft).apply();
                                    }
                                }else if(sresponse.contains("Paquetes: ") && sresponse.contains("no activos")){
                                    responseUssd = sresponse.substring(sresponse.indexOf("Paquetes: ")+10,sresponse.indexOf("no activos"));
                                    responseUssd = responseUssd.replace("solo","");
                                    if(view.getId()==R.id.textViewMegas){
                                        view.setText(String.format(context.getString(R.string.drawer_packages),responseUssd));
                                        sharedPreferencesSetings.edit().putString("megas",responseUssd).apply();
                                    }
                                }else if (sresponse.contains("Paquetes: ") && sresponse.contains("vencen hoy")){
                                    responseUssd = sresponse.substring(sresponse.indexOf("Paquetes: ")+10,sresponse.indexOf("vencen"));
                                    daysLeft = "0(Adquiera otro paquete)";
                                    if(view.getId()==R.id.textViewMegas){
                                        view.setText(String.format(context.getString(R.string.drawer_packages),responseUssd));
                                        sharedPreferencesSetings.edit().putString("megas",responseUssd).apply();
                                    }
                                    if (textViewDays!=null && textViewDays.getId()==R.id.textViewDaysLeft){
                                        textViewDays.setText(String.format(context.getString(R.string.drawer_days_left),daysLeft));
                                        sharedPreferencesSetings.edit().putString("days",daysLeft).apply();
                                    }
                                }
                                //NetDataNotification.createNotification("Internet",sresponse, NotificationsId.internet,context);
                            }
                            else if(sresponse.contains("Bono:LTE ")){
                                if(sresponse.contains("vence")){
                                    responseUssd = sresponse.substring(sresponse.indexOf("Bono: LTE ")+10,sresponse.indexOf("vence"));
                                    if(view.getId()==R.id.textViewMegasBonos){
                                        view.setText(responseUssd);
                                        sharedPreferencesSetings.edit().putString("bonos",responseUssd).apply();
                                    }
                                }else{
                                    responseUssd = sresponse.substring(sresponse.indexOf("Bono: LTE ")+10,sresponse.length());
                                    if(view.getId()==R.id.textViewMegasBonos){
                                        view.setText(String.format(context.getString(R.string.drawer_bonos),responseUssd));
                                        sharedPreferencesSetings.edit().putString("bonos",responseUssd).apply();
                                    }
                                }
                                //NetDataNotification.createNotification("Bonos",sresponse, NotificationsId.bono,context);
                            }
                            else if(sresponse.contains("Datos.cu")){
                                responseUssd = sresponse.substring(sresponse.indexOf("Datos.cu ")+9,sresponse.length());
                                if(view.getId()==R.id.textViewMegasBonos){
                                    view.setText(String.format(context.getString(R.string.drawer_bonos),responseUssd));
                                    sharedPreferencesSetings.edit().putString("bonos",responseUssd).apply();
                                }
                                //NetDataNotification.createNotification("Datos.cu",sresponse, NotificationsId.bono,context);
                            }else if(sresponse.contains("Usted no dispone de bonos activos")){
                                if(view.getId()==R.id.textViewMegasBonos){
                                    view.setText(R.string.no_bonus);
                                    sharedPreferencesSetings.edit().putString("bonos","Sin bonos").apply();
                                }
                            }
                            final Snackbar snackbar = Snackbar.make(view,sresponse, BaseTransientBottomBar.LENGTH_INDEFINITE);
                            View snackView = snackbar.getView();
                            TextView snackTextView = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                            snackTextView.setMaxLines(4);
                            snackbar.setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            }).show();
                        }else{
                            NetDataNotification.createNotification(context.getString(R.string.ussd_consult_completed),sresponse, NotificationsId.bono,context);
                            Toast.makeText(context, sresponse, Toast.LENGTH_LONG).show();
                        }
                        updateWidget(context);
                    }

                    @Override
                    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                        NetDataNotification.createNotification(context.getString(R.string.error_title),request, NotificationsId.error,context);
                        final Snackbar snackbar = Snackbar.make(view, R.string.ussd_consult_fail, BaseTransientBottomBar.LENGTH_INDEFINITE);
                        View snackView = snackbar.getView();
                        TextView snackTextView = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                        snackTextView.setMaxLines(4);
                        snackbar.setAction(R.string.snack_bar_retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view1) {
                                snackbar.dismiss();
                                makeCallUSSD(context,code,view,textViewDays,consultDate);
                            }
                        }).show();
                    }
                },new Handler());
            }else{
                String USSD = Uri.encode(code);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + USSD));
                context.startActivity(intent);
            }
        }
    }

    //save history if response is correct
    public static void makeCallUSSD(final Context context, String code, final Historial historial) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permiso no diponible", Toast.LENGTH_LONG).show();
        } else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
                    @Override
                    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                        super.onReceiveUssdResponse(telephonyManager, request, response);
                        String sresponse=String.valueOf(response);
                        if(!sresponse.toLowerCase().contains("saldo insuficiente") && !sresponse.toLowerCase().contains("error")){
                            try {
                                Connection.getConnection(context).getHistorialDao().create(historial);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Toast.makeText(context, R.string.error_bd_create_history,Toast.LENGTH_LONG).show();
                            }
                        }
                        Toast.makeText(context, sresponse, Toast.LENGTH_LONG).show();
                        NetDataNotification.createNotification("Compra",sresponse, NotificationsId.paquete_buy,context);
                    }

                    @Override
                    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    }
                },new Handler());
            }else{
                String USSD = Uri.encode(code);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + USSD));
                context.startActivity(intent);
            }
        }
    }

    //only make ussd call
    public static void makeCallUSSD(final Context context, String code) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permiso no diponible", Toast.LENGTH_LONG).show();
        } else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
                    @Override
                    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                        super.onReceiveUssdResponse(telephonyManager, request, response);
                        String sresponse=String.valueOf(response);
                        NetDataNotification.createNotification("Acción completada",sresponse, NotificationsId.transferencia_saldo,context);
                    }

                    @Override
                    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                        NetDataNotification.createNotification("Error",request, NotificationsId.error,context);
                    }
                },new Handler());
            }else{
                String USSD = Uri.encode(code);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + USSD));
                context.startActivity(intent);
            }
        }
    }

    public static void setTheme(SharedPreferences sharedPreferencesSetings) {
        String theme = sharedPreferencesSetings.getString("theme", "MODE_NIGHT_FOLLOW_SYSTEM");
        if (theme.equalsIgnoreCase("MODE_NIGHT_NO")) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme.equalsIgnoreCase("MODE_NIGHT_YES")) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (theme.equalsIgnoreCase("MODE_NIGHT_FOLLOW_SYSTEM")) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (theme.equalsIgnoreCase("MODE_NIGHT_AUTO_BATTERY")) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }
    }

    public static ArrayList<Historial> getHistorialsByMonth(String currentmonth, ArrayList<Historial> historials) {
        ArrayList<Historial> currentHistorials=new ArrayList<>();
        for(Historial current:historials){
            if(current.getMonth_name().toLowerCase().equals(currentmonth.toLowerCase())){
                currentHistorials.add(current);
            }
        }
        return currentHistorials;
    }

    public static void updateWidget(Context context){
        Intent intent = new Intent(context, NetDataWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context,NetDataWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }

    public static void shareAPK(Activity activity) {
        PackageManager packageManager = activity.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = activity.getApplicationContext().getApplicationInfo();
            String filePath = applicationInfo.sourceDir;
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("*/*");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            activity.startActivity(Intent.createChooser(sendIntent, "Compartir mediante:"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean validatePermiso(Context context, String perm) {
        boolean permiso_granted = false;
        if (ActivityCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
            permiso_granted = true;
        }
        return permiso_granted;
    }

    public static String obtenerIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return telephonyManager.getDeviceId();
        }
        return telephonyManager.getDeviceId();
    }

    public static String getVersionName(Context ctx) {
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMonthName(int month, Context context) {
        String month_name="";
        switch (month){
            case 1:month_name=context.getString(R.string.month_january);break;
            case 2:month_name=context.getString(R.string.month_february);break;
            case 3:month_name=context.getString(R.string.month_march);break;
            case 4:month_name=context.getString(R.string.month_april);break;
            case 5:month_name=context.getString(R.string.month_may);break;
            case 6:month_name=context.getString(R.string.month_june);break;
            case 7:month_name=context.getString(R.string.month_july);break;
            case 8:month_name=context.getString(R.string.month_august);break;
            case 9:month_name=context.getString(R.string.month_september);break;
            case 10:month_name=context.getString(R.string.month_october);break;
            case 11:month_name=context.getString(R.string.month_november);break;
            case 12:month_name=context.getString(R.string.month_december);break;
        }
        return month_name;
    }

    public static int getMonthNumbre(String month) {
        int month_name=1;
        switch (month.toLowerCase()){
            case "enero":month_name=1;break;
            case "febrero":month_name=2;break;
            case "marzo":month_name=3;break;
            case "abril":month_name=4;break;
            case "mayo":month_name=5;break;
            case "junio":month_name=6;break;
            case "julio":month_name=7;break;
            case "agosto":month_name=8;break;
            case "septiembre":month_name=9;break;
            case "octubre":month_name=10;break;
            case "noviembre":month_name=11;break;
            case "diciembre":month_name=12;break;
        }
        return month_name;
    }

    public static PaqueteInternet getPaqueteById(String widget_paquete) {
        PaqueteInternet paqueteInternet = new PaqueteInternet();
        switch (Integer.valueOf(widget_paquete)){
            case 5: paqueteInternet = new PaqueteInternet(5,"400MB","500MB");break;
            case 7: paqueteInternet = new PaqueteInternet(7,"600MB","800");break;
            case 10: paqueteInternet = new PaqueteInternet(10,"1GB","1.5GB");break;
            case 20: paqueteInternet = new PaqueteInternet(20,"2.5GB","3GB");break;
            case 30: paqueteInternet = new PaqueteInternet(30,"4GB","5GB");break;
            case 4: paqueteInternet = new PaqueteInternet(4,"1GB LTE","");break;
            case 8: paqueteInternet = new PaqueteInternet(8,"2.5GB LTE","");break;
            case 45: paqueteInternet = new PaqueteInternet(4,"14GB LTE","");break;
        }
        return paqueteInternet;
    }
}
