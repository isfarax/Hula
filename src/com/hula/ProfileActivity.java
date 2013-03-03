package com.hula;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hula.adapter.ProfileStuffAdapter;

public class ProfileActivity extends Activity {
	private final String TAG = "ProfileActivity";
	private Hula hula;
	private String profile;
	private ListView listView;
	private ArrayList<Stuff> myStuff;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		hula = ((Hula) ProfileActivity.this.getApplication());
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			profile = extras.getString("contact");
			Log.e(TAG, "profile: " + profile);
		}
				
		
		Log.e(TAG, "myprofile: " + hula.getUser().getEmail());
		
		updateProfile();
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		hula.profileActivityInit(this);
		updateProfile();
		
	} 
	
	@Override
	protected void onPause() {
		super.onPause();
		hula.profileActivityDeInit();
		this.finish();
	}

	public void updateProfile(){
		
		if(!hula.getUser().getEmail().equalsIgnoreCase(profile)){
			loadContactProfile(profile); 
		} else {
			loadMyProfile();
		}
		
	}


	private void loadContactProfile(final String contact) {
		Log.e(TAG, "loadContactProfile");
		setContentView(R.layout.profile_listview);
		listView = (ListView) findViewById(R.id.profile_lv);
		View headerView = getLayoutInflater().inflate(R.layout.contact_profile_layout, listView, false);
		ImageView avatar = (ImageView)headerView.findViewById(R.id.contact_profile_avatar);
		TextView name = (TextView)headerView.findViewById(R.id.contact_profile_name);
		TextView email = (TextView)headerView.findViewById(R.id.contact_profile_email);
		Button sendMessage = (Button)headerView.findViewById(R.id.contact_profile_sendMessageButton);
		if(sendMessage==null){
			Log.e(TAG, "shit is null");
		}
		Bitmap pic = hula.getUser().getContact(contact).getPic();
		String contactName = hula.getUser().getContact(contact).getName();
		
		
		if(pic!=null){
	    	avatar.setImageBitmap(pic);
	    } else{
	    	avatar.setImageResource(R.drawable.faraxxx);
	    } 
		
		name.setText(contactName);
		email.setText(contact);
		sendMessage.setText("Send Message");
		
		
		
		sendMessage.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        		
        		Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                intent.putExtra("contact", contact);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        	}
		});
		listView.addHeaderView(headerView);
		getMyStuff(contact);

		
	}
	
	
	private void loadMyProfile() {
		
		Log.e(TAG, "loadMyProfile");
		setContentView(R.layout.profile_listview);
		listView = (ListView) findViewById(R.id.profile_lv);
		View headerView = getLayoutInflater().inflate(R.layout.contact_profile_layout, listView, false);
		ImageView avatar = (ImageView)headerView.findViewById(R.id.contact_profile_avatar);
		TextView name = (TextView)headerView.findViewById(R.id.contact_profile_name);
		TextView email = (TextView)headerView.findViewById(R.id.contact_profile_email);
		Button editProfile = (Button)headerView.findViewById(R.id.contact_profile_sendMessageButton);
		Bitmap pic = hula.getUser().getPic();
		String contactName = hula.getUser().getName();
		String contactEmail = hula.getUser().getEmail();

		
		if(pic!=null){
	    	avatar.setImageBitmap(pic);
	    } else{
	    	avatar.setImageResource(R.drawable.faraxxx);
	    } 
		name.setText(contactName);
		email.setText(contactEmail);
		editProfile.setText("Edit Profile");
		editProfile.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        		
        		Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        	}
		});
		listView.addHeaderView(headerView);
		getMyStuff(contactEmail);
		
		
		
	}
	
	private void getMyStuff(String person){
		Log.e(TAG, "getMyStuff");
		myStuff = new ArrayList<Stuff>();
		int s = hula.getUser().getStuff().size();
		for(int i=0;i<s;i++){
			if(hula.getUser().getStuff().get(i).getOwner().equalsIgnoreCase(person)){
				myStuff.add(hula.getUser().getStuff().get(i));
				Log.e(TAG, "owner: "+hula.getUser().getStuff().get(i).getOwner()+" stuff text: "+ hula.getUser().getStuff().get(i).getStatus() );
			}
		}
		
		ProfileStuffAdapter itemsAdapter = new ProfileStuffAdapter(this, myStuff);
		listView.setAdapter(itemsAdapter);
		
	}

	
	

	
	
	
	
	
	
}
