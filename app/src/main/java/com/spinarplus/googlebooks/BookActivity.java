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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class BookActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Book>>{

    public final String LOG_TAG = BookActivity.class.getSimpleName();
    public final String BOOKS_LOADER_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=10";
    private static final int EARTHQUAKE_LOADER_ID = 1;

    private boolean mIsConnected;
    private BookAdapter mAdapter;
    private ListView mBookListView;
    private TextView mEmptyTextView;
    private EditText mSearchText;
    private Button mSearchButton;
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
        mSearchButton = (Button) findViewById(R.id.search_button);
        mSearchText = (EditText) findViewById(R.id.search_edit_text);
        mBookListView = (ListView) findViewById(R.id.list);
        mBookListView.setEmptyView(mEmptyTextView);



        final LoaderManager loaderManager = getLoaderManager();

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
                if(url != ""){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }else{
                    CharSequence text = "No info to display";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(getApplicationContext(),text,duration);
                    toast.show();
                }
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapter.clear();
                mProgressSpinner.setVisibility(View.VISIBLE);
                String search = mSearchText.getText().toString();
                Log.v(LOG_TAG,"Click: "+search);
                if(mIsConnected){
                    Bundle args = new Bundle();
                    args.putString("search",search);
                    loaderManager.restartLoader(EARTHQUAKE_LOADER_ID,args,BookActivity.this);
                }else{
                    mProgressSpinner.setVisibility(View.GONE);
                    mEmptyTextView.setText("No Internet Connection");
                }
            }
        });

    }


    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        String query = BOOKS_LOADER_URL;
        String search = "";
        Log.v(LOG_TAG,"Creating Loader args: "+args);
        if(args != null){
            search = args.getString("search");
            Log.v(LOG_TAG,"Keyword: "+search);
        }

        if(search.trim() != ""){
            query = query+"&q="+search;
        }else{
            query = query+"&q=android";
        }

        return new BookLoader(this,query);
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
        mAdapter.clear();
    }
}
