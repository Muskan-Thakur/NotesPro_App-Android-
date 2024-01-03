package com.mt.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailText, passwordText;
    Button loginbtn;
    ProgressBar pb;
    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText=findViewById(R.id.email_edit_text);
        passwordText=findViewById(R.id.password_edit_text);
        loginbtn=findViewById(R.id.Login_account_btn);
        pb=findViewById(R.id.Progress_bar);
        createAccountBtnTextView=findViewById(R.id.create_account_text_view_btn);

        loginbtn.setOnClickListener((v)->loginUser());
        createAccountBtnTextView.setOnClickListener((v)-> startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }
    void loginUser()
    {
        String email=emailText.getText().toString();
        String password=passwordText.getText().toString();

        boolean isValidated=validateData(email,password);
        if(!isValidated)
        {
            return;
        }
        loginAccountInFirebase(email,password);

    }
    void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()) {
                    //login is success
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        //got to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    } else {

                        Toast.makeText(LoginActivity.this, "Email not verified, Please verify your email", Toast.LENGTH_SHORT).show();
                    }
                }
                    else {
                        //login failed

                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }


    void changeInProgress(boolean inProgress){
        if(inProgress)
        {
            pb.setVisibility(View.VISIBLE);
            loginbtn.setVisibility(View.GONE);
        }
        else {
            pb.setVisibility(View.GONE);
            loginbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password)
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email is invalid");
            return false;
        }
        if(password.length()<6)
        {
            passwordText.setError("Password Length is too small");
            return false;
        }
        return true;
    }
}
