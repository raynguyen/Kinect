package apps.raymond.kinect;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class Core_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepository;
    private MutableLiveData<List<Event_Model>> mEventInvitations = new MutableLiveData<>();
    private MutableLiveData<List<Group_Model>> mGroupInvitations = new MutableLiveData<>();
    private MutableLiveData<List<Message_Model>> mEventMessages = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mAcceptedEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> publicEvents = new MutableLiveData<>();
    private MutableLiveData<List<Group_Model>> mUsersGroups = new MutableLiveData<>();

    public Core_ViewModel(){
        Log.w("ViewModel","Is a new instance of the viewmodel created?");
        this.mRepository = new Core_FireBaseRepo();

        /*mRepository.getUsersGroups().addOnCompleteListener(new OnCompleteListener<List<Task<Group_Model>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<Group_Model>>> task) {
                if(task.isSuccessful()&& task.getResult()!=null){
                    //Have to change the repository method to return a List<Group_Model>;
                }
            }
        });*/


        //We don't have to call this until we are in the CoreActivity and even then we might want to wait.
        //loadPublicEvents();
        //listenForInvitations();

    }

    //*-------------------------------------------USER-------------------------------------------*//
    public Task<Void> signOut(Context context){
        return mRepository.signOut(context);
    }

    public Task<List<User_Model>> fetchUsers(String userID){
        return mRepository.fetchUsers(userID);
    }

    public Task<List<User_Model>> getConnections(String userID){
        return mRepository.getConnections(userID);
    }
    public Task<Void> addUserConnection(String userID,User_Model user){
        return mRepository.addConnection(userID,user);
    }

    //Fetch event invitations (implement observer once I can figure out how to) and set as LiveData object here.
    private void listenForInvitations(){
        /*
        mRepository.getEventInvitations()
                .addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Event_Model>> task) {
                        if(task.isSuccessful() && task.getResult()!=null){
                            mEventInvitations.setValue(task.getResult());
                        }
                    }
                });
        mRepository.getGroupInvitations()
                .addOnCompleteListener(new OnCompleteListener<List<Group_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Group_Model>> task) {
                        if(task.isSuccessful()){
                            mGroupInvitations.setValue(task.getResult());
                        }
                    }
                });
               */
    }

    public MutableLiveData<List<Event_Model>> getEventInvitations(){
        return mEventInvitations;
    }

    public MutableLiveData<List<Group_Model>> getGroupInvitations(){
        return mGroupInvitations;
    }
    //*------------------------------------------EVENTS------------------------------------------*//
    public void loadAcceptedEvents(String userID){
        mRepository.getAcceptedEvents(userID).addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Event_Model>> task) {
                if(task.isSuccessful()&& task.getResult()!=null){
                    mAcceptedEvents.setValue(task.getResult());
                }
            }
        });
    }

    public MutableLiveData<List<Event_Model>> getAcceptedEvents(){
        return mAcceptedEvents;
    }

    public MutableLiveData<List<Message_Model>> getMessages(Event_Model event){
        mRepository.getMessages(event).addOnCompleteListener(new OnCompleteListener<List<Message_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Message_Model>> task) {
                if(task.isSuccessful() && task.getResult()!=null){
                    mEventMessages.setValue(task.getResult());
                }
            }
        });
        return mEventMessages;
    }

    public Task<Void> createEvent(Event_Model event){
        return mRepository.createEvent(event);
    }

    public Task<Void> addEventToUser(String userID, Event_Model event){
        return mRepository.addEventToUser(userID, event);
    }

    public Task<Void> addUserToEvent(String userID,User_Model user, String eventName){
        return mRepository.addUserToEvent(userID,user,eventName);
    }

    public void sendEventInvites(Event_Model event, List<User_Model> inviteList){
        mRepository.sendEventInvites(event,inviteList);
    }

    public Task<Void> incrementEventAttending(String eventName){
        return mRepository.incrementEventAttending(eventName);
    }

    public Task<Boolean> checkForEventInvitation(String userID, String eventName){
        return mRepository.checkForEventInvitation(userID,eventName);
    }

    public void removeEventInvitation(String userID, String eventName){
        mRepository.removeEventInvitation(userID, eventName);
    }

    public Task<Void> postNewMessage(Event_Model event, Message_Model message){
        return mRepository.postMessage(event,message);
    }
    private void loadPublicEvents(){
        mRepository.getPublicEvents().addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Event_Model>> task) {
                Log.w("RepositoryMethods","Task to fetch public events from store completed.");
                if(task.isSuccessful()){
                    Log.w("RepositoryModel","Getting public events complete with list size = " +task.getResult().size());
                    publicEvents.setValue(task.getResult());
                }
            }
        });
    }
    public MutableLiveData<List<Event_Model>> getPublicEvents(){
        return publicEvents;
    }

    public Task<List<User_Model>> getEventResponses(Event_Model event, String status){
        return mRepository.getEventResponses(event, status);
    }
    public Task<List<User_Model>> getEventInvitees(Event_Model event){
        return mRepository.getEventInvitees(event);
    }
    //*------------------------------------------GROUPS------------------------------------------*//


    public void loadAcceptedGroups(String userID){
        mRepository.getUsersGroups(userID);
    }
    public MutableLiveData<List<Group_Model>> getUsersGroups(){
        return mUsersGroups;
    }

    public void sendGroupInvites(Group_Model groupBase, List<User_Model> inviteList){
        mRepository.sendGroupInvites(groupBase,inviteList);
    }
    public Task<List<Group_Model>> fetchGroupInvites(String userID){
        return mRepository.getGroupInvitations(userID);
    }
    public Task<Void> createGroup(String userID, User_Model user, Group_Model groupBase, List<User_Model> inviteList){
        return mRepository.createGroup(userID,user, groupBase, inviteList);
    }
    public Task<Group_Model> getGroup(){
        return mRepository.getGroup();
    }
    public Task<List<Group_Model>> getUsersGroups(String userID){
        return mRepository.getUsersGroups(userID);
    }

    public Task<Void> addUserToGroup(String userID,User_Model userModel, Group_Model group){
        return mRepository.addUserToGroup(userID,userModel,group);
    }
    public Task<List<User_Model>> fetchGroupMembers(Group_Model group){
        return mRepository.fetchGroupMembers(group);
    }
    public void updateGroup(Group_Model groupBase){
        mRepository.editGroup(groupBase);
    }
    public Task<Void> declineGroupInvite(String userID, User_Model userModel, String groupName){
        return mRepository.declineGroupInvite(userID,userModel,groupName);
    }
    //*-------------------------------------------ETC--------------------------------------------*//
    public Task<Void> addLocation(String userID,Address address,String addressName){
        return mRepository.addLocation(userID,address,addressName);
    }
    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }
    public Task<Uri> uploadImage(Uri uri, String name){
        return mRepository.uploadImage(uri, name);
    }


    public Task<Void> testMethod(){
        return mRepository.testMethod();
    }
}
