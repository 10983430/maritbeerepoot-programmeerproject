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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoggedInUserProfileFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set listeners
        View view = inflater.inflate(R.layout.fragment_logged_in_user_profile, container, false);
        Button login = view.findViewById(R.id.buttonLogin);
        Button register = view.findViewById(R.id.buttonRegister);
        Button logout = view.findViewById(R.id.buttonLogout);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        register.setOnClickListener(this);
        // Shows or hides buttons depending on if there is a user logged in
        updateUI(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Sends the user to the right fragment, when clicking on the register or login button,
     * or refreshed the fragment when clicking the logout button
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                LoginFragment loginfragment = new LoginFragment();
                fragmentTransaction.replace(R.id.fragment_container, loginfragment).addToBackStack(null).commit();
                break;
            case R.id.buttonRegister:
                FragmentTransaction fragmentTransactionregister = getFragmentManager().beginTransaction();
                RegisterFragment registerFragment = new RegisterFragment();
                fragmentTransactionregister.replace(R.id.fragment_container, registerFragment).addToBackStack(null).commit();
                break;
            case R.id.buttonLogout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                // Refresh the layout, so the logout button isn't visible anymore
                // and the login and register button appear
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                break;
        }
    }

    /**
     * Updates the UI, depending on if there is a user logged in or not
     */
    public void updateUI(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateWhenLoggedIn(view);
        } else {
            updateWhenLoggedOut(view);
        }
    }

    /**
     * Hides the login and register button when a user is already logged in
     */
    public void updateWhenLoggedIn(View view) {
        Button login = view.findViewById(R.id.buttonLogin);
        Button register = view.findViewById(R.id.buttonRegister);
        login.setVisibility(View.GONE);
        register.setVisibility(View.GONE);
    }

    /**
     * Hides the logout button when there is no user logged in
     */
    public void updateWhenLoggedOut(View view) {
        Button logout = view.findViewById(R.id.buttonLogout);
        logout.setVisibility(View.GONE);
    }
}
