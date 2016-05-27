package com.example.prayertimescalculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.prayertimescalculator.RedScreenService;

public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
      //  if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, RedScreenService.class);

           context.startService(pushIntent);
//            Toast.makeText(context,"boot broadcast",
//                    Toast.LENGTH_SHORT).show();
      //  }


    }
}