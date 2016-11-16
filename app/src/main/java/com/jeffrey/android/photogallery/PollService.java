package com.jeffrey.android.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

import static android.R.attr.fragment;


public class PollService extends IntentService {  //pagina 467
    private static final String TAG = "PollService";

    private static final int POLL_INTERNAL = 1000 * 60; //ADDING ALARM METHOD PAGE 474
    //private static final long POLL_INTERNAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES; //CHANGING TO SENSIBLE ALARM PAGE 483

    public static final String ACTION_SHOW_NOTIFICATION =
                "com.jeffrey.android.photogallery.SHOW_NOTIFICATION"; //SENDING A BROADCAST EVENT, PAGE 497

    public static final String PERM_PRIVATE =  //SENDING WITH A PERMISSION, PAGE 501
            "com.jeffrey.android.photogallery.PRIVATE";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context){
       return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) { //ADDING ALARM METHOD PAGE 474
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERNAL, pi);
        }else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        QueryPreferences.setAlarmOn(context, isOn); //WRITING ALARM STATUS PREFERENCE WHEN ALARM IS SET, PAGE 496

    }

    public static boolean isServiceAlarmOn(Context context){  //ADDING ALARM ON, PAGE 477
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi !=null;
    }

    public PollService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) { //Checking vor background network availability
        if (!isNetworkAvailableAndConnected()){     // pagina 470
            return;
        }

        //Log.i(TAG, "Received an intent: " + intent);

        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem>  items;

        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.new FlickrFetch().getData();
        items = PhotoGalleryFragment.getGalleryItems();

        if (items!=null){

        if (items.size()==0){
            return;
        }

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
                Log.i(TAG, "Got an old result: " + resultId);
        }else{
            Log.i(TAG, "Got an new result: " + resultId);

          Resources resources = getResources();  //ADDING A NOTATION
          Intent i = PhotoGalleryActivity.newIntent(this);
          PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            //NotificationManagerCompat notificationManager =
            //       NotificationManagerCompat.from(this);
            //notificationManager.notify(0, notification);

            //sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION)); //SENDING A BROADCAST EVENT, PAGE 497
            showBackGroundNotification(0, notification);
        }

       QueryPreferences.setLastResultId(this, resultId);

    }}

    private void showBackGroundNotification(int requestCode, Notification notification){
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION); //SENDING AN ORDERED BROADCAST, PAGE 505
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }

    public boolean isNetworkAvailableAndConnected() { //Checking vor background network availability
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() !=null;
        boolean isNetworkConnected = isNetworkAvailable &&
                    cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
}
}
