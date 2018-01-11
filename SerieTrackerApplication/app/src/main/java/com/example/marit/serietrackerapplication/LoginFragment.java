package com.example.marit.serietrackerapplication;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;

    @Override
    public void onClick(View view){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.loginButton:
                EditText emailfield = getView().findViewById(R.id.emailField);
                EditText passwordfield = getView().findViewById(R.id.passwordField);
                String email = emailfield.getText().toString();
                String password = passwordfield.getText().toString();
                SignIn(email, password);

        }}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void SignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Loginstatus", "signInWithEmail:success");
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

    public void updateUI(FirebaseUser  user) {
        // If login is succesfull, navigate user to the database
        if (user != null) {
            Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "Failed, try again", Toast.LENGTH_SHORT).show();
        }
    }
}
