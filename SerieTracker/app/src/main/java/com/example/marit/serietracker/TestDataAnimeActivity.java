package com.example.marit.serietracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class TestDataAnimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data_anime);

        getData("http://api.jikan.me/anime/1/episodes");

        Button database = findViewById(R.id.buttonSer);
        database.setOnClickListener(new Click());
    }

    private class Click implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.buttonSer:
                    navAn();
                    break;
            }
        }
    }

    public void navAn() {
        Intent intent = new Intent(this, TestDataOMDBActivity.class);
        startActivity(intent);
    }
        /**
         * Gets data from the API
         */
    public void getData(String url) {
        // Create new queue
        RequestQueue RQ = Volley.newRequestQueue(getApplicationContext());

        // Create new stringrequest (Volley)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            TextView load = findViewById(R.id.textView);
                            load.setText(response);

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
}