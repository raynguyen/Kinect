package apps.raymond.kinect.login;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import apps.raymond.kinect.FirebaseLayer;
import apps.raymond.kinect.UserProfile.UserModel;

class LoginViewModel extends ViewModel {

    private FirebaseLayer mRepository;

    LoginViewModel(){
        this.mRepository = new FirebaseLayer();
    }

    Task<Void> createUserByEmail(UserModel userModel, String password){
        return mRepository.createUserByEmail(userModel,password);
    }

    Task<AuthResult> signIn(String name, String password){
        return mRepository.signInWithEmail(name,password);
    }
}
