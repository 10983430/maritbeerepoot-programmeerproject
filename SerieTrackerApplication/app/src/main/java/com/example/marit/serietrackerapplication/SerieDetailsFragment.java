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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * A simple {@link Fragment} subclass.
 */
public class SerieDetailsFragment extends Fragment implements View.OnClickListener {
    String imdbid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serie_details, container, false);
        Button search = view.findViewById(R.id.episodedetails);
        search.setOnClickListener(this);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            imdbid = bundle.getString("imdbid");
            Log.d("iiiii", imdbid);
        }
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
        getData(url);
        String url2 = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid + "&season=1";
        getData(url2);
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

    public void getData(String url) {
        // Create new queue
        RequestQueue RQe = Volley.newRequestQueue(getContext());
        // Create new stringrequest (Volley)
        StringRequest stringRequeset = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            TextView textviewtweje = getView().findViewById(R.id.textviewtje);
                            String current = textviewtweje.getText().toString();
                            textviewtweje.setText(current + response);

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
}
