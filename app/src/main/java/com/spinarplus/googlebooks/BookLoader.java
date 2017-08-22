package com.spinarplus.googlebooks;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Desarrollo on 8/21/2017.
 */

public class BookLoader extends AsyncTaskLoader{

    private static final String LOG_TAG = BookLoader.class.getSimpleName();
    private String mUrl;

    public BookLoader(Context context,String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if(mUrl == null){
            return null;
        }

        ArrayList<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
