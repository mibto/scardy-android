package me.scardy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends Fragment {

    private OnListFragmentInteractionListener listInteractionListener;
    private OnCompleteListener mListener;
    private ContactRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactsFragment() {
    }

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_contact_list, container, false );

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById( R.id.contact_list );
        recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
        mAdapter = new ContactRecyclerViewAdapter( new ArrayList<Profile>(), listInteractionListener );
        recyclerView.setAdapter( mAdapter );
        mListener.onComplete( 1 );
        return view;
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        this.mListener = (OnCompleteListener) getActivity();
        if ( context instanceof OnListFragmentInteractionListener ) {
            listInteractionListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException( context.toString() + " must implement OnListFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listInteractionListener = null;
    }

    public void addContact( Profile contact ) {
        mAdapter.addContact( contact );
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
        void onListFragmentInteraction( Profile item );
    }
}
