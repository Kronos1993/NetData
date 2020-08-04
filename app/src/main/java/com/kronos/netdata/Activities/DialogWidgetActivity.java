package com.kronos.netdata.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kronos.netdata.DB.Connection;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.PaqueteInternet;
import com.kronos.netdata.Domain.USSDCode;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;
import com.kronos.netdata.Widget.NetDataWidgetProvider;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogWidgetActivity extends AppCompatActivity {

    Context context = this;
    TextView body;
    Button ok,no;
    PaqueteInternet paqueteInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_widget);

        paqueteInternet = GeneralUtility.getPaqueteById(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("widget_paquete","8"));
        String body_text = String.format(context.getString(R.string.confirm_paquete_dialog_body_part1),paqueteInternet.getPaquete())+String.format(context.getString(R.string.confirm_paquete_dialog_body_part2),paqueteInternet.getId());

        body = findViewById(R.id.text_view_body);
        body.setText(body_text);
        ok = findViewById(R.id.widget_dialog_ok);
        ok.setOnClickListener(view -> executeUssd());
        no = findViewById(R.id.widget_dialog_no);
        no.setOnClickListener(view->onBackPressed());

    }

    private void goToMain() {
        GeneralUtility.navigate(context,MainActivityDrawer.class);
    }

    public void executeUssd(){
        String code = USSDCode.buy(paqueteInternet.getId());
        makeCallUSSD(context, code);
    }

    private void makeCallUSSD(final Context context, String code) {

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
                        Toast.makeText(context, sresponse, Toast.LENGTH_LONG).show();
                        GeneralUtility.updateWidget(getApplicationContext());
                        onBackPressed();
                    }
                    @Override
                    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                        Toast.makeText(context, "La consulta a fallado", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                },new Handler());
            }else{
                String USSD = Uri.encode(code);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + USSD));
                context.startActivity(intent);
                Log.i(NetDataWidgetProvider.WIDGETTAG, "Paquete comprado!");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMain();
    }
}
