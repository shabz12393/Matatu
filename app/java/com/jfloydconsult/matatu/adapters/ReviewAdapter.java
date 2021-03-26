package com.jfloydconsult.matatu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jfloydconsult.edas.R;

import java.util.List;
import com.jfloydconsult.edas.model.Reviews;

public class ReviewAdapter extends ArrayAdapter<Reviews> {
    private Context mContext;
    public ReviewAdapter(Context context, int resource, List<Reviews> reviews) {
        super(context, resource, reviews);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if(v == null)
            v = LayoutInflater.from(mContext).inflate(R.layout.list_item_review,parent,false);
        Reviews reviews = getItem(position);
        TextView mNameTextView;
        TextView mDateTextView;
TextView mCommentTextView;
        RatingBar mRatingBar;
        mNameTextView = v
                .findViewById(R.id.list_item_review_name_text_view);
        mDateTextView = v
                .findViewById(R.id.list_item_review_date_text_view);
        mCommentTextView = v
                .findViewById(R.id.list_item_mast_comment_text_view);
        mRatingBar = v
                .findViewById(R.id.list_item_ratings_rating_bar);
        if(reviews!=null){
            mNameTextView.setText(reviews.getUser());
            mDateTextView.setText(reviews.getDate());
            mCommentTextView.setText(reviews.getComment());
            mRatingBar.setRating(Float.parseFloat(reviews.getRating()));
        }


        return v;
    }
}
