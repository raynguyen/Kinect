package apps.raymond.kinect;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import apps.raymond.kinect.UserProfile.UserModel;

public class Connections_Fragment extends Fragment implements ProfileRecyclerAdapter.ProfileClickListener{
    private static final String TAG = "ConnectionsFragment";

    private List<UserModel> connectionsList;

    public interface LoadConnections{
        List<UserModel> loadConnections();
    }

    private LoadConnections connectionsInterface;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            connectionsInterface = (LoadConnections) context;
        } catch (ClassCastException e){
            Log.w(TAG,"The calling context does not implement the LoadConnections interface.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionsList = connectionsInterface.loadConnections();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_recycler_fragment,container,false);
    }

    RecyclerView connectionsRecycler;
    ProfileRecyclerAdapter adapter;
    TextView nullDataText;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.fragment_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.connections);
        toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getFragmentManager().popBackStack();
                } catch (NullPointerException npe){
                    Log.w(TAG,"Error on navigation icon click.",npe);
                    requireActivity().onBackPressed();
                }
            }
        });

        nullDataText = view.findViewById(R.id.fragment_null_data_text);
        if(connectionsList.size() == 0){
            nullDataText.setVisibility(View.VISIBLE);
        }

        connectionsRecycler = view.findViewById(R.id.fragment_recycler);
        connectionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProfileRecyclerAdapter(connectionsList,this);
        connectionsRecycler.setAdapter(adapter);
    }

    @Override
    public void onProfileClick(UserModel userModel) {
        Toast.makeText(getContext(),"Clicked on profile: "+userModel.getEmail(),Toast.LENGTH_LONG).show();
    }
}
