package com.hula;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.hula.adapter.FriendsAdapter;

public class FriendsFragment extends Fragment{
	
	private String TAG = "FriendsFragment";
	private Hula hula;
	private ArrayList<Contact> contacts;
	private FriendsAdapter adapter; 
	private GridView mGridView;
	private UserOptions mListener;
	
	 
	
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach");
		super.onAttach(activity);
		try {
            mListener = (UserOptions) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement UserOptions interface");
        }
		
		
		 
	}
	
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		hula = ((Hula) FriendsFragment.this.getActivity().getApplication());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.contact_gridview, container, false);
		mGridView = (GridView) view.findViewById(R.id.gridview);
		
		return view;
	}
 

	@Override
	public void onResume() {
		super.onResume();
		hula.friendsFragmentInit(this);
		updateContactList();
	}

	@Override
	public void onPause() {
		super.onPause();
		hula.friendsFragmentDeInit();
	}


	public void updateContactList() {
		
		contacts = new ArrayList<Contact>();
		int n = hula.getUser().getContacts().size();
		while (n-- > 0) {
			  
			contacts.add(hula.getUser().getContacts().get(n));
		}
			  
		
		adapter = new FriendsAdapter(getActivity().getApplicationContext(), contacts);
		if(mGridView != null){
			Log.e("FF", "grid is not null!!");
			mGridView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			
			
		}
		mGridView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	Hula hula = ((Hula) FriendsFragment.this.getActivity().getApplication());
				mListener.contactSelected(adapter.getEmail(position));  
		                         
		    }
		});
		
		
		Log.i(TAG, "FRIENDS UPDATED!!!");
	}

	
	
	
}