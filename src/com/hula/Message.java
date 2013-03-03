package com.hula;

import java.util.Date;

import android.util.Log;

public class Message {
	
	private String owner;
	private String message;
	private Date messageTime;
	
	public Message(String mOwner, String msg){
		Log.i("Message", "NEW message");
		this.owner = mOwner;
		this.message = msg;
		this.messageTime = new Date();
		
	}
	
	public String getOwner(){
		return this.owner;
	}
	
	public String getMessage(){
		return this.message;
	}
	

	public Date getMessageTime(){
		return this.messageTime;
	}
	
	
}
