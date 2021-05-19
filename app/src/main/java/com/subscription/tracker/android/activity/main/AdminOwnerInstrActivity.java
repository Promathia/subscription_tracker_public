package com.subscription.tracker.android.activity.main;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.activity.qrscanner.QRScannerActivity;

public class AdminOwnerInstrActivity extends ApplicationAbstractActivity {

    @Override
    public int getLayoutView() {
        return R.layout.activity_admin_owner_inst;
    }

    @Override
    public int getToolbar() {
        return R.id.admin_owner_inst_toolbar;
    }

    @Override
    public int getDrawer() {
        return R.id.admin_owner_inst_drawer_layout;
    }

    @Override
    public int getNavigationView() {
        return R.id.admin_owner_inst_nav_view;
    }

    @Override
    public AppBarConfiguration getAppBarConfiguration(DrawerLayout drawer) {
        return new AppBarConfiguration.Builder(
                R.id.admin_owner_inst_nav_item_home,
                R.id.visitor_nav_item,
                R.id.admin_timetable_nav_item,
                R.id.club_info_nav_item,
                R.id.nav_about_app_item)
                .setDrawerLayout(drawer)
                .build();
    }

    @Override
    public int getNavigationHostFragment() {
        return R.id.admin_owner_inst_nav_host_fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_right_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.launch_qr_scanner) {
            final Intent intent = new Intent(getBaseContext(), QRScannerActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
