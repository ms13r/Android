package edu.fsu.cs.mobile.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by Philip on 12/7/2016.
 */

public class FriendListActivity extends AppCompatActivity {

    protected ArrayList<User> Friends = new ArrayList<User>();
    private GoogleApiClient client;
    private User temp;
    private ListView FriendsList;
    private CustomImageList friendListAdapter;
    private ProgressBar pbar;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.friend_list);

        FriendsList = (ListView) findViewById(R.id.Friends_List);
        pbar = (ProgressBar) findViewById(R.id.progressBar3);

        // Show progressbar while the user list loads
        pbar.setVisibility(View.VISIBLE);

        Friends = new ArrayList<>();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            JSONArray jar = object.getJSONObject("friends").getJSONArray("data");
                            for (int x = 0; x < jar.length(); ++x) {
                                temp = new User();
                                temp.Name = jar.getJSONObject(x).getString("name");
                                temp.Photo = jar.getJSONObject(x).getJSONObject("picture").getJSONObject("data").getString("url");
                                temp.ID = jar.getJSONObject(x).getLong("id");
                                Friends.add(temp);
                            }
                        } catch (Exception e) {
                            Log.i("FriendsListActivity", "Exception " + e + " happened when trying to parse friends");
                        }

                        // Add the items to the adapter and set the adapter to the current
                        // friends list after it has been appended
                        friendListAdapter = new CustomImageList(getBaseContext(), 0, Friends);
                        FriendsList.setAdapter(friendListAdapter);
                        friendListAdapter.notifyDataSetChanged();
                        FriendsList.post(new Runnable(){
                            public void run() {
                                FriendsList.setSelection(FriendsList.getCount() - 1);
                            }});
                        FriendsList.smoothScrollToPosition(0);

                        FriendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                long ClickedUserID = Friends.get(position).ID;

                                Log.i("DEBUG: ", "ID = " + ClickedUserID);

                                String ClickedUserName = Friends.get(position).Name;

                                Intent toFriendLikes = new Intent(FriendListActivity.this, FriendLikesActivity.class);
                                toFriendLikes.putExtra("User ID", ClickedUserID);
                                toFriendLikes.putExtra("User Name", ClickedUserName);
                                startActivity(toFriendLikes);
                            }
                        });

                        pbar.setVisibility(View.INVISIBLE);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends{name,picture}");
        request.setParameters(parameters);
        request.executeAsync();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("FriendList Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
