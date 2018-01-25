package com.example.marit.serietrackerapplication;

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


public class LoggedInUserProfileFragment extends Fragment implements View.OnClickListener {
    HashMap<String, String> titles = new HashMap<>();
    HashMap<String, String> userdata = new HashMap<>();
    public View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set listeners
        view = inflater.inflate(R.layout.fragment_logged_in_user_profile, container, false);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getUserData(user.getUid());
        }
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
        TextView loginmessage = view.findViewById(R.id.DisplayLogout);
        loginmessage.setVisibility(View.VISIBLE);
        TextView UserInformationView = view.findViewById(R.id.UserInformationView);
        UserInformationView.setVisibility(View.GONE);
        TextView seriesseen = view.findViewById(R.id.seriesseen);
        seriesseen.setVisibility(View.GONE);
        TextView following = view.findViewById(R.id.following);
        following.setVisibility(View.GONE);
    }

    /**
     *
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
                String username = dataSnapshot.child("username").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                setTextviews(username, email);
                HashMap<String, String> series = (HashMap<String, String>) dataSnapshot.child("SerieWatched").getValue();
                if (series != null) {
                    //findSerieNames(series);
                    for (String key : series.keySet()) {
                        getSerieData(key);
                    }
                }
                else {
                    TextView seriesseen = view.findViewById(R.id.seriesseen);
                    seriesseen.setText(seriesseen.getText() + "(None yet)");
                }
                HashMap<String, String> users = (HashMap<String, String>) dataSnapshot.child("UsersFollowed").getValue();
                if (users != null) {
                    for (String key : users.keySet()) {
                        getFollowedUserData(key);
                    }
                    Log.d("ooooooo", "test2");

                }
                else {
                    TextView following = view.findViewById(R.id.following);
                    following.setText(following.getText() + "(None yet)");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log error
                Log.d("Database error", databaseError.toString());
            }
        });
    }

    public void setTextviews(String username, String email) {
        TextView usernameLogged = view.findViewById(R.id.usernameLogged);
        usernameLogged.setText(getString(R.string.usernameplaceholder) + username);
        TextView useremailLogged = view.findViewById(R.id.useremailLogged);
        useremailLogged.setText(getString(R.string.useremailplaceholder) + email + " (Only you can see your email!)");
    }

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
                            //Log.d("oooooo", key);
                            parseJSON(reaction.toString(), key);
                            //Log.d("lollol", titles.toString());
                            makeListView();

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

    public void makeListView() {
        ListView serielistview = view.findViewById(R.id.serielistview);
        ArrayList<String> serienames = new ArrayList<>();
        for (String key : titles.keySet()) {
            serienames.add(titles.get(key));
        }
        //TO-DO useroverview naam aanpassen
        ListAdapter adapter = new UsersOverviewAdapter(getContext(), serienames);
        serielistview.setAdapter(adapter);
        serielistview.setOnItemClickListener(new ClickDetailsSeries());
    }

    public void getFollowedUserData(final String key) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + key);

        // Get information from firebase with an listener
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Go through the database to get the username and email
                String username = dataSnapshot.child("username").getValue().toString();
                userdata.put(key, username);
                makeUsersListview();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void makeUsersListview() {
        ListView userslistview = view.findViewById(R.id.userslistview);
        ArrayList<String> usernames = new ArrayList<>();
        for (String key : userdata.keySet()) {
            usernames.add(userdata.get(key));
        }
        //TO-DO useroverview naam aanpassen
        ListAdapter adapter = new UsersOverviewAdapter(getContext(), usernames);
        userslistview.setAdapter(adapter);
        userslistview.setOnItemClickListener(new ClickDetailsUsers());
    }

    private class ClickDetailsSeries implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
            TextView hidden = view.findViewById(R.id.usernameHolder);
            String serietitle = hidden.getText().toString();
            String imdbid = new String();
            for (String key : titles.keySet()) {
                if (titles.get(key) == serietitle) {
                    imdbid = key;
                }
            }
            SerieDetailsFragment fragment = new SerieDetailsFragment();
            Bundle args = new Bundle();
            args.putString("imdbid", imdbid);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }
    }

    private class ClickDetailsUsers implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
            TextView hidden = view.findViewById(R.id.usernameHolder);
            String username = hidden.getText().toString();
            String userid = new String();
            for (String key : userdata.keySet()) {
                if (userdata.get(key) == username) {
                    userid = key;
                }
            }
            UserDetailsFragment fragment = new UserDetailsFragment();
            Bundle args = new Bundle();
            args.putString("userid", userid);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }
    }
}
