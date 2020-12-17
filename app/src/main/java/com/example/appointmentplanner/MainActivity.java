package com.example.appointmentplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LoginFragment.loginFragmentListener, SignUpFragment.SignUpFragmentListener {
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                loginFragment.show(getSupportFragmentManager(),"karin");

            }
        });

        //animation for login button
        ObjectAnimator beginnerLevelBtnScaleDownX = ObjectAnimator.ofFloat(loginBtn, "scaleX", 0.97f);
        ObjectAnimator beginnerLevelBtnScaleDownY = ObjectAnimator.ofFloat(loginBtn, "scaleY", 0.97f);
        beginnerLevelBtnScaleDownX.setDuration(1300).setRepeatCount(Animation.INFINITE);
        beginnerLevelBtnScaleDownY.setDuration(1300).setRepeatCount(Animation.INFINITE);
        beginnerLevelBtnScaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        beginnerLevelBtnScaleDownY.setRepeatMode(ValueAnimator.REVERSE);
        AnimatorSet beginnerLevelBtnScaleDown = new AnimatorSet();
        beginnerLevelBtnScaleDown.playTogether(beginnerLevelBtnScaleDownX,beginnerLevelBtnScaleDownY);
        beginnerLevelBtnScaleDown.start();
        ////


        Button signUpBtn = findViewById(R.id.btn_signUp);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment signUpFragment = new SignUpFragment();
                signUpFragment.show(getSupportFragmentManager(),"karin");
            }
        });
        auth = FirebaseAuth.getInstance();

        //animation for sign up button
        ObjectAnimator SignUpAnimX = ObjectAnimator.ofFloat(signUpBtn, "scaleX", 0.97f);
        ObjectAnimator SignUpAnimY = ObjectAnimator.ofFloat(signUpBtn, "scaleY", 0.97f);
        SignUpAnimX.setDuration(1300).setRepeatCount(Animation.INFINITE);
        SignUpAnimY.setDuration(1300).setRepeatCount(Animation.INFINITE);
        SignUpAnimX.setRepeatMode(ValueAnimator.REVERSE);
        SignUpAnimY.setRepeatMode(ValueAnimator.REVERSE);
        AnimatorSet SignUpAnim = new AnimatorSet();
        SignUpAnim.playTogether(SignUpAnimX,SignUpAnimY);
        SignUpAnim.start();
        ///
    }

    // click ok on sign in after completed the email and the password
    @Override
    public void clickOk(String email, String password) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Welcome!" , Toast.LENGTH_LONG).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/Doctor/"+FirebaseAuth.getInstance().getCurrentUser().getUid() );
                    reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Intent intentq = new Intent(MainActivity.this, Doctor.class);
                            intentq.putExtra("Sign In", true);
                            startActivity(intentq);
                            finish();
                        }
                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("users/patient/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference1.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Intent intentq = new Intent(MainActivity.this, Patient.class);
                                intentq.putExtra("Sign In", true);
                                startActivity(intentq);
                                finish();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void clickCancel() {
        Toast.makeText(MainActivity.this, "Login Canceled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clickOkSignUp(String email, final String position, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent;
                    if(position.equals("Doctor"))
                         intent = new Intent(MainActivity.this, Doctor.class);
                    else
                        intent = new Intent(MainActivity.this, Patient.class);
                    intent.putExtra("Sign Up",true);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to Sign up", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void clickCancelSignUp() {
        Toast.makeText(MainActivity.this, "Sign Up Canceled", Toast.LENGTH_SHORT).show();

    }
}