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

public class ManRegister extends AppCompatActivity {

    MaterialEditText username,email,password;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_register);

        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        auth= FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),ManMain.class));
            finish();
        }
    }

    public void signup(View v)
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

        auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(ManRegister.this, "User Registration Completed Successfully", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(getApplicationContext(),ManMain.class));
                }
                else
                {
                    Toast.makeText(ManRegister.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void login(View v)
    {
        startActivity(new Intent(getApplicationContext(),ManLogin.class));

    }

}