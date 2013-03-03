package com.hula;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class HulaService extends Service {  
	private static final String TAG = "HulaService"; 
	private Hula hula;
	private String domain = "10.2.106.126";//"10.0.2.2" 
	private String service = "hula";
	private ConnectionConfiguration cc;   
	private XMPPConnection mXmppConnection;
	private ConnectionListener conLis;
	private PacketListener presenceListener;
	private PacketListener messageListener;
	private Handler mHandler = new Handler(); 

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");	
		XMPPConnection.DEBUG_ENABLED = true;
		ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new
				org.jivesoftware.smackx.provider.VCardProvider());

		hula = ((Hula) HulaService.this.getApplication());
		SmackConfiguration.setKeepAliveInterval(10000);
		

		
		
	}
	
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Log.d(TAG, "onStartCommand");
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		Hula hula = ((Hula) HulaService.this.getApplication());
	    hula.doBindService();  
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(final Intent intent) {
		Log.d(TAG, "onBind");
		return new LocalBinder<HulaService>(this);
	}

	@Override
	public boolean onUnbind(final Intent intent) {
		return true;
	}
	
	@Override
	public void onDestroy() {		
		super.onDestroy();
		mXmppConnection.disconnect();
	}
	
	public void register(final String uname, final String pword){
		Log.e(TAG, "registerUser: "+uname+" - "+pword);
		final Hula hula = ((Hula) HulaService.this.getApplication());
		
		Thread registerThread = new Thread("registerThread") {
			
			public void run(){
					cc = new ConnectionConfiguration(domain, 5222, service );
					cc.setSASLAuthenticationEnabled(false); 
					mXmppConnection = new XMPPConnection(cc);  
					
					
					
					try {
						
						mXmppConnection.connect();
						setConListener();
						AccountManager am = mXmppConnection.getAccountManager();
						Map<String, String> map = new HashMap<String, String>();
						map.put("name", uname);
						
				 
				        am.createAccount(uname, pword, map);

					}
					catch (final XMPPException e) {
						Log.i(TAG, "Could not register user", e);
						//hula.registerFail();
						return; 
					} 

					if (!mXmppConnection.isConnected()) {
						Log.i(TAG, "Could not register user 2.");
						
						mHandler.post(new Runnable() {
							public void run() {
								hula.registerFail();
							}
						});
						
						return;
						
					}

					
					mHandler.post(new Runnable() {
						public void run() {
							mXmppConnection.disconnect();
							hula.registerSuccess(uname, pword);
						}
					});


			} // run
			
		}; // registerThread
	
		// start thread
		registerThread.start();	

		
	}
		
	public void login(final String uname, final String pword){ 
		Log.d(TAG, "login");
		 
		
		Thread loginThread = new Thread("loginThread") {
			
			public void run(){
				SmackConfiguration.setPacketReplyTimeout(3000);
				SmackConfiguration.setKeepAliveInterval(-1);
					cc = new ConnectionConfiguration(domain, 5222, service );
					cc.setSASLAuthenticationEnabled(false);
					cc.setRosterLoadedAtLogin(true);
					cc.setCompressionEnabled(false);
					cc.setReconnectionAllowed(true); 
					
					
					mXmppConnection = new XMPPConnection(cc);
					
					
					
					try {
						
						mXmppConnection.connect();
						
						mXmppConnection.login(uname, pword, "mobile" ); 
						
						// get vCard for logged in user
						//uservCard = getvCard(mXmppConnection.getUser());
						
						Log.i(TAG, "Yey! We're connected to the Xmpp server!");
						
						final String userId =  	uname+"@"+mXmppConnection.getServiceName();
												
						mHandler.post(new Runnable() {
							public void run() {
								
								
								
								Hula hula = ((Hula) HulaService.this.getApplication());
								hula.onLoginSuccess(userId, uname, pword);
								
								
							}
						});
						
						
						
						
					}
					catch (final XMPPException e) {
						Log.e(TAG, "Could not connect to Xmpp server 0.", e); 
						Hula hula = ((Hula) HulaService.this.getApplication());
						hula.onLoginFail();
						return; 
					}

					if (!mXmppConnection.isConnected()) {
						Log.e(TAG, "Could not connect to the Xmpp server.");
						
						HulaService.this.mXmppConnection = null;
						final Thread t = Thread.currentThread();
						Log.i(TAG, "THREAD NAME: "+ t.getName());
						Hula hula = ((Hula) HulaService.this.getApplication());
						hula.onLoginFail();
						
						return;
						
					}
					
					setConListener();
					
					

			} // run
			
		}; // loginThread
	
		// start thread
		loginThread.start();	
					
		
		
	
		
		
	}
	
	public void logout(){
		Presence presence = new Presence(Presence.Type.unavailable);
		 
		mXmppConnection.disconnect(presence);
		mHandler.post(new Runnable() {
			public void run() {
				
				hula.logoutSuccess(); 
			}
		});	
		
	}
			
	public void setConListener(){
		Log.e("connection", "setConListener");
		conLis = new ConnectionListener() {
			
			public void reconnectionSuccessful() {
				
				Log.e("connection", "reconnectionSuccessful");
				
				
			}
			
			public void reconnectionFailed(Exception e) {
				Log.e("connection", "reconnectionFailed");
				
				reconnect();
				
				
			}
			
			public void reconnectingIn(int seconds) {
					
				Log.e("connection", "reconnectingIn: " +seconds);
				makeToast("reconnectingIn");
			}
			
			public void connectionClosedOnError(Exception e) {
				mXmppConnection.removeConnectionListener(this);
				mXmppConnection.removePacketListener(presenceListener);
				mXmppConnection.removePacketListener(messageListener);
				
				Log.e("connection", "connectionClosedOnError: "+e.toString()); 
				mXmppConnection.disconnect();
				reconnect();
				
			}
			
			public void connectionClosed() {
				mXmppConnection.removeConnectionListener(this);
				mXmppConnection.removePacketListener(presenceListener);
				mXmppConnection.removePacketListener(messageListener);
				Log.e("connection", "connectionClosed");
				
				reconnect();
			} 
		};
		this.mXmppConnection.addConnectionListener(conLis); 
		
		
	}
		
	public void reconnect(){
		if(Hula.userLogged){
			
		
			if(!mXmppConnection.isConnected()){
				
				mXmppConnection = null;
				
				try {
					Log.d("connection", "reconnecting");
					Log.e("connection", "sleeping");
					makeToast("sleeping");
					Thread.sleep(5000);
					cc = new ConnectionConfiguration(domain, 5222, service );
					cc.setSASLAuthenticationEnabled(false);
					cc.setRosterLoadedAtLogin(true);
					cc.setCompressionEnabled(false);
					cc.setReconnectionAllowed(true);
					Presence presence = new Presence(Presence.Type.available);
					cc.setSendPresence(true);
					
					mXmppConnection = new XMPPConnection(cc);
					mXmppConnection.connect();
					if(mXmppConnection.isConnected()){
						//SASLAuthentication.supportSASLMechanism("PLAIN", 0); 
						String[] uname = hula.getUser().getEmail().split("@");
						
						mXmppConnection.login(uname[0].toString(), hula.getUser().getPassword(),"mobile");
						mXmppConnection.sendPacket(presence);
						setPacketFilters();
						setConListener();
					}
					
				} catch (XMPPException e) {
					Log.e("connection", "reconnect error: "+e.toString());
					
					reconnect();
					e.printStackTrace();
				} catch (InterruptedException e) {
					Log.e("connection", "reconnect thread error");
					
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(mXmppConnection.isConnected()){
					Log.e("connection", "reconnected");
					
					
				}
				
				if(!mXmppConnection.isConnected()){
					Log.e("connection", "not reconnected");
					
					
					
				}
				
			}
		
		}
		
	}
	
	public void setPacketFilters() {
		
		presenceListener = new PacketListener() {
			public void processPacket(Packet packet) {
				final Presence presence = (Presence) packet;
				mHandler.post(new Runnable() {
					public void run() {
						Hula hula = ((Hula) HulaService.this.getApplication()); 
						hula.presenceUpdates(presence); 
					}
				});	
				
			}
		};
		
		messageListener = new PacketListener() {
			public void processPacket(Packet packet) {
				final Message message = (Message) packet;
				mHandler.post(new Runnable() {
					public void run() {
						if (message.getBody() != null) {
							
							Hula hula = ((Hula) HulaService.this.getApplication());
							hula.handleMessageReceived(message);
						}
					}
				});
			}
		};
		PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);
		PacketFilter messageFilter = new MessageTypeFilter(Message.Type.chat);

		final Thread t = Thread.currentThread();
		Log.i(TAG, "setPacketFilters THREAD NAME: "+ t.getName());
		if (this.mXmppConnection != null) {
			
			this.mXmppConnection.addPacketListener(presenceListener, presenceFilter);
			
			
			this.mXmppConnection.addPacketListener(messageListener, messageFilter);  
				
		}	
		
	}   
		  
	public Roster getRoster() {
		return this.mXmppConnection.getRoster();
		
	}

	public void sendMessage(String to, String text) {
		Message msg = new Message(to, Message.Type.chat);
		msg.setBody(text);
		this.mXmppConnection.sendPacket(msg);
	}

	public void setStatus(String status) {
		Presence prs = new Presence(Presence.Type.available, status, 1, Presence.Mode.available);
		this.mXmppConnection.sendPacket(prs);
	}	
	
	public void checkVCard(final String person){
		
		Thread vCardThread = new Thread() {
			public synchronized void run(){ 
				final VCard vCard = new VCard(); 
				try { 
					SmackConfiguration.setPacketReplyTimeout(7000);
					vCard.load(HulaService.this.mXmppConnection, person);
					Log.v("CVC", "loading ");
					Thread.sleep(7000);
					
					 
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					Log.v("CVC", "checkVCard " + e.toString());
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				} 
		
				Thread vCardThreadt2 = new Thread() {
					public synchronized void run(){ 
						hula.updateVCard(person, vCard);
					} 	
				};
				vCardThreadt2.start();
				
				//Log.v("CVC", "checkVCard" +vCard.toXML());
					
			}
		};
		
		vCardThread.start();	
		
	}

	public void updateMyDetails(final String name, final byte[] avatar){
		Log.d("updatemy", "vcard hulaservice update");
		Thread uvCardThread = new Thread("uvCardThread") {
			public synchronized void run(){
				
				
				try {
					VCard vCard = new VCard();  
					//vCard.setField("FN", "Farax");
					vCard.setFirstName(name); 
					vCard.setField("FN", name);
					//vCard.setEncodedImage(encodedImage);
					if(avatar!=null){
						vCard.setAvatar(avatar);
					}
					
					vCard.save(mXmppConnection);
					setPacketFilters();
					mHandler.post(new Runnable() {
						public void run() {
							checkVCard(hula.getUser().getEmail());
							hula.updateMyDetailsSuccess();
						}
					});
					
					Log.d("updatemy", "vcard hulaservice done");
					
					//checkVCard(mXmppConnection.getUser().);
					
			} catch (XMPPException e) {
				//Toast.makeText(HulaService.this, "VCard updated by hulaService!!! EEEEEEEEEROR", Toast.LENGTH_SHORT).show();
				Log.e("updatemy", "vcard update ERROR!!! >> " + e.toString());
			}
				
			//Toast.makeText(HulaService.this, "VCard updated by hulaService!!! DONE", Toast.LENGTH_SHORT).show();
		
		}
	};
	uvCardThread.start();
	}
	
	public void makeToast(final String text){
		mHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(HulaService.this, text, Toast.LENGTH_SHORT).show();
			}
		});	
	}
	
	
}
