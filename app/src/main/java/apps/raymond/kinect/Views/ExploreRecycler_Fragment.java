package apps.raymond.kinect.Views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.Interfaces.ExploreEventsInterface;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.Core_ViewModel;

public class ExploreRecycler_Fragment extends Fragment implements
        ExploreRecycler_Adapter.ExploreAdapterInterface {
    private static final String TAG = "ExploreRecycler";
    private Core_ViewModel mViewModel;
    private ExploreEventsInterface mExploreInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        try{
            mExploreInterface = (ExploreEventsInterface) getParentFragment();
        } catch (ClassCastException e){
            //Do something??
        }
    }

    private RecyclerView mRecycler;
    private ExploreRecycler_Adapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_recycler, container, false);
        mRecycler = view.findViewById(R.id.recycler_explore_events);
        mRecycler.addItemDecoration(new Margin_Decoration_RecyclerView(requireActivity()));
        mAdapter = new ExploreRecycler_Adapter(this);
        mRecycler.setAdapter(mAdapter);

        setExitTransitionAnimation();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.text_explore_count);
        mViewModel.getSuggestedEvents().observe(requireActivity(), (List<Event_Model> events)->{
            if(events != null){
                mAdapter.setData(events);
                textView.setText(String.valueOf(events.size()));
            }
        });
    }

    @Override
    public void onItemViewClick(Event_Model event, int position, View transitionView) {
        mExploreInterface.setItemPosition(position);
        mExploreInterface.animateMapToLocation();
        Log.w(TAG,"Hello, we should switch fragments to from the recycler to the viewpager.");
        /*
        When an event card is clicked in the recycler fragment, we want to transition from the
        recycler view of public events to a view pager of public events.
         */
        if(getFragmentManager() != null){
            getFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .addSharedElement(transitionView, transitionView.getTransitionName())
                    .replace(R.id.container_explore_fragments, new ExplorePager_Fragment(),
                            ExplorePager_Fragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Prepares the exit and the reenter transition animation for the this fragment (i.e. we set
     * the animations for exiting this fragment when a recycler view holder item is clicked).
     *
     */
    private void setExitTransitionAnimation(){
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.recycler_to_pager));

        setExitSharedElementCallback(new SharedElementCallback() {

            /**
             * Method called when an exit shared transition is required?
             *
             * @param names list containing the transition names of each view added to the map.
             * @param sharedElements mapping of views that we want to animate during the transition
             */
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                //Grab a handle on the view that we want to animate when transitioning to the pager.
                RecyclerView.ViewHolder selectedViewHolder = mRecycler
                        .findViewHolderForAdapterPosition(mExploreInterface.getItemPosition());

                if(selectedViewHolder == null){
                    return;
                }

                sharedElements.put(names.get(0),
                        selectedViewHolder.itemView.findViewById(R.id.text_event_name));
            }
        });
    }
}