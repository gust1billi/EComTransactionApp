package com.example.ecomtransactionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import com.example.ecomtransactionapp.api.APIHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputLayout emailLayout, passLayout;
    TextInputEditText emailEdit, passEdit;

    Button loginButton;

//    String PREF_INTEGER = "Preference Integer";
//    String LOGIN_INSTANCE = "Preference Login";
//    String PREF_TOKEN = "Preference Token";
//    String LOGIN_PREFERENCE = "LOGIN PREFERENCES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applicationStartInit();

        // AUTO LOGIN FEATURE; IMPLEMENT LATER
//        SharedPreferences preferences = getSharedPreferences(LOGIN_PREFERENCE, MODE_PRIVATE);
//        // For some reason, it doesn't work without this variable
//        boolean autoLogin = preferences.getBoolean( LOGIN_INSTANCE, false );
//        if ( autoLogin ) {
//            nextActivity( preferences.getString( PREF_TOKEN, "" ) );
//        }

        emailEdit   = findViewById(R.id.emailInputEdit);
        emailLayout = findViewById(R.id.emailInputLayout);
        passEdit    = findViewById(R.id.passwordInputEdit);
        passLayout  = findViewById(R.id.passwordInputLayout);
        loginButton = findViewById(R.id.loginButton);

        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void afterTextChanged(Editable editable) {
                emailLayout.setError("Please input an Email correctly");
                emailLayout.setErrorEnabled( !Utils.isEmail( editable.toString( ) ) );
            }
        });

        passEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void afterTextChanged(Editable editable) {
                passLayout.setError("Please input a Password with at least 6 digits");
                passLayout.setErrorEnabled( !Utils.isPassword( editable.toString( ) ) );
            }
        });

        loginButton.setOnClickListener(view -> {
            String email = Objects.requireNonNull( emailEdit.getText( ) ).toString();
            String pass  = Objects.requireNonNull( passEdit.getText( ) ).toString();

            if ( emailEdit.length() == 0 || passEdit.length() == 0 ){
                Utils.showToast(MainActivity.this, "EMAIL OR  PASSWORD IS EMPTY");

            } else if ( emailLayout.isErrorEnabled( ) || passLayout.isErrorEnabled( ) ){
                Utils.showToast(MainActivity.this, "Please input email & password correctly");
            } else {
                checkLogin( email, pass );
            }
        });
    }

    private void checkLogin(String email, String password) {
        Log.e("Values", email + " & " + password);
        APIHandler.doLogin(MainActivity.this, email, password);
    }

    public void nextActivity(String token) {
        Intent i = new Intent(MainActivity.this, TransactionActivity.class );
        i.putExtra("code", token);
        startActivity( i );
    }

    private void applicationStartInit() {
//        Utilities.showToast(MainActivity.this, "Hello World!");
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
    }
}