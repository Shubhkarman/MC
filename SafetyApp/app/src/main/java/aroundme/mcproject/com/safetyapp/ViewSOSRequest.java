package aroundme.mcproject.com.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewSOSRequest extends AppCompatActivity implements Constants {

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private final String TAG = "ViewSOS";

    private ArrayList<SOSMessage> sosMessages;
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sosrequest);

        pollToServer();


        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );
        sosMessages = new ArrayList<SOSMessage>();
        sosMessages.add(new SOSMessage("Help Me!!", "7508584253", "1.00000", "0.00000"));

        this.mAdapter = new MyListAdapter(this, sosMessages);
        mainListView.setAdapter(mAdapter);
        // creating a list view adapter
        sosMessages.clear();
        mAdapter.notifyDataSetChanged();
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                //Log.i("m", "-"+pos);

                Intent myIntent = new Intent(ViewSOSRequest.this, AcceptedSOSRequest.class);
                myIntent.putExtra("latitude", sosMessages.get(pos).latitude);
                myIntent.putExtra("longitude", sosMessages.get(pos).longitude);
                startActivity(myIntent);
            }
        });
    }

    private void pollToServer() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
        builder.appendEncodedPath(OPEN_SOS_REQUEST_URI).appendQueryParameter(
                AUTH_TOKEN, pref.getString(AUTH_TOKEN, null)).appendQueryParameter(
                        DISTANCE, pref.getString(DISTANCE, String.valueOf(1)));
        String urlString = builder.build().toString();
        Log.v(TAG, "Get Request URI: " + urlString);
        // fetching data from server
        GetRequestHandler requestHandler = new GetRequestHandler(this) {
            @Override
            protected void onPostExecute(String response) {
                try {
                    whatToDo(response);
                } catch (JSONException exception) {
                    Log.e(TAG, exception.getLocalizedMessage().toString());
                }
            }
        };
        requestHandler.execute(urlString);
//        JSONObject requestBody = new JSONObject();
//        while (true) {
//            try {
//                requestBody.put(DISTANCE, DISTANCE_VALUE);
//            } catch (JSONException exception) {
//                Log.v(TAG, exception.getStackTrace().toString());
//                exception.printStackTrace();
//            }
//
//            Log.v(TAG, "URL: " + urlString);
//            Log.v(TAG, "Request body: " + requestBody.toString());
//            PostRequestHandler handler = new PostRequestHandler(getApplicationContext()) {
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    super.onPostExecute(aVoid);
//                    if (this.response == null || this.response.isEmpty()) {
//                        Toast.makeText(this.appContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    Toast.makeText(appContext, this.response, Toast.LENGTH_SHORT).show();
//                    try {
//                        whatToDo(this.response);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            };
//            handler.execute(urlString, requestBody.toString());
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                Log.v(TAG, e.getStackTrace().toString());
//            }
//        }
    }

    public void whatToDo(String response) throws JSONException {
        Log.v("Size", "" + sosMessages.size());
        Log.v("ref", sosMessages + "");
        sosMessages.clear();
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        //string to json and parse
        JSONObject responseObj = new JSONObject(response);
        JSONArray openRequests = responseObj.getJSONArray(OPEN_SOS_REQUEST);
        Log.v(TAG, "Json array of open sos requests " + openRequests.toString());
        Log.v(TAG, openRequests.toString());
        for (int i = 0; i < openRequests.length(); i++) {
            JSONObject object = openRequests.getJSONObject(i);
            String username = object.getString("username");
            String message = object.getString("message");
            String lat = object.getString("latitude");
            String lon = object.getString("longitude");

            sosMessages.add(new SOSMessage(username,message,lat,lon));
        }
        Log.v("Dilraj", sosMessages.toString());
        mAdapter.addItem(sosMessages);
    }

    public void postRequestActivity(View view) {
        Intent intent = new Intent(this, PostSOSRequest.class);
        startActivity(intent);
    }
}
