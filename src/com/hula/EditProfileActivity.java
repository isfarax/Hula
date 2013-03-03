package com.hula;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfileActivity extends Activity {
	private String TAG = "EditProfileActivity";
	private Hula hula;
	private ImageButton img;
	private EditText name;
	private Button update;
	private String newPicPath;
	private Bitmap newPicThumb;
	private AlertDialog.Builder alertBuilder;
	private AlertDialog alertDialog;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		hula = ((Hula) EditProfileActivity.this.getApplication());	
		setContentView(R.layout.edit_profile_layout);
		newPicPath = null;
		
		name = (EditText)findViewById(R.id.edit_myname);
		name.setText(hula.getUser().getName());
		
		img = (ImageButton)findViewById(R.id.avatar_img_button);
		
		if(hula.getUser().getPic()!=null){
			img.setImageBitmap(hula.getUser().getPic());
		} else{
			img.setImageResource(R.drawable.faraxxx);
		}
		
		img.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        		
        		// select image for me
        		Log.i("EditProfile", "Get picture");
        		piclistDialog();
        		
        		
        		        
        	}
		});
		
		update = (Button)findViewById(R.id.update_profile_button);;
		
		update.setOnClickListener(new OnClickListener() {
            
        	public void onClick(View v) {
        		String newName = name.getText().toString();
        		Log.v("update", "file: "+newPicPath+" newName: " +newName);
        		
        		// create byte array from image file
        		hula.updateMyDetails(newName, newPicPath);
        		
        		showLoading("Updating your details...");
        		
        	}
		});
		
		
		
		
		
		
	}
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hula.editProfileActivityInit(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		hula.editProfileActivityDeInit();
		
		dialog.dismiss();
		

		this.finish();
	}


	public void piclistDialog(){
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {
		    // We can read and write the media
			Log.v("STORAGE", "STORAGE R+W: "+state);
			
			 alertBuilder = new AlertDialog.Builder(this);

		        alertBuilder.setTitle("Select picture...");
		       
				final GridView grid = new GridView(this);
				
				grid.setColumnWidth(200);
				grid.setNumColumns(GridView.AUTO_FIT);
				grid.setVerticalSpacing(5);
				grid.setHorizontalSpacing(5);
				grid.setAdapter(new ImageAdapter(this));
				alertBuilder.setView(grid);
				
				alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						
						Log.i("PicList", "OK");
						img.setImageBitmap(newPicThumb);
						Log.i("PicList", "path: "+newPicPath);
						
					  }
					});

				alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					public void onCancel(DialogInterface dialog) {
						//alertBuilder.show().dismiss();	 			
					}
				});
	
				alertBuilder.show();
			
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
			Log.v("STORAGE", "STORAGE SUMTING WENT WONG " +state);
			Toast.makeText(this, "STORAGE DEVICE NOT READY", Toast.LENGTH_SHORT).show();
			
		}
		
		
       

	} 
	

	public Bitmap decodeFileToBm(String path){
		Bitmap bm;
		
		System.gc();
	    Runtime.getRuntime().gc();  
		Log.i("decodeFileToBm", "decodeFileToBm");
		
		//Decode image size
        BitmapFactory.Options oo = new BitmapFactory.Options();
        oo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, oo);

        //The new size we want to scale to
        final int REQUIRED_SIZE=150;
 
        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(oo.outWidth/scale/2>=REQUIRED_SIZE && oo.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        o2.inPurgeable = true;
        o2.inInputShareable = true;
       
        bm = BitmapFactory.decodeFile(path, o2);
        System.gc();
        Runtime.getRuntime().gc(); 
		return bm;
	}
		
	
    public void showLoading(String msg){
	   Log.i(TAG, "msg::::::: "+msg );
	   if(dialog!=null){
		   if(dialog.isShowing()){
			   hideLoading();
		   }
	   }
	   
	   
	   dialog = ProgressDialog.show(EditProfileActivity.this, "", 
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
	

	class ImageAdapter extends BaseAdapter {
	    private LinkedHashMap<String,Bitmap> pics;
	    private Set<Entry<String, Bitmap>> set;
	    private Iterator<Entry<String, Bitmap>> i;
	    private Entry<String, Bitmap> me;
	    private String[] mKeys;
	    // references to our images
	    private File dir = new File(Environment.getExternalStorageDirectory() + "/transformers/");
	    private File[] files = dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String filename) {

                return true;//filename.contains(".jpg");
            }
        });
	      

	    ImageAdapter(Context c) {
	        Log.e("PIC","dir:");
	         pics = new LinkedHashMap<String, Bitmap>();
	        
	        for (File file : files){
	        	String path = file.getPath();
	        	Bitmap bm = decodeFileToBm(file.getPath());
	        	
	  	      Log.v("PIC", "file: "+file.getPath());
	  	      	pics.put(path, bm);
	  	     }
	        set = pics.entrySet(); 
			// Get an iterator 
			i = set.iterator(); 
			mKeys = pics.keySet().toArray(new String[pics.size()]);
			
	    }

	    public int getCount() {
	        return pics.size();
	    }

	    public Object getItem(int position) {
	        return pics.get(mKeys[position]);
	    } 

	    public long getItemId(int position) {
	        return position;
	    } 
	    
	    
	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(final int position, View convertView, ViewGroup parent) {
	    	
	    	int pos = 0; 
	    	
	    	LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	View view = inflater.inflate(R.layout.picture_list_layout, null);
			
			
			final String key = mKeys[position];
			final Bitmap pic = (Bitmap) getItem(position);
			ImageView imageView = (ImageView)view.findViewById(R.id.pic_list_img);
			imageView.setImageBitmap(pic);

	        view.setOnClickListener(new OnClickListener() {
	            
	        	public void onClick(View v) {
	        		newPicPath = key;
	        		newPicThumb = pic;
	        		//Log.d("EditProfile", "newAvPath: "+EditProfileActivity.this.newAvPath);
	        		//newAvPath = key;
	        		
	        		//Toast.makeText(EditProfileActivity.this, "img: "+newAvPath, Toast.LENGTH_SHORT).show();
	        	}
			});
	        return view;
	    } 

	    
	}
	
	
}
