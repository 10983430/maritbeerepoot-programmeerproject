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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Shows details from the serie to the UI
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

    /**
     * Checks if all the data is gathered and puts the data in de right format
     */
    private void fixData() {
        if (count == totalseasons) {
            for (int i = 1; i <= Integer.parseInt(serieinfo.getTotalSeasons()); i++) {
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


    /**
     * Sets the expandable listview adapter and puts listeners on it
     */
    public void setAdapter() {
        // Set adapter
        adapter = new ExpandableListAdapter(getContext(), SeasonList, hashMap, imdbid);
        ExpandableListView view = getView().findViewById(R.id.ExpandableListview);
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        // Put listeners on the adapter
        view.setOnChildClickListener(new ChildClickListener());
        view.setOnItemLongClickListener(new ChildLongClickListener());
    }

    /**
     * Navigates user to the corresponding EpisodeDetailsFragment, when clicking on a child
     */
    private class ChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            TextView episodetitleholder = v.findViewById(R.id.EpisodeTitleView);
            String episodetitle = episodetitleholder.getText().toString();
            for (int x = 0; x < episodeitems.size(); x++) {
                if (episodeitems.get(x).getTitle() == episodetitle) {
                    String imdbidepisode = episodeitems.get(x).getImdbid();
                    EpisodeDetailsFragment fragment = new EpisodeDetailsFragment();
                    Bundle args = new Bundle();
                    args.putString("imdbidserie", imdbid);
                    args.putString("imdbid", imdbidepisode);
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
            }
            return true;
        }
    }

    /**
     * Marks an episode as seen or unseen on a long click
     */
    private class ChildLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                CheckBox checkBox = view.findViewById(R.id.CheckBox);
                // Get the object from class Episode that was clicked on
                Episode clickedEpisode = (Episode) parent.getAdapter().getItem(position);

                if (user != null) {
                    String seasonnumber = clickedEpisode.getSeasonnumber().toString();
                    String episodenumber = clickedEpisode.getEpisode().toString();
                    String episodetitle = clickedEpisode.getTitle();

                    if (checkBox.isChecked()) {
                        // If the checkbox is checked, this means that the episode was already seen,
                        // so mark it as unseen
                        FireBaseHelper.deleteEpisodeFromFirebase(seasonnumber, episodenumber, user, imdbid);
                        Toast.makeText(getContext(), "Marked as not seen", Toast.LENGTH_SHORT).show();
                        checkBox.setChecked(false);
                    } else {
                        // If it was not seen yet, mark it as seen
                        FireBaseHelper.markAsSeen(episodetitle, seasonnumber, episodenumber, user, imdbid);
                        checkBox.setChecked(true);
                        Toast.makeText(getContext(), "Marked as seen", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                } else {
                    Toast.makeText(getContext(), "Please log in to mark episodes as seen", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
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
                Integer episode = Integer.valueOf(data.getJSONObject(i).getString("Episode"));
                String imdbid = data.getJSONObject(i).getString("imdbID");

                // Make an Episode class object en put all the episode in the list
                Episode episodeinfo = new Episode(title, episode, imdbid, seasonnumber);
                episodeitems.add(episodeinfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Parses the data when requesting data about the serie, also sends requests for all the seasons
     */
    public void parseJSONSerieDetails(String response) {
        try {
            JSONObject responsedata = new JSONObject(response);
            serieinfo = setSettersSerieClass(responsedata);
            serieName = responsedata.getString("Title");
            if (!responsedata.getString("totalSeasons").equals("N/A")) {
                totalseasons = Integer.parseInt(responsedata.getString("totalSeasons"));
                for (int i = 1; i <= Integer.parseInt(serieinfo.getTotalSeasons()); i++) {
                    String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid + "&season=" + String.valueOf(i);
                    getData(url, 2, i);
                    SeasonList.add("Season " + String.valueOf(i));
                }
            } else {
                fillTextviews();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets all the setters of the Serie class
     */
    public Serie setSettersSerieClass(JSONObject responsedata) {
        Serie serieinfo = new Serie();
        try {
            Log.d("serieinfo", responsedata.toString());
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
            serieinfo.setTotalSeasons(responsedata.getString("totalSeasons"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serieinfo;
    }

    /**
     * Puts the serie information in the textiews
     */
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

        ImageView imageView = getView().findViewById(R.id.imageView);
        if (!serieinfo.getPoster().equals("N/A")) {
            Picasso.with(getContext()).load(serieinfo.getPoster()).into(imageView);
        }
    }

}