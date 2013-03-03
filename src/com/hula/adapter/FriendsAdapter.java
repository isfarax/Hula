package com.hula.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hula.Contact;
import com.hula.R;

public class FriendsAdapter extends BaseAdapter{
		private String TAG = "FriendsAdapter";
		private LayoutInflater mInflater;
		private ArrayList<Contact> contacts;
		private ViewHolder viewHolder;
		
		static class ViewHolder {
			public TextView text;
			public ImageView image;
			public String contactEmail;
		}
		
		public FriendsAdapter(Context context, ArrayList<Contact> contacts){ 
			
			Log.e(TAG, "Friends ADAPTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
			this.contacts = new ArrayList<Contact>();
			this.contacts = contacts;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			notifyDataSetChanged();
			
		}

		public int getCount() {
			return contacts.size(); 
		}

		public Object getItem(int position) {
			return null; 
		}
		
		public String getEmail(int position){
			return contacts.get(position).getJID();
		}

		public long getItemId(int position) {
			return 0;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("GV", "getView"); 
			Contact contactItem = contacts.get(position);
			View view;
			//View view = inflater.inflate(R.layout.new_layout, null);
			
			if (convertView  == null) {
				Log.e("GV", "cv is null"); 
				//LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = mInflater.inflate(R.layout.new_layout, null);
				viewHolder = new ViewHolder();
				viewHolder.text = (TextView) view.findViewById(R.id.contactName);
				viewHolder.image = (ImageView) view.findViewById(R.id.avatar);
				view.setTag(viewHolder);
				
			}	else {
		        view = convertView;
		    }

			ViewHolder holder = (ViewHolder) view.getTag();
			
			holder.contactEmail = contactItem.getJID();
			holder.text.setText(contactItem.getName());
			Log.d("GV", "displaying: "+holder.contactEmail); 
			if(contactItem.getPic()!=null){
				
				holder.image.setImageBitmap(contactItem.getPic());
			
			} else {
				holder.image.setImageResource(R.drawable.usericon);
			}
			

			return view;

			
		    
		}
		
	}