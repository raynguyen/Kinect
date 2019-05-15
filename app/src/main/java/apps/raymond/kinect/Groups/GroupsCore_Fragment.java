/*
 * ToDo:
 * 1)Implement the swiperefresher that updates the RecyclerView CardViews.
 * 2)We should only ever reload all the information on the pull down refresher.
 * 3)If a new group is created or if a group is modified, manually add or update the corresponding card.
 */

//https://stackoverflow.com/questions/32303492/android-animate-recyclerview-item-of-fragment-inside-viewpager
package apps.raymond.kinect.Groups;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Core_ViewModel;

public class GroupsCore_Fragment extends Fragment implements GroupRecyclerAdapter.GroupClickListener, Core_Activity.UpdateGroupRecycler {
    private static final String TAG = "GroupsCore_Fragment";
    private Groups_ViewModel model;

    //Required empty fragment. Not sure why it is needed.
    public GroupsCore_Fragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(requireActivity()).get(Groups_ViewModel.class);
        viewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        subscribeToModel();
    }

    Core_ViewModel viewModel;
    ArrayList<Group_Model> myGroups;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.groups_core_fragment, container, false);
    }

    GroupRecyclerAdapter mAdapter;
    ProgressBar progressBar;
    int scrolledHeight = 0;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myGroups = new ArrayList<>();
        progressBar = view.findViewById(R.id.progress_bar);

        final Button exploreGroupsBtn = view.findViewById(R.id.explore_groups_btn);
        final RecyclerView groupsRecycler = view.findViewById(R.id.card_container);
        groupsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledHeight = scrolledHeight + dy;
                if(scrolledHeight >= exploreGroupsBtn.getHeight() && exploreGroupsBtn.getVisibility()==View.VISIBLE){
                    exploreGroupsBtn.setVisibility(View.GONE);
                    groupsRecycler.scrollTo(0,scrolledHeight);
                }
                if(scrolledHeight < exploreGroupsBtn.getHeight()){
                    exploreGroupsBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        mAdapter = new GroupRecyclerAdapter(myGroups, this);
        updateCardViews();

        groupsRecycler.setAdapter(mAdapter);
        groupsRecycler.addItemDecoration(new Margin_Decoration_RecyclerView());
        groupsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void subscribeToModel(){
        model.getGroups().observe(this, new Observer<List<Group_Model>>() {
            @Override
            public void onChanged(@Nullable List<Group_Model> groupBases) {
                Log.i(TAG,"DOES THIS EVER GET CALLED");
                mAdapter.setData(myGroups);
            }
        });
    }

    /*
     * To truly follow SoC principle, the Fragment should not do the conversion from
     * DocumentSnapshot to Group_Model object, it should be dealt with by the repository.
     *
     * Need to check to see what happens if there is no photoURI in the group.
     *
     * Create the cards, then download the images is probably the cleaner approach. This prevents the
     * user from having to wait extended periods of time before viewing all their groups.
     */
    //This guy reads from the fields of the document. App crashes at this point because it is returning an object that is not a groupbase so groupbase.getname blows up!
    private void updateCardViews(){
        viewModel.getUsersGroups();
                /*
                .addOnCompleteListener(new OnCompleteListener<List<Task<Group_Model>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<Group_Model>>> task) {
                if(task.getResult()!=null){
                    Tasks.whenAllSuccess(task.getResult()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            if(objects.size()>0){
                                myGroups = new ArrayList<>();
                                for(Object object:objects){
                                    myGroups.add((Group_Model) object);
                                }
                                model.setGroups(myGroups);
                            } else {
                                // DISPLAY THE NO GROUPS ATTACHED TO USER IMAGE.
                                //ToDO WHY IS THIS SHOWING UP TWICE????
                                TextView nullText = getView().findViewById(R.id.fragment_null_data_text);
                                nullText.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Log.i(TAG,"Fetching Groups for the user returned null.");
                }
            }
        });*/
    }

    @Override
    public void onGroupClick(int position, Group_Model groupBase, View sharedView) {
        /*Fragment detailedGroup = GroupDetail_Fragment
                .newInstance(mUser, groupBase, sharedView.getTransitionName());

        //detailedGroup.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));
        detailedGroup.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedView,sharedView.getTransitionName())
                .replace(R.id.core_frame,detailedGroup, GroupDetail_Fragment.TAG)
                .addToBackStack(GroupDetail_Fragment.TAG)
                .commit();*/
    }

    @Override
    public void updateGroupRecycler(Group_Model groupBase) {
        myGroups.add(groupBase);
        mAdapter.addData(groupBase);
    }

    public void filterRecycler(String constraint){
        mAdapter.getFilter().filter(constraint);
    }

}