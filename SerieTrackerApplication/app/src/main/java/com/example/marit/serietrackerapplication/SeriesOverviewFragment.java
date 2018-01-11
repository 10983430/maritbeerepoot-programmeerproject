package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesOverviewFragment extends ListFragment {
    private ArrayList<SearchResult> items = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_series_overview, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData("http://www.omdbapi.com/?apikey=14f4cb52&type=series&s=robot");
    }


    public void getData(String url) {
        // Create new queue
        RequestQueue RQ = Volley.newRequestQueue(getContext());
        // Create new stringrequest (Volley)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse JSON to a object and make set adapter
                            parseJSON(response.toString());
                            makeListView(items);

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
        RQ.add(stringRequest);
    }

    /**
     * Parses the JSON string to a SearchResult instance
     */
    public void parseJSON(String respons) throws JSONException {
        try {
            JSONObject data = new JSONObject(respons);
            JSONArray results = data.getJSONArray("Search");
            for (int i = 0; i < results.length(); i++) {
                items.add(new SearchResult(results.getJSONObject(i).getString("Title"),
                                            results.getJSONObject(i).getString("Poster"),
                                            results.getJSONObject(i).getString("imdbID"),
                                            results.getJSONObject(i).getString("Year")));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the listviewadapter
     */
    public void makeListView(ArrayList<SearchResult> items) {
        SeriesOverviewAdapter adapter = new SeriesOverviewAdapter(getContext(), items);
        this.setListAdapter(adapter);
    }
}
