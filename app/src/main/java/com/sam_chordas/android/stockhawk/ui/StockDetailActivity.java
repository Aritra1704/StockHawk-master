package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sam_chordas.android.stockhawk.DataObject.StockDO;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.Utilities.StringUtils;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

/**
 * Created by Aritra on 5/11/2016.
 */
public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //http://www.android-graphview.org/documentation/how-to-create-a-simple-graph

    private StockDO objStockDO;
    private GraphView graph;
    private static final int CURSOR_LOADER_ID = 1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stock_detail);

        if(getIntent().hasExtra("StockDO"))
            objStockDO = (StockDO)getIntent().getExtras().get("StockDO");

        graph = (GraphView) findViewById(R.id.graph);
        toolbar                 = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns.BIDPRICE },
                QuoteColumns.SYMBOL + " = ?",
                new String[]{objStockDO.symbol},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        if(data != null && data.moveToFirst()){
            DataPoint[] objDataPoint = new DataPoint[data.getCount()];
            int i = 0;
            do{
                objDataPoint[i] = new DataPoint(i++,StringUtils.getDouble(data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE))));
            }while(data.moveToNext());

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(objDataPoint);
            graph.addSeries(series);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
    }
}
