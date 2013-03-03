package com.hula;

import java.sql.Wrapper;
import java.util.ArrayList;

import com.hula.adapter.ConversationsAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Farax
 *
 */
public class ConversationsFragment extends ListFragment {
	
	private static final String TAG = "ConversationsFragment";
	private Hula hula;
	private ConversationsAdapter adapter;
	private UserOptions mListener;
	

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		Log.d(TAG, "onAttach");
		try {
            mListener = (UserOptions) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement UserOptions interface");
        }
		hula = ((Hula) ConversationsFragment.this.getActivity().getApplication());
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		
		
		 
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		
		
		
		if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
		
		return (LinearLayout)inflater.inflate(R.layout.conversations_fragment_layout, container, false);
	}


	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		hula.conversationsFragmentInit(this);
		updateConversationList();
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		hula.conversationsFragmentDeInit();
	}


	public void updateConversationList() {
		final Thread t = Thread.currentThread();
		Log.i(TAG, "updateConversationList THREAD NAME: "+ t.getName());
		try {
			
				Hula hula = ((Hula) ConversationsFragment.this.getActivity().getApplication());
		        
				adapter = new ConversationsAdapter(getActivity(), hula.getUser().getConversations());
			
			setListAdapter(adapter);
			Log.e(TAG, "updated!");
			
		} catch (Exception e) {
			Log.e(TAG, "not updated!");
		}
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		
		Hula hula = ((Hula) ConversationsFragment.this.getActivity().getApplication());
		ArrayList<Conversation> convo = hula.getUser().getConversations();

		mListener.convoSelected(convo.get(position).getWith());
	}









	

}
