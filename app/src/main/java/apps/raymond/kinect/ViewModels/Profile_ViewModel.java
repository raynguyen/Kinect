package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

public class Profile_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo;
    private MutableLiveData<List<User_Model>> mConnectionsList = new MutableLiveData<>();

    public Profile_ViewModel(){
        mRepo = new Core_FireBaseRepo();
    }

    public void loadConnectionsList(String userID){
        mRepo.getUserConnections(userID)
                .addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<User_Model>> task) {
                        if(task.isSuccessful()){
                            mConnectionsList.setValue(task.getResult());
                        }
                    }
                });
    }

    public MutableLiveData<List<User_Model>> getUserConnections(){
        return mConnectionsList;
    }

    public Task<Void> createUserConnection(String userID, User_Model profileModel){
        return mRepo.addConnection(userID,profileModel);
    }

    public Task<Void> deleteUserConnection(String userID, String connectionID){
        return mRepo.deleteUserConnection(userID, connectionID);
    }

    public Task<Boolean> checkForConnection(String userID, String checkID){
        return mRepo.checkForConnection(userID,checkID);
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
    }


}