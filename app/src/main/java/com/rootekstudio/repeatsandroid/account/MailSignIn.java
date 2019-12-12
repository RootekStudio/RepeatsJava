package com.rootekstudio.repeatsandroid.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.activities.MainActivity;

public class MailSignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Context context;
    boolean register = true;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputEditText repeatPasswordInput;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextInputLayout repeatPasswordLayout;
    ProgressBar progressBar;
    Button acceptSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Views settings
        setContentView(R.layout.activity_mail_sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.secondColorAccent));

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        register = intent.getBooleanExtra("register", true);

        emailInput = findViewById(R.id.emailEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        repeatPasswordInput = findViewById(R.id.repeatPassword);

        emailLayout = findViewById(R.id.enterEmailLayout);
        passwordLayout = findViewById(R.id.enterPasswordLayout);
        repeatPasswordLayout = findViewById(R.id.layoutRepeatPassword);

        progressBar = findViewById(R.id.progressBarLogin);
        acceptSignIn = findViewById(R.id.registerViaEmail);

        if(!register) {
            repeatPasswordLayout.setVisibility(View.GONE);
            acceptSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkCorrectness()) {
                        progressBar.setVisibility(View.VISIBLE);
                        emailInput.setEnabled(false);
                        passwordInput.setEnabled(false);
                        acceptSignIn.setEnabled(false);

                        loginToFirebase();
                    }
                }
            });
        }
        else {
            acceptSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkCorrectness()) {
                        progressBar.setVisibility(View.VISIBLE);
                        emailInput.setEnabled(false);
                        passwordInput.setEnabled(false);
                        repeatPasswordInput.setEnabled(false);
                        acceptSignIn.setEnabled(false);

                        registerEmail();
                    }
                }
            });
        }
        context = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    boolean checkCorrectness() {
        boolean allCorrect = true;
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.incorrect_email));
            allCorrect = false;
        }
        else {
            emailLayout.setError(null);
        }

        if(password.length() < 6) {
            passwordLayout.setError(getString(R.string.password_length));
            return false;
        }
        else {
            passwordLayout.setError(null);
        }

        if(register) {
            String repeatPassword = repeatPasswordInput.getText().toString();
            if(!password.equals(repeatPassword)) {
                passwordLayout.setError(getString(R.string.passwords_notsame));
                repeatPasswordLayout.setError(getString(R.string.passwords_notsame));
                allCorrect = false;
            }
            else {
                passwordLayout.setError(null);
                repeatPasswordLayout.setError(null);
            }
        }

        return allCorrect;
    }

    public void loginToFirebase() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, getString(R.string.signin_success),
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Log.w("failure", task.getException());
                            Toast.makeText(context, getString(R.string.signin_error),
                                    Toast.LENGTH_LONG).show();

                            progressBar.setVisibility(View.GONE);
                            emailInput.setEnabled(true);
                            passwordInput.setEnabled(true);
                            acceptSignIn.setEnabled(true);
                        }
                    }
                });
    }
    public void registerEmail() {
        final String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(context, getString(R.string.signin_success) ,Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    //set nick
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String displayName = user.getDisplayName();
                    if(displayName == null) {
                        displayName = email.substring(0, email.indexOf("@"));
                    }
                    UserProfileChangeRequest nickUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build();

                    user.updateProfile(nickUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, "Success changing nick!" ,Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(context, "Nick not changed" ,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(context, getString(R.string.signup_problem) ,Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);
                    emailInput.setEnabled(true);
                    passwordInput.setEnabled(true);
                    repeatPasswordInput.setEnabled(true);
                    acceptSignIn.setEnabled(true);
                }
            }
        });
    }
}
