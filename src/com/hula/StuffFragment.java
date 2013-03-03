package com.hula;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hula.adapter.StuffAdapter;

/**
 * @author mwho
 *
 */
public class StuffFragment extends ListFragment {
	
	private static final String TAG = "StuffFragment";
	private Hula hula;
	private StuffAdapter adapter;
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
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		hula = ((Hula) StuffFragment.this.getActivity().getApplication());
		
	}

	@Override
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
		
		return (LinearLayout)inflater.inflate(R.layout.stuff_fragment_layout, container, false);
	}
	

	@Override
	public void onResume() {
		super.onResume();
		hula.stuffFragmentInit(this);
		updateStuff();
	}

	@Override
	public void onPause() {
		super.onPause();
		hula.stuffFragmentDeInit();
	}
	
	
	/*
	 *  Update Contacts List
	 */
	public void updateStuff() {

		Hula hula = ((Hula) StuffFragment.this.getActivity().getApplication());
		
		adapter = new StuffAdapter(getActivity(), hula.getUser().getStuff());
		setListAdapter(adapter);
		

	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Hula hula = ((Hula) StuffFragment.this.getActivity().getApplication());
		mListener.stuffSelected(hula.getUser().getStuff().get(position).getOwner());
	
	}

	


}
