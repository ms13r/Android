package edu.fsu.cs.mobile.finalproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miroslavsanader on 12/7/16.
 */

public class CustomImageList extends ArrayAdapter<User> {
    private List<User> list;
    private Context context;

    public CustomImageList(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        this.context = context;
        list = objects;
    }

    public View getView(int position, View view, ViewGroup parent){
        final User current_user = list.get(position); // Get the current item in the list

        // Standard inflation
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.single_list_item, null);

        final ImageView img = (ImageView) v.findViewById(R.id.img);
        TextView name = (TextView) v.findViewById(R.id.txt);

        name.setText(current_user.Name);

        try {
            // Attempt to get a proper input stream (should be accurate from Facebook)
            new AsyncTask<Void, Void, Void>() {
                Bitmap bmp;
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        InputStream in = new URL(current_user.Photo).openStream();
                        bmp = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.i("AsyncTask", "Exception " + e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (bmp != null)
                        img.setImageBitmap(bmp);
                }

            }.execute();
        }
        catch (Exception e){ e.printStackTrace(); }

        return v;
    }
}