package com.mahendri.permatic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class ResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        final EditText inputEmail = (EditText) findViewById(R.id.email_reset);
        Button buttonBack = (Button) findViewById(R.id.back);
        Button buttonReset = (Button) findViewById(R.id.reset);
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();

                if (email.isEmpty()){
                    Toast.makeText(ResetActivity.this, "Email kosong dul", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ResetActivity.this,
                                            "Sukses dikirim, cek email", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetActivity.this,
                                            "Gagal mengirim email reset", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}