package com.droidclan.mela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private ImageView back_btn;
    private CircleImageView profile_image;
    private CardView name, email, password, notification;
    private TextView profile_name, profile_email, profile_password, notification_text;
    private Button logout_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String photo_url, username, useremail, provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //Finding view By ID
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        back_btn = findViewById(R.id.back_btn);
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        profile_password = findViewById(R.id.profile_password);
        logout_btn = findViewById(R.id.logout_btn);





        //Changing the Typefaces
        Typeface extravaganzza = Typeface.createFromAsset(getAssets(), "fonts/extravaganzza.ttf");
        title.setTypeface(extravaganzza);
        profile_name.setTypeface(extravaganzza);
        profile_email.setTypeface(extravaganzza);
        profile_password.setTypeface(extravaganzza);
        logout_btn.setTypeface(extravaganzza);




        mAuth = FirebaseAuth.getInstance();
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    SettingsActivity.this.finish();
                    Intent main =  new Intent(SettingsActivity.this, LoginActivity.class);
                    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        user = mAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.onBackPressed();
            }
        });

        //Setting profile information
        getProfileDetails();


    }

    private void getProfileDetails(){
        if (user != null){

            SharedPreferences sharedPreferences = getSharedPreferences("notification", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            if (!sharedPreferences.contains("on")){
                editor.putBoolean("on", true);
                editor.apply();
            }




            photo_url = user.getPhotoUrl().toString();
            username = user.getDisplayName();
            useremail = user.getEmail();
            provider = user.getProviders().get(0);


            profile_name.setText(username);
            profile_email.setText(useremail);
            setProfileImage(photo_url);


            if (emailLogin()){
                password.setVisibility(View.VISIBLE);
            }else {
                password.setVisibility(View.GONE);
            }
        }
    }

    private void setProfileImage(String url){
        Glide.with(SettingsActivity.this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url)
                .into(profile_image);
    }


    private boolean emailLogin(){
        return provider.equals("firebase");
    }

}
