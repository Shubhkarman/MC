package aroundme.mcproject.com.safetyapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dilraj on 11/14/2017.
 */

public class GetRequestHandler extends AsyncTask <String, Void, String> implements Constants {

    protected Context appContext;
    protected String response = null;
    private final String TAG = "GetRequestHandler";

    public GetRequestHandler(Context appContext) {
        this.appContext = appContext;
    }
    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String updates = null;

        try {
            URL url = new URL(params[0]);

            Log.v("Built URI " , params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            updates = buffer.toString();
            Log.v("Fetched string: ", updates);

            // parsing the get response
            return updates;
        }
        catch (IOException e) {
            Log.e("Error ", e.toString());
            return null;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Error closing stream", e.toString());
                }
            }
        }
    }
}

