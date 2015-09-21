package com.example.yasina.firsttask.marker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.yasina.firsttask.MainActivity;
import com.example.yasina.firsttask.R;

/**
 * Created by yasina on 19.09.15.
 */
public class AlertDialogMarker {

    private static int mReturnNum;

    public static void setAlertDialogSuccessfulAddedNewMarkers(Activity activity){

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(activity.getBaseContext().getResources().getString(R.string.title_congratulation));
        ad.setMessage(activity.getBaseContext().getResources().getString(R.string.title_new_places_added));
        ad.setPositiveButton(activity.getBaseContext().getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        AlertDialog alert = ad.create();
        alert.show();
    }

    public static int setAlertDialogError(Activity a, Exception e){

        mReturnNum = 0;
        String exceptionMessage = e.getMessage();
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.mContext);
        ad.setTitle(a.getBaseContext().getResources().getString(R.string.title_error));
        ad.setMessage(exceptionMessage);
        ad.setPositiveButton(a.getBaseContext().getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                mReturnNum = -1;
                dialog.cancel();
            }
        });
        ad.setNegativeButton(a.getBaseContext().getResources().getString(R.string.btn_repeat), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                mReturnNum = 1;
                dialog.cancel();
            }
        });
        AlertDialog alert = ad.create();
        alert.show();
        return mReturnNum;
    }

}
