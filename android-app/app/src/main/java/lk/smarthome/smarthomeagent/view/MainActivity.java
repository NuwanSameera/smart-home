package lk.smarthome.smarthomeagent.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import lk.smarthome.smarthomeagent.Constants;
import lk.smarthome.smarthomeagent.R;
import lk.smarthome.smarthomeagent.SmartHomeApplication;
import lk.smarthome.smarthomeagent.controller.DbHandler;
import lk.smarthome.smarthomeagent.model.SmartRegion;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static int regionIndex = 0;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regionIndex == 0) {
                    Intent intent = new Intent(MainActivity.this, AddRegionActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, AddDeviceActivity.class);
                    intent.putExtra(Constants.ARG_REGION_INDEX, regionIndex);
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (SmartHomeApplication.getCurrentRegion() != null) {
            regionIndex = -1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        if (regionIndex == -1) {
            navigationView.setCheckedItem(R.id.nav_home);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        } else {
            navigationView.setCheckedItem(R.id.nav_areas);
            onNavigationItemSelected(navigationView.getMenu().getItem(1));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            Region currentRegion = SmartHomeApplication.getCurrentRegion();
            if (currentRegion != null) {
                setTitle(currentRegion.getIdentifier());
                SmartRegion smartRegion = DbHandler.getInstance(getApplicationContext())
                        .getRegion(currentRegion.getMajor(), currentRegion.getMinor());
                regionIndex = smartRegion.getId();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, ListViewFragment.newInstance(regionIndex))
                        .commit();
            } else {
                regionIndex = -1;
                navigationView.setCheckedItem(R.id.nav_areas);
                onNavigationItemSelected(navigationView.getMenu().getItem(1));
            }
        } else if (id == R.id.nav_areas) {
            setTitle(item.getTitle());
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, ListViewFragment.newInstance(0))
                    .commit();
            regionIndex = 0;
        } else if (id == R.id.nav_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Smart Home Agent");
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=lk.smarthome.smarthomeagent \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception ignored) {
            }
            return true;
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
