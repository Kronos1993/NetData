package com.kronos.netdata.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.kronos.netdata.Domain.USSDCode;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;

public class DonateActivity extends AppCompatActivity {

    private Context context = this;

    private TextInputLayout textInputLayoutCafe,textInputLayoutClave;
    private EditText editTextCafe,editTextClave;
    private TextView textViewAccountNumber;
    private Button buttonDonate,buttonCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GeneralUtility.setTheme(PreferenceManager.getDefaultSharedPreferences(context));

        initViews();

    }

    public void initViews(){
        textInputLayoutCafe = findViewById(R.id.textInputLayoutCafe);
        textInputLayoutClave = findViewById(R.id.textInputLayoutClave);
        editTextCafe = findViewById(R.id.editTextCafe);
        editTextClave = findViewById(R.id.editTextClave);
        textViewAccountNumber = findViewById(R.id.textViewAccountNumber);
        buttonCopy = findViewById(R.id.buttonCopy);
        buttonDonate = findViewById(R.id.buttonDonate);

        buttonDonate.setOnClickListener(view -> {
            boolean validCafe = validateCafe();
            boolean validKey = validateClave();
            if(validCafe && validKey){
                showConfirmDialog();
            }
        });

        buttonCopy.setOnClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Cuenta",textViewAccountNumber.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, R.string.text_copied, Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateClave() {
        boolean valid = false;
        if (editTextClave.getText().toString().length()==4){
            try {
                Integer value = Integer.valueOf(editTextClave.getText().toString());
                if (value instanceof Integer){
                    valid = true;
                    textInputLayoutClave.setError(null);
                }
                else
                    textInputLayoutClave.setError(getString(R.string.only_int_number));
            }catch (Exception e){
                e.printStackTrace();
                textInputLayoutClave.setError(getString(R.string.only_int_number));
            }
        }else if(editTextClave.getText().toString().length()<4){
            textInputLayoutClave.setError(getString(R.string.transfer_key_error));
        }
        return valid;
    }

    private boolean validateCafe() {
        boolean valid = false;
        if (!editTextCafe.getText().toString().isEmpty()){
            try {
                Double value = Double.valueOf(editTextCafe.getText().toString());
                if (value instanceof Double){
                    if(value>0){
                        valid = true;
                        textInputLayoutCafe.setError(null);
                    }else
                        textInputLayoutCafe.setError(getString(R.string.number_not_valid));
                }
                else
                    textInputLayoutCafe.setError(getString(R.string.number_not_valid));
            }catch (Exception e){
                e.printStackTrace();
                textInputLayoutCafe.setError(getString(R.string.number_not_valid));
            }
        }
        else{
            textInputLayoutCafe.setError(getString(R.string.empty_field));
        }
        return valid;
    }

    private void showConfirmDialog() {
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.confirm_transferencia);
        builder.setIcon(R.drawable.ic_cafe);
        String body=context.getString(R.string.confirm_transferencia_dialog_body);
        builder.setMessage(body);
        builder.setPositiveButton(context.getString(R.string.ok),(dialogInterface, i) -> {
            GeneralUtility.makeCallUSSD(context,String.format(getString(R.string.transferir_saldo_code),editTextClave.getText().toString(),editTextCafe.getText().toString()));
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(context.getString(R.string.cancel),(dialogInterface, i) -> dialogInterface.dismiss());

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context,R.color.colorDialogButton));
        });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
