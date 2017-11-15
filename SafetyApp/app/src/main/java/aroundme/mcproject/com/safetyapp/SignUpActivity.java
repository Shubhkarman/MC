package aroundme.mcproject.com.safetyapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements Constants {

    private TextView nameTextView, emailTextView, passwordTextView, contactTextView;
    private Button signupButton;
    private final String TAG = "SignUpActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Getting view references
        // todo: use dependency injection using Google Guice or Dagger
        nameTextView = (TextView) findViewById (R.id.input_name_signup);
        emailTextView = (TextView) findViewById (R.id.input_email_signup);
        passwordTextView = (TextView) findViewById(R.id.input_password_signup);
        contactTextView = (TextView) findViewById(R.id.input_contact_signup);
        signupButton = (Button) findViewById(R.id.signup_button);
    }



    public void signUp(View view) {
        if (!validate()) {
            Toast.makeText(this, "Please fix the error with fields before signing up", Toast.LENGTH_SHORT).show();
            signupButton.setEnabled(true);
            return;
        }
        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = nameTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String contact = contactTextView.getText().toString();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        builder.appendPath(SIGN_UP_URI);
        String urlString = builder.build().toString();
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put(USERNAME, name);
            requestBody.put(PASSWORD, password);
            requestBody.put(EMAIL, email);
            requestBody.put (CONTACT, contact);
            requestBody.put (LATITUDE, gpsTracker.getLatitude());
            requestBody.put (LONGITUDE, gpsTracker.getLongitude());
            PostRequestHandler handler = new PostRequestHandler(getApplicationContext()) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (this.response.toLowerCase().contains("successfully"))
                        finish();
                    else
                        signupButton.setEnabled(true);
                }
            };
            Log.v(TAG, String.format("URL: %s", urlString));
            Log.v(TAG, String.format("Request Body: %s", requestBody.toString()));
            handler.execute(urlString, requestBody.toString());
        } catch (JSONException exception) {
            Log.d(TAG, exception.getMessage().toString());
        }
        progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String contact = contactTextView.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameTextView.setError("at least 3 characters");
            valid = false;
        } else
            nameTextView.setError(null);

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextView.setError("Enter a valid email address");
            valid = false;
        } else
            emailTextView.setError(null);

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordTextView.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else
            passwordTextView.setError(null);

        if (contact.isEmpty() || contact.length() < 6 || contact.length() > 13 || !android.util.Patterns.PHONE.matcher(contact).matches()) {
            contactTextView.setError("Enter a valid contact number");
            valid = false;
        } else
            contactTextView.setError(null);
        return valid;
    }

    public void loginActivity(View view) {
        finish();
    }
}

