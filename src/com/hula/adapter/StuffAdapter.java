package com.hula.adapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hula.Hula;
import com.hula.R;
import com.hula.Stuff;
import com.hula.adapter.MessageAdapter.ViewHolder;

	public class StuffAdapter extends ArrayAdapter<Stuff>  {
		
		
		private Hula hula;
		private ArrayList<Stuff> stuff;
		private LayoutInflater mInflater;
		private String TAG = "StuffAdapter";
		private ViewHolder viewHolder;
				
		static class ViewHolder {
			public ImageView avatar;
			public TextView name;
			public TextView time;
			public TextView status;
			
		}
		
		public StuffAdapter(Context context, ArrayList<Stuff> stuff) {
			super(context, R.layout.stuff_status_layout, stuff);
			this.stuff = new ArrayList<Stuff>();
			this.stuff = stuff;
			this.hula = ((Hula) context.getApplicationContext());
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			notifyDataSetChanged();		
		}

		@Override
		public synchronized View getView(int position, View convertView, ViewGroup parent) {			
			
			View view = convertView;
			if (view == null) {
				view = mInflater.inflate(R.layout.stuff_status_layout, null);
				viewHolder = new ViewHolder();	
				viewHolder.avatar = (ImageView) view.findViewById(R.id.stuff_status_avatar);
				viewHolder.name = (TextView) view.findViewById(R.id.stuff_status_user);
				viewHolder.time = (TextView)view.findViewById(R.id.stuff_status_time);
				viewHolder.status = (TextView) view.findViewById(R.id.stuff_status_text);
				
				
				view.setTag(viewHolder);
				
			} 			
		
			final Stuff stuffItem = stuff.get(position);
			ViewHolder holder = (ViewHolder) view.getTag();
			if (stuffItem != null) {
				
				
				if(hula.getUser().getEmail().equals(stuffItem.getOwner())){
					// write user stuff
					holder.name.setText(hula.getUser().getName());
					holder.status.setText(stuffItem.getStatus());
					SimpleDateFormat s = new SimpleDateFormat("EEEE hh:mm");
					String date = s.format(stuffItem.getTime());
					holder.time.setText(date);
					
					if (hula.getUser().getPic() != null) {
						
						
						
						holder.avatar.setImageBitmap(hula.getUser().getPic());
							
					} else {
						holder.avatar.setImageResource(R.drawable.usericon);
					}
				} else {
					// write contact stuff
					holder.name.setText(hula.getUser().getContact(stuffItem.getOwner()).getName());
					SimpleDateFormat s = new SimpleDateFormat("EEEE HH:mm");
					String date = s.format(stuffItem.getTime());
					holder.time.setText(date);
					holder.status.setText(stuffItem.getStatus());
					
					if (hula.getUser().getContact(stuffItem.getOwner()).getPic() != null) {
						
						holder.avatar.setImageBitmap(hula.getUser().getContact(stuffItem.getOwner()).getPic());
							
					} else {
						holder.avatar.setImageResource(R.drawable.usericon);
					}
					
				}
				
				
				
				 
			}
	
			return view;
		}
		
	}
