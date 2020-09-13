package com.example.mypr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class FarmerRegister extends AppCompatActivity {

    MaterialEditText username,email,password;
    FirebaseAuth auth;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_register);

        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),FarmerMain.class));
            finish();
        }
    }

    public void signup(View v)
    {
        final String us=username.getText().toString();
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

                    FirebaseUser firebaseuser=auth.getCurrentUser();
                    String userid=firebaseuser.getUid();

                    ref= FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String,String> map =new HashMap<>();
                    map.put("id",userid);
                    map.put("username",us);
                    map.put("imageURL","default");
                    map.put("status","Offline");
                    map.put("search",us.toLowerCase());
                    map.put("type","Farmer");

                    ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(FarmerRegister.this, "User Registration Completed Successfully", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(getApplicationContext(),FarmerMain.class));

                        }
                    });


                }
                else
                {
                    Toast.makeText(FarmerRegister.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void login(View v)
    {
        startActivity(new Intent(getApplicationContext(),FarmerLogin.class));

    }
}