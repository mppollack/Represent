package com.iscool.michael.represent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private com.google.android.gms.location.FusedLocationProviderClient mFusedLocationClient;
    public static final String REPRESENTATIVES_KEY = "com.iscool.michael.represent.REPRESENTATIVES_KEY";
    final Location user_location = new Location("user");

    //GeoCodio information
    private String geocodio_url = "https://api.geocod.io/v1.3/geocode?q=";
    private String geocodio_Suffix = "&fields=cd,stateleg&api_key=56d4bab0018c3b5106186ac38c85b3a3badbbdc";
    private JSONObject geocodio_response;

    //Pro-Publica information
    private String proPublica_url = "https://api.propublica.org/congress/v1/members/";
    private String proPublica_Suffix = "/current.json";
    private String proPublica_Suffix2 = ".json";
    private String proPublica_Suffix3 = "/bills/cosponsored.json";
    private String getProPublica_APIKey = "nPGyMauQ3buLU0JcaiVZIdmiC3FFVXarsSHrMy5h";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        Button use_location = (Button) findViewById(R.id.use_your_location);
        final EditText zipcode_input = (EditText) findViewById(R.id.zipcode_input);
        Button zipcode_button = (Button) findViewById(R.id.zipcode_button);



        use_location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location){
                                if (location != null) {
                                    user_location.set(location);
                                } else {
                                    Toast.makeText(MainActivity.this, "Cannot access your location, please enter zipcode below", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                ArrayList<Representative> representatives = getRepInfo_Location(user_location);
                sendReps(representatives);
            }
        });

        zipcode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipcode = zipcode_input.getText().toString();
                getDistrictNumber_Zipcode(zipcode);
            }
        });

    }

    public void sendReps(ArrayList<Representative> representatives){
        Intent intent = new Intent(MainActivity.this, All_Representative_View.class);
        intent.putExtra(REPRESENTATIVES_KEY, representatives);
        startActivity(intent);
    }


    /*
    These methods get tbe representative district numbers from Geocodio.
    I am a sad boi.
    Pls give me gud grades.
     */

    public ArrayList<Representative> getRepInfo_Location(Location l){

        String lat = Double.toString(l.getLatitude());
        String longi = Double.toString(l.getLongitude());
        String this_url = geocodio_url + lat + "," + longi + geocodio_Suffix;
        final ArrayList<Representative> representatives = new ArrayList<>();

        //This was taken from the Google developer page at:
        //https://developer.android.com/training/volley/request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, this_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getInfo(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "API Request Error (Location): " + error.toString(), Toast.LENGTH_LONG).show();

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        return representatives;
    }

    public void getDistrictNumber_Zipcode(String zipcode){
        String this_url = geocodio_url + zipcode + geocodio_Suffix;

        //This was taken from the Google developer page at:
        //https://developer.android.com/training/volley/request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, this_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getInfo(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "API Request Error (Zipcode): " + error.toString(), Toast.LENGTH_LONG).show();

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void getInfo(JSONObject response) {
        ArrayList<Representative> representatives = new ArrayList<>();
        try {
            JSONArray results = response.getJSONArray("results");
                JSONArray districts = results.getJSONObject(0).getJSONObject("fields").getJSONArray("congressional_districts");

            for (int i = 0; !districts.isNull(i); i++) {
                JSONObject d = districts.getJSONObject(i);
                JSONArray legislators = d.getJSONArray("current_legislators");

                for (int x = 0; !legislators.isNull(x); x++) {
                    Representative representative = new Representative();
                    representatives.add(representative);
                    JSONObject this_fucker = legislators.getJSONObject(x);
                    representative.setDistrict(d.getString("district_number"));

                    representative.setState(results.getJSONObject(0).getJSONObject("address_components").getString("state"));

                    representative.setName(this_fucker.getJSONObject("bio").getString("first_name")
                            + " " + this_fucker.getJSONObject("bio").getString("last_name"));

                    representative.setParty(this_fucker.getJSONObject("bio").getString("party"));

                    representative.setWebsite(this_fucker.getJSONObject("contact").getString("url"));
                    representative.setEmail(this_fucker.getJSONObject("contact").getString("contact_form"));

                    representative.setBioguide_id(this_fucker.getJSONObject("references").getString("bioguide_id"));

                    String title = this_fucker.getString("type");
                    if (title.equals("representative")) {
                        representative.setTitle("Representative");
                    } else {
                        representative.setTitle("Senator");
                    }
                }
            }
        } catch(org.json.JSONException e) {
            Toast.makeText(MainActivity.this, "Hey, please stop being a little bitch: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        sendReps(representatives);
    }

    /*
    The next two functions access data from ProPublica for the House and Senate, respectively.

    All of the following code is buggy af and it turns out that I don't need any of it so, like, bye Felicia

    public void proPublica_HouseIDs(final Representative representative) {
        //TODO: Need to fix this url?
        String this_url = proPublica_url + "house/" + representative.getState() + "/" + representative.getDistrict() + proPublica_Suffix;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, this_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id = response.getJSONArray("results").getJSONObject(0).getString("id");
                            representative.setMember_id(id);
                            getMemberInformation(representative);
                        } catch(org.json.JSONException e) {
                            Toast.makeText(MainActivity.this, "There was a JSON error while trying to get the member id:  " + e.toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "API Request Error (House): " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-type", "application/json");
                headers.put("X-API-KEY", getProPublica_APIKey);
                return headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }



    public void proPublica_SenateIDs(final Representative representative) {
        String this_url = proPublica_url + "senate/" + representative.getState() + proPublica_Suffix;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, this_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray array = response.getJSONArray("results");
                            JSONObject this_bitch = array.getJSONObject(0);
                            if (this_bitch.getString("name").equals(representative.getName())) {
                                representative.setMember_id(this_bitch.getString("id"));
                                getMemberInformation(representative);
                            }

                        } catch(org.json.JSONException e) {
                            Toast.makeText(MainActivity.this, "There was a JSON error while trying to get the member id:  " + e.toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "API Request Error (House): " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-type", "application/json");
                headers.put("X-API-KEY", getProPublica_APIKey);
                return headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void getMemberInformation(final Representative representative) {

        //This gets all of the necessary information besides the bills that they have sponsored
        String this_url = proPublica_url + representative.getMember_id() + proPublica_Suffix2;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, this_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject member = response.getJSONArray("results").getJSONObject(0);
                            JSONObject roles = member.getJSONArray("roles").getJSONObject(0);
                            representative.setParty(roles.getString("party"));
                            representative.setEndDate(roles.getString("end_date"));

                            JSONArray committees = roles.getJSONArray("committees");
                            int j = 0;
                            while (!committees.isNull(j)) {
                                String committee = committees.getJSONObject(j).getString("name");
                                representative.addCommittee(committee);
                            }
                        } catch (org.json.JSONException e) {
                            Toast.makeText(MainActivity.this, "There was a JSON error while trying to get the parties and committees: " + e.toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "API Request Error (Get Information 1): " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-type", "application/json");
                headers.put("X-API-KEY", getProPublica_APIKey);
                return headers;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        //This is a whole other loop for the fucking bills that they've sponsored
        String this_url2 = proPublica_url + representative.getMember_id() + proPublica_Suffix3;
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest
                (Request.Method.GET, this_url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray bills = response.getJSONArray("results").getJSONObject(0).getJSONArray("bills");
                            int x = 0;
                            while (!bills.isNull(x)) {
                                JSONObject bill = bills.getJSONObject(x);
                                representative.addBill(bill.getString("short_title"), bill.getString("introduced_date"));
                            }
                        } catch (org.json.JSONException e) {
                            Toast.makeText(MainActivity.this, "There was a JSON error while trying to get the bills: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "API Request Error (Get Information 1): " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-type", "application/json");
                headers.put("X-API-KEY", getProPublica_APIKey);
                return headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest2);
    }
    */

}

