package me.scardy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {
    private ViewPager mViewPager;
    private FragmentManager fm;

    SectionsPagerAdapter( ViewPager mViewPager, FragmentManager fm ) {
        super( fm );
        this.mViewPager = mViewPager;
        this.fm = fm;
    }

    @Override
    public Fragment getItem( int position ) {

        Fragment fragment = fm.findFragmentByTag( "android:switcher:" + mViewPager.getId() + ":" + getItemId( position ) );

        // Return the fragment if it was stored in the fragment manager
        if ( fragment != null ) {
            return fragment;
        }

        // getItem is called to instantiate the fragment for the given page.
        switch ( position ) {
            case 0:
                return ProfileFragment.newInstance();
            case 1:
                return ContactsFragment.newInstance();
            case 2:
                return SharesFragment.newInstance();
            default:
                return ProfileFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle( int position ) {
        switch ( position ) {
            case 0:
                return "Profile";
            case 1:
                return "Contacts";
            case 2:
                return "Shares";
        }
        return null;
    }
}
