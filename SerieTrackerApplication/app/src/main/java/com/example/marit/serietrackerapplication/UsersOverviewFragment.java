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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Displays a list of all the Firebase users
 */
public class UsersOverviewFragment extends ListFragment {
    private HashMap<String, String> usernameUserid = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_overview, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    /**
     * Gets all the user ids usernames from Firebase
     */
    public void getData() {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String userId = child.getKey();
                    String userName = dataSnapshot.child(userId).child("username").getValue().toString();
                    usernameUserid.put(userName, userId.toString());
                }
                makeListView(usernameUserid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//TODO
            }
        });
    }

    /**
     * Creates a listview from the usernames
     */
    public void makeListView(HashMap hashMap) {
        ArrayList<String> users = new ArrayList<String>(hashMap.keySet());
        Adapter adapter = new UsersOverviewAdapter(getActivity(), users);
        this.setListAdapter((ListAdapter) adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Get the userId by checking which username has which ID
        TextView usernameHolder = v.findViewById(R.id.UserNameHolder);
        String username = usernameHolder.getText().toString();
        // Navigate to user details
        Bundle args = new Bundle();
        args.putString("userid", getUserId(username));
        UserDetailsFragment fragment = new UserDetailsFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    /**
     * Finds the corresponding userid for the input username
     */
    public String getUserId(String username) {
        for (String key : usernameUserid.keySet()) {
            if (key == username) {
                String value = usernameUserid.get(key);
                return value;
            }
        }
        return null;
    }
}
