package lk.smarthome.smarthomeagent.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import lk.smarthome.smarthomeagent.model.SmartDevice;
import lk.smarthome.smarthomeagent.model.SmartRegion;

public class AddDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        final DbHandler dbHandler = DbHandler.getInstance(getApplicationContext());
        Intent intent = getIntent();
        final SmartRegion region = dbHandler.getRegion(intent.getIntExtra(Constants.ARG_REGION_INDEX, 0));

        if (region != null) {
            setTitle(region.getName() + " devices");

            Button btnAdd = (Button) findViewById(R.id.buttonAdd);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SmartDevice device = new SmartDevice();
                    device.setRegionId(region.getId());
                    EditText txtName = (EditText) findViewById(R.id.editTextName);
                    device.setName(txtName.getText().toString());
                    EditText txtDeviceId = (EditText) findViewById(R.id.editTextDeviceId);
                    device.setId(Integer.parseInt(txtDeviceId.getText().toString()));
                    dbHandler.addDevice(device);
                    Toast.makeText(getApplicationContext(), "New device added", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Invalid region", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
