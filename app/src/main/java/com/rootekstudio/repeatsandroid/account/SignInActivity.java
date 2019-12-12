package com.rootekstudio.repeatsandroid.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.account.MailSignIn;
import com.rootekstudio.repeatsandroid.activities.MainActivity;

public class SignInActivity extends AppCompatActivity {

    Context context;

    private FirebaseAuth mAuth;
    Button mailButton;
    Button googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        context = this;
        getSupportActionBar().hide();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.secondColorAccent));

        mAuth = FirebaseAuth.getInstance();

        mailButton = findViewById(R.id.signInViaEmail);
        googleButton = findViewById(R.id.signInViaGoogle);
    }

    public void emailClicked(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.login_or_signin, null);
        layout.setBackgroundColor(Color.parseColor("#D3FFFFFF"));
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(layout);
        final AlertDialog alertDialog = dialog.show();

        RelativeLayout signin = layout.findViewById(R.id.relSignin);
        RelativeLayout login = layout.findViewById(R.id.relLogin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MailSignIn.class);
                intent.putExtra("register", true);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MailSignIn.class);
                intent.putExtra("register", false);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
    }

    public void continueWithoutSignIn(View view) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void googleSignIn(View view) {
        mailButton.setEnabled(false);
        googleButton.setEnabled(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RequestCodes.RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCodes.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                mailButton.setEnabled(true);
                googleButton.setEnabled(true);

                Log.w("Tag", "Google sign in failed", e);
                Toast.makeText(context, R.string.google_auth_failed ,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("tag", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, getString(R.string.signin_success) ,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            mailButton.setEnabled(true);
                            googleButton.setEnabled(true);

                            Log.w("tag", "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, R.string.signin_error ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
