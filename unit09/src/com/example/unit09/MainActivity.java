package com.example.unit09;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener
{

    private ArrayList<PhoneInfo> phones = new ArrayList<PhoneInfo>();
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      ListView listview = (ListView)findViewById(R.id.list);

      phones.add(new PhoneInfo(R.drawable.girl1, "태연", "010-1989-0309"));
      phones.add(new PhoneInfo(R.drawable.girl2, "제시카", "010-1989-0418"));
      phones.add(new PhoneInfo(R.drawable.girl3, "써니", "010-1989-0515"));
      phones.add(new PhoneInfo(R.drawable.girl4, "티파니", "010-1989-0801"));
      phones.add(new PhoneInfo(R.drawable.girl5, "효연", "010-1989-0922"));
      phones.add(new PhoneInfo(R.drawable.girl6, "유리", "010-1989-1205"));
      phones.add(new PhoneInfo(R.drawable.girl7, "수영", "010-1990-0210"));
      phones.add(new PhoneInfo(R.drawable.girl8, "윤아", "010-1990-0530"));
      phones.add(new PhoneInfo(R.drawable.girl9, "서현", "010-1991-0628"));
      
      listview.setAdapter(new PhoneListAdapter(this, phones));
      listview.setOnItemClickListener(this);
      }

   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
   {
       PhoneInfo pi = phones.get(position);
       Toast.makeText(this, pi.name, Toast.LENGTH_SHORT).show();
   }

    private class PhoneInfo {
       int icon;
       String name;
       String phone;
      
    PhoneInfo(int icon, String name, String phone) {
       this.icon = icon;
       this.name = name;
       this.phone = phone;
      }
    }
    
    private class PhoneListAdapter extends ArrayAdapter<PhoneInfo> {
   private final LayoutInflater inflater;
      
   PhoneListAdapter(Context context, ArrayList<PhoneInfo> list) {
       super(context, 0, list);
       inflater = LayoutInflater.from(context);
      }
      
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if(convertView == null)
      {
          convertView = inflater.inflate(R.layout.item, null);
          holder = new ViewHolder();
          holder.icon = (ImageView)convertView.findViewById(R.id.icon);
          holder.name = (TextView)convertView.findViewById(R.id.name);
          holder.phone = (TextView)convertView.findViewById(R.id.phone);
          holder.button = (ImageButton)convertView.findViewById(R.id.button);
          convertView.setTag(holder);
      }
      else
          holder = (ViewHolder)convertView.getTag();

      final PhoneInfo pi = getItem(position);

      holder.icon.setImageResource(pi.icon);
      holder.name.setText(pi.name);
      holder.phone.setText(pi.phone);
      holder.button.setOnClickListener(new OnClickListener()
      {
          @Override
          public void onClick(View v)
          {
        	  Toast.makeText(getContext(), pi.phone, Toast.LENGTH_SHORT).show();

          }
      });
      convertView.setOnClickListener(new OnClickListener()
      {
          @Override
          public void onClick(View v)
          {
              Toast.makeText(getContext(), pi.name, Toast.LENGTH_SHORT).show();
          }
      });

      return convertView;
   }

    private class ViewHolder {
   ImageView icon;
   TextView name;
   TextView phone;
   ImageButton button;

       }
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