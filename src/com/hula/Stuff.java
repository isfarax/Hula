package com.hula;

import java.util.Date;

import android.app.Activity;
import android.text.format.Time;
import android.util.Log;

public class Stuff {
	String TAG = "STuff";
	private String owner, status;
	private Date date;
	private int type;
	
	public Stuff(String owner, String status, int type){
		this.owner = owner;
		this.status = status;
		this.date = new Date();
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public String getStatus() {
		return status;
	}
	
	public Date getTime() {
		return date;
	}
	
	public int getType(){
		return type;
	}
	
	
}
