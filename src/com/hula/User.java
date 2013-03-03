package com.hula;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class User {
	
	private String email, name, password;
	private byte[] avatar;
	private Bitmap pic;
	private ArrayList<Contact> contacts;
	private ArrayList<Stuff> stuff;
	private ArrayList<Conversation> conversations;
	private String status;
	
	/**
	 *  Create new user:
	 *  
	 *  @param email (jid/userId)
	 *  @param name
	 *  @param avatar
	 *  @param password
	 */
	public User(String email, String name,  String password){
		this.email = email;
		this.name = name; 
		this.password = password;
		this.contacts = new ArrayList<Contact>();
		this.stuff = new ArrayList<Stuff>();
		this.conversations = new ArrayList<Conversation>();
		this.status = null;
		this.avatar = null;
		this.pic = null;
		
		Log.e("USER", "NEW USER: "+name);
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public synchronized String getName(){
		return this.name;
	}	
	
	public void setName(String name){
		Log.e("USER", "NEW NAME: "+name);
		if(name!= null){
			if(name!=""){
				this.name = name;
				Log.e("USER", "NEW NAME2: "+name);
			}
			
			
		}
	}
	
	public byte[] getAvatar(){
		return this.avatar;
	}
	
	public void setAvatar(byte[] av){
		if(av!=null){
			this.avatar = av;
			
		}
		
	}
	
	public Bitmap getPic(){
		return this.pic;
	}
	
	public void setPic(Bitmap pic){
		this.pic = pic;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	// add stuff
	public synchronized void addStuff(String owner, String status, int type){
		this.stuff.add(0,new Stuff(owner, status, type));
	}
	
	// get stuff
	public synchronized ArrayList<Stuff> getStuff(){
		
		return this.stuff;
	}
	
	// add contact with: email, name, avatar, online, status
	public synchronized void addContact(String email, String name, byte[] avatar, boolean online, String status){
		this.contacts.add(new Contact(email, name, avatar, online, status));
													  
	}
	
	// add contact with: email, name, avatar, online, status
	public synchronized void addOnContact(String email, String name, byte[] avatar, boolean online, String status){
		this.contacts.add(0,new Contact(email, name, avatar, online, status));
													  
	}
	
	
	public synchronized ArrayList<Contact> getContacts(){
		return this.contacts;
		
	}
	
	public synchronized Contact getContact(String jId){
		Contact con = null;
		for(int i=0;i<this.contacts.size(); i++){
			if(this.contacts.get(i).getJID().equalsIgnoreCase(jId)){
				con= this.contacts.get(i);
			}
		}
		
		return con;
		
		
	}
	
	public synchronized void newConversation(String cWith){
		
		this.conversations.add(new Conversation(cWith));
		
	}
	
	public synchronized void newConversation(String cWith, String owner, String msg){
		Log.i("User", "newConversation with message");
		this.conversations.add(0, new Conversation(cWith, owner, msg));
		
	}
	
	public synchronized void cAddMsg(int pos, String from, String msg){

		getConversations().get(pos).addMessage(from, msg);

		Conversation convo = this.conversations.remove(pos);

		this.conversations.add(0,convo);
		
		
				
	}
	
	public synchronized ArrayList<Conversation> getConversations(){
		
		return this.conversations;
	}
	
	
}
