package com.sam_chordas.android.stockhawk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.Utilities.WidgetUtils;
import com.sam_chordas.android.stockhawk.ui.WidgetActivity;

/**
 * Created by ARPaul on 03-03-2016.
 */
public class MyWidgetIntentReceiver extends BroadcastReceiver {
    public static int clickCount = 0;
    private String msg[] = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WidgetUtils.WIDGET_UPDATE_ACTION)) {
            updateWidgetPictureAndButtonListener(context);
        }
    }

    private void updateWidgetPictureAndButtonListener(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_cell);

        // updating view
        remoteViews.setTextViewText(R.id.title, getTitle());

        WidgetActivity.pushWidgetUpdate(context.getApplicationContext(),
                remoteViews);
    }

    private String getDesc(Context context) {
        // some static jokes from xml
        /*msg = context.getResources().getStringArray(R.array.news_headlines);
        if (clickCount >= msg.length) {
            clickCount = 0;
        }
        return msg[clickCount];*/

        return "test message";
    }

    private String getTitle() {
        return "Funny Jokes";
    }
}
