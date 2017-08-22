package com.spinarplus.googlebooks;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Desarrollo on 8/21/2017.
 */

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils(){}

    public static ArrayList<Book> fetchBookData(String requestUrl){
        Log.v("Query Utils","Fetching Books Data");
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        URL url = createUrl(requestUrl);

        String jsonResponse = "";

        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(LOG_TAG,"Error fetch input stream",e);
        }
        return extractBooks(jsonResponse);
    }

    private static URL createUrl(String stringUrl){
        Log.v("Query Utils","Creating URL");
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG,"Error creating URL",e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        Log.v("Query Utils","Requesting from Server");
        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG,"Error Response Code: "+urlConnection.getResponseCode());
                Log.e(LOG_TAG,"Error Response Message: "+urlConnection.getResponseMessage());
            }
        }catch (IOException e){
            Log.e(LOG_TAG,"Problem retrieving books data",e);
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.v("Query Utils","Reading from Stream");
        StringBuilder output = new StringBuilder();

        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    private static ArrayList<Book> extractBooks(String booksJSON){
        Log.v("Query Utils","Extracting Books from Response");
        ArrayList<Book> books = new ArrayList<Book>();

        try{
            JSONObject root = new JSONObject(booksJSON);
            JSONArray bookArray = root.getJSONArray("items");
            int arrayLength = bookArray.length();

            for(int i = 0; i < arrayLength; i++){
                JSONObject book = bookArray.getJSONObject(i);
                JSONObject bookInfo = book.getJSONObject("volumeInfo");

                String bookTitle = bookInfo.getString("title");
                String bookAuthors = "";
                try{
                    bookAuthors = formatBookAuthors(bookInfo.getJSONArray("authors"));
                }catch(JSONException e){
                    Log.v(LOG_TAG,"Problem parsing authors from json response",e);
                }

                Double bookRating;
                if(bookInfo.has("averageRating")){
                    bookRating = bookInfo.getDouble("averageRating");
                }else{
                    bookRating = 0.0;
                }

                String bookCoverUrl = "";
                if(bookInfo.has("imageLinks")){
                    JSONObject imageLinks = bookInfo.getJSONObject("imageLinks");
                    bookCoverUrl = imageLinks.getString("smallThumbnail");
                }

                String bookInfoUrl = "";
                if(bookInfo.has("infoLink")){
                    bookInfoUrl = bookInfo.getString("infoLink");
                }

                Book newBook = new Book(bookTitle,bookAuthors,bookRating,bookCoverUrl,bookInfoUrl);

                books.add(newBook);

            }

        }catch(JSONException e){
            Log.e("QueryUtils","Problem parsing the Books JSON",e);
        }

//        Log.v("Query Utils","We got these books: "+books.toString());
        return books;
    }

    private static String formatBookAuthors(JSONArray bookAuthors) throws JSONException{
        Log.v("Query Utils","Formatting some Authors");
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < bookAuthors.length();i++){
            builder.append(bookAuthors.getString(i));
        }

        return builder.toString();
    }
}
