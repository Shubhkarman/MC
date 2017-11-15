package aroundme.mcproject.com.safetyapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

public class AcceptedSOSRequest extends AppCompatActivity {

    public String latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_sosrequests);
        Intent intent = getIntent();
        this.latitude = intent.getStringExtra("latitude");
        this.longitude = intent.getStringExtra("longitude");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ourmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_repo)
        {
            String x = (String) item.getTitle();
            item.setTitle("Reputation: " + String.valueOf(Integer.valueOf(x.charAt(x.length()-1))-48));
            return true;
        }
        else if(item.getItemId()==R.id.action_settings)
        {
            Intent i = new Intent(this,Setting.class);
            startActivity(i);
        }
        return false;
    }

    public void goToLocation(View view) {
        Log.v("Dilraj", "Calling intent for maps");
        String uri = String.format(Locale.ENGLISH, "geo:%s,%s", latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        this.startActivity(intent);
    }
}
