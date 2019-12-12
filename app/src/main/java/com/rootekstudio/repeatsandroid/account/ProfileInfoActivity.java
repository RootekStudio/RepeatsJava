package com.rootekstudio.repeatsandroid.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.activities.MainActivity;
import com.rootekstudio.repeatsandroid.community.GroupsListActivity;
import com.rootekstudio.repeatsandroid.community.MySetsActivity;

public class ProfileInfoActivity extends AppCompatActivity {

    Context context;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_profile_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        TextView nick = findViewById(R.id.nickInfo);
        nick.setText(MainActivity.userNick);
        TextView email = findViewById(R.id.emailInfo);
        email.setText(MainActivity.userEmail);

        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    public void mySets (View view) {
        Intent intent = new Intent(this, MySetsActivity.class);
        startActivity(intent);
    }

    public void changePassword(View view) {

    }

    public void logout(View view) {
        if (account != null) {
            view.setEnabled(false);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            MainActivity.mAuth.signOut();
                            Toast.makeText(context, R.string.logout_success, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
        }
        else {
            MainActivity.mAuth.signOut();
            Toast.makeText(context, R.string.logout_success, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
