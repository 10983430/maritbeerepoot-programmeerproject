package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UsersOverviewFragment extends ListFragment { //implements View.OnClickListener {
    private HashMap<String, String> UsernameUserid = new HashMap<>();
    private ArrayList<UserInfoClass> allusers = new ArrayList<UserInfoClass>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        View view = inflater.inflate(R.layout.fragment_users_overview, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position,id);
            FragmentManager fragmentManager = getFragmentManager();
            UserDetailsFragment fragment = new UserDetailsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, "SeriesOverview");
            fragmentTransaction.commit();
    }

    public void getData() {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String userid = child.getKey();

                    String username = dataSnapshot.child(userid).child("username").getValue().toString();
                    String email = dataSnapshot.child(userid).child("email").getValue().toString();

                    // TO-DO, dit klopt niet meer
                    HashMap<String, String> followseries = new HashMap<>();
                    HashMap<String, String> follususers = new HashMap<>();

                    UserInfoClass user = new UserInfoClass(userid, username, followseries, follususers, email);

                    allusers.add(user);
                    UsernameUserid.put(username, userid.toString());
                }
                makeListView(UsernameUserid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void makeListView(HashMap hashMap) {
        ArrayList<String> users = new ArrayList<String>(hashMap.keySet());
        Adapter adapter = new UsersOverviewAdapter(getActivity(), users);
        this.setListAdapter((ListAdapter) adapter);
    }
}
