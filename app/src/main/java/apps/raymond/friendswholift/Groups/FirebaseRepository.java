/*
 * Creating a FireBase Repository allows for better separation of concerns as recommended in the
 * MVVM principles.
 *
 * We are not implementing a FireBase DAO, not sure if it is required.
 */

package apps.raymond.friendswholift.Groups;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Set;


public class FirebaseRepository {

    private static final String TAG = "FirebaseRepository";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private DocumentSnapshot groupSnapShot,userSnapShot;

    //@Override //Should we store groupBase.getName() into a variable? Is it quicker?
    // ToDo: Need to check if the GroupName is already used, if yes throw error.
    public Task<Void> createGroup(final GroupBase groupBase){
        Log.i(TAG,"Creating group " + groupBase.getName());
        return groupCollection.document(groupBase.getName()).set(groupBase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully added Document: " + groupBase.getName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Failed to create group: " + groupBase.getName());
                    }
                });
    }

    public Task<Void> addGroupToUser(final String groupName){
        Log.i(TAG,"Adding group " + groupName + " to user " + currentUser.getUid());
        return userCollection.document(currentUser.getUid()).set(groupName, SetOptions.merge());
    }

    public Task<Void> createUserDoc(){
        Log.i(TAG,"Creating a collection for user " + currentUser.getUid());
        return userCollection.document(currentUser.getUid()).set("test");
    }


    /*
     * This method will query our FireStore for a key set of the Groups current user is attached to.
     * It shall return a LiveData object of Set<String> to its caller.
     */
    public void getGroups(final TestGroupGetFragment.FirestoreCallBack firestoreCallBack){
        Log.i(TAG,"Attempting to retrieve a user's groups.");
        userCollection.document(currentUser.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Log.i(TAG,"Success inside the onComplete method of our document .get() and retrieved: "+ document.getData().keySet());
                            firestoreCallBack.onCallBack(document.getData().keySet());
                        } else {
                            Log.d(TAG,"The .get() failed for document: " + currentUser.getUid(), task.getException());
                        }
                    }
                });
        Log.i(TAG, "Added onCompleteListener to our document.");
    }



}
            /*
            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            List<String> list = new ArrayList<>();
                            Map<String, Object> map = document.getData();
                            if(map != null){
                                for (Map.Entry<String, Object> entry : map.entrySet()){
                                    list.add(entry.getKey());
                                    Log.d(TAG,entry.getKey());
                                }
                            }
                        }
                    }
                }
            });*/





        /*
        userCollection.document(currentUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.w(TAG,"Listen failed.", e);
                        }
                        if (snapshot != null && snapshot.exists() ){
                            Log.d(TAG,"Current data: " + snapshot.getData());

                        } else {
                            Log.d(TAG,"Current data: null");
                        }
                    }
                });*/