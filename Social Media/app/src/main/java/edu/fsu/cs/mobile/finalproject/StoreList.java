package edu.fsu.cs.mobile.finalproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miroslavsanader on 12/11/16.
 */

public class StoreList extends ArrayAdapter<User> {
    private List<User> list;
    private Context context;

    public StoreList(Context context, int resource, ArrayList<User> objects) {
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
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), (int) current_user.ID);
        img.setImageBitmap(icon);

        return v;
    }
}
