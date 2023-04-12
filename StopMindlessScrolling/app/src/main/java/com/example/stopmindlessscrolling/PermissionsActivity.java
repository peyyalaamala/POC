package com.example.stopmindlessscrolling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.stopmindlessscrolling.utility.Utility;

public class PermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }
    /**
     * To show draw over other apps permission dialog
     */
    private void usageAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Utility.getUsageStatsList(PermissionsActivity.this) != null &&
                    Utility.getUsageStatsList(PermissionsActivity.this).isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(PermissionsActivity.this)) {
                    }
                }
                homePermission();
            } else {

            }
        }
    }

    /**
     * To show usage access permission
     */
    public void homePermission() {

        Dialog dialog = new Dialog(PermissionsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.usage_access_permission_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
        TextView ok = (TextView) dialog.findViewById(R.id.okBtn);
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent1 = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);

                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);
                }

            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    private void displayoverotherApps(){
        if (!Settings.canDrawOverlays(PermissionsActivity.this)) {

            displayoverotherappsdialog();
        }
    }
    /**
     * To show usage access permission
     */
    public void displayoverotherappsdialog() {

        Dialog dialog = new Dialog(PermissionsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.usage_access_permission_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
        TextView ok = (TextView) dialog.findViewById(R.id.okBtn);
        TextView okTxt = (TextView) dialog.findViewById(R.id.okTxt);
        okTxt.setText(R.string.display_over_apps);
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));

            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        usageAccessPermission();
        displayoverotherApps();
    }
}