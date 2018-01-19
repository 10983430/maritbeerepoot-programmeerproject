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
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SerieDetailsFragment extends Fragment implements View.OnClickListener {
    String imdbid;
    private ArrayList<Episode> episodeitems = new ArrayList<Episode>();
    private ExpandableListAdapter adapter;
    private ExpandableListView listview;
    private List<String> SeasonList;
    private HashMap<String, List<Episode>> hashMap;
    Integer totalseasons;
    Integer count = 0;
    Serie serieinfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serie_details, container, false);
        Button search = view.findViewById(R.id.episodedetails);

        // Create the expandable list view
        ExpandableListView viewtje = view.findViewById(R.id.ExpandableListview);
        makelistview(viewtje);

        // Set a listener on the search button to make it operational
        search.setOnClickListener(this);
        return view;
    }


    private void makelistview(ExpandableListView viewtje) {
        List<String> emtDev = new ArrayList<>();
        emtDev.add("listviewtje");
        adapter =  new ExpandableListAdapter(getContext(), SeasonList, hashMap);
        viewtje.setAdapter(adapter);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        SeasonList = new ArrayList<>();
        hashMap = new HashMap<>();
        // Get the imdbid from the serie that was clicked on
        if (bundle != null) {
            imdbid = bundle.getString("imdbid");
        }

        // Get the data from the clicked serie
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
        getData(url, 1, 0);
    }


    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.episodedetails:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                EpisodeDetailsFragment frag = new EpisodeDetailsFragment();
                fragmentTransaction.replace(R.id.fragment_container, frag).addToBackStack(null).commit();

        }
    }


    /**
     * Sends an volley request to get the JSON response from the api
     * @param url url that leads to the API
     * @param type 1 for the serie information, 2 for the season information
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

                                Log.d("dsfsuewirewpirpwe", "hoiii1111");
                                parseJSONSerieDetails(response);
                            }
                            else {
                                count += 1;
                                Log.d("dsfsuewirewpirpwe", "hoiii");
                                parseJSONSeasons(response, seasonnumber);
                                Log.d("dsfsdfsdfsd", String.valueOf(episodeitems.size()));
                                fixData();
                                //fillTextviews();

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
        Log.d("dsfsuewirewpirpwe", count.toString());
        if (count == totalseasons) {
            Log.d("dsfsuewirewpirpwe", String.valueOf(totalseasons) + "   " + String.valueOf(SeasonList.size()));
            for (int i = 1; i <= serieinfo.getTotalSeasons(); i++) {
                ArrayList<Episode> listje = new ArrayList<>();
                for (int x = 1; x <= episodeitems.size(); i++) {
                    Log.d("dsffffffffffffffffffffffffff", episodeitems.get(i).getTitle() + " " + episodeitems.get(i).getSeasonnumber());
                    if (episodeitems.get(i).getSeasonnumber() == i) {
                        Log.d("dsffffffffffffffffffffffffff","lol1111112222");
                        listje.add(episodeitems.get(x));
                        Log.d("dsffffffffffffffffffffffffff","lol111111");
                    }
                }
                Log.d("dsffffffffffffffffffffffffff","lol2222222");
                hashMap.put("Season " + i, listje);
                Log.d("dsffffffffffffffffffffffffff","lol333333");
            }
            Log.d("dsffdfdfdfdfdfdfdfdfd",hashMap.toString());
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
                double imdbrating = Double.parseDouble(data.getJSONObject(i).getString("imdbRating"));
                String imdbid = data.getJSONObject(i).getString("imdbID");

                // Make the information userfull by making an class object en put alle the episodes in the list
                Episode episodeinfo = new Episode(title, released, episode, imdbrating, imdbid, seasonnumber);
                addToEpisodes(episodeinfo);
            }
            Log.d("yyyyyxxxxx", String.valueOf(episodeitems.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("yyyyyxxxxx", String.valueOf(episodeitems.toString()));
    }

    public void addToEpisodes(Episode episodeinfo){
        episodeitems.add(episodeinfo);
        Log.d("Tesstttttt23423", String.valueOf(episodeitems.size()));
    }

    /**
     * Parses the data when requesting data about the serie, also sends requests for all the seasons
     * @param response
     */
    public void parseJSONSerieDetails(String response) {
    try{
        JSONObject responsedata = new JSONObject(response);
        serieinfo = new Serie(responsedata.getString("Title"), responsedata.getString("Year"),
                responsedata.getString("Released"), responsedata.getString("Runtime"),
                responsedata.getString("Genre"), responsedata.getString("Director"),
                responsedata.getString("Writer"), responsedata.getString("Plot"),
                responsedata.getString("Language"), responsedata.getString("Country"),
                responsedata.getString("Awards"), responsedata.getString("Poster"),
                responsedata.getDouble("imdbRating"), responsedata.getString("imdbVotes"),
                responsedata.getInt("totalSeasons")) ;
        totalseasons = responsedata.getInt("totalSeasons");
        for (int i = 1; i <= serieinfo.getTotalSeasons(); i++) {
            String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid + "&season=" + String.valueOf(i);
            Log.d("dsfsuewirewpirpwe", "hoiii222");
            getData(url, 2, i);
            SeasonList.add("Season " + String.valueOf(i));
        };
    } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fillTextviews() {
        Log.d("xxxxxxxooooo", serieinfo.getAwards());
        TextView nameview = getView().findViewById(R.id.SerieNameInfo);
        nameview.setText(serieinfo.getTitle());
        TextView releaseview = getView().findViewById(R.id.SerieReleaseInfo);
        releaseview.setText(serieinfo.getReleased());
    }


}
