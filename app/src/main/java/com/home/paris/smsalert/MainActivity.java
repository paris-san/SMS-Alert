package com.home.paris.smsalert;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class MainActivity extends Activity {

    private EditText number, ignoreText;
    private CheckBox active, ignoreMute;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = findViewById(R.id.number);
        ignoreText = findViewById(R.id.ignore);
        active = findViewById(R.id.active);
        ignoreMute = findViewById(R.id.ignore_mute);
        save = findViewById(R.id.save);

        TextWatcher textChangeListener = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                save.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        CompoundButton.OnCheckedChangeListener onCheckListener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        save.setEnabled(true);
                    }
                };

        number.addTextChangedListener(textChangeListener);
        ignoreText.addTextChangedListener(textChangeListener);
        active.setOnCheckedChangeListener(onCheckListener);
        ignoreMute.setOnCheckedChangeListener(onCheckListener);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setEnabled(false);
                SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                editor.putString("number", number.getText().toString());
                editor.putString("ignore_text", ignoreText.getText().toString());
                editor.putBoolean("active", active.isChecked());
                editor.putBoolean("ignore_mute", ignoreMute.isChecked());
                editor.apply();
            }
        });

        SharedPreferences prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        number.setText(prefs.getString("number", null));
        ignoreText.setText(prefs.getString("ignore_text", null));
        active.setChecked(prefs.getBoolean("active", false));
        ignoreMute.setChecked(prefs.getBoolean("ignore_mute", false));

        requestUnmutePermission();
        requestSmsPermission();
        save.setEnabled(false);

    }

    private void requestUnmutePermission() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings
                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }


    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }
}
