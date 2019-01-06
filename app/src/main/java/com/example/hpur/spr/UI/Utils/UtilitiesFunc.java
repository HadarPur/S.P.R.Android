package com.example.hpur.spr.UI.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class UtilitiesFunc {
    private ProgressDialog mProgressDialog;
    private Context ctx;

    public UtilitiesFunc(Context ctx) {
        this.ctx = ctx;
        this.mProgressDialog = new ProgressDialog(ctx);
        this.mProgressDialog.setCancelable(false);
    }

    // show progress dialog
    public void showProgressDialog(String message) {
        this.mProgressDialog.setMessage(message);
        this.mProgressDialog.show();
    }

    // hide progress dialog
    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    //note to the user
    public void showOnSettingsAlert(String titel, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
        alertDialog.setTitle(titel);
        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}
