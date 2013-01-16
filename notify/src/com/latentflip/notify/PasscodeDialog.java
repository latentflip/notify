package com.latentflip.notify;

import android.app.*;
import android.content.*;
import android.os.Bundle;
/**
 * Created with IntelliJ IDEA.
 * User: latentflip
 * Date: 16/01/2013
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class PasscodeDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Fire ze missiles?")
                .setPositiveButton("fire", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
