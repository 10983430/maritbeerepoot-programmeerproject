package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        // Get the imdbid from the serie that was clicked on
        if (bundle != null) {
            String userID = bundle.getString("userid");
            getData(userID);
        }

    }

    public void getData(String userID) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/"+userID);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email =  dataSnapshot.child("emai").getValue().toString();
                Log.d("lollol", email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
