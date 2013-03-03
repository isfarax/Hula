package com.hula.adapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hula.Hula;
import com.hula.Message;
import com.hula.R;

	public class MessageAdapter extends ArrayAdapter<Message>  {
		private Hula hula;
		private ArrayList<Message> messages;
		private Context context;
		private LayoutInflater mInflater;
		private String TAG = "MessageAdapter";
		private ViewHolder viewHolder;
		
		static class ViewHolder {
			public TextView name;
			public TextView time;
			public TextView message;
			
		}
		
		public MessageAdapter(Context context, ArrayList<Message> messages) {
			super(context, R.layout.list_convo_msgs_layout, messages);
			Log.e(TAG, "MESSAGE ADAPTERRRRRRRRRRRRRRRRRRRRR!!!");
			this.context = context;
			this.messages = new ArrayList<Message>();
			this.messages = messages;
			this.hula = ((Hula) context.getApplicationContext());
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			notifyDataSetChanged();	
			
		} 

		@Override
		public synchronized View getView(int position, View convertView, ViewGroup parent) {			
			final Message msg = messages.get(position);
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.list_convo_msgs_layout, null);
				viewHolder = new ViewHolder();			
				viewHolder.name = (TextView) view.findViewById(R.id.list_c_msgs_name);
				viewHolder.time = (TextView)view.findViewById(R.id.list_c_msgs_time);
				viewHolder.message = (TextView) view.findViewById(R.id.list_c_msgs_text);
				
				
				view.setTag(viewHolder);
			} else {
				view = convertView;
			}
		
			
			ViewHolder holder = (ViewHolder) view.getTag();
			

				
				
				if(msg.getOwner().equalsIgnoreCase(hula.getUser().getEmail())){
					
					
				}
				
				if(!msg.getOwner().equalsIgnoreCase(hula.getUser().getEmail())){
					
					holder.name.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
					
					holder.name.setText(hula.getUser().getContact(msg.getOwner()).getName());
					
				} else {
					holder.name.setTextColor(Color.WHITE);
					holder.name.setText("Me");
				}
				
				
				SimpleDateFormat s = new SimpleDateFormat("EE HH:mm");
				String date = s.format(msg.getMessageTime());
				holder.time.setText(date);
				holder.message.setText(msg.getMessage());
			

			
			
			return view;
		}
		
	}
