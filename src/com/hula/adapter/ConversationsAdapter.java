package com.hula.adapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hula.Conversation;
import com.hula.Hula;
import com.hula.R;
import com.hula.adapter.MessageAdapter.ViewHolder;


	public class ConversationsAdapter extends ArrayAdapter<Conversation>  {
		
		
		private Hula hula;
		private ArrayList<Conversation> convo;
		private LayoutInflater mInflater;
		private String TAG = "ConvoAdapter";
		private ViewHolder viewHolder;
		
		static class ViewHolder {
			public ImageView avatar;
			public TextView name;
			public TextView time;
			public TextView message;
			
		}
		
		public ConversationsAdapter(Context context, ArrayList<Conversation> convo) {
			super(context, R.layout.list_conversations_layout, convo);
			Log.i("CA", "COnvo Adapter!!!!!!!!!!!!!!!!!!!!!!");
			this.convo = new ArrayList<Conversation>();
			this.convo = convo;
			this.hula = ((Hula) context.getApplicationContext());
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			notifyDataSetChanged();
			
			
			
		}
		
		public String getEmail(int position){
			return convo.get(position).getWith();
		}

		@Override
		public synchronized View getView(int position, View convertView, ViewGroup parent) {			
			final Conversation convoItem = convo.get(position);
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.list_conversations_layout, null);
				viewHolder = new ViewHolder();	
				viewHolder.avatar = (ImageView) view.findViewById(R.id.list_convo_avatar);
				viewHolder.name = (TextView) view.findViewById(R.id.list_convo_user);
				viewHolder.time = (TextView) view.findViewById(R.id.list_convo_time);
				viewHolder.message = (TextView) view.findViewById(R.id.list_convo_text);
				view.setTag(viewHolder);
			} else {
				view = convertView;
			}
		
			ViewHolder holder = (ViewHolder) view.getTag();
			
			if (convoItem != null) {
				 
				String convo = convoItem.getMessages().get(convoItem.getMessages().size()-1).getMessage();
				SimpleDateFormat s = new SimpleDateFormat("EE HH:mm");
				String date = s.format(convoItem.getMessages().get(convoItem.getMessages().size()-1).getMessageTime());
				
				holder.name.setText(hula.getUser().getContact(convoItem.getWith()).getName());
				holder.time.setText(date);
				if(convo !=null){
					if(convo.length()<50){
						holder.message.setText(convo);
					} else {
						holder.message.setText(convo.substring(0, 50));
					}
				}
				
				
				if (hula.getUser().getContact(convoItem.getWith()).getPic() != null) {
					holder.avatar.setImageBitmap(hula.getUser().getContact(convoItem.getWith()).getPic());	
				} else {
					holder.avatar.setImageResource(R.drawable.usericon);
				}
				
				 
			}
			

			
			
			return view;
		}
		
	}
