package com.example.faceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.faceapplication.Fragment.HomeFragment;
import com.example.faceapplication.Fragment.NotificationFragment;
import com.example.faceapplication.Fragment.ProfileFragment;
import com.example.faceapplication.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedfragment=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d(" msjcn"," Main activity");

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        Bundle intent=getIntent().getExtras();
        if(intent!=null)
        {
            String publisher=intent.getString("publisherid");

            SharedPreferences.Editor editor= getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch(menuItem.getItemId())
            {
                case R.id.nav_home: selectedfragment=new HomeFragment();
                    break;


                case R.id.nav_search:selectedfragment=new SearchFragment();
                    break;


                case R.id.nav_add:selectedfragment=null;
                    startActivity(new Intent(MainActivity.this,PostActivity.class));
                    break;


                case R.id.nav_heart:selectedfragment=new NotificationFragment();
                    break;


                case R.id.nav_profile:
                    SharedPreferences.Editor editor=getSharedPreferences("Prefs",MODE_PRIVATE).edit();
                    editor.apply(); // why??????
                    selectedfragment= new ProfileFragment();

                break;
            }
            if(selectedfragment!=null)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                ,selectedfragment).commit();

            return true;
        }
    };
}
