package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class Create_Fragment extends Fragment implements
    AddUsers_Adapter.CheckProfileInterface{
    private static final String TAG = "CreateFragment: ";
    private Core_ViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        ViewPager viewPager = view.findViewById(R.id.pager_create_locations);
        CreateFragmentAdapter pagerAdapter = new CreateFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs_create_locations);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_create_invite);
        AddUsers_Adapter adapter = new AddUsers_Adapter(this);
        recyclerView.setAdapter(adapter);

        mViewModel.getPublicUsers().observe(requireActivity(), (List<User_Model> publicUsers) -> {
            if(publicUsers != null){
                adapter.setData(publicUsers);
            }
        });
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        Log.w(TAG,"ADD THE USER TO THE LIST TO INVITE..");
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        Log.w(TAG,"REMOVE USER FROM INVITE LIST..");
    }

    private class CreateFragmentAdapter extends FragmentPagerAdapter{

        CreateFragmentAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Log.w(TAG,"Creating fragment for adapter for: "+i);
            switch (i){
                case 0:
                    return new BaseMap_Fragment();
                case 1:
                    return new CreateLocations_Fragment();
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Map";
                case 1:
                    return "Saved Locations";
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}