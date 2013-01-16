package com.latentflip.notify;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created with IntelliJ IDEA.
 * User: latentflip
 * Date: 16/01/2013
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
public class NotifyPreferences extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        //Restore preferences
        String passcode = settings().getString("passcode", "");
        setPasscode(passcode);
    }



    public void sendTestNotification(View view) {
        DialogFragment newFragment = new PasscodeDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.passcode, null);

        builder.setView(dialogView)
                .setPositiveButton("fire", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText passcodeView = (EditText) dialogView.findViewById(R.id.dpasscode);
                        String newPasscode = passcodeView.getText().toString();

                        SharedPreferences.Editor editor = editSettings();
                        editor.putString("passcode", newPasscode);
                        editor.apply();

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        // Create the AlertDialog object and return it




//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.icon)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
//        mNotificationManager.notify(1, mBuilder.build());
    }

    public void savePreferences(View view) {
        System.out.println("Saving preferences");

        SharedPreferences.Editor editor = editSettings();
        editor.putString("passcode", getPasscode());
        editor.apply();

        finish();
    }

    private SharedPreferences settings() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
    private SharedPreferences.Editor editSettings() {
        return settings().edit();
    }
    private String getPasscode() {
        EditText mEdit   = (EditText)findViewById(R.id.passcode);
        return mEdit.getText().toString();
    }
    private void setPasscode(String passcode) {
        EditText mEdit   = (EditText)findViewById(R.id.passcode);
        mEdit.setText(passcode);
        System.out.println("Set view passcode to " + passcode);
    }
}