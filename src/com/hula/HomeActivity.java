package com.hula;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
 

public class HomeActivity extends FragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, UserOptions {
	
	String TAG = "HomeActivity";
	private Hula hula;
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, HomeActivity.TabInfo>();
	private PagerAdapter mPagerAdapter;
	private AlertDialog.Builder alertBuilder;
		
	
	private class TabInfo {
		 private String tag;
         private Class<?> clss;
         private Bundle args;
         private Fragment fragment;
         TabInfo(String tag, Class<?> clazz, Bundle args) {
        	 this.tag = tag;
        	 this.clss = clazz;
        	 this.args = args;
         }

	}
	
	class TabFactory implements TabContentFactory {

		private final Context mContext;

	   
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	   
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }

	}
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hula = (Hula) HomeActivity.this.getApplication();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Inflate the layout
		setContentView(R.layout.tabs_swipe);
		// Initialise the TabHost
		this.initialiseTabHost(savedInstanceState);
		// Intialise ViewPager
		this.intialiseViewPager();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		hula.homeActivityInit(this);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		hula.homeActivityDeInit();
		
	}

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    private void intialiseViewPager() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, StuffFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, FriendsFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, ConversationsFragment.class.getName()));
		this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
		//
		this.mViewPager = (ViewPager)super.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
		this.mViewPager.setCurrentItem(1);
    }

	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        
        HomeActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Stuff"),
        		( tabInfo = new TabInfo("Tab1", StuffFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        
        HomeActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Friends"), 
        		( tabInfo = new TabInfo("Tab2", FriendsFragment.class, args)));
        
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        HomeActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("Messages"), 
        		( tabInfo = new TabInfo("Tab3", ConversationsFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        //this.onTabChanged("Tab2");
        //
        mTabHost.setCurrentTab(1);
        mTabHost.setOnTabChangedListener(this);
        
	}
	
	private static void AddTab(HomeActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
	}

	public void onTabChanged(String tag) {
		TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = this.mTabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);

    }
	
	public void onPageScrolled(int position, float positionOffset,int positionOffsetPixels) {
	}

	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		this.mTabHost.setCurrentTab(position);
	}

	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

	public void stuffSelected(String contact) {
		Log.i(TAG, "selected stuff contact: " +contact);
		Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
		intent.putExtra("contact", contact);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
	}

	public void contactSelected(String contact) {
		Log.i(TAG, "selected contact: " +contact);
		Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
		intent.putExtra("contact", contact);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
		
	}

	public void convoSelected(String contact) {
		Log.i(TAG, "selected convo contact: " +contact);
		Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
		intent.putExtra("contact", contact);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.home_activity_menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.view_myprofile:
	        	Log.i(TAG, "MY PROFILE!!" );
	        	
	        	Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
	    		intent.putExtra("contact", hula.getUser().getEmail());
	    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	startActivity(intent);
	            return true;
	        case R.id.update_stuff:
	        	stuffDialog();
	        	return true;
	        case R.id.logout:
	        	hula.Logout();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void stuffDialog(){
        alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setTitle("Update new stuff...");
		final EditText input = new EditText(this);
		input.setFocusable(true);
		alertBuilder.setView(input);
		
		alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {

			// update stuffv
			hula.updateMyStuff(input.getText().toString());
			//input.getText().toString()
			
		  }
		});

		alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			  Log.i(TAG, "Cancel" );
		  }
		});

		alertBuilder.show();

	}

}

