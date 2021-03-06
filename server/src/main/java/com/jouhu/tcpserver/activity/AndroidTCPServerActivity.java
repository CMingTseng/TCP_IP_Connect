package com.jouhu.tcpserver.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jouhu.tcpserver.R;
import com.jouhu.tcpserver.fragment.AndroidTCPServerFragment;

public class AndroidTCPServerActivity extends AppCompatActivity {
    private static final String TAG = "TCPServerActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setFragment(new AndroidTCPServerFragment(), false, false);
    }

    public void setFragment(Fragment fragment, boolean stack, boolean anim) {
        try {
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (anim) {
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            }
            transaction.replace(R.id.main_content, fragment);
            if (stack) {
                Log.d(TAG, "setFragment() addToBackStack : " + fragment.getClass().getSimpleName());
                transaction.addToBackStack(fragment.getClass().getSimpleName());
            }
            transaction.commit();
        } catch (IllegalStateException ex) {
            Log.d(TAG, "setFragment(): illegal state: " + ex.getMessage());
        }
    }
}
