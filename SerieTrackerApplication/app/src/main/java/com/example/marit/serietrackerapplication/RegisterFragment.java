package com.example.marit.serietrackerapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Makes it possible for a user to register and sends the register information to firebase
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button register = view.findViewById(R.id.RegisterButton);
        register.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        EditText emailinput = getView().findViewById(R.id.RegistrationEmailField);
        EditText passwordinput = getView().findViewById(R.id.RegistrationPasswordField);
        String email = emailinput.getText().toString();
        String password = passwordinput.getText().toString();
        if (passwordinput.length() >= 6) {
            try {
                createAccount(email, password);
            } catch (Exception e) {
                // When createAccount has no input, it throws an error so this lets the user know that no information was filled out
                Toast.makeText(getContext(), "Please fill out your information", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Please make sure your password has a length of atleast 6!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates the new user in firebase
     */
    public void createAccount(String email, String password) {
        // mAuth is final, because it needs to be accessed from the inner class
        // and won't be changed after this
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Loginstatus", "createUserWithEmail:success");

                    // Add the username to Firebase and update UI
                    FirebaseUser user = mAuth.getCurrentUser();
                    String id = user.getUid();
                    EditText emailinput = getView().findViewById(R.id.RegistrationEmailField);
                    userInformation(emailinput.getText().toString(), id);

                    // Navigate to the logged in user info
                    LoggedInUserProfileFragment fragment = new LoggedInUserProfileFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    // If sign in fails, display a message to the user
                    Log.w("Loginstatus", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * Saves the additional register information, the username, to Firebase
     */
    public void userInformation(String email, String id) {
        // Create an empty hashmap for the favorites, to use when adding items
        HashMap<String, String> favorites = new HashMap<>();

        // Get username
        EditText usernameinput = getView().findViewById(R.id.RegistrationUsernameField);
        String username = usernameinput.getText().toString();

        // Create a new instance of the class UserInfoClass for an user and insert into Firebase
        UserInfo user = new UserInfo(id, username, email);
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User");
        dbref.child(id).setValue(user);

    }

}
