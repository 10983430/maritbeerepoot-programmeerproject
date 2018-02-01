package com.example.marit.serietrackerapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Shows the user information for the user that is logged including shortcuts to the users he follows
 * and the series he saw at least one episode from
 */
public class LoggedInUserProfileFragment extends Fragment implements View.OnClickListener {
    private HashMap<String, String> titles = new HashMap<>();
    private HashMap<String, String> userData = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set listeners
        View view = inflater.inflate(R.layout.fragment_logged_in_user_profile, container, false);
        Button login = view.findViewById(R.id.ButtonLogin);
        Button register = view.findViewById(R.id.ButtonRegister);
        Button logout = view.findViewById(R.id.ButtonLogout);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getUserData(user.getUid());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getUserData(user.getUid());
        }
        updateUI(getView());
    }

    /**
     * Sends the user to the right fragment, when clicking on the register or login button,
     * or refreshed the fragment when clicking the logout button
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ButtonLogin:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                LoginFragment loginfragment = new LoginFragment();
                fragmentTransaction.replace(R.id.fragment_container, loginfragment).addToBackStack(null).commit();
                break;
            case R.id.ButtonRegister:
                FragmentTransaction fragmentTransactionregister = getFragmentManager().beginTransaction();
                RegisterFragment registerFragment = new RegisterFragment();
                fragmentTransactionregister.replace(R.id.fragment_container, registerFragment).addToBackStack(null).commit();
                break;
            case R.id.ButtonLogout:
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
        Button login = view.findViewById(R.id.ButtonLogin);
        Button register = view.findViewById(R.id.ButtonRegister);
        login.setVisibility(View.GONE);
        register.setVisibility(View.GONE);
    }

    /**
     * Hides the logout button and headers when there is no user logged in and shows a message
     */
    public void updateWhenLoggedOut(View view) {
        Button logout = view.findViewById(R.id.ButtonLogout);
        logout.setVisibility(View.GONE);

        TextView UserInformationView = view.findViewById(R.id.UserInformationView);
        UserInformationView.setVisibility(View.GONE);

        TextView seriesseen = view.findViewById(R.id.SeriesSeen);
        seriesseen.setVisibility(View.GONE);

        TextView following = view.findViewById(R.id.Following);
        following.setVisibility(View.GONE);

        TextView loginmessage = view.findViewById(R.id.DisplayLogout);
        loginmessage.setVisibility(View.VISIBLE);
    }

    /**
     * Gets the user data from firebase
     */
    public void getUserData(String userid) {
        // Set the database references
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + userid);

        // Get information from firebase with an listener
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Go through the database to get the username and email
                setTextviews(dataSnapshot.child("username").getValue().toString(), dataSnapshot.child("email").getValue().toString());
                // Get the watched series and followed users
                getFollowedUsersWatchedSeries(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // This error can only occur when there is an server-side reason to do so
                System.out.println("FIREBASE ERROR");
            }
        });
    }

    /**
     * Gets the watched series and requests the seriename from getSerieData and gets the followed
     * users and requests their usernames from getFollowedUserData
     */
    public void getFollowedUsersWatchedSeries(DataSnapshot dataSnapshot) {
        // Get series
        HashMap<String, String> series = (HashMap<String, String>) dataSnapshot.child("SerieWatched").getValue();
        if (series != null) {
            for (String key : series.keySet()) {
                getSerieData(key);
            }
        } else {
            TextView seriesSeen = getView().findViewById(R.id.SeriesSeen);
            seriesSeen.setText(seriesSeen.getText() + " (None yet)");
        }
        // Get users
        HashMap<String, String> users = (HashMap<String, String>) dataSnapshot.child("UsersFollowed").getValue();
        if (users != null) {
            for (String key : users.keySet()) {
                getFollowedUserData(key);
            }
        } else {
            TextView following = getView().findViewById(R.id.Following);
            following.setText(following.getText() + " (None yet)");
        }
    }


    /**
     * Shows the username and email in the UI
     */
    public void setTextviews(String username, String email) {
        TextView usernameLogged = getView().findViewById(R.id.UserNameLogged);
        usernameLogged.setText(getString(R.string.usernameplaceholder) + " " + username);
        TextView useremailLogged = getView().findViewById(R.id.UserEmailLogged);
        useremailLogged.setText(getString(R.string.useremailplaceholder) + " " + email + " (Only you can see your email!)");
    }

    /**
     * Gets the data from the serie to convert the imdbid to the title of the serie
     */
    public void getSerieData(final String key) {
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + key;
        // Create new queue
        RequestQueue RequestQueue = Volley.newRequestQueue(getContext());
        // Create new stringrequest (Volley)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String reaction) {
                        try {
                            // Parse JSON to a object and make set adapter
                            parseJSON(reaction.toString(), key);
                            makeSerieListView();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue.add(stringRequest);
    }

    /**
     * Parses the title of a serie from the JSON and puts it in a Hashmap<Title, imdbid>
     */
    public void parseJSON(String response, String imdbid) throws JSONException {
        try {
            JSONObject data = new JSONObject(response);
            String title = data.getString("Title");
            titles.put(imdbid, title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a listview consisting of the series saved in Firebase
     */
    public void makeSerieListView() {
        ListView serielistview = getView().findViewById(R.id.SerieListview);
        // Create an arraylist with serienames
        ArrayList serieNames = new ArrayList<>();
        for (String key : titles.keySet()) {
            serieNames.add(titles.get(key));
        }
        // Set the adapter
        ListAdapter adapter = new UsersOverviewAdapter(getContext(), serieNames);
        serielistview.setAdapter(adapter);
        serielistview.setOnItemClickListener(new ClickDetailsSeries());
    }

    /**
     * Gets information about who the user follows from firebase
     */
    public void getFollowedUserData(final String key) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + key);

        // Get information from firebase with an listener
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Go through the database to get the username and email
                String username = dataSnapshot.child("username").getValue().toString();
                userData.put(key, username);
                makeUsersListview();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // This error can only occur when there is an server-side reason to do so
                System.out.println("FIREBASE ERROR");
            }
        });
    }

    /**
     * Creates a listview consisting of the users the logged in user is following
     */
    public void makeUsersListview() {
        ListView userslistview = getView().findViewById(R.id.UsersListview);
        // Create an arraylist with usernames
        ArrayList userNames = new ArrayList<>();
        for (String key : userData.keySet()) {
            userNames.add(userData.get(key));
        }

        // Set the adapter
        ListAdapter adapter = new UsersOverviewAdapter(getContext(), userNames);
        userslistview.setAdapter(adapter);
        userslistview.setOnItemClickListener(new ClickDetailsUsers());
    }

    /**
     * Handles a click on the listview with series
     */
    private class ClickDetailsSeries implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
            String serieTitle = ((TextView) view.findViewById(R.id.UserNameHolder)).getText().toString();
            String imdbid = new String();
            for (String key : titles.keySet()) {
                if (titles.get(key).equals(serieTitle)) {
                    imdbid = key;
                }
            }

            // Navigate to the serie details fragment
            SerieDetailsFragment fragment = new SerieDetailsFragment();
            Bundle args = new Bundle();
            args.putString("imdbid", imdbid);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .addToBackStack(null).commit();
        }
    }

    /**
     * Handles a click on the listview with followed users
     */
    private class ClickDetailsUsers implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
            String username = ((TextView) view.findViewById(R.id.UserNameHolder)).getText().toString();
            String userid = new String();
            for (String key : userData.keySet()) {
                if (userData.get(key).equals(username)) {
                    userid = key;
                }
            }

            // Navigate to the user details fragment
            UserDetailsFragment fragment = new UserDetailsFragment();
            Bundle args = new Bundle();
            args.putString("userid", userid);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .addToBackStack(null).commit();
        }
    }
}
