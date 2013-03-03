package com.hula;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Hula extends Application {
	
	private static final String TAG = "Hula App";
	private HulaService hulaService;
	private boolean mBounded;
	private final ServiceConnection mConnection = new ServiceConnection() {
		
		public synchronized void onServiceConnected(final ComponentName name, final IBinder service) {
			hulaService = ((LocalBinder<HulaService>) service).getService();
			mBounded = true;
			Log.d(TAG, "onServiceConnected");	
			//login(tempEmail, tempPword);
						
		}

		
		public synchronized void onServiceDisconnected(final ComponentName name) {
			hulaService = null;
			mBounded = false;
			Log.d(TAG, "onServiceDisconnected");
		}
		
	};
	private ArrayList<User> users;
	private User user;
	static boolean userLogged;
	private Handler mHandler = new Handler();
	private LoginActivity loginActivity;
	private HomeActivity homeActivity;
	private StuffFragment stuffFragment;
	private FriendsFragment friendsFragment;
	private ConversationsFragment conversationsFragment;
	private MessageActivity messageActivity;
	private ProfileActivity profileActivity;
	private EditProfileActivity editProfileActivity;
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		userLogged = false;			
		// start the fucking service...
		 startService(new Intent(this, HulaService.class));
		 ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new
					org.jivesoftware.smackx.provider.VCardProvider());
		 
		 users  = new ArrayList<User>();
		 
	}
	
	/*
	 *  Binding methods!
	 *  
	 *  Bind this class (Hula) to the service (HulaService) to receive response
	 *  from the XMPP server
	 */
	public void doBindService() {
		bindService(new Intent(this, HulaService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void doUnbindService() {
		if (mBounded) {
			unbindService(mConnection);
		}
	}	
	
	public synchronized User getUser(){
		
		return this.user;
		
	}

	public void register(String uname, String pword){
		Hula.this.loginActivity.showLoading("Registering... Please wait.");
		Hula.this.hulaService.register(uname, pword);	
		
	}
	
	public void registerSuccess(final String uname, final String pword){
		Hula.this.loginActivity.hideLoading();
		Hula.this.loginActivity.showDialog("Registeration Success!.");
		new Thread(new Runnable() {
    	    public void run() {
    	      try {
				Thread.sleep(1500);
				mHandler.post(new Runnable() {
					public void run() {
						Hula.this.loginActivity.login(uname, pword);
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	     
    	    }
    	}).start();	
	
	
	}
	
	public void registerFail(){
		Hula.this.loginActivity.hideLoading();
		Hula.this.loginActivity.showDialog("Registeration failed. Please try again.");
		new Thread(new Runnable() {
    	    public void run() {
    	      try {
				Thread.sleep(1500);
				mHandler.post(new Runnable() {
					public void run() {
						Hula.this.loginActivity.hideDialog();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	      
    	      
    	    }
    	}).start();
		
	}
	
	
	// prepare user to login
	public void prepareLogin(final String uname, final String pword){
		
		
		
		Log.i(TAG, "prepareLogin");
		
		
		
		if (mBounded) { 
			login(uname, pword);
		} else {
			// bind service
			doBindService();
			Thread loginThread = new Thread("loginThread") {
				
				public void run(){
					try {
						sleep(5000);
						login(uname, pword);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			loginThread.start();
		}
		
		
		
	}
	
	
	
	public void login(final String uname, final String pword){
		
		hulaService.login(uname, pword);
		
	}
	
	

	public void onLoginSuccess(String userId, String name, String password){
		
		Log.i(TAG, "LoginSuccess");
		Hula.this.loginActivity.showDialog("Login Sucess!!");
		userLogged = true;
		
		// check if user has logged in before
		if(users.isEmpty()){
			// create new user
			Log.i("relog", "no users > create new user");
			users.add(new User(userId, name, password));
			user = users.get(users.size()-1);
			hulaService.checkVCard(userId);
			retrieveContacts();
			
		} else {
			ArrayList<String> userIds = new ArrayList<String>();
			for(int i=0;i<users.size();i++){
				Log.i("relog", "searching for existing users: "+i+" size: "+users.size());
				userIds.add(users.get(i).getEmail());
				
				if(users.get(i).getEmail().equalsIgnoreCase(userId)){
					// user exists
					Log.i("relog", "existing user found");
					user = users.get(i);
					Hula.this.loginActivity.hideLoading();
					Intent intent = new Intent(Hula.this, HomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					hulaService.setPacketFilters();
					break;
				}
				
				if(i==users.size()-1){
					
					Log.i("relog", "search for user exhausted: "+i +" == "+ (users.size()-1));
					users.add(new User(userId, name, password));
					user = users.get(users.size()-1);
					hulaService.checkVCard(userId);
					retrieveContacts();
					break;
				}
				
			}
		}

		
		 
		
		
		
	}
	 
	
	// onLoginFail
	public void onLoginFail(){
		
		Log.i(TAG, "LoginFail");
		
		mHandler.post(new Runnable() {
			public void run() {
				Hula.this.loginActivity.showDialog("Login failed! Please try again");
				Hula.this.loginActivity.onLoginFail();
			}
		});
	}

	

	
	// retrieves contact from a user's roster/contacts list
	private void retrieveContacts(){
		
		final ArrayList<String> contacts = new ArrayList<String>();
		final HashMap<String, VCard> map = new HashMap<String, VCard>();
		Log.i(TAG, "retrieveContacts");
		
		//synchronized (this.user) {	
		Thread rosterThread = new Thread("rosterThread") {
			public void run(){ 
				final Roster roster = Hula.this.hulaService.getRoster();
				Log.i(TAG, "rosterThread");
				
				for (final RosterEntry entry : roster.getEntries()) {
					if (entry.getType() == ItemType.to || entry.getType() == ItemType.both) {
						Log.d(TAG, "RETRIEVING CONTACTS!!: ");
									//map.put(entry.getUser(), null);
									hulaService.checkVCard(entry.getUser());
									//contacts.add(entry.getUser());

									String contactName;
									String status;
									String description; 
									boolean online; 
									
									contactName = entry.getName();
									
									
									// is contact online?
									online = roster.getPresence(entry.getUser()).isAvailable();
									
									// get contact status
									status = roster.getPresence(entry.getUser()).getStatus();
																
									Log.d(TAG, "contact: "+contactName+" status: "+ status);
									if(online){
										Log.d(TAG, "CONTACT: "+contactName);
										getUser().addOnContact(entry.getUser(), contactName, 
												null, online, status );
										if(status!=null){
											// add stuff
											if(status != ""){
												getUser().addStuff(entry.getUser(), status, 1);
											}
										}
										
										
									} else {
										Log.d(TAG, "CONTACT: "+contactName);
										getUser().addContact(entry.getUser(), contactName, 
												null, online, null );
									}

							
						
						
							
						
								

						
					} // if
				} // for
				
				Log.d(TAG, "CONTACTS RETRIEVED !!: ");
				Log.d(TAG, "CONTACTS RETRIEVED SIZE: "+contacts.size());
				//hulaService.getvCardt(map);
				mHandler.post(new Runnable() {
					public void run() {
						Hula.this.loginActivity.hideLoading();
						Intent intent = new Intent(Hula.this, HomeActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						hulaService.setPacketFilters();
					}
				});
				
				//
			}
		};
		rosterThread.start();
		//Log.i(TAG, "rrrr "+roster.getEntryCount());
			
			
			
		//} // sync
		
		
		

//	
		
	}
	

	public synchronized void presenceUpdates(final Presence presence) {
		Log.e(TAG, "presenceUpdate: " + presence.toXML());
		final String fromJid = StringUtils.parseBareAddress(presence.getFrom());
		Log.e(TAG,"from: "+fromJid);
		// if presence is from user, do nothing...
		if (fromJid.equalsIgnoreCase(getUser().getEmail())) {
			return;
		}
		
		try {
			Log.d(TAG, "trying presenceUpdate...");
			
			synchronized(this.user){ 
				// contact is not available
				if (presence.getType() == Type.unavailable) {
					this.user.getContact(fromJid).setOnlineStatusCode(false);
					
				}
				// contact is available
				else if (presence.isAvailable()) {
					
					mHandler.post(new Runnable() {
						public void run() {
							
							Hula.this.user.getContact(fromJid).setOnlineStatusCode(true);
							
							Log.d(TAG, "contact: "+fromJid+" status: "+ presence.getStatus());
							
							if(presence.getStatus()!=null){
								if(presence.getStatus()!=""){
									// update status to stuff
									Hula.this.updateStuff(fromJid, presence.getStatus());
								}
							}
				
						}
					});
					
					hulaService.checkVCard(fromJid);
		
				} 
				else {
					return;
				}

				
			}
			

			
			// update status 
			
			
			
			
			
		}
		catch (Exception e){
			Log.d(TAG, "presenceUpdate error..." + e.toString());
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public synchronized void handleMessageReceived(Message message) {
		try {
			String fromName = StringUtils.parseBareAddress(message.getFrom());

			Roster roster = hulaService.getRoster();
			
			RosterEntry entry = roster.getEntry(fromName);

			if (entry == null) {
				return;
			}

			synchronized (this.user) {
				//this.messages.add(entry.getName() + ": " + message.getBody());
				Log.i(TAG, fromName + " : " + message.getBody());
				
				
				
				// if no conversations exist...
				if (this.user.getConversations().size() == 0){
					Log.i(TAG,"no convos, new convo with message");
					// create new conversation with this contacts and add message...
					this.user.newConversation(fromName, fromName, message.getBody());
					
					
				} else  { // if conversations exist...
					
					Boolean cE = false;
					
					// check if there is a conversation with this contact already...
					for (int i = 0;  i < this.user.getConversations().size(); i++){
						
						// if true...
						if (this.user.getConversations().get(i).getWith().equalsIgnoreCase(fromName)){
							Log.i(TAG, "old convo, new message");
							// add message to conversation....
							this.user.cAddMsg(i, fromName, message.getBody());
							Log.i(TAG, "return");
							cE = true;
							break;
						} 
						
						
					}
					
					// if conversation with contacts doesn't already exist...
					if (cE == false){
						Log.i(TAG, "new convo, new message");
						// create new conversation with this contacts...
						this.user.newConversation(fromName, fromName, message.getBody());
						
					}
					
					
					
				} 
				
				
				
				
			}
		} catch (Exception e) {
			Log.e(TAG, "FAM, suttin went fucked "+e);
		}
		
		
		if(this.conversationsFragment != null){
			// update dat shit!!!
			mHandler.post(new Runnable() {
				public void run() {
					
					Hula.this.conversationsFragment.updateConversationList();
				}
			});		
		}
		
		if(this.messageActivity != null){
			// update dat shit!!!
			mHandler.post(new Runnable() {
				public void run() {
					
					Hula.this.messageActivity.updateConversationList();
				}
			});		
		}

	} 

	public void sendMessage(String to, String text){
		this.hulaService.sendMessage(to, text);
		updateActivities();
	}
	
	public void setStatus(String status){
		this.hulaService.setStatus(status);
	}
	
	protected  void updateMyStuff(String status) {
		int type = 1; // status update
		
		// check if contact has status already.
		if(this.user.getStatus() !=null){
			// if status is not the same, add new stuff
			
			if(!this.user.getStatus().equals(status)){
				setStatus(status);
				this.user.addStuff(this.user.getEmail(), status, type);	
			} 
			
		} else { // if no status already contacts, add stuff
			Log.e(TAG, "Status null, add stuff");
			setStatus(status);
			this.user.addStuff(this.user.getEmail(), status, type);	
		
		}
		
		hulaService.setStatus(status);
		updateActivities();

	}

	
	protected  void updateStuff(String contact, String status) {
		int type = 1; // status update
		
		// check if contact has status already.
		if(this.user.getContact(contact).getStatus() !=null){
			// if status is not the same, add new stuff
			Log.e(TAG, "contact status NOT null: " + contact);
			if(!this.user.getContact(contact).getStatus().equals(status)){
				Log.i(TAG, "Status is NOT the same! " + contact);
				for(int i=0;i<this.user.getContacts().size(); i++){
					if(this.user.getContacts().get(i).getJID().equalsIgnoreCase(contact)){
						
						this.user.getContacts().get(i).setStatus(status);
						this.user.addStuff(contact, status, type);
						Contact con = this.user.getContacts().remove(i);
						this.user.getContacts().add(con);
						break;
					}
				}
				
			} else {
				Log.i(TAG, "Status is the same! "+ contact);
			}
			
		} else { // if no status already contacts, add stuff
			Log.e(TAG, "Status null, add stuff");
			for(int i=0;i<this.user.getContacts().size(); i++){
				if(this.user.getContacts().get(i).getJID().equalsIgnoreCase(contact)){
					
					this.user.getContacts().get(i).setStatus(status);
					this.user.addStuff(contact, status, type);
					Contact con = this.user.getContacts().remove(i);
					this.user.getContacts().add(con);
					break;
					
					
					
				}
				
			}
		
		}
		
		updateActivities();

	}



	
	public void updateVCard(final String person, VCard vCard){
		
		Log.i("VU", "Person: "+person +" name: "+vCard.getFirstName());
		if(person.equalsIgnoreCase(getUser().getEmail())){
			Log.i("VU", "MyVCard "+vCard.getFirstName());
			
			// name update
			if(vCard.getFirstName()!=null){
				if(vCard.getFirstName()!=""){
					final String name = vCard.getFirstName();
					
					
					if(!getUser().getName().equalsIgnoreCase(name)){
						mHandler.post(new Runnable() {
							public void run() {
								getUser().setName(name);
							}
						});
					}
					
					
				}
			}
			
			// pic update
			if(vCard.getAvatar()!=null){
				final byte[] avatar = vCard.getAvatar();
				final int type = 2; // avatar update
				final String picStatus = this.user.getName()+" has updated their picture.";
				Log.i("VU", "MyVCard is NOT null");
				if(getUser().getAvatar() != null){
					Log.i("VU", "MY CURRENT AVATAR is NOT null");
					if(!Arrays.equals(getUser().getAvatar(), avatar)){
						Log.i("VU", "NOT SAME AVATAR");
						Log.i("VU", "Avatar us null? "+person);
						final Bitmap pic = decodeAbtoBm(avatar);
						mHandler.post(new Runnable() {
							public void run() {
								getUser().setPic(pic);
								Log.i("VU", "Avatar is NOT same! "+person);
								getUser().setAvatar(avatar);
								user.addStuff(person, picStatus, type);
							}
						});
						
						
					} 
	
				} else {
					Log.i("VU", "MY CURRENT AVATAR is null");
					Log.i(TAG, "Avatar us null? "+person);
					final Bitmap pic = decodeAbtoBm(avatar);
					mHandler.post(new Runnable() {
						public void run() {
							getUser().setPic(pic);
							Log.i("VU", "Avatar is NOT same! "+person);
							getUser().setAvatar(avatar);
							user.addStuff(person, picStatus, type);
						}
					});
		
				}
				
				
			
			} else {
				Log.i("VU", "MyVCard is null");
			}
			
			
			
			
		} else {
			
			// name update
			if(vCard.getFirstName()!=null){
				if(vCard.getFirstName()!=""){
					final String name = vCard.getFirstName();
					
					
					if(!getUser().getContact(person).getName().equalsIgnoreCase(name)){
						mHandler.post(new Runnable() {
							public void run() {
								getUser().getContact(person).setName(name);
							}
						});
					}
					
					
				}
			}
			
			// pic update
			if(vCard.getAvatar()!=null){
				final byte[] avatar = vCard.getAvatar();
				final int type = 2; // avatar update
				final String picStatus = this.user.getContact(person).getName()+" has updated their picture.";
				
				if(this.user.getContact(person).getAvByte() != null){
					if(!Arrays.equals(this.user.getContact(person).getAvByte(), avatar)){
						
						Log.i(TAG, "Avatar us null?"+avatar.toString());
						final Bitmap pic = decodeAbtoBm(avatar);
						mHandler.post(new Runnable() {
							public void run() {
								getUser().getContact(person).setPic(pic);
								Log.i(TAG, "Avatar is NOT same!");
								user.getContact(person).setAvByte(avatar);
								user.addStuff(person, picStatus, type);
							}
						});
						
						
					} 
	
				} else {
					Log.i(TAG, "Avatar us null?"+avatar.toString());
					final Bitmap pic = decodeAbtoBm(avatar);
					mHandler.post(new Runnable() {
						public void run() {
							getUser().getContact(person).setPic(pic);
							Log.i(TAG, "Avatar is NOT same!");
							user.getContact(person).setAvByte(avatar);
							user.addStuff(person, picStatus, type);
						}
					});
		
				}
				
				Log.i("VU", "Their VCard");
				Log.i("VU", "Their VCard "+vCard.getFirstName());
			
			}
			
		} // end else
		updateActivities();
		
	}
	
	
	
	public Bitmap decodeAbtoBm(byte[] b){
		Bitmap bm;
		
		System.gc();
	    Runtime.getRuntime().gc();  
		Log.i("decodeAbtoBm", "decodeAbtoBm");
		
		//Decode image size
        BitmapFactory.Options oo = new BitmapFactory.Options();
        oo.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(b, 0, b.length ,oo);

        //The new size we want to scale to
        final int REQUIRED_SIZE=200;
 
        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(oo.outWidth/scale/2>=REQUIRED_SIZE && oo.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        o2.inPurgeable = true;
        o2.inInputShareable = true;
       
        bm = BitmapFactory.decodeByteArray(b, 0, b.length,o2);
        System.gc();
        Runtime.getRuntime().gc(); 
		return bm;
	}
	
	public byte[] decodeFileToBm(String path){
		Bitmap bm;
		
		System.gc();
	    Runtime.getRuntime().gc();  
		Log.i("decodeFile", "decodeFileToBm");
		
		//Decode image size
        BitmapFactory.Options oo = new BitmapFactory.Options();
        oo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, oo);

        //The new size we want to scale to
        final int REQUIRED_SIZE=200;
 
        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(oo.outWidth/scale/2>=REQUIRED_SIZE && oo.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;
        
        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=(int) scale;
        o2.inPurgeable = true;
        o2.inInputShareable = true; 
        
        
        bm = BitmapFactory.decodeFile(path, o2);
        Log.e("decodeFile", "bm w: "+bm.getWidth());
		Log.e("decodeFile", "bm w: "+bm.getHeight());
        //byte[] b = scalePic(bm);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();   
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
        byte[] b = baos.toByteArray();
        Log.i("decodeFile", "bm size: "+bm.getByteCount()/1024);
        Log.i("decodeFile", "b size: "+b.length/1024);
        System.gc();
        Runtime.getRuntime().gc(); 
		return b;
	}
	


	public void updateMyDetails(final String newName, final String newPicPath) {
		Log.i("updatemy", "updateMyDetails");
		// create byte array from image file
		Thread thread = new Thread("thread") {
			public synchronized void run(){
				
				if(newPicPath!=null){
					if(newPicPath!=""){
						byte[] b = decodeFileToBm(newPicPath);
						Log.i("updatemy", "newPicPath is good");
						
						try {
							Thread.sleep(1500);
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						hulaService.updateMyDetails(newName, b);
					}

				} else {
					hulaService.updateMyDetails(newName, null);
				}
				
				
				
			}
		};
		thread.start();
		// send to hulaService (String newName, byte[] newAvatar)
		
		
	}
	
	public void updateMyDetailsSuccess(){
		Log.i("updatemy", "updateMyDetails Sucess");
		
		editProfileActivity.showDialog("Your detials have been update!");
		Thread thread = new Thread("thread") {
			public synchronized void run(){
				try {
					sleep(2000);
					mHandler.post(new Runnable() {
						public void run() {
							Intent intent = new Intent(Hula.this, ProfileActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("contact", getUser().getEmail());
							startActivity(intent);
						}
					});
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		
		
	}
	
	
	public synchronized void updateActivities(){
		mHandler.post(new Runnable() {
			public void run() {
				// stuff 
				if(Hula.this.stuffFragment != null){
					Hula.this.stuffFragment.updateStuff();
				}
				
				// friend
				if(Hula.this.friendsFragment != null){
					Hula.this.friendsFragment.updateContactList();	
				}
				
				// message
				if(Hula.this.messageActivity != null){
					Hula.this.messageActivity.updateContactProfile();
				}
				
				// convo
				if(Hula.this.conversationsFragment != null){
					Hula.this.conversationsFragment.updateConversationList();
				}
				
				// profile
				if(Hula.this.profileActivity != null){
					Hula.this.profileActivity.updateProfile();
				}
				
			}
		});	

		
		
	}
		
	
	

	// loginActivityInit...
	public void loginActivityInit(LoginActivity loginActivity){
		this.loginActivity = loginActivity;
		Log.d(TAG, "LoginActivityInit");
	}
	
	// loginActivityInit...
	public void loginActivityDeInit(){
			this.loginActivity = null;
			Log.d(TAG, "LoginActivityInit");
		}
	
	// registerActivityInit...
	public void homeActivityInit(HomeActivity homeActivity) {
		this.homeActivity = homeActivity;
		Log.d(TAG, "homeActivityInit");
		
	}
	// registerActivityInit...
	public void homeActivityDeInit() {
		this.homeActivity = null;
		Log.d(TAG, "homeActivityDeInit");
		
	}

	
	// stuffFragmentInit...
	public void stuffFragmentInit(StuffFragment stuffFragment){
		this.stuffFragment = stuffFragment;
		Log.d(TAG, "stuffFragmentInit");
	}
	
	// stuffFragmentDeInit...
	public void stuffFragmentDeInit(){
		this.stuffFragment = null;
		Log.d(TAG, "stuffFragmentDeInit");
	}
	

	// friendsFragmentInit...
	public void friendsFragmentInit(FriendsFragment friendsFragment){
		this.friendsFragment = friendsFragment;
		Log.d("FF", "friendsFragmentInit"); 
	}
	
	
	// friendsFragmentDeInit...
	public void friendsFragmentDeInit(){
		this.friendsFragment = null;
		Log.d("FF", "friendsFragmentDeInit");
	}

	
	// conversationsFragmentInit...
	public void conversationsFragmentInit(ConversationsFragment conversationsFragment){
		this.conversationsFragment = conversationsFragment;
		Log.d("FF", "conversationsFragmentInit");
	}

	// conversationsFragmentDeInit...
	public void conversationsFragmentDeInit(){
		this.conversationsFragment = null;
		Log.d("FF", "conversationsFragmentDeInit");
	}

	
	// messageActivityInit...
	public void messageActivityInit(MessageActivity messageActivity){
		this.messageActivity = messageActivity;
		Log.d(TAG, "messageActivityInit");
	}
	
	// messageActivityInit...
	public void messageActivityDeInit(){
		this.messageActivity = null;
		Log.d(TAG, "messageActivityDeInit");
	}
	
	// profileActivityInit...
	public void profileActivityInit(ProfileActivity profileActivity){
		this.profileActivity = profileActivity;
		Log.d(TAG, "profileActivityInit");
	}
	
	// profileActivityDeInit...
	public void profileActivityDeInit() {
		this.profileActivity = null;
		Log.d(TAG, "profileActivityDeInit");
		
	}
	
	// profileActivityInit...
	public void editProfileActivityInit(EditProfileActivity editProfileActivity){
		this.editProfileActivity = editProfileActivity;
		Log.d(TAG, "profileActivityInit");
	}
	
	// profileActivityDeInit...
	public void editProfileActivityDeInit() {
		this.editProfileActivity = null;
		Log.d(TAG, "profileActivityDeInit");
		
	}
	
		
	
	public void Logout(){
		userLogged = false;
		Thread logoutThread = new Thread() {
			public synchronized void run(){ 
				hulaService.logout();
			}
		};
		logoutThread.start();
		
		
	}
	
	public void logoutSuccess(){
		homeActivity.finish();
		Intent intent = new Intent(Hula.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		hulaService.makeToast("You have been logged out bitch!");
	}
	
}
