package com.example.marit.serietrackerapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoggedInUserProfileFragment extends Fragment implements View.OnClickListener {
    private FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logged_in_user_profile, container, false);
        Button login = view.findViewById(R.id.buttonLogin);
        Button register = view.findViewById(R.id.buttonRegister);
        Button logout = view.findViewById(R.id.buttonLogout);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        register.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        updateUI();
    }

    @Override
    public void onClick(View view){

        switch (view.getId()) {
            case R.id.buttonLogin:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                LoginFragment loginfragment = new LoginFragment();
                fragmentTransaction.replace(R.id.fragment_container, loginfragment).addToBackStack(null).commit();
            case R.id.buttonRegister:
                FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                RegisterFragment registerFragment = new RegisterFragment();
                fragmentTransaction1.replace(R.id.fragment_container, registerFragment).addToBackStack(null).commit();
            case R.id.buttonLogout:
                FirebaseAuth.getInstance().signOut();
                }
        }

    public void updateUI() {
        if (user == null) {
            updateWhenLoggedIn();
        }
        else{
            updateWhenLoggedOut();
        }
    }

    public void updateWhenLoggedIn() {
        //Button login = getView().findViewById(R.id.buttonLogin);
        //Button register = getView().findViewById(R.id.buttonRegister);
        //login.setVisibility(View.GONE);
        //register.setVisibility(View.GONE);

    }

    public void updateWhenLoggedOut() {
        //Button logout = getView().findViewById(R.id.buttonLogout);
        //logout.setVisibility(View.GONE);
    }

}
