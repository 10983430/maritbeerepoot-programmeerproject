package com.example.marit.serietrackerapplication;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
    private String imdbid;
    private ArrayList<Episode> episodeItems = new ArrayList<Episode>();
    private List<String> SeasonList = new ArrayList<>();
    private Integer count = 0;
    private Serie serieInfo;

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
        // Get the imdbid from the serie that was clicked on
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            imdbid = bundle.getString("imdbid");

            // Override the shared preferences, so that the episode clicked on before
            // is not in de shared preferences anymore
            SharedPreferences prefs = getContext().getSharedPreferences("SerieDetails", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putString("imdbid", imdbid);
            prefsEditor.apply();
        }
        // Get data
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
        getData(url, 1, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getContext().getSharedPreferences("SerieDetails", MODE_PRIVATE);
        String imdbid = prefs.getString("imdbid", "Default");
        count = 0;
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
        getData(url, 1, 0);
    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getContext().getSharedPreferences("SerieDetails", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("imdbid", imdbid);
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
                                // This counts keeps track of if every season is requested
                                count += 1;
                                parseJSONSeasons(response, seasonnumber);

                                // Get data ready for the expandable listview
                                fixData();

                                // Set textviews
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
                Episode episodeInfo = new Episode(title, episode, imdbid, seasonnumber);
                episodeItems.add(episodeInfo);
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
            // Parse JSON to instance of class Serie
            JSONObject responseData = new JSONObject(response);
            serieInfo = setSettersSerieClass(responseData);
            // Send an request for every season
            getAdditionalInfo(responseData.getString("totalSeasons"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends requests to the API for every season of a serie
     */
    public void getAdditionalInfo(String numberOfSeasons) {
        // Check if there are seasons known in the API
        if (!numberOfSeasons.equals("N/A")) {
            //totalseasons = Integer.parseInt(numberOfSeasons);
            for (int i = 1; i <= Integer.parseInt(serieInfo.getTotalSeasons()); i++) {
                String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid + "&season=" + String.valueOf(i);
                getData(url, 2, i);
                // Keep an list with the name of every season
                if (!SeasonList.contains("Season " + String.valueOf(i))) {
                    SeasonList.add("Season " + String.valueOf(i));
                }
            }
        } else {
            fillTextviews();
        }
    }

    /**
     * Sets all the setters of the Serie class
     */
    public Serie setSettersSerieClass(JSONObject responsedata) {
        Serie serieinfo = new Serie();
        try {
            serieinfo.setTitle(responsedata.getString("Title"));
            serieinfo.setReleased(responsedata.getString("Released"));
            serieinfo.setPlot(responsedata.getString("Plot"));
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
     * Checks if all the data is gathered and puts the data in de right format
     */
    private void fixData() {
        // Check if the information off all seasons is requested
        if (count == Integer.valueOf(serieInfo.getTotalSeasons())) {
            // Create an hashmap that consists of the season name and the episodes
            // from that season
            HashMap<String, List<Episode>> seasonAndEpisodes = new HashMap<>();
            for (int i = 1; i <= Integer.parseInt(serieInfo.getTotalSeasons()); i++) {
                ArrayList<Episode> episodes = new ArrayList<>();
                for (int x = 0; x < episodeItems.size(); x++) {
                    if (episodeItems.get(x).getSeasonnumber() == i) {
                        episodes.add(episodeItems.get(x));
                    }
                }
                seasonAndEpisodes.put("Season " + i, episodes);
            }
            setAdapter(seasonAndEpisodes);
        }

    }

    /**
     * Sets the expandable listview adapter and puts listeners on it
     */
    public void setAdapter(HashMap hashMap) {
        ExpandableListAdapter adapter = new ExpandableListAdapter(getContext(), SeasonList, hashMap, imdbid);
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
            // Get the name of the clicked episode
            TextView episodetitleholder = v.findViewById(R.id.EpisodeTitleView);
            String episodetitle = episodetitleholder.getText().toString();

            // Get the corresponding imdbid
            for (int x = 0; x < episodeItems.size(); x++) {
                if (episodeItems.get(x).getTitle() == episodetitle) {
                    String imdbidepisode = episodeItems.get(x).getImdbid();

                    // Navigate to the fragment with details about the episode
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
     * Puts the serie information in the textiews
     */
    public void fillTextviews() {
        ((TextView) getView().findViewById(R.id.SerieNameInfo)).setText(serieInfo.getTitle());
        ((TextView) getView().findViewById(R.id.SerieReleaseInfo)).setText(serieInfo.getReleased());
        ((TextView) getView().findViewById(R.id.PlotInfo)).setText(serieInfo.getPlot());
        ((TextView) getView().findViewById(R.id.AwardsInfo)).setText(serieInfo.getAwards());

        TextView imdbratingView = getView().findViewById(R.id.imdbratinginfo);
        imdbratingView.setText(serieInfo.getImdbrating() + " based on " + serieInfo.getImdbvotes() + " votes");

        ImageView imageView = getView().findViewById(R.id.imageView);
        if (!serieInfo.getPoster().equals("N/A")) {
            Picasso.with(getContext()).load(serieInfo.getPoster()).into(imageView);
        }
    }

}