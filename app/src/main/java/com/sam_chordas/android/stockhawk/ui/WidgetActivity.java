package com.sam_chordas.android.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.Utilities.WidgetUtils;
import com.sam_chordas.android.stockhawk.service.MyWidgetIntentReceiver;
import com.sam_chordas.android.stockhawk.service.WidgetService;

/**
 * Created by ARPaul on 03-03-2016.
 */
public class WidgetActivity extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context,appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i],remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        /*// initializing widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_cell);

        // register for button event
        remoteViews.setOnClickPendingIntent(R.id.sync_button,buildButtonPendingIntent(context));

        // updating view with initial data
        remoteViews.setTextViewText(R.id.title, getTitle());
        remoteViews.setTextViewText(R.id.desc, getDesc());

        // request for widget update
        pushWidgetUpdate(context, remoteViews);*/
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_cell);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, WidgetService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(appWidgetId, R.id.rvWidget,svcIntent);
        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.rvWidget, R.id.empty_view);
        return remoteViews;
    }

    public static PendingIntent buildButtonPendingIntent(Context context) {
        ++MyWidgetIntentReceiver.clickCount;

        // initiate widget update request
        Intent intent = new Intent();
        intent.setAction(WidgetUtils.WIDGET_UPDATE_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static CharSequence getDesc() {
        return "Sync to see some of our funniest joke collections";
    }

    private static CharSequence getTitle() {
        return "Funny Jokes";
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context,WidgetActivity.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}
