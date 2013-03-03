package com.hula;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.hula.adapter.MessageAdapter;
import com.hula.adapter.StuffAdapter;



import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageActivity extends Activity {
	private static final String TAG = "MessageActivity";
	private Hula hula;
	private String currentContact;
	private MessageAdapter mAdapter;
	private Conversation currentConvo;
	private EditText msgField;
	private Button sendButton;
	private ListView listView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		hula = ((Hula) MessageActivity.this.getApplication());
       
		setContentView(R.layout.msg_activity_layout);
	    
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			currentContact = extras.getString("contact");
			
		}
		Log.d(TAG, "Contact:" +currentContact);
		listView = (ListView) findViewById(R.id.msg_activity_msgs_list);
		// get edittext field
		this.msgField = (EditText)findViewById(R.id.msg_activity_msg_input);
		
		// set send button event
		sendButton = (Button) findViewById(R.id.msg_activity_send_msg);
		sendButton.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        		
        		sendMessage(MessageActivity.this.msgField.getText().toString());
        		
            }

			
        });
		
		

	    
	}

	@Override
	protected void onResume() {
		super.onPause();
		hula.messageActivityInit(this);
		updateContactProfile();
		updateConversationList();
	}
	
	@Override
	protected void onPause() {
		Log.e(TAG, "kill actvity");
		hula.messageActivityDeInit();
		super.onPause();
		this.finish();
	}

	/*
	 *  Update mini contact profile
	 * 
	 */
	public void updateContactProfile(){
		Log.i(TAG, "updateContactProfile");
	    ImageView avatar = (ImageView)findViewById(R.id.msg_activity_contact_avatar);
	    TextView contact = (TextView)findViewById(R.id.msg_activity_contact_name);
	    TextView status = (TextView)findViewById(R.id.msg_activity_contact_status);
	    
	    
	    if(hula.getUser().getContact(currentContact).getPic()!=null){
	    	avatar.setImageBitmap(hula.getUser().getContact(currentContact).getPic());
	    } else {
	    	avatar.setImageResource(R.drawable.usericon);
	    }
	    contact.setText(hula.getUser().getContact(currentContact).getName());
	    status.setText(hula.getUser().getContact(currentContact).getStatus());
	    
	    
	    // animation
	    final Animation a = AnimationUtils.loadAnimation(this, R.anim.alpha);
	    a.reset();
	    // listen for click events on contact profile....
	    final RelativeLayout profileL = (RelativeLayout) findViewById(R.id.msg_activity_rlayout);
	    profileL.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        		Log.d(TAG, "profile clicked!!!!");
        		
        		profileL.setAnimation(a);
        		a.reset();
        		a.start();
        		Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
                intent.putExtra("contact", currentContact);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                MessageActivity.this.finish();
        	}
		});	
		
		
	}
	
	/*
	 *  Update Contacts List
	 */
	public void updateConversationList() {
		Log.d(TAG, "updateConversationList" );
		if(!hula.getUser().getConversations().isEmpty()){  
			Log.d(TAG, "convos exist" );
			// search conversation with this contact	
			Log.e(TAG, "List size: "+hula.getUser().getConversations().size() );
			for (int i = 0;  i < hula.getUser().getConversations().size(); i++){
				Log.e(TAG, "size number: "+i);
				Log.e(TAG, "with: "+ hula.getUser().getConversations().get(i).getWith()+" ("+currentContact+")");
				// if convo is with this contact
				if (hula.getUser().getConversations().get(i).getWith().equalsIgnoreCase(this.currentContact)){
					// set current conversation
					Log.e(TAG, "contact: "+this.currentContact );
					currentConvo = hula.getUser().getConversations().get(i);
					// get messages
					mAdapter = new MessageAdapter(this,
								hula.getUser().getConversations().get(i).getMessages());
					listView.setAdapter(mAdapter);
					listView.setSelection(hula.getUser().getConversations().get(i).getMessages().size() - 1);
					break;
				} else {
					currentConvo = null;
					Log.d(TAG, "no convo with this user!" );
					
				}
			}
			
		} else {
			Log.d(TAG, "No convos at all matey!" );
		}
	
	}
	
	public void sendMessage(String msg){
		
		if(msg!=null){
			if(msg!=""){
				Log.i(TAG, "sendMessage: "+msg);
				
				// if no convo with this contact...
        		if(currentConvo == null){
        			// create convo with message
        			
        			hula.getUser().newConversation(MessageActivity.this.currentContact, hula.getUser().getEmail(), msg);
        			Log.d(TAG, "no convo exists with this contact bitch!!!, CREATE NEW!!" );
        			hula.sendMessage(MessageActivity.this.currentContact, msg );
        			MessageActivity.this.msgField.setText("");
	        		updateConversationList();
        		} else{
	        		
	        		currentConvo.addMessage(hula.getUser().getEmail(), msg);
	        		hula.sendMessage(MessageActivity.this.currentContact, msg );      		     		
	        		MessageActivity.this.msgField.setText("");
	        		updateConversationList();
        		}
        		
				
			}
		}
		
	}


	
	
}
