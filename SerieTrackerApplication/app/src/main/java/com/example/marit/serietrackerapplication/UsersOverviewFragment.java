package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersOverviewFragment extends ListFragment {

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
        String[] values = new String[]{"Testuser1", "TestUser", "Test"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
        this.setListAdapter(adapter);

        //this.setOnItemClickListener(new ClickDetails());

    }
/*
    private class ClickDetails implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
            FragmentManager fragmentManager = getFragmentManager();
            UserDetailsFragment fragment = new UserDetailsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, "SeriesOverview");
            fragmentTransaction.commit();
        }
    }
}*/
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position,id);
            FragmentManager fragmentManager = getFragmentManager();
            UserDetailsFragment fragment = new UserDetailsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, "SeriesOverview");
            fragmentTransaction.commit();
    }
}
