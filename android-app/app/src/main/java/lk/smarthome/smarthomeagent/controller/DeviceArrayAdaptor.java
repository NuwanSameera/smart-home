package lk.smarthome.smarthomeagent.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lk.smarthome.smarthomeagent.R;
import lk.smarthome.smarthomeagent.SmartHomeApplication;
import lk.smarthome.smarthomeagent.model.SmartDevice;

public class DeviceArrayAdaptor extends ArrayAdapter<SmartDevice> {

    private final Context context;
    private final List<SmartDevice> values;

    public DeviceArrayAdaptor(Context context, List<SmartDevice> values) {
        super(context, R.layout.row_device, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder {
        private TextView textViewName;
        private Switch deviceSwitch;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_device,
                    parent, false);
            mViewHolder.textViewName = (TextView) convertView
                    .findViewById(R.id.textViewName);
            mViewHolder.deviceSwitch = (Switch) convertView
                    .findViewById(R.id.deviceSwitch);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.textViewName.setText(values.get(position).getName());
        mViewHolder.deviceSwitch.setTag(values.get(position).getId());
        mViewHolder.deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                List<SmartDevice> devices = new ArrayList<>();
                SmartDevice device = new SmartDevice();
                device.setId(Integer.parseInt(compoundButton.getTag().toString()));
                devices.add(device);
                SmartHomeApplication.publishMessage(devices, b ? "On" : "Off");
            }
        });
        return convertView;
    }
}