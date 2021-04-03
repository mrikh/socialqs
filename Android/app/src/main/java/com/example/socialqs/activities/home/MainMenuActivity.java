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
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

/**
 * Main App Navigation Menu Controller
 *
 * Landing Activity once logged in or exploring app
 */
public class MainMenuActivity extends AppCompatActivity {

    public ArrayList<CategoryModel> categoryList;
    public String searchString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_create, R.id.navigation_profile)
                    .build();

        //No profile view if not logged in
        if (UserModel.current == null){
            navView.getMenu().findItem(R.id.navigation_login).setVisible(true);
            navView.getMenu().findItem(R.id.navigation_profile).setVisible(false);
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.navigation_create:

                    if (UserModel.current == null){
                        //just as a safety
                        Utilities.getInstance().createSingleActionAlert("You need to login before using this feature.", "Okay", MainMenuActivity.this, null).show();
                        return false;
                    }

                    if (categoryList == null){
                        Utilities.getInstance().createSingleActionAlert("Please wait while we fetch all the categories.", "Okay", MainMenuActivity.this, null).show();
                        return false;
                    }

                    Intent myIntent = new Intent(MainMenuActivity.this, CreateActivity.class);
                    Bundle arguments = new Bundle();
                    arguments.putParcelableArrayList("categories", categoryList);
                    myIntent.putExtras(arguments);
                    startActivity(myIntent);
                    return false;
                default:
                    NavigationUI.onNavDestinationSelected(item, navController);
                    break;

                case R.id.navigation_login:
                    Intent intent = new Intent(MainMenuActivity.this, PreLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
            }


            return true;
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