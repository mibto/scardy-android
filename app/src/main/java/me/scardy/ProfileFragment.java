package me.scardy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ProfileFragment extends Fragment {

    private OnCompleteListener mListener;

    private EditText fullName;
    private EditText address;
    private EditText phone;
    private EditText email;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_profile, container, false );
        fullName = (EditText) view.findViewById( R.id.full_name_input );
        address = (EditText) view.findViewById( R.id.address_input );
        phone = (EditText) view.findViewById( R.id.phone_input );
        email = (EditText) view.findViewById( R.id.email_input );

        mListener.onComplete( 0 );
        return view;
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
        inflater.inflate( R.menu.menu_profile, menu );
        super.onCreateOptionsMenu( menu, inflater );
    }

    public String getName() {
        return fullName.getText().toString();
    }

    public String getAddress() {
        return address.getText().toString();
    }

    public String getPhone() {
        return phone.getText().toString();
    }

    public String getEmail() {
        return email.getText().toString();
    }

    public void setFullName( String fullName ) {
        this.fullName.setText( fullName );
    }

    public void setAddress( String address ) {
        this.address.setText( address );
    }

    public void setPhone( String phone ) {
        this.phone.setText( phone );
    }

    public void setEmail( String email ) {
        this.email.setText( email );
    }


    public void onAttach( Context context ) {
        super.onAttach( context );
        try {
            this.mListener = (OnCompleteListener) getActivity();
        } catch ( final ClassCastException e ) {
            throw new ClassCastException( getActivity().toString() + " must implement OnCompleteListener" );
        }
    }
}
