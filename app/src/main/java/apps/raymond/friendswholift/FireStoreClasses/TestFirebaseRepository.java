package apps.raymond.friendswholift.FireStoreClasses;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import apps.raymond.friendswholift.Groups.GroupBase;

public class TestFirebaseRepository {

    private static final String TAG = "TestFirebaseRepository";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    /*
     * Method call to create a Document under the 'Users' Collection. This collection will contain
     * details about the user as Fields in their respective document. The Document name is currently
     * created under the user's email.
     */
    public Task<Void> createUserDoc(String name){
        Log.i(TAG,"Creating a user Document under id: TESTID");
        Map<String,String> testMap = new HashMap<>();
        testMap.put("hello","test"); // ToDo need to figure out how to set a null field when creating a Document.
        return userCollection.document(name).set(testMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "User document successfully created.");
                        } else {
                            Log.w(TAG, "Failed to create document.");
                        }
                    }
                });
    }

    /*
     * Creates a Document under the 'Groups' Collection. The fields of this document are saved as a
     * GroupBase POJO.
     */
    public Task<Void> createGroupDoc(String name, GroupBase groupBase){
        Log.i(TAG,"Creating Group Document: " + name);
        return groupCollection.document(name).set(groupBase)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "User document successfully created.");
                        } else {
                            Log.w(TAG, "Failed to create document.");
                        }
                    }
                });
    }

    /*
     * Collects the fields of a User document and returns them as a Set<String>.
     * The actual method call returns a Task to gather the fields of our Document. Once this task is
     * complete, we can utilise the data as needed (refer to the calling Context for
     * onCompleteListener).
     * Current version of the database only stores the User's attached groups in the form of a
     * <String> GroupName : <String> Authorization pair.
     */
    public Task<Set<String>> getGroupTags(){
        return userCollection.document(currentUser.getEmail()).get()
                .continueWith(new Continuation<DocumentSnapshot, Set<String>>() {
                    @Override
                    public Set<String> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Log.i(TAG,"Successfully retrieved the document.");
                            return document.getData().keySet();
                        } else {
                            Log.w(TAG, "Error when retrieving the Document.");
                            return null;
                        }
                    }
                });
    }

    /*
     * Query Firestore against a List of names. For each name in the list, retrieve the POJO of that
     * document and append it to a List that will be returned to the context Caller.
     */
    public List<Task<DocumentSnapshot>> getGroups(List<String> myGroupTags){
        // Consider moving the following List<String> manipulation to the Fragment or???

        List<DocumentReference> myDocs = new ArrayList<>();
        for(String name : myGroupTags){
            DocumentReference docRef = groupCollection.document(name);
            myDocs.add(docRef);
        }
        List<Task<DocumentSnapshot>> myTasks = new ArrayList<>();
        for(String name : myGroupTags){
            // We don't need OnCompleteListener because it will be implemented in the calling context.
            Task<DocumentSnapshot> snapshot = groupCollection.document(name).get();
            myTasks.add(snapshot);
        }

        return myTasks;
    }


}
