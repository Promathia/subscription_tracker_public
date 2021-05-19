package com.subscription.tracker.android.activity.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.StartActivity;
import com.subscription.tracker.android.entity.response.Club;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.services.SharedPreferencesService;
import com.subscription.tracker.android.services.utils.ClubUtils;
import com.subscription.tracker.android.services.utils.ImageUtils;

public abstract class ApplicationAbstractActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    public abstract int getLayoutView();

    public abstract int getToolbar();

    public abstract int getDrawer();

    public abstract int getNavigationView();

    public abstract int getNavigationHostFragment();

    public abstract AppBarConfiguration getAppBarConfiguration(DrawerLayout drawer);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        Toolbar toolbar = findViewById(getToolbar());
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(getDrawer());
        NavigationView navigationView = findViewById(getNavigationView());
        updateNavigationDrawer(navigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = getAppBarConfiguration(drawer);
        NavController navController = Navigation.findNavController(this, getNavigationHostFragment());
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    protected void updateNavigationDrawer(final NavigationView navigationView) {
        final SingleUserData singleUserData = new SharedPreferencesService(this)
                .getObject(R.string.preference_user_data_key, SingleUserData.class);
        if (singleUserData != null) {
            final View headerLayout = navigationView.getHeaderView(0);
            final ImageView drawerImage = headerLayout.findViewById(R.id.drawer_image);
            final Club activeClub = ClubUtils.getActiveClub(singleUserData);
            final Drawable drawable = ImageUtils.resolveImageResource(
                    this,
                    activeClub.getImageName());
            drawerImage.setImageDrawable(drawable);
            final TextView userName = headerLayout.findViewById(R.id.drawer_user_name);
            userName.setText(String.format("%s %s", singleUserData.getFirstName(), singleUserData.getLastName()));
        }
    }

    public boolean doLogout(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_do_logout:
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, getNavigationHostFragment());
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
