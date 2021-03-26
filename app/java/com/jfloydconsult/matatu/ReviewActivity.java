package com.jfloydconsult.matatu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jfloydconsult.edas.Utility.SurveyLab;
import com.jfloydconsult.edas.model.Reviews;
import com.jfloydconsult.edas.model.Session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {
    private RatingBar mRatingBar;
    private ProgressBar mProgressBar;
    private EditText mCommentEditText;
    private Button mButton;

    private Session mSession;

    private Reviews mReview;
    public SurveyLab mSurveyLab;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("App Review");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mSession = new Session(getApplicationContext());
        // Initialise reference to views;
        mRatingBar = findViewById(R.id.activity_review_rating_bar);
        mProgressBar = findViewById(R.id.activity_review_progressBar);
        mCommentEditText = findViewById(R.id.activity_review_comment_editText);
        mButton = findViewById(R.id.activity_review_button);
        mSurveyLab = SurveyLab.get(getApplicationContext());
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSurveyLab.checkConnectivity())
                RecordReview();
            else
                Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
            }});

    }

    private void RecordReview() {
        String date;
    // initialise progress bar
    mProgressBar.setVisibility(ProgressBar.VISIBLE);
    mButton.setEnabled(false);
    myRef = myRef.child(mSession.getUserId());
    // Get date
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    date = outputDateFormat.format(calendar.getTime());
    String rate = String.valueOf(mRatingBar.getRating());
    String comment = mCommentEditText.getText().toString();
    mReview = new Reviews(comment, date,rate,mSession.getUser());
    myRef.setValue(mReview);
    Toast.makeText(getApplicationContext(), "Success!!!", Toast.LENGTH_LONG).show();
    mCommentEditText.setText("");
    mButton.setEnabled(true);
    // initialise progress bar
    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    startActivity(new Intent(getApplicationContext(), ReviewListActivity.class));
    finish();
}
}
