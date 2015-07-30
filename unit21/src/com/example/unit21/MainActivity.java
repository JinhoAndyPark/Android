package com.example.unit21;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	
	private Button showButton;
    private Button showButton2;
    private Button showButton3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
            intent.putExtra("text", R.drawable.boy);
            startActivity(intent);
        }
    });
    
    showButton2.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent("example.android.TEST");
            intent.putExtra("text", R.drawable.girl);
            startActivity(intent);
        }
    });
    
    showButton3.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent("example.android.TEST");
            intent.putExtra("text", R.drawable.phone);
            startActivity(intent);
        }
    });
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
