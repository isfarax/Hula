package com.hula;

import java.util.ArrayList;

import android.util.Log;

public class Conversation {
	
	private String with;
	private ArrayList<Message> messages;
	
	public Conversation(String cWith){
		
		this.with = cWith;
		this.messages = new ArrayList<Message>();
		
	}
	
	// new conversation with message...
	public Conversation(String cWith, String owner, String msg){
		Log.i("Conversation", "NEW conversation with message "+cWith);
		
		this.with = cWith;
		this.messages = new ArrayList<Message>();
		this.messages.add(new Message(owner, msg));
		
	}
	
	public String getWith(){
		return this.with;
	}
	
	public void addMessage(String owner, String msg){
		this.messages.add(new Message(owner, msg));
	}
	
	public ArrayList<Message> getMessages(){
	
		return this.messages;		
	}
	
	

}
