package com.example.marit.serietrackerapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Handles login events with firebase
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button login = view.findViewById(R.id.loginButton);
        login.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                // Get the entered email and password and try to login with it
                EditText emailfield = getView().findViewById(R.id.EmailField);
                EditText passwordfield = getView().findViewById(R.id.PasswordField);
                String email = emailfield.getText().toString();
                String password = passwordfield.getText().toString();
                SignIn(email, password);

        }
    }

    /**
     * Tries to sign the user in by checking if his information is known in Firebase
     */
    public void SignIn(String email, String password) {
        // mAuth is final, because it needs to be accessed from the inner class
        // and won't be changed after this
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Loginstatus", "signInWithEmail:success");
                            Toast.makeText(getContext(), "Logged in",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Loginstatus", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    /**
     * Updates the UI according to if a user logged in successful
     */
    public void updateUI(FirebaseUser user) {
        // If login is succesfull, navigate user to the database
        if (user != null) {
            Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
            LoggedInUserProfileFragment fragment = new LoggedInUserProfileFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else {
            Toast.makeText(getContext(), "Failed, try again", Toast.LENGTH_SHORT).show();
        }
    }
}
