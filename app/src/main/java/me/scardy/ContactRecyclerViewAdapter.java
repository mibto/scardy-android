package me.scardy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.scardy.ContactsFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Profile} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> {

    private List<Profile> profiles;
    private final OnListFragmentInteractionListener mListener;

    public ContactRecyclerViewAdapter( List<Profile> profiles, OnListFragmentInteractionListener listener ) {
        this.profiles = profiles;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.fragment_contact, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        holder.profile = profiles.get( position );
        Permission fullName = holder.profile.getPermission( Permission.PermissionType.FULL_NAME );
        Permission address = holder.profile.getPermission( Permission.PermissionType.ADDRESS );
        Permission phone = holder.profile.getPermission( Permission.PermissionType.PHONE );
        Permission email = holder.profile.getPermission( Permission.PermissionType.EMAIL );
        if ( fullName != null ) {
            holder.full_name.setText( fullName.getValue() );
        }
        if ( address != null ) {
            holder.address.setText( address.getValue() );
        }
        if ( phone != null ) {
            holder.phone.setText( phone.getValue() );
        }
        if ( email != null ) {
            holder.email.setText( email.getValue() );
        }

        holder.mView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if ( null != mListener ) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction( holder.profile );
                }
            }
        } );

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void addContact( Profile contact ) {
        profiles.add( contact );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView full_name;
        public final TextView address;
        public final TextView phone;
        public final TextView email;
        public Profile profile;

        public ViewHolder( View view ) {
            super( view );
            mView = view;
            full_name = (TextView) view.findViewById( R.id.list_full_name );
            address = (TextView) view.findViewById( R.id.list_address );
            phone = (TextView) view.findViewById( R.id.list_phone );
            email = (TextView) view.findViewById( R.id.list_email );
        }

        @Override
        public String toString() {
            return super.toString() + " '" + address.getText() + "'";
        }
    }
}
