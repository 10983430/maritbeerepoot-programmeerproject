package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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


public class SeriesOverviewFragment extends ListFragment implements View.OnClickListener {
    private ArrayList<SearchResult> items = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_series_overview, container, false);

        // Put a listener on the bottom to make search possible
        Button search = view.findViewById(R.id.buttonSearch);
        search.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setOnItemClickListener(new ClickDetails());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData("http://www.omdbapi.com/?apikey=14f4cb52&type=series&s=robot");
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.buttonSearch:
                // Get the searchinput
                EditText searchinputfield = getView().findViewById(R.id.editTextSearch);
                String searchinput = searchinputfield.getText().toString();

                // Put back the start list when searchinput is empty, but search is clicked
                if (searchinput.length() != 0) {

                    // Clear the current list with results
                    items = new ArrayList<>();

                    // Get the data
                    String url = "http://www.omdbapi.com/?apikey=14f4cb52&type=series&s=" + searchinput;
                    getData(url);
                }
        }
    }

    /**
     * Gets the data from the api and calls the parser function on the data
     */
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

        getListView().setOnItemClickListener(new ClickDetails());
    }

    /**
     * Makes the listview clickable and shares the id to get the data in the next fragment
     */
    private class ClickDetails implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
            TextView hidden = view.findViewById(R.id.hidden);
            String imdbid = hidden.getText().toString();

            SerieDetailsFragment fragment = new SerieDetailsFragment();
            Bundle args = new Bundle();
            args.putString("imdbid", imdbid);
            fragment.setArguments(args);

            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }
    }
}
