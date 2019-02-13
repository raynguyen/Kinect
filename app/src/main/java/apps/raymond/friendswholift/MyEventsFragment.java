package apps.raymond.friendswholift;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Activity_Main.EventsRecyclerAdapter;
import apps.raymond.friendswholift.Events.EventViewModel;
import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Interfaces.EventClickListener;

public class MyEventsFragment extends Fragment implements EventClickListener,View.OnClickListener {
    private static final String TAG = "EventsFragment";

    private EventViewModel viewModel;
    private List<GroupEvent> eventList;
    private ProgressBar progressBar;
    private EventsRecyclerAdapter mAdapter;

    public MyEventsFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this.getActivity()).get(EventViewModel.class); // Is there a better callback for this?
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.core_events_frag, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventList = new ArrayList<>();

        ImageButton addEventBtn = view.findViewById(R.id.create_event);
        addEventBtn.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);

        RecyclerView eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAdapter = new EventsRecyclerAdapter(eventList, this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            updateEvents();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
        }
    }

    @Override
    public void onEventClick(int position, GroupEvent groupEvent) {

    }

    /*
     * Repository calls should provide a clean API from the rest of the application from the underlying data.
     * Clearly, requiring an onComplete listener here voids the previous statement so we will have to
     * revisit this.
     */
    private void updateEvents(){
        viewModel.getUsersEvents().addOnCompleteListener(new OnCompleteListener<List<Task<DocumentSnapshot>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<DocumentSnapshot>>> task) {
                if(task.isSuccessful()){
                    Tasks.whenAllSuccess(task.getResult()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            for(Object object:objects){
                                Log.i(TAG,"Adding event " +((DocumentSnapshot)object).toObject(GroupEvent.class).getName() + " to RecyclerView.");
                                eventList.add(((DocumentSnapshot) object).toObject(GroupEvent.class)); //Do the toObject in the repo
                            }
                            progressBar.setVisibility(View.GONE);
                            if(eventList.size() == 0){
                                // If there are no upcoming events, we want to fill the fragment with a text that says No Upcoming Events.
                                ImageView nullImage = getView().findViewById(R.id.null_data_image);
                                TextView nullText = getView().findViewById(R.id.null_data_text);
                                nullImage.setVisibility(View.VISIBLE);
                                nullText.setVisibility(View.VISIBLE);
                            } else {
                                mAdapter.setData(eventList);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }




}
