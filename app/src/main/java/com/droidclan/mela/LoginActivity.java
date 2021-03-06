package com.droidclan.mela;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView toptext;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private Button login;
    private ProgressDialog pdialog;
    CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");

        toptext = findViewById(R.id.toptext);
        toptext.setTypeface(pacifico);

        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinwithmail();
            }
        });
    }

    private void signinwithmail(){
        String useremail = email.getText().toString();
        String userpassword = password.getText().toString();

        if (TextUtils.isEmpty(useremail)){
            email.setError("Please enter your email");
        }else if (!useremail.contains("@") || !useremail.contains(".")){
            email.setError("Please enter a valid Email");
        }else if (TextUtils.isEmpty(userpassword)){
            password.setError("Please enter your password");
        } else {
            pdialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
            pdialog.setMessage("Please wait...");
            pdialog.setIndeterminate(true);
            pdialog.setCancelable(false);
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();

            mAuth.signInWithEmailAndPassword(useremail, userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startactivity();
                                pdialog.dismiss();
                            } else {
                                String message = task.getException().toString();
                                if (message.contains("password is invalid")){
                                    pdialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Email or Password is Incorrect", Toast.LENGTH_LONG).show();
                                }else if (message.contains("There is no user")){
                                    pdialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Account doesn't exists", Toast.LENGTH_LONG).show();
                                }else {
                                    pdialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Unable to Login !", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

//
//    private void saveUserDetails(String name, String url){
//        Map<String, Object> user = new HashMap<>();
//        user.put("name",name);
//        user.put("url",url);
//
//
//        FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid())
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        startactivity();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "Something went wrong :(",Toast.LENGTH_LONG).show();
//                    }
//                });
//
//    }

    private void startactivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void buttonsState(boolean option){
        login.setEnabled(option);
    }
}
