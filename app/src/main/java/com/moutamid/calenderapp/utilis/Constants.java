package com.moutamid.calenderapp.utilis;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.calenderapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Constants {
    // LINKS
    public static final String TERMS = "https://google.com";
    public static final String POLICY = "https://google.com";
    static Dialog dialog;
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String KEY = "KEY";
    public static final String EventsPics = "EventsPics";
    public static final String NOTI_SCHEDULE = "NOTI_SCHEDULE";
    public static final String MONTH_FORMAT = "MMMM";
    public static final String MONTH_YEAR= "MM-yyyy";
    public static final String REQUESTS = "REQUESTS";
    public static final String SEND_REQUESTS = "SEND_REQUESTS";
    public static final String ACTIVE_TASKS = "ACTIVE_TASKS";
    public static final String USER = "USER";
    public static final String STASH_USER = "STASH_USER";
    public static final String DATE = "DATE";
    public static final String YES = "YES";  // Accepted
    public static final String PEN = "PEN";  // Pending
    public static final String REJ = "REJ";  // Rejected
    public static final String CHAT_LIST = "CHAT_LIST";
    public static final String CHATS = "CHATS";

    public static String getFormattedDate(long date){
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
    }
    public static String getFormattedTime(long date){
        return new SimpleDateFormat("hh:mm", Locale.getDefault()).format(date);
    }

    public static String getDays(Date date) {
        return new SimpleDateFormat("d", Locale.getDefault()).format(date);
    }
    public static String getHours(long date) {
        return new SimpleDateFormat("HH", Locale.getDefault()).format(date);
    }
    public static String getMinutes(long date) {
        return new SimpleDateFormat("mm", Locale.getDefault()).format(date);
    }
    public static String getZone(long date) {
        return new SimpleDateFormat("aa", Locale.getDefault()).format(date);
    }
    public static String CurrentMonth() {
        return new SimpleDateFormat(Constants.MONTH_FORMAT, Locale.US).format(new Date());
    }
    public static void initDialog(Context context){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
    }

    public static void showDialog(){
        dialog.show();
    }

    public static void dismissDialog(){
        dialog.dismiss();
    }

    public static void createSnackbar(Context context, View view, String message) {
        Snackbar.make(context, view, message, Snackbar.LENGTH_LONG).show();
    }
    public static void createSnackbar(Context context, View view, String message, String buttonText){
        Snackbar.make(context, view, message, Snackbar.LENGTH_LONG).setAction(buttonText, v -> {

        }).setActionTextColor(context.getResources().getColor(R.color.orange)).show();
    }

    public static String greetingMessage() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay < 12){
            return "Good Morning";
        }else if(timeOfDay < 16){
            return "Good Afternoon";
        }else if(timeOfDay < 21){
            return "Good Evening";
        }else {
            return "Good Night";
        }
    }

    public static void checkApp(Activity activity) {
        String appName = "calenderapp";

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if ((input = in != null ? in.readLine() : null) == null) break;
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> {
                        new AlertDialog.Builder(activity)
                                .setMessage(msg)
                                .setCancelable(false)
                                .show();
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("calenderapp");
        db.keepSynced(true);
        return db;
    }

    public static StorageReference storageReference(String auth) {
        StorageReference sr = FirebaseStorage.getInstance().getReference().child("calenderapp").child(auth);
        return sr;
    }
}
