package com.example.mypr;




import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public void farmer(View v)
    {
        Intent intent=new Intent(getApplicationContext(), FarmerRegister.class);
        startActivity(intent);
    }

    public void man(View v)
    {
        startActivity(new Intent(getApplicationContext(),ManRegister.class));
    }
    public void sup(View v)
    {
        startActivity(new Intent(getApplicationContext(),SupRegister.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


}