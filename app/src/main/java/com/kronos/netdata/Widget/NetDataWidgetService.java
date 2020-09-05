package com.kronos.netdata.Widget;

import android.Manifest;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.kronos.netdata.Activities.DialogWidgetActivity;
import com.kronos.netdata.Activities.Notifications.NetDataNotification;
import com.kronos.netdata.Activities.Notifications.NotificationsId;
import com.kronos.netdata.DB.Connection;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.PaqueteInternet;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NetDataWidgetService extends Service {

	public static final String INTERNET = "INTERNET";
	public static final String BONOS = "BONOS";
	public static final String PAQUETE_INTERNET = "PAQUETE_INTERNET";
	public static final String SALDO = "SALDO";

	private RemoteViews views;

	public NetDataWidgetService() {
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStart(intent, startId);

		Log.i(NetDataWidgetProvider.WIDGETTAG, "onStartCommand");

		executeUssd(intent);

		stopSelf(startId);

		return START_STICKY;
	}

	private void executeUssd(Intent intent) {
		Log.i(NetDataWidgetProvider.WIDGETTAG, "This is the intent " + intent);

		views = new RemoteViews(this.getPackageName(), R.layout.widget_layout);

		if (intent != null) {
			String requestedAction = intent.getAction();
			Log.i(NetDataWidgetProvider.WIDGETTAG, "This is the action " + requestedAction);
			if (requestedAction != null && requestedAction.equals(INTERNET)) {

				makeCallUSSD(getApplicationContext(), "*222*328#", INTERNET);
				int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
				Log.i(NetDataWidgetProvider.WIDGETTAG, "Se selecciono " + INTERNET + widgetId);

				AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
				appWidgetMan.updateAppWidget(widgetId, views);

			} else if (requestedAction != null && requestedAction.equals(BONOS)) {

				makeCallUSSD(getApplicationContext(), "*222*266#", BONOS);
				int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
				Log.i(NetDataWidgetProvider.WIDGETTAG, "Se selecciono " + BONOS + widgetId);

				AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
				appWidgetMan.updateAppWidget(widgetId, views);

			} else if (requestedAction != null && requestedAction.equals(PAQUETE_INTERNET)) {

				/*PaqueteInternet paqueteInternet = GeneralUtility.getPaqueteById(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("widget_paquete","8"));
				String code = USSDCode.confitmToBuy(paqueteInternet.getId());

				makeCallUSSD(getApplicationContext(), code, PAQUETE_INTERNET);
				int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
				Log.i(NetDataWidgetProvider.WIDGETTAG, "Se selecciono " + PAQUETE_INTERNET + widgetId);

				AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
				appWidgetMan.updateAppWidget(widgetId, views);*/
				Intent intentPaquete = new Intent(getApplicationContext(), DialogWidgetActivity.class);
				intentPaquete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(intentPaquete);

			} else if (requestedAction != null && requestedAction.equals(SALDO)) {
				makeCallUSSD(getApplicationContext(), "*222#", SALDO);
				int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
				Log.i(NetDataWidgetProvider.WIDGETTAG, "Se selecciono " + SALDO + widgetId);

				AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
				appWidgetMan.updateAppWidget(widgetId, views);
			}
        }
	}

	private void makeCallUSSD(final Context context, String code, final String action) {

		final SharedPreferences sharedPreferencesSetings = PreferenceManager.getDefaultSharedPreferences(context);

		if(action.equals(INTERNET) || action.equals(BONOS)){
			Date date = Calendar.getInstance().getTime();
/*			SimpleDateFormat formatter = new SimpleDateFormat();
			formatter.applyPattern("dd/MMM/yyyy");
			String last_consult = formatter.format(date);*/
			sharedPreferencesSetings.edit().putLong("last_consult",date.getTime()).apply();
		}

		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(context, "Permiso no diponible", Toast.LENGTH_LONG).show();
		} else {
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				telephonyManager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
					@Override
					public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
						super.onReceiveUssdResponse(telephonyManager, request, response);
						String responseUssd = "";
						String daysLeft;
                        /*double current = Double.parseDouble(sharedPreferencesSetings.getString("megas","0"));
                        double lastConsult;*/
						String sresponse=String.valueOf(response);
						if(action.equals(INTERNET)){
							if(sresponse.contains("Paquetes: ")){
								if(sresponse.contains("validos")){
									responseUssd = sresponse.substring(sresponse.indexOf("Paquetes: ")+10,sresponse.indexOf("validos"));
									responseUssd = responseUssd.replace("solo","");
									daysLeft = sresponse.substring(sresponse.indexOf("validos ")+8,sresponse.indexOf("dia"));
									sharedPreferencesSetings.edit().putString("megas",responseUssd).apply();
									sharedPreferencesSetings.edit().putString("days",daysLeft).apply();
								}else if(sresponse.contains("no activos")){
									responseUssd = sresponse.substring(sresponse.indexOf("Paquetes: ")+10,sresponse.indexOf("no activos"));
									responseUssd = responseUssd.replace("solo","");
									sharedPreferencesSetings.edit().putString("megas",responseUssd).apply();
								}
								views.setTextViewText(R.id.text_view_paquete_widget, responseUssd);
								Log.i(NetDataWidgetProvider.WIDGETTAG, "Internet consultado!");
								NetDataNotification.createNotification("Internet",sresponse, NotificationsId.internet,context);
							}
						}else if(action.equals(BONOS)){
							if(sresponse.contains("Bono:LTE ")){
								if(sresponse.contains("vence")){
									responseUssd = sresponse.substring(sresponse.indexOf("Bono: LTE ")+10,sresponse.indexOf("vence"));
									sharedPreferencesSetings.edit().putString("bonos",responseUssd).apply();
								}else{
									responseUssd = sresponse.substring(sresponse.indexOf("Bono: LTE ")+10,sresponse.length());
									sharedPreferencesSetings.edit().putString("bonos",responseUssd).apply();
								}
								NetDataNotification.createNotification("Bono LTE",sresponse, NotificationsId.bono,context);
							}else if(sresponse.contains("Datos.cu")){
								responseUssd = sresponse.substring(sresponse.indexOf("Datos.cu ")+9,sresponse.length());
								sharedPreferencesSetings.edit().putString("bonos",responseUssd).apply();
								NetDataNotification.createNotification("Datos.cu",sresponse, NotificationsId.bono,context);
							}else if(sresponse.contains("Usted no dispone de bonos activos")){
								sharedPreferencesSetings.edit().putString("bonos","Sin bonos").apply();
								NetDataNotification.createNotification("Sin bonos",sresponse, NotificationsId.bono,context);
							}
							views.setTextViewText(R.id.text_view_bono_widget, responseUssd);
							Log.i(NetDataWidgetProvider.WIDGETTAG, "Bono consultado!");
						}else if (action.equals(PAQUETE_INTERNET)){
							if(!sresponse.toLowerCase().contains("saldo insuficiente") && !sresponse.toLowerCase().contains("error")){
								PaqueteInternet paqueteInternet = GeneralUtility.getPaqueteById(androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("widget_paquete","8"));
								try {
									Historial historial = new Historial();
									historial.setId_paquete(paqueteInternet.getId());
									historial.setPaquete(paqueteInternet.getPaquete());
									historial.setDate(new Date().getTime());
									historial.setMonth_name(GeneralUtility.getMonthName(new Date().getMonth()+1,context));
									Connection.getConnection(context).getHistorialDao().create(historial);
									Log.i(NetDataWidgetProvider.WIDGETTAG, "Paquete comprado!");
								} catch (SQLException e) {
									e.printStackTrace();
									Log.i(NetDataWidgetProvider.WIDGETTAG,  context.getString(R.string.error_bd_create_history));
									Toast.makeText(context, R.string.error_bd_create_history,Toast.LENGTH_LONG).show();
								}
							}
						}else if (action.equals(SALDO)){
							NetDataNotification.createNotification("Saldo",sresponse, NotificationsId.saldo,context);
						}
						Toast.makeText(context, sresponse, Toast.LENGTH_LONG).show();
						GeneralUtility.updateWidget(getApplicationContext());
					}

					@Override
					public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
						super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
						NetDataNotification.createNotification("Error",request, NotificationsId.error,context);
						Toast.makeText(context, "La consulta a fallado", Toast.LENGTH_LONG).show();
					}
				},new Handler());
			}else{
				String USSD = Uri.encode(code);
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + USSD));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				Log.i(NetDataWidgetProvider.WIDGETTAG, "Saldo consultado!");
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
