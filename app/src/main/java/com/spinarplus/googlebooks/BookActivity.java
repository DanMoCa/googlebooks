package com.spinarplus.googlebooks;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class BookActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Book>>{

    public final String LOG_TAG = BookActivity.class.getSimpleName();
    public final String BOOKS_LOADER_URL = "https://www.googleapis.com/books/v1/volumes/Akha4zgQUzIC";
    private static final int EARTHQUAKE_LOADER_ID = 1;

    private boolean mIsConnected;
    private BookAdapter mAdapter;
    private ListView mBookListView;
    private TextView mEmptyTextView;
    private ProgressBar mProgressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        mIsConnected = networkInfo != null && networkInfo.isConnected();
        mProgressSpinner = (ProgressBar) findViewById(R.id.spinner_progress_bar);
        mEmptyTextView = (TextView) findViewById(R.id.empty_text_view);
        mBookListView = (ListView) findViewById(R.id.list);
        mBookListView.setEmptyView(mEmptyTextView);

        LoaderManager loaderManager = getLoaderManager();

        if(mIsConnected){
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID,null,this);
        }else{
            mProgressSpinner.setVisibility(View.GONE);
            mEmptyTextView.setText("No Internet Connection.");
        }

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        mBookListView.setAdapter(mAdapter);

        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book selectedBook = (Book) parent.getItemAtPosition(position);
                String url = selectedBook.getURL();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

    }


    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this,BOOKS_LOADER_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        mAdapter.clear();
        mProgressSpinner.setVisibility(View.GONE);

        if(books != null && !books.isEmpty()){
            mAdapter.addAll(books);
            mEmptyTextView.setText("");
        }else{
            mEmptyTextView.setText("No books found.");
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {

    }
}
