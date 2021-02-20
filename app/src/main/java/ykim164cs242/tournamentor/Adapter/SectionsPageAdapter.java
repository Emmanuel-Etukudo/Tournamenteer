package ykim164cs242.tournamentor.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * SectionsPageAdapter is a class that represents an adapter that represents
 * each page as a Fragment that is persistently kept in the fragment manager.
 * It creates lists of fragments and strings and has a method that adds the
 * information of the tab into these lists. This class can be used in the
 * MainActivty class to add tab options in the application.
 */

public class SectionsPageAdapter extends FragmentPagerAdapter{

    // Storages for tab fragments and tab name strings
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> tabNameList = new ArrayList<>();

    // Default constructor
    public SectionsPageAdapter(FragmentManager manager) {
        super(manager);
    }

    // add fragments to the fragment list
    public void addFragment(Fragment fragment, String tabName) {
        fragmentList.add(fragment);
        tabNameList.add(tabName);
    }

    // Used to return the page title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabNameList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}
