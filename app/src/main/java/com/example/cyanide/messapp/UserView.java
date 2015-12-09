package com.example.cyanide.messapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.os.Handler;
import android.widget.Toast;

import com.example.cyanide.messpp.R;
import com.firebase.client.Firebase;

import java.util.Map;

public class UserView extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private class Sign_out_async extends AsyncTask<Void, Void, Void> {

        //An async class to deal with signing user out
        private ProgressDialog pd;
        private Context context;
        private boolean net_connected;

        public Sign_out_async(Context context){
            this.context = context;
            pd = new ProgressDialog(context);
            net_connected = StaticUserMap.getInstance().getConnectedStatus();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Signing you out");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Display progressDialog for at least 3 seconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();

                    if(!net_connected)
                        Toast.makeText(context, "Internet Disconnected. Try Again", Toast.LENGTH_SHORT).show();

                    else {
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                        finish();
                    }
                }
            }, 500);  // 500 milliseconds
        }

        @Override
        protected Void doInBackground(Void... params) {

            Map< String, Object> userArgs = StaticUserMap.getInstance().getUserViewExtras();
            Map< String, Object> userLogOut = StaticUserMap.getInstance().getUserMap();

            Firebase update_ref = new Firebase(userArgs.get("EXTRA_FireBase_Node_Ref").toString());
            String session_val = userArgs.get("EXTRA_Node_Session_Field").toString();

            userLogOut.put(session_val, "1");
            update_ref.updateChildren(userLogOut);
            System.out.println("Signed out");
            StaticUserMap.getInstance().setUserMap(userLogOut);

            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        Intent intent = getIntent();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.title_section1);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {

            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new home())
                        .commit();
                mTitle = getString(R.string.title_section1);

                break;

            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new DietTracker())
                        .commit();
                mTitle = getString(R.string.title_section2);
                break;

            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new MessOff())
                        .commit();
                mTitle = getString(R.string.title_section3);
                break;

            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new UserProfile());
                mTitle = getString(R.string.title_section4);

                break;

            case 4:

                fragmentManager.beginTransaction()
                        .replace(R.id.container,new ChangePassword())
                        .commit();
                mTitle = getString(R.string.title_section5);
                break;

            case 5:
                Sign_out_async user_sign_out = new Sign_out_async(this);
                user_sign_out.execute();
                mTitle = getString(R.string.title_section6);
                break;
        }

    }


    @Override
    public void onBackPressed() {
        //Overridden to prevent user from exiting without sign out
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.openOptionsMenu();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.user_view, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
