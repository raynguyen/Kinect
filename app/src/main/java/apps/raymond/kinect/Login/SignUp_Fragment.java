/*
 * Consider using continueWithTask after finishing the signUp method to sign the user in!
 */

package apps.raymond.kinect.Login;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.VerticalTextView;

public class SignUp_Fragment extends Fragment{
    private static final String TAG = "SignUp_Fragment";

    private Login_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Login_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    private TextView txtUserID, txtPassword1, txtPassword2;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtUserID = view.findViewById(R.id.text_userid);
        txtPassword1 = view.findViewById(R.id.text_password1);
        txtPassword2 = view.findViewById(R.id.text_password2);

        Button btnSignUp = view.findViewById(R.id.button_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = txtUserID.getText().toString();
                String p1 = txtPassword1.getText().toString();
                String p2 = txtPassword2.getText().toString();
                if(validateInput(userID, p1, p2)){
                    mViewModel.registerWithEmail(userID,p1);
                    /*
                     * If registration is successful:
                     * 1. Create User_Model for the new user,
                     * 2. Save the User_Model as a new User document in the DB.
                     * 3. Log the user into the application
                     * ---A User_Model must be created and saved into the DB prior to logging the user into the application
                     * ---because the parent activity listens for changes in the user token; if it
                     * ---detects a change, it will try and fetch the User_Model document from DB and start
                     * ---the core activity.
                     */
                }
            }
        });

        VerticalTextView loginTxt = view.findViewById(R.id.vtext_login);
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: potentially create an interface that the parent activity implements and switches
                // the ViewPager accordingly as opposed to grabbing the view via below.
                ViewPager viewPager = requireActivity().findViewById(R.id.viewpager_login);
                viewPager.setCurrentItem(0);
            }
        });
    }

    /*
            User_Model userModel = new User_Model(username,"invisible");
            mLoginViewModel.createUserByEmail(userModel,password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(activity,"Successfully registered "+username,Toast.LENGTH_SHORT).show();
                                signInCallback.signIn();
                            } else {
                                Toast.makeText(activity,"Error registering user.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/

    private boolean validateInput(String userID, String p1, String p2){
        boolean b = true;
        if(userID.length()==0){
            txtUserID.setError("This must not be empty!");
            b=false;
        }
        if(p1.length()<6){
            txtPassword1.setError("Your password does not meet the required length!");
            b=false;
        }
        if(!p1.equals(p2)){
            txtPassword2.setError("Your password fields do not match!");
            b=false;
        }
        return b;
    }

}
