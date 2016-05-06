package com.sam_chordas.android.stockhawk.service;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;

/**
 * Created by Aritra on 5/6/2016.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory,LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;
    private static final int CURSOR_LOADER_ID = 1;
    private QuoteCursorAdapter mCursorAdapter;
    private Cursor mCursor;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        ((Activity)context).getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        mCursorAdapter = new QuoteCursorAdapter(context, null);

        /*for (int i = 0; i < 10; i++) {
            LauncherActivity.ListItem listItem = new LauncherActivity.ListItem();
            listItem.heading = "Heading" + i;
            listItem.content = i
                    + " This is the content of the app widget listview.Nice content though";
            listItemList.add(listItem);
        }*/

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(context, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mCursorAdapter.swapCursor(data);
        mCursor = data;


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        ((Activity)context).getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

       /* mCursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);*/

    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider
        /*String day = "Unknown Day";
        int temp = 0;
        if (mCursor.moveToPosition(position)) {
            final int dayColIndex = mCursor.getColumnIndex(WeatherDataProvider.Columns.DAY);
            final int tempColIndex = mCursor.getColumnIndex(
                    WeatherDataProvider.Columns.TEMPERATURE);
            day = mCursor.getString(dayColIndex);
            temp = mCursor.getInt(tempColIndex);
        }
        // Return a proper item with the proper day and temperature
        final int itemId = R.layout.list_item_quote;
        RemoteViews rv = new RemoteViews(context.getPackageName(), itemId);
        rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp, day));
        // Set the click intent so that we can handle it and show a toast message
        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putString(WeatherWidgetProvider.EXTRA_DAY_ID, day);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);*/

        final int itemId = R.layout.list_item_quote;
        RemoteViews rv = new RemoteViews(context.getPackageName(), itemId);
        rv.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex("symbol")));
        rv.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex("bid_price")));

        int sdk = Build.VERSION.SDK_INT;
        /*if (mCursor.getInt(mCursor.getColumnIndex("is_up")) == 1){
            if (sdk < Build.VERSION_CODES.JELLY_BEAN){
                change.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }else {
                change.setBackground(context.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }
        } else{
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                change.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.percent_change_pill_red));
            } else{
                change.setBackground(context.getResources().getDrawable(R.drawable.percent_change_pill_red));
            }
        }*/
        if (Utils.showPercent){
            rv.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex("percent_change")));
        } else{
            rv.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex("change")));
        }

        return rv;

        /*final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        LauncherActivity.ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.heading, listItem.heading);
        remoteView.setTextViewText(R.id.content, listItem.content);

        return remoteView;*/
    }
}
