package com.example.unit21;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;




public class SecondActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        ImageView textView = (ImageView)findViewById(R.id.text);

        Intent intent = getIntent();
        int text = intent.getIntExtra("text",0);
        textView.setImageResource(text);
      
    }
    
}