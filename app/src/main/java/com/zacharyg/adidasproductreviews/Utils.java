package com.zacharyg.adidasproductreviews;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {
    public static boolean deviceIsConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean internetIsAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.getHostAddress().equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public static void showToast(@Nullable Activity activity, String message, int duration) {
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
