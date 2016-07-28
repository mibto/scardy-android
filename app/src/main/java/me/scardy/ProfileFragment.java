package me.scardy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ProfileFragment extends Fragment {

    EditText fullName;
    EditText address;
    EditText phone;
    EditText email;

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
}
