package com.subscription.tracker.android.activity.main;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.subscription.tracker.android.R;

public class VisitorActivity extends ApplicationAbstractActivity {

    @Override
    public int getLayoutView() {
        return R.layout.activity_visitor;
    }

    @Override
    public int getToolbar() {
        return R.id.visitor_toolbar;
    }

    @Override
    public int getDrawer() {
        return R.id.visitor_drawer_layout;
    }

    @Override
    public int getNavigationView() {
        return R.id.visitor_nav_view;
    }

    @Override
    public AppBarConfiguration getAppBarConfiguration(DrawerLayout drawer) {
        return new AppBarConfiguration.Builder(
                R.id.nav_home_visitor,
                R.id.visitor_timetable_nav_item,
                R.id.club_info_nav_item,
                R.id.nav_about_app_item)
                .setDrawerLayout(drawer)
                .build();
    }

    @Override
    public int getNavigationHostFragment() {
        return R.id.visitor_nav_host_fragment;
    }

}
