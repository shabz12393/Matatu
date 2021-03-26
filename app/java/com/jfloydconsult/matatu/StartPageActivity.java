package com.jfloydconsult.matatu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jfloydconsult.edas.Utility.SurveyLab;
import com.jfloydconsult.edas.model.Session;
import com.jfloydconsult.edas.model.SurveyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import needle.Needle;
import needle.UiRelatedProgressTask;

public class StartPageActivity extends AppCompatActivity {

    ImageButton mMapButton, mPaymentButton, mStationButton, mContactButton, mReviewButton;
    private static SurveyModel.ResponseToken sResponseToken;
    private static Gson sGson = new Gson();
    private static Session sSession;
    public SurveyLab mSurveyLab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        sSession = new Session(getApplicationContext());
        mSurveyLab = SurveyLab.get(getApplicationContext());

        mMapButton = findViewById(R.id.activity_start_page_map_btn);
        mPaymentButton = findViewById(R.id.activity_start_page_payment_btn);
        mStationButton = findViewById(R.id.activity_start_page_station_btn);
        mContactButton = findViewById(R.id.activity_start_page_contact_btn);
        mReviewButton = findViewById(R.id.activity_start_page_review_btn);

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
        mStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MastListActivity.class));
            }
        });

        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ContactActivity.class));
            }
        });
        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReviewListActivity.class));
            }
        });
        mPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        OnCheckUser();
    }

    private void OnCheckUser() {
        if(sSession.getUserId()==null || sSession.getUserId().equals("")){
            startActivity(new Intent(getApplicationContext(), StartPageActivity.class));

        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date strDate;
            try {
                strDate = sdf.parse(sSession.getExpiryDate());
                if (new Date().after(strDate)) {
                    if (mSurveyLab.checkConnectivity())
                        FetchLoginInfo();
                    else
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                onSignOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSignOut() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date strDate;
        try {
            strDate = sdf.parse(sSession.getExpiryDate());
            if (new Date().after(strDate))
                    sSession.clearSession();
                else
                    sSession.clearUserCredential();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

    private void FetchLoginInfo(){
        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<String, Void>() {

            @Override
            protected String doWork() {
                String auth_url = getResources().getString(R.string.auth_url);
                String userName = getResources().getString(R.string.devname_string);
                String password = getResources().getString(R.string.devname_password_string);
                // Create the Request Body
                JSONObject jsonParam = new JSONObject();

                try {
                    jsonParam.put("userName", userName);
                    jsonParam.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    // Create a URL Object
                    URL url = new URL(auth_url);
                    // Open a connection
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // Set the request method
                    conn.setRequestMethod("POST");
                    // Set the Request Content-Type Header Parameter
                    conn.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    // Set Response Format Type
                    //conn.addRequestProperty("Accept","application/vnd.relworx.v2");

                    //conn.addRequestProperty("Accept","application/json");
                    //  conn.addRequestProperty("Authorization","Bearer "+key);
                    // Ensure the connection will be used to send content
                    conn.setDoOutput(true);


                    Log.i("JSON", jsonParam.toString());

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                    // Read the Response from Input Stream
                    InputStream inputStream = conn.getInputStream();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    Log.i("SUCCESS", buffer.toString());


                    return buffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(String s) {
                Calendar calendar = Calendar.getInstance();
                s = s.replaceAll(System.getProperty("line.separator"), "");
                sResponseToken =  sGson.fromJson(s, SurveyModel.ResponseToken.class);
                sSession.setRandomKey(sResponseToken.getRandomKey());
                sSession.setToken(sResponseToken.getToken());
                String expiresIn = sResponseToken.getExpiresIn();
                calendar.add(Calendar.SECOND, Integer.parseInt(expiresIn));
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String date = outputDateFormat.format(calendar.getTime());
                sSession.setExpiryDate(date);
            }
            @Override
            protected void onProgressUpdate(Void aVoid) {

            }


        });
    }
}
