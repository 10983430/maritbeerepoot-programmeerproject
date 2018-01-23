package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends ListFragment implements View.OnClickListener {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID;
    String currentuserid = user.getUid();
    private DatabaseReference dbref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        Button follow = view.findViewById(R.id.FollowButton);
        follow.setOnClickListener(this);
        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FollowButton:
                putUserInDatabase();
        }
    }

    public void putUserInDatabase() {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        dbref = fbdb.getReference("User/"+currentuserid+"/UsersFollowed");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> usersfollowed = (ArrayList<String>) dataSnapshot.getValue();
                if (usersfollowed == null) {
                    usersfollowed = new ArrayList<>();
                }
                usersfollowed.add(userID);
                try{
                    dbref.setValue(usersfollowed);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        // Get the imdbid from the serie that was clicked on
        if (bundle != null) {
            userID = bundle.getString("userid");
            Log.d("lollollll", userID);
            getData(userID);
        }

    }

    public void getData(final String userID) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + userID);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username =  dataSnapshot.child("username").getValue().toString();
                TextView usernamehold = getView().findViewById(R.id.UsernameInfo);
                usernamehold.setText(username);
                HashMap<String, HashMap<String, HashMap<String, String>>> info =
                        (HashMap<String, HashMap<String, HashMap<String, String>>>) dataSnapshot.child("SerieWatched").getValue();
                if (info == null) {
                    TextView nonewatched = getView().findViewById(R.id.Nonewatched);
                    nonewatched.setVisibility(View.VISIBLE);
                }
                else {
                    Log.d("lollol", "hiii");
                }

                Log.d("lollol11111111", userID + " " + currentuserid);
                if (userID == currentuserid ) {
                    Log.d("lollol122222222222", userID + " " + currentuserid);
                    Button follow = getView().findViewById(R.id.FollowButton);
                    follow.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
