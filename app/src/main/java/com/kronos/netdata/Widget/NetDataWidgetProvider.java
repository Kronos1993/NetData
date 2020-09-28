package com.kronos.netdata.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.kronos.netdata.Domain.PaqueteInternet;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;

public class NetDataWidgetProvider extends AppWidgetProvider {

	public static final String WIDGETTAG = "NetDataWidget";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		ComponentName componentName = new ComponentName(context,NetDataWidgetProvider.class);

	    Log.i(WIDGETTAG, "onUpdate");

	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(componentName);


	    GeneralUtility.setTheme(PreferenceManager.getDefaultSharedPreferences(context));

		/*final int N = appWidgetIds.length;*/

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i=0; i<allWidgetIds.length; i++) {
			int appWidgetId = allWidgetIds[i];


			Log.i(WIDGETTAG, "updating widget[id] " + appWidgetId);

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);
			updateWidgetSize(bundle,views);

		    Intent intentInternet = new Intent(context, NetDataWidgetService.class);
			intentInternet.setAction(NetDataWidgetService.INTERNET);
			intentInternet.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		    PendingIntent pendingIntentInternet = PendingIntent.getService(context, 0, intentInternet, 0);

			Intent intentBonos = new Intent(context, NetDataWidgetService.class);
			intentBonos.setAction(NetDataWidgetService.BONOS);
			intentBonos.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			PendingIntent pendingIntentBonos = PendingIntent.getService(context, 0, intentBonos, 0);

			Intent intentPaquete = new Intent(context, NetDataWidgetService.class);
			intentPaquete.setAction(NetDataWidgetService.PAQUETE_INTERNET);
			intentPaquete.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			PendingIntent pendingIntentPaquete = PendingIntent.getService(context, 0, intentPaquete, 0);

			Intent intentSaldo = new Intent(context, NetDataWidgetService.class);
			intentSaldo.setAction(NetDataWidgetService.SALDO);
			intentSaldo.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			PendingIntent pendingIntentSaldo = PendingIntent.getService(context, 0, intentSaldo, 0);

			views.setOnClickPendingIntent(R.id.widget_check_internet, pendingIntentInternet);

			views.setOnClickPendingIntent(R.id.widget_check_bonos, pendingIntentBonos);

			views.setOnClickPendingIntent(R.id.widget_check_saldo, pendingIntentSaldo);

			PaqueteInternet paqueteInternet = GeneralUtility.getPaqueteById(PreferenceManager.getDefaultSharedPreferences(context).getString("widget_paquete","8"));
			views.setTextViewText(R.id.text_view_pqt_name, paqueteInternet.getPaquete());
			views.setOnClickPendingIntent(R.id.widget_buy_pqt, pendingIntentPaquete);

			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
				views.setTextViewText(R.id.text_view_paquete_widget, PreferenceManager.getDefaultSharedPreferences(context).getString("megas",context.getString(R.string.package_not_consult)));
				views.setTextViewText(R.id.text_view_bono_widget, PreferenceManager.getDefaultSharedPreferences(context).getString("bonos",context.getString(R.string.bono_not_consult)));
				views.setTextViewText(R.id.text_view_days_left_widget,String.format(context.getString(R.string.drawer_days_left),PreferenceManager.getDefaultSharedPreferences(context).getString("days",context.getString(R.string.days_left_not_consult))));
			}else{
				views.setViewVisibility(R.id.text_view_paquete_widget, View.GONE);
				views.setViewVisibility(R.id.text_view_bono_widget, View.GONE);
                views.setViewVisibility(R.id.text_view_days_left_widget,View.GONE);
            }


			Log.i(WIDGETTAG, "pending intent set");
		    
		    // Tell the AppWidgetManager to perform an update on the current App Widget
		    appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_layout);

		updateWidgetSize(newOptions,views);

		appWidgetManager.updateAppWidget(appWidgetId,views);

	}

	private void updateWidgetSize(Bundle bundle,RemoteViews views){
		int minWidth = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
		int minHeight = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
		int maxWidth = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
		int maxHeight = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

		if (maxHeight>84){
			views.setViewVisibility(R.id.layout_hide,View.VISIBLE);
		}else{
			views.setViewVisibility(R.id.layout_hide,View.GONE);
		}
	}
}
