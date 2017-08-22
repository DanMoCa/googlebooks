package com.spinarplus.googlebooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.Currency;
import java.util.List;

import static android.R.attr.resource;

/**
 * Created by Desarrollo on 8/21/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(@NonNull Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listViewItem = convertView;

        if(listViewItem == null){
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        Book currentBook = getItem(position);

        String title = currentBook.getTitle();
        String author = currentBook.getAuthor();
        Double rating = currentBook.getRating();
        String coverUrl = currentBook.getCover();

        TextView titleTextView = (TextView) listViewItem.findViewById(R.id.title_text_view);
        titleTextView.setText(title);

        TextView authorTextView = (TextView) listViewItem.findViewById(R.id.author_text_view);
        authorTextView.setText(author);

        TextView ratingTextView = (TextView) listViewItem.findViewById(R.id.rating_text_view);
        ratingTextView.setText(rating + "");

        ImageView coverImageView = (ImageView) listViewItem.findViewById(R.id.cover_image_view);
        if(coverUrl != ""){
            new DownloadImageTask(coverImageView).execute(coverUrl);
        }

        return listViewItem;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
