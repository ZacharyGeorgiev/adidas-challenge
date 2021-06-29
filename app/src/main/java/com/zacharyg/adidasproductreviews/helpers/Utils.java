package com.zacharyg.adidasproductreviews.helpers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zacharyg.adidasproductreviews.R;

public class Utils {
    /**
     * Checks if the device is connected to the internet
     */
    public static boolean internetIsUnavailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * Shows a 'No internet' toast message
     * @param activity - activity in which to show the toast
     */
    public static void showNoInternetToast(Activity activity) {
        if (activity == null) { return; }
        showToast(activity, activity.getString(R.string.no_internet), Toast.LENGTH_LONG);
    }

    /**
     * Shows a custom toast message
     * @param activity - activity in which to show the toast
     * @param message - the toast message
     * @param duration - the toast duration
     */
    public static void showToast(Activity activity, String message, int duration) {
        if (activity == null) { return; }

        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.rectangular_toast, activity.findViewById(R.id.cl_root));
        TextView tvMessage = layout.findViewById(R.id.tv_message);
        tvMessage.setText(message);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 50);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
}
