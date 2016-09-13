package lk.smarthome.smarthomeagent.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.UUID;

import lk.smarthome.smarthomeagent.Constants;
import lk.smarthome.smarthomeagent.R;
import lk.smarthome.smarthomeagent.SmartHomeApplication;
import lk.smarthome.smarthomeagent.controller.DbHandler;
import lk.smarthome.smarthomeagent.model.SmartRegion;

public class AddRegionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_region);

        Button btnAdd = (Button) findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHandler dbHandler = DbHandler.getInstance(getApplicationContext());
                SmartRegion region = new SmartRegion();
                EditText txtName = (EditText) findViewById(R.id.editTextName);
                region.setName(txtName.getText().toString());
                EditText txtMajor = (EditText) findViewById(R.id.editTextMajor);
                region.setMajor(Integer.parseInt(txtMajor.getText().toString()));
                EditText txtMinor = (EditText) findViewById(R.id.editTextMinor);
                region.setMinor(Integer.parseInt(txtMinor.getText().toString()));
                dbHandler.addRegion(region);
                Toast.makeText(getApplicationContext(), "New region added", Toast.LENGTH_LONG).show();
                BeaconManager beaconManager = SmartHomeApplication.getBeaconManager();
                beaconManager.startMonitoring(new Region(region.getName(),
                        UUID.fromString(Constants.UUID), region.getMajor(), region.getMinor()));
                finish();
            }
        });
    }
}
