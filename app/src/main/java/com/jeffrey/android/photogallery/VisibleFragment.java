package com.jeffrey.android.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;


public abstract class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, filter,
                    PollService.PERM_PRIVATE, null);  //PERMISSIONS ON A BROADCAST RECEIVER, PAGE 501
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "canceling notification");           //IF WE RECEIVE THIS, WE'RE VISIBLE, SO
            setResultCode(Activity.RESULT_CANCELED);        //CANCEL THE NOTIFICATION

            //Toast.makeText(getActivity(),
            //        "Got a broadcast:" + intent.getAction(),
            //        Toast.LENGTH_LONG)
            //        .show();
        }
    };

}
