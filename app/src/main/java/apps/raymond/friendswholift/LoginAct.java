package apps.raymond.friendswholift;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import apps.raymond.friendswholift.Login.LoginPagerAdapter;
import apps.raymond.friendswholift.R;

public class LoginAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceStance){
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.login_screen);

        //Adapter class that will generate the views to be displayed by the ViewPager.
        LoginPagerAdapter loginAdapter = new LoginPagerAdapter(getSupportFragmentManager());

        //The ViewPager that displays the views generated by loginAdapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.login_container);
        //We set the Adapter for this ViewPager (defined in login_screen.xml).
        mViewPager.setAdapter(loginAdapter);
    }
}
