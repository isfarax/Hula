package com.hula;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	private static final String TAG = "LoginActivity";
	private Hula hula;
	private EditText uname;
	private EditText pword;
	private AlertDialog.Builder alertBuilder;
	private AlertDialog alertDialog;
	private ProgressDialog dialog;
	private Handler mHandler = new Handler();
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        hula = ((Hula) LoginActivity.this.getApplication());
    }
  
	@Override
	protected void onStart() {
		super.onStart();
		if(Hula.userLogged){
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			loadLoginScreen();
		}
	}
   
	@Override
	protected void onResume() {
		super.onResume();
		hula.loginActivityInit(this);
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		hula.loginActivityDeInit();
	}
	
    @Override
	protected void onDestroy() {
    	Log.i(TAG, "onDestroy" );
    	super.onDestroy();
	}
	
	
    public void loadLoginScreen(){
        setContentView(R.layout.login_layout);
        
        
       
        
        
        //uname = (EditText)findViewById(R.id.username);
        //pword = (EditText)findViewById(R.id.password);
        
        //hula.prepareLogin("abdi", "ealing12");
        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button register = (Button)findViewById(R.id.login_register_button);
        
        
        loginButton.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) { 
        		uname = (EditText)findViewById(R.id.username);
                pword = (EditText)findViewById(R.id.password);
        		Log.i(TAG, "uname: " + uname.getText().toString() + "password: " + pword.getText().toString() );
        		login(uname.getText().toString(), pword.getText().toString());
        		
        		
        		
            }

			
        });
        
        register.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        		 Log.i(TAG, "register" );
        		//Log.i(TAG, "uname: " + uname.getText().toString() + "password: " + pword.getText().toString() );
        		 prepareRegister("Repeat password.");
        		
            }

			
        });
    }
    
	public void login(String uname, String pword){
    	
    	
		hideDialog();
		hideLoading();
		showLoading("Connecting, please wait...");
    	Hula hula = ((Hula) LoginActivity.this.getApplication());
        hula.prepareLogin(uname, pword);
    	
    }
	
    public void onLoginFail(){
    	
    	
    	new Thread(new Runnable() {
    	    public void run() {
    	      try {
				Thread.sleep(1000);
				mHandler.post(new Runnable() {
					public void run() {
						hideDialog();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	      
    	      
    	    }
    	}).start();
  	
    	
    }
	
	public void prepareRegister(final String title){
		final Hula hula = ((Hula) LoginActivity.this.getApplication());
		uname = (EditText)findViewById(R.id.username);
        pword = (EditText)findViewById(R.id.password);
        alertBuilder = new AlertDialog.Builder(this);
		if(!uname.getText().toString().isEmpty() && !pword.getText().toString().isEmpty() ){
			

			alertBuilder.setTitle(title);
			//alert.setMessage("Message");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			input.setPadding(5, 5, 5, 5);
			alertBuilder.setView(input);
			
			alertBuilder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				

				
				Log.i(TAG, "Register 2 button" );
				if(input.getText().toString().equals(pword.getText().toString())){
					Log.i(TAG, "password same!" );
					hula.register(uname.getText().toString(), pword.getText().toString());
				} else {
					Log.i(TAG, "password NOT same!" );
					prepareRegister("Incorrect password repeat! Please try again.");
					
					
				}
				
			  }
			});

			alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				  Log.i(TAG, "Cancel" );
			  }
			});

			alertBuilder.show();
		}
		
		// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
    	//setContentView(R.layout.login_loading_layout);
    	
    	//Hula hula = ((Hula) LoginActivity.this.getApplication());
		//hula.register(uname, pword);
    	
    }
	
    
    public void showLoading(String msg){
	   Log.i(TAG, "msg::::::: "+msg );
	   if(dialog!=null){
		   if(dialog.isShowing()){
			   hideLoading();
		   }
	   }
	   
	   
	   dialog = ProgressDialog.show(LoginActivity.this, "", 
               msg, true);
	   
	   
	   dialog.show();
	   
   }
 
    public void hideLoading(){
	   if(dialog!=null){
		   if(dialog.isShowing()){
			   dialog.hide();
			   dialog.dismiss();
		   }
	   }
	   
	   
	   
   }
   
    public void showDialog(String msg){
	   Log.i(TAG, "msg::::::: "+msg );
	   alertBuilder = new AlertDialog.Builder(this);
	   alertBuilder.setMessage(msg)
	          .setCancelable(false);
	   alertDialog = alertBuilder.create();
	   hideLoading();
	   
	   alertDialog.show();
	   
   }
   
   
    public void hideDialog(){
	   if(alertDialog!=null){
		   if(alertDialog.isShowing()){
			   alertDialog.hide();
			   alertDialog.dismiss();
		   }
	   }
	   
	   
	   
   }

    
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    
}