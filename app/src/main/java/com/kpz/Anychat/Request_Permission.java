package com.kpz.Anychat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vrv.imsdk.VIMClient;

public class Request_Permission extends Activity {

    Context context = Request_Permission.this;
    static final Integer WRITE_EXST = 1;
    String[] PERMISSIONS = {Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
    Button ap_button1;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_permission);

        ap_button1 = (Button) findViewById(R.id.ap_button1);
        // This function called when user press complete button
        ap_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForPermission(Manifest.permission.READ_PHONE_STATE, WRITE_EXST);
            }
        });

        askForPermission(Manifest.permission.READ_PHONE_STATE, WRITE_EXST);

    }

    // Check required permission have been granted or not
    private void askForPermission(String permission, Integer requestCode) {
        // If Permission not Granted
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            //This is called if user has denied the permission before
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, requestCode);
                Log.e("UCC Log", "Code: 1001001 Some required has been denied before but request again");
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, requestCode);
                Log.e("UCC Log", "Code: 1001002 Some permission haven't granted.");
            }
        } else {
            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE).edit();
            editor.putString("init_bool", "true");
            editor.apply();

            boolean init = VIMClient.init(this, "com.kpz.AnyChat");
            if (!init) {
                Log.e("UCC Log", "Code: 1101001 SDK failed to initial");
            } else {
                Log.e("UCC Log", "Code: 1101002 SDK successfully initial");
            }

            Log.e("UCC Log", "Code: 1001003 All required permission granted");
            Request_Permission.this.startActivity(new Intent(Request_Permission.this, LoginActivity.class));
            finish();
        }
    }
}
