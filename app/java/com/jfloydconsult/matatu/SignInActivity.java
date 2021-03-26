package com.jfloydconsult.matatu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class SignInActivity extends AppCompatActivity {

    EditText mUsernameEditText, mPasswordEditText;
    Button mButton;
    ProgressBar mProgressBar;
    private static SurveyModel.ResponseToken sResponseToken;
    private static Gson sGson = new Gson();
    private static Session sSession;
    public SurveyLab mSurveyLab;
    private String mPassword;
    private String mUser;
    private static SurveyModel.Result sResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_);
        sSession = new Session(getApplicationContext());
        mSurveyLab = SurveyLab.get(getApplicationContext());
        mUsernameEditText = findViewById(R.id.activity_customer_login_email_et);
        mPasswordEditText = findViewById(R.id.activity_customer_login_password_et);
        mButton = findViewById(R.id.activity_customer_login_button);
        mProgressBar=findViewById(R.id.activity_customer_login_pb);
        mProgressBar.setVisibility(View.INVISIBLE);

        //FetchLoginInfo();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSurveyLab.checkConnectivity()) {
                    String userName = mUsernameEditText.getText().toString().trim();
                    mPassword = mPasswordEditText.getText().toString().trim();
                    new SignInTask().execute(userName);
                } else
                    Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
       }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(sSession.getUserId()==null || sSession.getUserId().equals("")){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date strDate;
        try {
            if(!sSession.getExpiryDate().equals("")){
                strDate = sdf.parse(sSession.getExpiryDate());
                if (new Date().after(strDate)){
                    sSession.clearSession();
                    FetchLoginInfo();
                }
            }else{
                FetchLoginInfo();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        }
        else
            startActivity(new Intent(getApplicationContext(), StartPageActivity.class));
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
            Log.i("expiry date: ", date);
        }
        @Override
        protected void onProgressUpdate(Void aVoid) {

        }


    });
}
   private class SignInTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mUser = params[0];

            try {
                String text = "{" + "\"name\"" + ":" + "\"" + mUser + "\"" + "}";
                String randomKey = sSession.getRandomKey();
                String token = sSession.getToken();
                String values = Encrypt(text, randomKey);
                String[] results = values.split(":");
                String sign = results[0];
                String object = results[1];

                String json_url=getResources().getString(R.string.get_url);

                JSONObject surveyParam = new JSONObject();

                surveyParam.put("object", object);
                surveyParam.put("sign", sign);

                URL url = new URL(json_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
                //conn.addRequestProperty("Accept","application/json");
                String JWT = "Bearer " + token;
                conn.addRequestProperty("Authorization", JWT);
                conn.setDoOutput(true);
                conn.setDoInput(true);


                Log.i("JSON", surveyParam.toString());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(surveyParam.toString());
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

                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                conn.disconnect();
                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

       @Override
       protected void onPostExecute(String s) {
           super.onPostExecute(s);
           sResult = sGson.fromJson(s, SurveyModel.Result.class);
           if(sResult.code==200){
               if(mPassword.equals(sResult.getData().getPassword())){
                   // set session.
                   sSession.setUserId(sResult.getData().getId());
                   sSession.setUser(mUser);
                   startActivity(new Intent(getApplicationContext(), StartPageActivity.class));
                   finish();
               }
           }else if(sResult.code==700){
               Toast.makeText(getApplicationContext(),"Signature verification failed", Toast.LENGTH_LONG).show();
           }else
               Toast.makeText(getApplicationContext(),"Please check Username and Password", Toast.LENGTH_LONG).show();
             }

       @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public String Encrypt(String text, String randomKey)
    {
        String object_string = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
        object_string = object_string.replaceAll(System.getProperty("line.separator"), "");

        String md5String = object_string + randomKey;

        //String sign = new String(Hex.encodeHex(DigestUtils.md5(md5String)));
        String sign = mSurveyLab.MD5(md5String);

        return sign + ":" + object_string;


    }
    private class FetchLoginInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
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

                conn.addRequestProperty("Accept","application/json");
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
        protected void onPreExecute() {}

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sResponseToken =  sGson.fromJson(s, SurveyModel.ResponseToken.class);
            sSession.setRandomKey(sResponseToken.getRandomKey());
            sSession.setToken(sResponseToken.getToken());

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


}
