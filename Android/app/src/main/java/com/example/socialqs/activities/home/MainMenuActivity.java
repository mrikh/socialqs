package com.example.socialqs.activities.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.activities.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * Main App Navigation Menu Controller
 */
public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_following, R.id.navigation_create, R.id.navigation_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                NavController navController = Navigation.findNavController(MainMenuActivity.this, R.id.nav_host_fragment);
                Intent myIntent;
                switch (item.getItemId()){
                    case R.id.navigation_create:
                        myIntent = new Intent(MainMenuActivity.this, CreateActivity.class);
                        startActivity(myIntent);
                        return false;
                    case R.id.navigation_profile:
                        myIntent = new Intent(MainMenuActivity.this, ProfileActivity.class);
                        startActivity(myIntent);
                        return false;
                    default:
                        NavigationUI.onNavDestinationSelected(item, navController);
                        break;
                }
                
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() > 0 ) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title, String color, int titleColorId) {

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        if (title == null) {
            return;
        }

        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(getResources().getColor(titleColorId)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
    }

    public void updateActionBarBack(boolean show){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(show);
        }
    }
}