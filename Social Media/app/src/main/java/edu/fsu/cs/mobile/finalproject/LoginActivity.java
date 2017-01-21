package edu.fsu.cs.mobile.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity{

    public LoginButton loginButton;
    public CallbackManager callbackManager;
    public TextView info;
    public Button FriendButton;

    DatabaseReference giftrRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userRef = giftrRootRef.child("users");

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        info = (TextView) findViewById(R.id.ResultText);
        loginButton = (LoginButton) findViewById(R.id.LoginButton);
        loginButton.setReadPermissions(Arrays.asList("user_friends", "user_likes"));
        FriendButton = (Button) findViewById(R.id.friend_button);

        FriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toFriendsList = new Intent(LoginActivity.this, FriendListActivity.class);
                startActivity(toFriendsList);
            }
        });

        if(isLoggedin()){
            Intent toFriendsList = new Intent(LoginActivity.this, FriendListActivity.class);
            startActivity(toFriendsList);
            FriendButton.setVisibility(View.VISIBLE);
        }

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(!isLoggedin()){
                    FriendButton.setVisibility(View.INVISIBLE);
                    info.setText(R.string.Blank);
                }
            }
        };

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        info.setText(R.string.Login_Successful);
                        FriendButton.setVisibility(View.VISIBLE);

                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            Log.d("response", response + "");
                                            Log.d("Json", object + "");


                                            JSONArray jArray = object.getJSONObject("likes").getJSONArray("data");
                                            String tempLike;
                                            String allLikes = "";
                                            String tempPicture;
                                            String allPicture = "";
                                            
                                            for(int i = 0; i < jArray.length(); ++i) {
                                                tempLike = jArray.getJSONObject(i).getString("name");
                                                allLikes = allLikes + tempLike + "||";

                                                tempPicture = jArray.getJSONObject(i).getJSONObject("picture")
                                                        .getJSONObject("data").getString("url");

                                                allPicture = allPicture + tempPicture + "||";
                                            }

                                            giftrRootRef.child("users").child(object.getString("id"))
                                                    .child("name").setValue(object.getString("name"));

                                            giftrRootRef.child("users").child(object.getString("id")).child("likes")
                                                    .setValue(allLikes);

                                            giftrRootRef.child("users").child(object.getString("id")).child("pictures")
                                                    .setValue(allPicture);


                                        }
                                        catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,likes{name, picture}");
                        request.setParameters(parameters);
                        request.executeAsync();

                        Intent toFriendsList = new Intent(LoginActivity.this, FriendListActivity.class);
                        startActivity(toFriendsList);
                    }

                    @Override
                    public void onCancel() {
                        info.setText(R.string.Login_Cancelled);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        info.setText(R.string.Login_Failed);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}

