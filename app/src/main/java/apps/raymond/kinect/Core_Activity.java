/*
 * ToDo:
 * 1. Get the user permission for camera and document access on start up and store as a SharedPreference.
 * 3. Move all the currentUser stuff to repository return methods.
 * 4. Allow user to remove/delete events and or Groups if they are owner for deletes.
 * 6. Recycler View for events does not properly display information.
 * 8. When inflating detail and clicking edit, the edit text should be filled with the current data instead of blank.
 * 10.Redefine the layout for the edit group/event
 *
 * MESSAGING FOR INVITES:
 * Doc listener for a invites doc.
 *
 * OPTIMIZE:
 * If a fragment is committed and has setHasOptionsMenu(true), it will call the parent activity's
 * onCreateOptionsMenu. This means that to optimize computing, only mandatory calls should be executed
 * inside the activity's onCreateOptionsMenu method.
 */

package apps.raymond.kinect;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import apps.raymond.kinect.DialogFragments.InviteDialog;
import apps.raymond.kinect.Events.Event_Create_Fragment;
import apps.raymond.kinect.Events.GroupEvent;
import apps.raymond.kinect.Groups.Core_Group_Fragment;
import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.Groups.Group_Create_Fragment;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.UserProfile.ProfileFrag;

public class Core_Activity extends AppCompatActivity implements
        Group_Create_Fragment.AddGroup, Event_Create_Fragment.AddEvent, View.OnClickListener,
        SearchView.OnQueryTextListener, ProfileFrag.DestroyProfileFrag {

    private static final String TAG = "Core_Activity";
    private static final String INV_FRAG = "InviteFragment";
    private static final String PROFILE_FRAG = "ProfileFragment";
    private static final String CREATE_EVENT_FRAG = "CreateEvent";
    private static final String CREATE_GROUP_FRAG = "CreateGroup";

    public static final int YESNO_REQUEST = 21;
    public UpdateGroupRecycler updateGroupRecycler;

    public interface UpdateGroupRecycler{
        void updateGroupRecycler(GroupBase groupBase);
    }

    public UpdateEventRecycler updateEventRecycler;
    public interface UpdateEventRecycler{
        void updateEventRecycler(GroupEvent groupEvent);
    }

    ViewPager viewPager;
    Repository_ViewModel viewModel;
    SearchView toolbarSearch;
    Core_Activity_Adapter pagerAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewModel = ViewModelProviders.of(this).get(Repository_ViewModel.class);
        setContentView(R.layout.core_activity);

        toolbar = findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarSearch = findViewById(R.id.toolbar_search);
        toolbarSearch.setOnQueryTextListener(this);

        viewPager = findViewById(R.id.core_ViewPager);
        pagerAdapter = new Core_Activity_Adapter(getSupportFragmentManager());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if(fragment instanceof Core_Group_Fragment){
            try {
                updateGroupRecycler = (UpdateGroupRecycler) fragment;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Group_Fragment does not implement UpdateGroupRecycler interface.");
            }
            return;
        }

        if(fragment instanceof Core_Events_Fragment){
            try {
                updateEventRecycler = (UpdateEventRecycler) fragment;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Events_Fragment does not implement UpdateEventRecycler interface.");
            }
        }

    }

    MenuItem eventCreate, groupCreate;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.i(TAG,"PLEASE WTF");
        getMenuInflater().inflate(R.menu.core_menu,menu);

        toolbar.setBackgroundColor(getColor(R.color.colorAccent));
        toolbar.setNavigationIcon(R.drawable.baseline_face_black_18dp);
        toolbar.setNavigationOnClickListener(this);

        eventCreate = menu.findItem(R.id.action_create_event);
        groupCreate = menu.findItem(R.id.action_create_group);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            /*case R.id.action_logout:
                Log.i(TAG,"Clicked the logout button.");
                logout();
                return true;
            case R.id.action_settings:
                Log.i(TAG,"Clicked the settings button.");
                return true;
            case R.id.action_test:
                Log.i(TAG,getSupportFragmentManager().getFragments().toString());
                return true;*/
            case R.id.action_invites:
                Log.i(TAG,"Clicked on invites button");
                InviteDialog inviteDialog = new InviteDialog();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,inviteDialog,INV_FRAG)
                        .addToBackStack(INV_FRAG)
                        .commit();
                return true;
            case R.id.action_create_event:
                Event_Create_Fragment eventFragment = new Event_Create_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,eventFragment,CREATE_EVENT_FRAG)
                        .addToBackStack(CREATE_EVENT_FRAG)
                        .commit();
                return true;
            case R.id.action_create_group:
                Group_Create_Fragment groupFragment = new Group_Create_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,groupFragment,CREATE_GROUP_FRAG)
                        .addToBackStack(CREATE_GROUP_FRAG)
                        .commit();
                return true;
            case R.id.action_edit:
                return false;
            case R.id.action_save:
                return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case -1:
                ProfileFrag profileFrag = new ProfileFrag();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_down, 0,0,R.anim.slide_out_up)
                        .replace(R.id.full_core_frame,profileFrag,PROFILE_FRAG)
                        .addToBackStack(PROFILE_FRAG)
                        .commit();
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        int i = viewPager.getCurrentItem();
        Fragment fragment = pagerAdapter.getRegisteredFragment(i);
        switch (i){
            case 0:
                ((Core_Events_Fragment) fragment).filterRecycler(s);
                break;
            case 1:
                ((Core_Group_Fragment) fragment).filterRecycler(s);
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void addToGroupRecycler(GroupBase groupBase) {
        Log.i(TAG,"Called addToGroupRecycler implementation in Core Activity");
        updateGroupRecycler.updateGroupRecycler(groupBase);
    }

    @Override
    public void addToEventRecycler(GroupEvent groupEvent) {
        Log.i(TAG,"Called addToEventRecycler implementation in the CoreAct");
        updateEventRecycler.updateEventRecycler(groupEvent);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG,"List of fragments: \n " + getSupportFragmentManager().getFragments().toString());
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count==0){
            Log.i(TAG,"There is nothing in the back stack");
            if(viewPager.getCurrentItem() == 0){
                super.onBackPressed();
            } else if (viewPager.getCurrentItem() == 1){
                viewPager.setCurrentItem(0);
            }
        } else {
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(count-1).getName();
            Fragment topFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if(topFragment instanceof BackPressListener){
                Log.i(TAG,"Top fragment is an instanceof OnBackListener");
                ((BackPressListener)topFragment).onBackPress();
            } else {
                Log.i(TAG,"BackPressListener interface is not inherited by this fragment.");
                super.onBackPressed();
            }
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!toolbarSearch.isIconified()){
            toolbarSearch.setIconified(true);
        }

        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof EditText){
                v.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(),0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void destroyProfileFrag() {
        getSupportFragmentManager().popBackStack(PROFILE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}

/* Creates a connection to the current user. Should move so that this code is only called when you are viewing another user.
    userViewModel.createConnection().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                Log.i(TAG,"Successfully added connection.");
            }
        }
    });
 */
