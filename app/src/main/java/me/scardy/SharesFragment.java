package me.scardy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import me.scardy.dummy.DummyContent;
import me.scardy.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SharesFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SharesFragment() {
    }

    public static SharesFragment newInstance() {
        return new SharesFragment();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_share_list, container, false );

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById( R.id.share_list );
        recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
        recyclerView.setAdapter( new ShareRecyclerViewAdapter( DummyContent.ITEMS, mListener ) );
        return view;
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnListFragmentInteractionListener ) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException( context.toString() + " must implement OnListFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult( requestCode, resultCode, intent );
        if ( scanResult != null ) {
            String re = scanResult.getContents();
            Log.d( "code", re );
        }
        // else continue with any other code you need in the method
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction( DummyItem item );
    }
}
