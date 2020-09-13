package com.example.mypr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ManLogin extends AppCompatActivity {

    MaterialEditText password,email;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_login);

        password=findViewById(R.id.password);
        email=findViewById(R.id.email);

        auth=FirebaseAuth.getInstance();
    }

    public void login(View v)
    {
        String e=email.getText().toString();
        String p=password.getText().toString();

        if(TextUtils.isEmpty(e))
        {
            email.setError("E-mail is Required");

            return;

        }

        if(TextUtils.isEmpty(p)) {
            password.setError("Password is Required");

            return;
        }

        if(p.length() <=6) {
            password.setError("Password Length must be greater than 6 characters");
            //Toast.makeText(this, "Password Length must be greater than 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(ManLogin.this, "User LoggedIn", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(getApplicationContext(),ManMain.class));
                }
                else
                {
                    Toast.makeText(ManLogin.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}