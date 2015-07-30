package com.example.unit06;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private ImageView img;
	private TextView txt_num;
	private Button b_prev;
	private Button b_next;
	public Integer num;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		img = (ImageView)findViewById(R.id.img);
		txt_num = (TextView)findViewById(R.id.txt_num);
		b_prev = (Button)findViewById(R.id.b_prev);
		b_next = (Button)findViewById(R.id.b_next);
		num = 1;
		
		b_next.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(num<5)
				{
					num++;
					txt_num.setText(num.toString());
					switch(num)
					{
					case 1:
						img.setImageResource(R.drawable.p01);
						break;
					case 2:
						img.setImageResource(R.drawable.p02);
						break;
					case 3:
						img.setImageResource(R.drawable.p03);
						break;
					case 4:
						img.setImageResource(R.drawable.p04);
						break;
					case 5:
						img.setImageResource(R.drawable.p05);
						break;
					}
				}
			}
		});
		b_prev.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(num>1)
				{
					num--;
					txt_num.setText(num.toString());
					switch(num)
					{
					case 1:
						img.setImageResource(R.drawable.p01);
						break;
					case 2:
						img.setImageResource(R.drawable.p02);
						break;
					case 3:
						img.setImageResource(R.drawable.p03);
						break;
					case 4:
						img.setImageResource(R.drawable.p04);
						break;
					case 5:
						img.setImageResource(R.drawable.p05);
						break;
					}
				}
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
