package com.rootekstudio.repeatsandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContentResolverCompat;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MailSignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Views settings
        setContentView(R.layout.activity_mail_sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.secondColorAccent));


        mAuth = FirebaseAuth.getInstance();
        context = this;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void registerEmail(View view) {

        TextInputEditText emailInput = findViewById(R.id.emailEditText);
        TextInputEditText passwordInput = findViewById(R.id.passwordEditText);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(context, "Success!" ,Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Failure" ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
