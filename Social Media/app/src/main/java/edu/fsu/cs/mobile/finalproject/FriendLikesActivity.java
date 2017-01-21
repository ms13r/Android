package edu.fsu.cs.mobile.finalproject;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Philip on 12/7/2016.
 */

public class FriendLikesActivity extends AppCompatActivity {/**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ProgressBar pbar;
    protected ArrayList<User> Friends = new ArrayList<>();
    protected ArrayList<User> storeArray = new ArrayList<>();
    private StoreList store;
    private ListView FriendLikes;
    private CustomImageList friendLikesAdapter;
    private AlertDialog.Builder storeList;

    final private int PAGELIMIT = 25;

    DatabaseReference giftrRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userRef = giftrRootRef.child("users");

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.friend_likes);

        TextView Title = (TextView) findViewById(R.id.Friend_Likes_Title);
        long FriendID = getIntent().getLongExtra("User ID", -1);

        final String FriendName = getIntent().getStringExtra("User Name");
        String temp = FriendName + getString(R.string.Friend_Likes);
        Title.setText(temp);

        FriendLikes = (ListView) findViewById(R.id.Friends_Likes);
        pbar = (ProgressBar) findViewById(R.id.progressBar2);

        // Show progressbar while the user list loads
        pbar.setVisibility(View.VISIBLE);

        if (FriendID != -1){
            for(int x = 0; x < PAGELIMIT; ++x)
                Friends.add(new User());

            //Read from Database
            userRef.child(Long.toString(FriendID)).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("ReadData", dataSnapshot.getValue() + "");
                    String[] LikeNames = new String[PAGELIMIT];
                    String result = dataSnapshot.getValue().toString();

                    Log.i("DEBUG", "result = " + result);

                    Pattern regex = Pattern.compile("(.*?)\\|\\|");
                    Matcher matcher = regex.matcher(result);

                    int x = 0;
                    while(matcher.find() && x < PAGELIMIT){
                        Log.i("DEBUG", "Name Matcher.Group = " + matcher.group(1));
                        LikeNames[x] = matcher.group(1);
                        Friends.get(x).Name = LikeNames[x];
                        x++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // end of read from database

            // Read from database
            userRef.child(Long.toString(FriendID)).child("pictures").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("ReadData", dataSnapshot.getValue() + "");
                    String[] LikeURLs = new String[PAGELIMIT];
                    String result = dataSnapshot.getValue().toString();

                    Pattern regex = Pattern.compile("(.*?)\\|\\|");
                    Matcher matcher = regex.matcher(result);

                    int x = 0;
                    while(matcher.find() && x < PAGELIMIT){
                        Log.i("DEBUG", "Photo Matcher.Group = " + matcher.group(1));
                        LikeURLs[x] = matcher.group(1);
                        Friends.get(x).Photo = LikeURLs[x];
                        x++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // end of read from database

            friendLikesAdapter = new CustomImageList(getBaseContext(), 0, Friends);
            FriendLikes.setAdapter(friendLikesAdapter);
//            FriendLikes.invalidateViews();
            friendLikesAdapter.notifyDataSetChanged();
            FriendLikes.post(new Runnable(){
                public void run() {
                    FriendLikes.setSelection(FriendLikes.getCount() - 1);
                }});
            FriendLikes.smoothScrollToPosition(0);
            pbar.setVisibility(View.INVISIBLE);

            FriendLikes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    final int LikeChosen = position;
                    // Dialog for store selection
                    storeList = new AlertDialog.Builder(FriendLikesActivity.this);
                    LayoutInflater inflate = getLayoutInflater();
                    View listView = (View) inflate.inflate(R.layout.storeview, null);
                    storeList.setTitle("Select A Store");
                    storeList.setView(listView);
                    ListView slist = (ListView) listView.findViewById(R.id.storelist);

                    // Build contents for dialog
                    if(storeArray.isEmpty()) {

                        User t = new User();
                        t.Name = "Best Buy";
                        t.ID = (long) R.drawable.bestbuy;

                        storeArray.add(t);
                        t = new User();
                        t.Name = "eBay";
                        t.ID = (long) R.drawable.ebay;

                        storeArray.add(t);
                        t = new User();
                        t.Name = "Amazon";
                        t.ID = (long) R.drawable.amazon;

                        storeArray.add(t);
                        t = new User();
                        t.Name = "Barnes & Noble";
                        t.ID = (long) R.drawable.barnesandnoble;

                        storeArray.add(t);
                        t = new User();
                        t.Name = "Nordstrom";
                        t.ID = (long) R.drawable.nordstrom;

                        storeArray.add(t);
                        t = new User();
                        t.Name = "Kohl's";
                        t.ID = (long) R.drawable.kohls;

                        storeArray.add(t);
                        t = new User();
                        t.Name = "Belk";
                        t.ID = (long) R.drawable.belk;
                        storeArray.add(t);
                    }

                    store = new StoreList(getApplicationContext(), 0, storeArray);
                    slist.setAdapter(store);
                    store.notifyDataSetChanged();

                    final AlertDialog dialog = storeList.show();
                    storeList.setCancelable(true);

                    slist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            // On Item Click listener: get the index and
                            // find the store, launch the webview
                            String chosenStore = storeArray.get(position).Name;
                            String LikeName = Friends.get(LikeChosen).Name;
                            String url;

                            if(chosenStore.equals("Barnes & Noble")){
                                url = "http://www.barnesandnoble.com/s/" + LikeName.replaceAll(" ", "+");
                            }
                            else if(chosenStore.equals("Target")){
                                url = "http://www.target.com/s?searchTerm=" + LikeName;
                            }
                            else if(chosenStore.equals("Sears")){
                                url = "http://www.sears.com/search=" + LikeName;
                            }
                            else if(chosenStore.equals("Best Buy")){
                                url = "http://www.bestbuy.com/site/searchpage.jsp?st=" + LikeName.replaceAll(" ", "+")
                                + "&_dyncharset=UTF-8&id=pcat17071&type=page&sc=Global&cp=1&nrp=&sp=&qp=&list=n&af=true&iht=y&usc=All+Categories&ks=960&keys=keys";
                            }
                            else if(chosenStore.equals("eBay")){
                                url = "http://www.ebay.com/sch/i.html?_from=R40&_trksid=p2050601.m570.l1313.TR12.TRC2.A0.H0.X" + LikeName.replaceAll(" ", "+")
                                + ".TRS0&_nkw=" + LikeName.replaceAll(" ", "+") + "&_sacat=0";
                            }
                            else if(chosenStore.equals("Macy's")){
                                url = "http://www1.macys.com/shop/featured/" + LikeName.replaceAll(" ", "-");
                            }
                            else if(chosenStore.equals("Kohl's")){
                                url = "http://www.kohls.com/search.jsp?search=" + LikeName;
                            }
                            else if(chosenStore.equals("Belk")){
                                url = "https://www.belk.com/search/search_results.jsp?ZZ_ST=" + LikeName
                                + "&ZZ<>tP=4294923540&ZZ<>t=" + LikeName + "&FOLDER<>folder_id=1408474395191292";
                            }
                            else if(chosenStore.equals("Dillard's")){
                                url = "http://www.dillards.com/search-term/" + LikeName.replaceAll(" ", "+");
                            }
                            else if(chosenStore.equals("Amazon")){
                                url = "https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords="
                                        + LikeName.replaceAll(" ", "+");
                            }
                            else if(chosenStore.equals("TicketMaster")){
                                url = "http://www.ticketmaster.com/search?tm_link=tm_homeA_header_search&user_input=" + LikeName.replaceAll(" ", "+")
                                        + "&q=" + LikeName.replaceAll(" ", "+");
                            }
                            else if(chosenStore.equals("JCPenney")){
                                url = "http://www.jcpenney.com/s/" + LikeName.replaceAll(" ", "+") + "?Ntt=" + LikeName.replaceAll(" ", "+");
                            }
                            else if(chosenStore.equals("Neiman Marcus")){
                                url = "http://www.neimanmarcus.com/search.jsp?from=brSearch&responsive=true&request_type=search&search_type=keyword&q=" + LikeName;
                            }
                            else {
                                url = "http://shop.nordstrom.com/sr?keyword=" + LikeName.replaceAll(" ", "+");
                            }

                            Intent toFriendLikes = new Intent(FriendLikesActivity.this, AmazonActivity.class);
                            toFriendLikes.putExtra("Amazon URL", url);
                            dialog.cancel();
                            startActivity(toFriendLikes);
                        }
                    });
                }
            });} else {
            Toast.makeText(getApplicationContext(),
                    "Invalid User ID", Toast.LENGTH_SHORT).show();
        }

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
                .setName("FriendLikes Page") // TODO: Define a title for the content shown.
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
