package com.mahendri.permatic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    static boolean called = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!called) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            called = true;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        Button mLoginButon = (Button) findViewById(R.id.button_login);
        Button mResetButton = (Button) findViewById(R.id.button_reset);


        mLoginButon.setOnClickListener(this);
        mResetButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void logIn(){
        if (!validateForm()){
            return;
        }

        showProgressDialog("Sabar. . .");
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Login gagal",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void reset(){
        startActivity(new Intent(LoginActivity.this, ResetActivity.class));
    }

    private void onAuthSuccess(FirebaseUser user) {
        mDatabase.child("anggota").child(user.getUid()).setValue(user.getEmail());
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(inputEmail.getText().toString())) {
            inputEmail.setError("isi dulu");
            return false;
        } else {
            inputEmail.setError(null);
        }

        if (TextUtils.isEmpty(inputPassword.getText().toString())) {
            inputPassword.setError("isi dulu");
            return false;
        } else {
            inputPassword.setError(null);
        }

        return true;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.button_login:
                logIn();
                break;
            case R.id.button_reset:
                reset();
                break;
        }
    }
}
