package com.hula;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Contact {
	private static final String TAG = "Contact";
	private String jID;
	private String name;
	private String status; 
	private boolean OnlineStatusCode;
	private byte[] avByte;
	private Bitmap pic;
	
	// add contact with: jid, name, avatar, online, status
	public Contact(String jid, String name, byte[] avByte, boolean online, String status){
		this.jID = jid;
		this.name = name;
		this.avByte = avByte; 
		this.OnlineStatusCode = online;
		this.status = status;
		this.pic = null;
	}
	
	
	// get jid
	public String getJID(){
		return jID; 
	}
	
	// get name
	public String getName(){
		return this.name;
	}
	
	// set name
	public void setName(String name){
		this.name = name;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	
	public void setStatus(String status_){
		
		this.status = status_;
	}
	
	// setOnlineStatusCode
	public void setOnlineStatusCode(boolean online){		
		this.OnlineStatusCode = online;		
	}
	
	// getOnlineStatusCode
	public boolean getOnlineStatusCode(){		
		return this.OnlineStatusCode;		
	}	 
	
	public void setAvByte(byte[] avByte){
		this.avByte = avByte;
		
	}
	
	public byte[] getAvByte(){
		return this.avByte;
	}
	
	public void setPic(Bitmap pic){
		this.pic = pic;
	}
	
	public Bitmap getPic(){
		return this.pic;
	}

}
