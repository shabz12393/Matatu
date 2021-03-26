package com.jfloydconsult.matatu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jfloydconsult.edas.Utility.SurveyLab;
import com.jfloydconsult.edas.adapters.ReviewAdapter;
import com.jfloydconsult.edas.model.GeoPoints;
import com.jfloydconsult.edas.model.Reviews;

import java.util.ArrayList;
import java.util.List;

public class ReviewListActivity extends AppCompatActivity {
    LinearLayout mainLayout, empty_layout;

    private static final String TAG = "ReviewActivity";
    ListView mListView;
    ProgressBar mProgressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("App Review");
    ReviewAdapter mReviewAdapter;
    FloatingActionButton mButton;
    Button tryButton;
    public SurveyLab mSurveyLab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        mainLayout = findViewById(R.id.activity_review_list_main);
        empty_layout = findViewById(R.id.activity_review_list_empty_view);

        mListView = findViewById(R.id.activity_review_list_view);
        mProgressBar= findViewById(R.id.activity_list_review_progressBar);
        mButton = findViewById(R.id.activity_review_Add_fab);
        tryButton = findViewById(R.id.activity_review_list_try_button);
        List<Reviews> reviews = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(this, R.layout.list_item_review, reviews);
        mListView.setAdapter(mReviewAdapter);
        mSurveyLab = SurveyLab.get(getApplicationContext());

        if (mSurveyLab.checkConnectivity()) {
            mReviewAdapter.clear();
            mProgressBar.setVisibility(View.VISIBLE);
            onFetchReview();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mainLayout.setVisibility(View.INVISIBLE);
            empty_layout.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.INVISIBLE);
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReviewActivity.class));
            }
        });

        tryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTryAgain();
            }
        });
    }


    private void onTryAgain() {
        if (mSurveyLab.checkConnectivity()) {
            mainLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.VISIBLE);
            empty_layout.setVisibility(View.INVISIBLE);
            mReviewAdapter.clear();
            onFetchReview();
        } else
            Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
    }
    public void onFetchReview() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Reviews reviews = dataSnapshot.getValue(Reviews.class);
                if(reviews!=null){
                    //Masts masts = new Masts(geoPoints, 0);
                    // mMastAdapter.add(masts);
                    mReviewAdapter.add(reviews);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                GeoPoints geoPoints = dataSnapshot.getValue(GeoPoints.class);
                //  if(geoPoints!=null)
                //Log.d(TAG, "Base:" + geoPoints.getBase());

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String geoPointKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                GeoPoints geoPoints = dataSnapshot.getValue(GeoPoints.class);
                // if(geoPoints!=null)
                //     Log.d(TAG, "Base:" + geoPoints.getBase());

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };
        mProgressBar.setVisibility(View.INVISIBLE);
        myRef.addChildEventListener(childEventListener);
    }
}
