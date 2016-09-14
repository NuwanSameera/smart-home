package lk.smarthome.smarthomeagent.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import lk.smarthome.smarthomeagent.Constants;
import lk.smarthome.smarthomeagent.R;
import lk.smarthome.smarthomeagent.controller.DbHandler;
import lk.smarthome.smarthomeagent.model.SmartRegion;

public class DeviceListActivity extends AppCompatActivity {

    private static int regionIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        DbHandler dbHandler = DbHandler.getInstance(getApplicationContext());
        SmartRegion region = dbHandler.getRegion(intent.getIntExtra(Constants.ARG_REGION_INDEX, 0));

        if (region != null) {
            setTitle(region.getName() + " devices");
            regionIndex = region.getId();
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DeviceListActivity.this, AddDeviceActivity.class);
                    intent.putExtra(Constants.ARG_REGION_INDEX, regionIndex);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, ListViewFragment.newInstance(regionIndex))
                .commit();
    }

}
