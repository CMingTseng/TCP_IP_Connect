package com.demo.client.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.demo.client.R;
import com.demo.client.fragment.ClientFragment;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setFragment(new ClientFragment(), false, false);
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


