package com.example.unit17;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

	TextView textView;
	
	private final ArrayList<Action> actions = new ArrayList<Action>();
    private ActionListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textView = (TextView)findViewById(R.id.text);
		
		private final ArrayList<Action> actions = new ArrayList<Action>();
	    private ActionListAdapter adapter;
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
	    switch(item.getItemId())
        {
        case R.id.add:
            textView.setText(R.string.add);
            actions.add(new Action(Action.FORWARD));
            adapter.notifyDataSetChanged();
            return true;
        case R.id.delete:
            actions.add(new Action(Action.FORWARD));
            adapter.notifyDataSetChanged();
            textView.setText(R.string.delete);
            return true;
        case R.id.edit:
            textView.setText(R.string.edit);
            return true;
        }
	    
	
		return super.onOptionsItemSelected(item);
	}
	

    private static final class Action
    {
        int id;
        int icon;
        int name;

        private final int[] icons = new int[] { R.drawable.forward_on, R.drawable.left_on };
        private final int[] names = new int[] { R.string.forward, R.string.turn_left };
        static final int FORWARD = 0;
        static final int TURN_LEFT = 1;

        Action(int id)
        {
            this.id = id;
            this.icon = icons[id];
            this.name = names[id];
        }
    }
    private static final class ActionListAdapter extends ArrayAdapter<Action>
    {
        private final LayoutInflater inflater;

        ActionListAdapter(Context context, ArrayList<Action> list)
        {
            super(context, 0, list);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.icon);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder)convertView.getTag();

            final Action action = getItem(position);
            holder.icon.setImageResource(action.icon);
            holder.name.setText(action.name);
            return convertView;
        }

        private static final class ViewHolder
        {
            ImageView icon;
            TextView name;
        }
    }


}
