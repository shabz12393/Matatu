package com.jfloydconsult.matatu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jfloydconsult.edas.Utility.SurveyLab;
import com.jfloydconsult.edas.model.Periods;
import com.jfloydconsult.edas.model.Session;
import com.jfloydconsult.edas.model.SurveyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {
    LinearLayout mainLayout, empty_layout;

    Spinner mPeriodSpinner, mAmountSpinner;
    ArrayAdapter<Periods> mAdapter;
    List<Periods> mPeriods;
    Spinner priceSpinner;
    ArrayList<String> mPriceList;
    ArrayList<String> mAmountList;
    ArrayAdapter<String> mPriceAdapter;
    ArrayAdapter<String> mAmountAdapter;
    TextView countTextView, telephoneTextView;
    EditText userEditText;
    Button incrementButton, mButton, mCheckButton, tryButton;
    Button decrementButton;
    int mPrice;
    int count;
    int mAmount;
    String mPeriod;
    public SurveyLab mSurveyLab;
    ProgressBar mProgressBar;
    private static Gson sGson = new Gson();
    private static Session sSession;
    private static SurveyModel.Result sResult;

    private String mUser;

    private String mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mainLayout = findViewById(R.id.activity_payment_main_layout);
        empty_layout = findViewById(R.id.activity_payment_empty_layout);

        mPeriodSpinner = findViewById(R.id.activity_payment_spinner_period);
        priceSpinner = findViewById(R.id.activity_payment_priceSpinner);
        mAmountSpinner = findViewById(R.id.activity_payment_amountSpinner);
        countTextView = findViewById(R.id.activity_payment_period_text_view);
        userEditText = findViewById(R.id.activity_payment_userName_editText);
        incrementButton = findViewById(R.id.activity_payment_increment_button);
        decrementButton = findViewById(R.id.activity_payment_decrement_button);
        tryButton = findViewById(R.id.activity_payment_try_button);
        telephoneTextView = findViewById(R.id.activity_payment_telephone);
        mButton = findViewById(R.id.activity_payment_button);
        mCheckButton = findViewById(R.id.activity_payment_check_button);
        mProgressBar = findViewById(R.id.activity_payment_progressBar);

        mSurveyLab = SurveyLab.get(getApplicationContext());
        sSession = new Session(getApplicationContext());

        final List<Periods> periods = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, periods);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPeriodSpinner.setAdapter(mAdapter);

        mPriceList = new ArrayList<>();
        mPriceAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, mPriceList);
        mPriceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(mPriceAdapter);

        mAmountList = new ArrayList<>();
        mAmountAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, mAmountList);
        mAmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAmountSpinner.setAdapter(mAmountAdapter);

        mPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPriceAdapter.clear();
                count = Integer.parseInt(countTextView.getText().toString().trim());
                Periods period = (Periods) adapterView.getSelectedItem();
                mPriceAdapter.add(String.valueOf(period.getPrice()));
                mPrice = period.getPrice();
                mPeriod = period.getPeriod();
                onCalculateAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onIncrement();
            }
        });

        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTryAgain();
            }
        });
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDecrement();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                mAction = "Payment";
                onCheckUser();
            }
        });

        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                mAction = "Check User";
                onCheckUser();
            }
        });
    }

    private void onTryAgain() {
        if (mSurveyLab.checkConnectivity()) {
            mainLayout.setVisibility(View.VISIBLE);
            empty_layout.setVisibility(View.INVISIBLE);
            mAdapter.clear();
            mProgressBar.setVisibility(View.VISIBLE);
            new FetchPeriodTask().execute();
        } else
            Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
    }

    private void onCheckUser() {
        if (mSurveyLab.checkConnectivity()) {
            mUser = userEditText.getText().toString().trim();
            if (!userEditText.getText().toString().trim().equals("")) {
                new onCheckUserTask().execute(mUser);
            } else {
                Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSurveyLab.checkConnectivity()) {
            mAdapter.clear();
            mProgressBar.setVisibility(View.VISIBLE);
            new FetchPeriodTask().execute();
        } else {
            mainLayout.setVisibility(View.INVISIBLE);
            empty_layout.setVisibility(View.VISIBLE);
        }
    }


    private void onIncrement() {
        count = Integer.parseInt(countTextView.getText().toString().trim());
        if (count >= 0) {
            count = count + 1;
            countTextView.setText(String.valueOf(count));
            onCalculateAmount();
        }
    }

    private void onDecrement() {
        count = Integer.parseInt(countTextView.getText().toString().trim());
        if (count > 0) {
            count = count - 1;
            countTextView.setText(String.valueOf(count));
            onCalculateAmount();
        }
    }

    private void onCalculateAmount() {
        mAmountAdapter.clear();
        mAmount = count * mPrice;
        mAmountAdapter.add(String.valueOf(mAmount));
    }



    private class FetchPeriodTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;

// Will contain the raw JSON response as a string.
            String periodJsonStr = null;

            try {
                // Construct the URL for the period query
                URL url = new URL("https://www.eaglecorsug.net/Services/Transactions.asmx/FetchPeriods");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                periodJsonStr = fetchUrlData(urlConnection);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return periodJsonStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getPeriodData(s);
        }
    }

    private String fetchUrlData(HttpURLConnection urlConnection) {
        String result;
        BufferedReader reader = null;
        int HttpResult;
        try {
            HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                // Read the input stream int a String
                InputStream inputStream;
                inputStream = urlConnection.getInputStream();

                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    result = null;
                } else
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                if (reader != null) {
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line);
                    }
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    result = null;
                }
                result = buffer.toString();
                return result;
            } else {
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e("PaymentActivity", "Error closing stream", e);
            }
        }
        return null;
    }

    private void getPeriodData(String jsonStr) {

        Gson gson = new Gson();

        Type periodListType = new TypeToken<ArrayList<Periods>>() {
        }.getType();
        mPeriods = gson.fromJson(jsonStr, periodListType);
        if (getApplicationContext() != null && mPeriods != null) {
            mAdapter.addAll(mPeriods);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    class onCheckUserTask extends AsyncTask<String, Void, String> {

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

                String json_url = getResources().getString(R.string.get_url);

                JSONObject surveyParam = new JSONObject();

                surveyParam.put("object", object);
                surveyParam.put("sign", sign);

                URL url = new URL(json_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.addRequestProperty("Accept", "application/json");
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
                Log.i("MSG", conn.getResponseMessage());
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
                Log.i("SUCCESS: ", buffer.toString());

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
            if (sResult.code == 200) {
                if (mAction.equals("Check User")) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(PaymentActivity.this).
                            setMessage("Expiry Date: " + sResult.data.getExpiredate()).setNegativeButton("Cancel", null).create().show();
                } else {
                    onPayment(sResult.data.id);
                }
            } else if (sResult.code == 700) {
                Toast.makeText(getApplicationContext(), "Signature verification failed", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "Please check Username and Password", Toast.LENGTH_LONG).show();


        }
    }

    public String Encrypt(String text, String randomKey) {
        String object_string = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
        object_string = object_string.replaceAll(System.getProperty("line.separator"), "");

        String md5String = object_string + randomKey;

        //String sign = new String(Hex.encodeHex(DigestUtils.md5(md5String)));
        String sign = mSurveyLab.MD5(md5String);

        return sign + ":" + object_string;


    }

    private void onPayment(String user_id) {
        String provider = "";
        String msisdn;
        String tel = telephoneTextView.getText().toString().trim();
        if (tel.length() == 9) {
            msisdn = "+256" + tel;
            String account_code = getResources().getString(R.string.account_code_string);
            String transaction_id = UUID.randomUUID().toString();
            String currency = "UGX";
            int charge = Integer.parseInt(getResources().getString(R.string.charge_string));
            int transaction_fee = (mAmount * charge) / 100;
            int total_amount = mAmount + transaction_fee;
            String description = "(MyCORS) Payment Request for: " + mUser;

            SurveyModel.Payment payment = new SurveyModel.Payment(account_code, transaction_id, provider, msisdn, currency,
                    total_amount, description);
            String payment_string = sGson.toJson(payment);
            new onSubmitPaymentTask().execute(payment_string, user_id);
        } else {
            Toast.makeText(getApplicationContext(), "Incorrect Phone Number", Toast.LENGTH_LONG).show();
        }
    }

    class onSubmitPaymentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String json_string = params[0];
            String user_id = params[1];

            Log.i("JSON", json_string);
            try {

                String payment_url = getResources().getString(R.string.payment_url_string);
                String token = getResources().getString(R.string.relworx_token_string);

                URL url = new URL(payment_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.addRequestProperty("Accept", "application/vnd.relworx.v2");
                String JWT = "Bearer " + token;
                conn.addRequestProperty("Authorization", JWT);
                conn.setDoOutput(true);
                conn.setDoInput(true);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(json_string);
                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());
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
                Log.i("SUCCESS: ", buffer.toString());

                conn.disconnect();
                return json_string + ";" + buffer.toString() + ";" + user_id;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String[] results = s.split(";");
            String json_string = results[0];
            String result = results[1];
            String user_id = results[2];

            try {
                JSONObject sys = new JSONObject(result);
                boolean success = sys.getBoolean("success");
                if (success) {
                    RecordTransaction(json_string, user_id);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void RecordTransaction(String json_string, String user_id) {

        SurveyModel.Payment payment = sGson.fromJson(json_string, SurveyModel.Payment.class);

        int charge = Integer.parseInt(getResources().getString(R.string.charge_string));
        int transaction_fee = (mAmount * charge) / 100;
        double relworx_charge = Double.parseDouble(getResources().getString(R.string.relworx_charge_string));
        double relworx_fee = (mAmount * relworx_charge) / 100;
        double developer_fee = (double) transaction_fee - relworx_fee;
        String tran_type = "Collection";

        SurveyModel.Logs logs = new SurveyModel.Logs(payment.account_no, payment.provider, payment.msisdn, payment.currency,
                payment.amount, mAmount, charge, developer_fee, payment.reference, payment.description, tran_type,
                user_id, mPeriod, count);

        String log_string = sGson.toJson(logs);
        new onLogTransactionTask().execute(log_string);
    }

    class onLogTransactionTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String log_string = params[0];

            Log.i("JSON", log_string);
            try {

                String log_url = getResources().getString(R.string.log_url);
                String data = "log=" + log_string;
                URL url = new URL(log_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.getOutputStream().write(data.getBytes("UTF-8"));
                conn.getInputStream();


                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());
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
                Log.i("SUCCESS: ", buffer.toString());

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
            try {
                JSONObject sys = new JSONObject(s);
                String message = sys.getString("message");
                if (message.equals("success")) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(PaymentActivity.this).
                            setMessage(message).setPositiveButton("OK", null).create().show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
