package com.example.android_0518;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{
    private Button showButton;
    private Button showButton2;
    private Button showButton3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showButton = (Button)findViewById(R.id.show);
        showButton2 = (Button)findViewById(R.id.Button01);
        showButton3 = (Button)findViewById(R.id.Button02);

        showButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent("example.android.TEST");
                intent.putExtra("text", R.drawable.backward_on);
                startActivity(intent);
            }
        });
        
        showButton2.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent("example.android.TEST");
                intent.putExtra("text", R.drawable.forward_on);
                startActivity(intent);
            }
        });
        
        showButton3.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent("example.android.TEST");
                intent.putExtra("text", R.drawable.right_on);
                startActivity(intent);
            }
        });
        
    }
}