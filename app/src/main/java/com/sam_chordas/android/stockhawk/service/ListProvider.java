package com.sam_chordas.android.stockhawk.service;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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

import com.sam_chordas.android.stockhawk.DataObject.StockDO;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;

/**
 * Created by Aritra on 5/6/2016.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;
    private static final int CURSOR_LOADER_ID = 1;
    //private QuoteCursorAdapter mCursorAdapter;
    //private Cursor mCursor;
    private ArrayList<StockDO> arrStockDO = new ArrayList<>();

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {

        Thread thread = new Thread() {
            public void run() {
                query();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    private void query(){
        Cursor cursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);

        if(cursor.moveToFirst()){
            do{
                StockDO objStockDO          = new StockDO();
                objStockDO.symbol           = cursor.getString(cursor.getColumnIndex("symbol"));
                objStockDO.bid_price        = cursor.getString(cursor.getColumnIndex("bid_price"));
                objStockDO.percent_change   = cursor.getString(cursor.getColumnIndex("percent_change"));
                objStockDO.change           = cursor.getString(cursor.getColumnIndex("change"));
                arrStockDO.add(objStockDO);
            } while(cursor.moveToNext());
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onDestroy() {
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
        return arrStockDO.size();
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

        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        StockDO objStockDO = arrStockDO.get(position);
        remoteView.setTextViewText(R.id.stock_symbol, objStockDO.symbol);
        remoteView.setTextViewText(R.id.bid_price, objStockDO.bid_price);

        int sdk = Build.VERSION.SDK_INT;
        if (Utils.showPercent){
            remoteView.setTextViewText(R.id.change, objStockDO.percent_change);
        } else{
            remoteView.setTextViewText(R.id.change, objStockDO.change);
        }

        return remoteView;
    }
}
