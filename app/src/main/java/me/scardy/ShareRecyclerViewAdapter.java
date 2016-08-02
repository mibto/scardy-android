package me.scardy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.scardy.SharesFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactKey} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ShareRecyclerViewAdapter extends RecyclerView.Adapter<ShareRecyclerViewAdapter.ViewHolder> {

    private final List<SharedKey> shares;
    private final OnListFragmentInteractionListener mListener;

    public ShareRecyclerViewAdapter( List<SharedKey> items, OnListFragmentInteractionListener listener ) {
        shares = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.fragment_share, parent, false );
        return new ViewHolder( view );
    }

    public void addShare( SharedKey sharedKey ) {
        shares.add( sharedKey );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        holder.mItem = shares.get( position );
        holder.mIdView.setText( new CryptoClient().getHash( shares.get( position ).getKey().getEncoded() ) );
        holder.mContentView.setText( shares.get( position ).getLabel() );

        holder.mView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if ( null != mListener ) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction( holder.mItem );
                }
            }
        } );

    }

    @Override
    public int getItemCount() {
        return shares.size();
    }

    public void addShares( List<SharedKey> sharedKeys ) {
        for ( SharedKey sharedKey : sharedKeys ) {
            shares.add( sharedKey );
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public SharedKey mItem;

        public ViewHolder( View view ) {
            super( view );
            mView = view;
            mIdView = (TextView) view.findViewById( R.id.list_full_name );
            mContentView = (TextView) view.findViewById( R.id.list_address );
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
