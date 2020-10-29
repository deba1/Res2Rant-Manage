package com.deba1.res2rant.manage;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.deba1.res2rant.manage.ui.chef.OrdersFragment;
import com.deba1.res2rant.manage.ui.manager.FoodFragment;
import com.deba1.res2rant.manage.ui.manager.ManagerDashboardFragment;
import com.deba1.res2rant.manage.ui.manager.UserListFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

public class ManagerActivity extends AppCompatActivity {
    private NavigationView navView;
    private DrawerLayout drawer;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        navView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.nav_drawer);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);

        // Nav Header
        TextView headerName = navView.getHeaderView(0).findViewById(R.id.nav_user_name);
        TextView headerEmail = navView.getHeaderView(0).findViewById(R.id.nav_user_sub);
        String userName = getIntent().getStringExtra("name");
        String userEmail = getIntent().getStringExtra("sub");
        headerName.setText(userName);
        headerEmail.setText(userEmail);

        // Action Bar
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

        // Nav Drawer
        navView.setNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            int itemId = item.getItemId();
            int container = R.id.fragment_container;
            Menu menu = navView.getMenu();
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setChecked(false);
            item.setChecked(true);
            if (itemId == R.id.nav_manager_dashboard) {
                transaction.replace(container, new ManagerDashboardFragment()).commit();
            }
            else if (itemId == R.id.nav_manager_foods) {
                transaction.replace(container, new FoodFragment()).commit();
            }
            else if (itemId == R.id.nav_manager_orders) {
                transaction.replace(container, new OrdersFragment()).commit();
            }
            else if (itemId == R.id.nav_manager_users) {
                transaction.replace(container, new UserListFragment()).commit();
            }
            else if (itemId == R.id.nav_manager_logout) {
                auth.signOut();
                finish();
            }
            drawer.closeDrawers();
            return false;
        });
        drawer.addDrawerListener(toggle);
    }
}
