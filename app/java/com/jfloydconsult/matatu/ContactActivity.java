package com.jfloydconsult.matatu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jfloydconsult.edas.Utility.SurveyLab;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    EditText mSubjectEditText,  mMessageEditText;
    Button mSendButton;
    ProgressBar mProgressBar;
    public SurveyLab mSurveyLab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mSubjectEditText = findViewById(R.id.activity_contact_subject_tv);
        mMessageEditText=findViewById(R.id.activity_contact_message_tv);
        mSendButton = findViewById(R.id.activity_contact_button);
        mProgressBar = findViewById(R.id.activity_contact_progressBar);
        mSurveyLab = SurveyLab.get(getApplicationContext());

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSurveyLab.checkConnectivity())
                    composeEmail();
                else
                    Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
                }
        });
    }

    private void composeEmail() {
        String subject, message;
        if(mSubjectEditText.getText().toString().trim().length()>0 && mMessageEditText.getText().toString().trim().length()>0){
            subject = mSubjectEditText.getText().toString().trim();
            message = mMessageEditText.getText().toString().trim();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            String mailto = "mailto:support@eaglecors.com" +
                    "?cc=" + "shabzy.husnam@gmail.com" +
                    "&subject=" + Uri.encode(subject) +
                    "&body=" + Uri.encode(message);

            intent.setData(Uri.parse(mailto)); // only email apps should handle this
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }else{
            Toast.makeText(this, "Enter Subject & Message", Toast.LENGTH_SHORT).show();
        }

    }
}
