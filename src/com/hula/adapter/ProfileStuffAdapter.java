package com.hula.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hula.R;
import com.hula.Stuff;

public class ProfileStuffAdapter extends ArrayAdapter<Stuff>  {
	private Context context;
	private ArrayList<Stuff> stuff;
	
	
	public ProfileStuffAdapter(Context context, ArrayList<Stuff> stuff) {
		super(context, R.layout.list_contact_stuff, stuff);
		this.context = context;
		this.stuff = stuff;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null){
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.list_contact_stuff, null);
		}
	
		Stuff stuffItem = stuff.get(position);
		
		if (stuffItem != null) {
			TextView title = (TextView) view.findViewById(R.id.list_contact_stuff_title);
			TextView time = (TextView) view.findViewById(R.id.list_contact_stuff_time);
			
			title.setText(stuffItem.getStatus());
        
			SimpleDateFormat s = new SimpleDateFormat("EEEE hh:mm");
			String date = s.format(stuffItem.getTime());
	
			time.setText(date);
	
			 
		}
		

		
		return view;
	}

}
