package com.example.marit.serietrackerapplication;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SerieDetailsFragment extends Fragment {
    String imdbid;
    private ArrayList<Episode> episodeitems = new ArrayList<Episode>();
    private ExpandableListAdapter adapter;
    private ExpandableListView listview;
    private List<String> SeasonList;
    private HashMap<String, List<Episode>> hashMap;
    Integer totalseasons;
    Integer count = 0;
    Serie serieinfo;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DataSnapshot watched;
    String serieName;
    Parcelable state;
    private ArrayList seenEpisodes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serie_details, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SeasonList = new ArrayList<>();
        hashMap = new HashMap<>();
        seenEpisodes = new ArrayList();
        Bundle bundle = this.getArguments();
        // Get the imdbid from the serie that was clicked on
        Log.d("sdsfsddsf", bundle.toString());
        if (bundle != null) {

            imdbid = bundle.getString("imdbid");
        }
        synchronized (this) {
            // Get the data from the clicked serie
            String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
            getData(url, 1, 0);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getContext().getSharedPreferences("SerieDetails", MODE_PRIVATE);
        String title = prefs.getString("title", "Default");
        String plot = prefs.getString("plot", "Default");
        //String s = prefs.getString("name", "defaultValue");
        Log.d("Test", title);
        TextView view1 = getView().findViewById(R.id.SerieNameInfo);
        TextView view2 = getView().findViewById(R.id.PlotInfo);
        view1.setText(title);
        view2.setText(plot);
    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getContext().getSharedPreferences("SerieDetails", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        TextView view1 = getView().findViewById(R.id.SerieNameInfo);
        TextView view2 = getView().findViewById(R.id.PlotInfo);
        prefsEditor.putString("title", view1.getText().toString());
        prefsEditor.putString("plot", view2.getText().toString());
        //prefsEditor.put
        prefsEditor.apply();
    }

    /**
     * Sends an volley request to get the JSON response from the api
     *
     * @param url          url that leads to the API
     * @param type         1 for the serie information, 2 for the season information
     * @param seasonnumber indicates the season number, 0 when general serie information is requested
     */
    public void getData(String url, Integer type, final Integer seasonnumber) {
        // Create new queue
        RequestQueue RQe = Volley.newRequestQueue(getContext());
        final Integer type2 = type;
        // Create new stringrequest (Volley)
        StringRequest stringRequeset = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (type2 == 1) {
                                parseJSONSerieDetails(response);
                            } else {
                                count += 1;
                                parseJSONSeasons(response, seasonnumber);
                                fixData();
                                fillTextviews();

                            }

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
        RQe.add(stringRequeset);
    }

    private void fixData() {
        if (count == totalseasons) {
            for (int i = 1; i <= serieinfo.getTotalSeasons(); i++) {
                ArrayList<Episode> listje = new ArrayList<>();
                for (int x = 0; x < episodeitems.size(); x++) {
                    if (episodeitems.get(x).getSeasonnumber() == i) {

                        listje.add(episodeitems.get(x));
                    }
                }
                hashMap.put("Season " + i, listje);
            }
            setAdapter();
        }

    }

    public void setAdapter() {
        Log.d("kkkkkkkkkkkkoooooo", seenEpisodes.toString());
        adapter = new ExpandableListAdapter(getContext(), SeasonList, hashMap, imdbid);
        //adapter = new ExpandableListAdapter(getContext(), SeasonList, hashMap, imdbid, seenEpisodes);
        ExpandableListView view = getView().findViewById(R.id.ExpandableListview);
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        view.invalidateViews();

        // TO-DO: deze moet nog private!!

        view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView episodetitleholder = v.findViewById(R.id.EpisodeTitleView);
                String episodetitle = episodetitleholder.getText().toString();
                for (int x = 0; x < episodeitems.size(); x++) {
                    if (episodeitems.get(x).getTitle() == episodetitle) {
                        String imdbidepisode = episodeitems.get(x).getImdbid();
                        EpisodeDetailsFragment fragment = new EpisodeDetailsFragment();
                        Bundle args = new Bundle();
                        Log.d("testje1", imdbid.toString() + " " + imdbidepisode);
                        args.putString("imdbidserie", imdbid);
                        args.putString("imdbid", imdbidepisode);
                        fragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                    }
                }
                return true;
            }
        });

        view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    CheckBox checkBox = view.findViewById(R.id.checkBox);
                    TextView episodetitleholder = view.findViewById(R.id.EpisodeTitleView);
                    String episodetitle = episodetitleholder.getText().toString();
                    for (int x = 0; x < episodeitems.size(); x++) {
                        if (episodeitems.get(x).getTitle() == episodetitle) {
                            String imdbidepisode = episodeitems.get(x).getImdbid();
                            String seasonnumber = episodeitems.get(x).getSeasonnumber().toString();
                            String episodenumber = episodeitems.get(x).getEpisode().toString();
                            if (checkBox.isChecked()) {
                                deleteEpisodeFromFirebase(episodetitle, seasonnumber, episodenumber);
                                Toast.makeText(getContext(), "Marked as not seen", Toast.LENGTH_SHORT).show();
                                checkBox.setChecked(false);
                            } else {
                                markAsSeen(episodetitle, seasonnumber, episodenumber);
                                checkBox.setChecked(true);
                                Toast.makeText(getContext(), "Marked as seen", Toast.LENGTH_SHORT).show();

                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    public void deleteEpisodeFromFirebase(final String episodetitle, final String seasonnumber, final String episodenumber) {
        if (user != null) {
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            String userid = user.getUid();
            DatabaseReference dbref = fbdb.getReference("User/" + userid + "/SerieWatched/" + imdbid + "/Season " + seasonnumber + "/E-" + episodenumber);
            dbref.removeValue();
        }
    }

    // TO-DO dit moet een helper functie worden want dubbele code
    public void markAsSeen(final String episodetitle, final String seasonnumber, final String episodenumber) {
        if (user != null) {
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            String userid = user.getUid();
            final DatabaseReference dbref = fbdb.getReference("User/" + userid);

            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot value = dataSnapshot.child("SerieWatched");
                    HashMap<String, HashMap<String, HashMap<String, String>>> seen = (HashMap<String, HashMap<String, HashMap<String, String>>>) value.getValue();

                    // If this is the first episode ever added to firebase, create a new
                    // new hashmap for all the episodes
                    if (seen == null) {
                        seen = new HashMap<>();
                        HashMap<String, HashMap<String, String>> season = new HashMap<>();
                        HashMap<String, String> episodeHashmap = new HashMap<>();
                        episodeHashmap.put("E-" + episodenumber, episodetitle);
                        season.put("Season " + seasonnumber, episodeHashmap);
                        seen.put(imdbid, season);
                    } else {
                        // Check if there is an episode of the serie that needs to be added
                        // in the database, by checking if there is a key with the serie title
                        DataSnapshot serietitle = dataSnapshot.child("SerieWatched").child(imdbid);
                        HashMap<String, HashMap<String, String>> seriefb = (HashMap<String, HashMap<String, String>>) serietitle.getValue();

                        // If not, create a new hashmap for the serie with the episode and season in it
                        if (seriefb == null) {
                            HashMap<String, HashMap<String, String>> season = new HashMap<>();
                            HashMap<String, String> episodeHashmap = new HashMap<>();
                            episodeHashmap.put("E-" + episodenumber, episodetitle);
                            season.put("Season " + seasonnumber, episodeHashmap);
                            seen.put(imdbid, season);
                        } else {
                            // If there is a episode from a specific serie in the database,
                            // check if there is already an episode added from the season
                            DataSnapshot seasontje = dataSnapshot.child("SerieWatched").child(imdbid).child("Season " + seasonnumber);
                            HashMap<String, String> episodeHashmap = (HashMap<String, String>) seasontje.getValue();

                            // If there isn't, add the season and the episode to the hashmap
                            // with watched episodes
                            if (episodeHashmap == null) {
                                episodeHashmap = new HashMap<>();
                                episodeHashmap.put("E-" + episodenumber, episodetitle);
                                seriefb.put("Season " + seasonnumber, episodeHashmap);
                                seen.put(imdbid, seriefb);
                            }

                            // If there is, add the episode to the hasmap of the season and
                            // to the hashmap with watched episodes
                            else {
                                episodeHashmap.put("E-" + episodenumber, episodetitle);
                                seriefb.put("Season " + seasonnumber, episodeHashmap);
                                seen.put(imdbid, seriefb);
                            }
                        }
                    }

                    // Update the database by inserting the hashmap with watched episodes
                    try {
                        dbref.child("SerieWatched").setValue(seen);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    /**
     * Parses the JSON respons when requesting details about an specifid season and puts
     * all the episode information in an arraylist
     */
    public void parseJSONSeasons(String response, Integer seasonnumber) {
        try {
            // Get the JSONArray with the episodes
            JSONObject responsedata = new JSONObject(response);
            JSONArray data = responsedata.getJSONArray("Episodes");

            // Get all the different characteristics of the episode
            for (int i = 0; i < data.length(); i++) {
                String title = data.getJSONObject(i).getString("Title");
                String released = data.getJSONObject(i).getString("Released");
                Integer episode = Integer.valueOf(data.getJSONObject(i).getString("Episode"));

                // TO-DO: Dit moet een string worden want geeft double error als er geen rating is
                String imdbrating = data.getJSONObject(i).getString("imdbRating");
                String imdbid = data.getJSONObject(i).getString("imdbID");

                // Make the information userfull by making an class object en put alle the episodes in the list
                Episode episodeinfo = new Episode(title, released, episode, imdbrating, imdbid, seasonnumber);
                addToEpisodes(episodeinfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addToEpisodes(Episode episodeinfo) {
        episodeitems.add(episodeinfo);
        //Log.d("Tesstttttt23423", String.valueOf(episodeitems.size()));
    }

    /**
     * Parses the data when requesting data about the serie, also sends requests for all the seasons
     *
     * @param response
     */
    public void parseJSONSerieDetails(String response) {
        try {
            JSONObject responsedata = new JSONObject(response);
            serieinfo = setSettersSerieClass(responsedata);
            /**serieinfo = new Serie(responsedata.getString("Title"), responsedata.getString("Year"),
                    responsedata.getString("Released"), responsedata.getString("Runtime"),
                    responsedata.getString("Genre"), responsedata.getString("Director"),
                    responsedata.getString("Writer"), responsedata.getString("Plot"),
                    responsedata.getString("Language"), responsedata.getString("Country"),
                    responsedata.getString("Awards"), responsedata.getString("Poster"),
                    responsedata.getString("imdbRating"), responsedata.getString("imdbVotes"),
                    responsedata.getInt("totalSeasons"));*/
            totalseasons = responsedata.getInt("totalSeasons");
            serieName = responsedata.getString("Title");
            //seenEpisodes = findSeenEpisodes();
            for (int i = 1; i <= serieinfo.getTotalSeasons(); i++) {
                String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid + "&season=" + String.valueOf(i);
                Log.d("dsfsuewirewpirpwe", "hoiii222");
                getData(url, 2, i);
                SeasonList.add("Season " + String.valueOf(i));
            }
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Serie setSettersSerieClass(JSONObject responsedata) {
        Serie serieinfo = new Serie();
        try {
            serieinfo.setTitle(responsedata.getString("Title"));
            serieinfo.setYear(responsedata.getString("Year"));
            serieinfo.setReleased(responsedata.getString("Released"));
            serieinfo.setRuntime(responsedata.getString("Runtime"));
            serieinfo.setGenre(responsedata.getString("Genre"));
            serieinfo.setDirector(responsedata.getString("Director"));
            serieinfo.setWriter(responsedata.getString("Writer"));
            serieinfo.setPlot(responsedata.getString("Plot"));
            serieinfo.setLanguage(responsedata.getString("Language"));
            serieinfo.setCountry(responsedata.getString("Country"));
            serieinfo.setAwards(responsedata.getString("Awards"));
            serieinfo.setPoster(responsedata.getString("Poster"));
            serieinfo.setImdbrating(responsedata.getString("imdbRating"));
            serieinfo.setImdbvotes(responsedata.getString("imdbVotes"));
            serieinfo.setTotalSeasons(responsedata.getInt("totalSeasons"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return serieinfo;
    }

    public void fillTextviews() {
        TextView nameview = getView().findViewById(R.id.SerieNameInfo);
        nameview.setText(serieinfo.getTitle());

        TextView releaseview = getView().findViewById(R.id.SerieReleaseInfo);
        releaseview.setText(serieinfo.getReleased());

        TextView plotview = getView().findViewById(R.id.PlotInfo);
        plotview.setText(serieinfo.getPlot());

        TextView imdbratingview = getView().findViewById(R.id.imdbratinginfo);
        imdbratingview.setText(serieinfo.getImdbrating() + " based on " + serieinfo.getImdbvotes() + " votes");

        TextView awardsview = getView().findViewById(R.id.AwardsInfo);
        awardsview.setText(serieinfo.getAwards());
    }

}