package lk.smarthome.smarthomeagent.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import lk.smarthome.smarthomeagent.Constants;
import lk.smarthome.smarthomeagent.R;
import lk.smarthome.smarthomeagent.SmartHomeApplication;
import lk.smarthome.smarthomeagent.controller.DbHandler;
import lk.smarthome.smarthomeagent.controller.DeviceArrayAdaptor;
import lk.smarthome.smarthomeagent.controller.RegionArrayAdaptor;
import lk.smarthome.smarthomeagent.model.SmartDevice;
import lk.smarthome.smarthomeagent.model.SmartRegion;

public class ListViewFragment extends Fragment {

    private static final String ARG_REGION_INDEX = "region_index";
    private int regionIndex;

    private View rootView;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message != null){
                String[] params = message.split(":");
                if (params.length > 1){
                    ListView listView = (ListView) rootView.findViewById(R.id.listView);
                    List<View> views = getViewsByTag(listView, params[0]);
                    for (View v : views) {
                        if (v instanceof Switch) {
                            Switch deviceSwitch = (Switch) v;
                            deviceSwitch.setChecked("On".equals(params[1]));
                        }
                    }
                }
            }
        }
    };

    public ListViewFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section.
     */
    public static ListViewFragment newInstance(int section) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REGION_INDEX, section);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        populateList();
        return rootView;
    }

    private void populateList() {
        regionIndex = getArguments().getInt(ARG_REGION_INDEX);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        DbHandler dbHandler = DbHandler.getInstance(getActivity().getApplicationContext());

        switch (regionIndex) {
            case 0:
                final List<SmartRegion> regions = dbHandler.getRegions();
                RegionArrayAdaptor regionAdaptor = new RegionArrayAdaptor(this.getActivity().getBaseContext(),
                        dbHandler.getRegions());
                listView.setAdapter(regionAdaptor);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent = new Intent(getActivity(), DeviceListActivity.class);
                        intent.putExtra(Constants.ARG_REGION_INDEX, regions.get(position).getId());
                        startActivity(intent);
                    }
                });
                break;
            default:
                List<SmartDevice> devices = dbHandler.getDevices(regionIndex);
                DeviceArrayAdaptor deviceAdapter
                        = new DeviceArrayAdaptor(this.getActivity().getBaseContext(), devices);
                listView.setAdapter(deviceAdapter);
                SmartHomeApplication.publishMessage(devices, "status");
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (regionIndex != 0) {
            IntentFilter filter = new IntentFilter(Constants.MESSAGE_ARRIVED);
            LocalBroadcastManager.getInstance(this.getActivity().getBaseContext())
                    .registerReceiver(broadcastReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (regionIndex != 0) {
            LocalBroadcastManager.getInstance(this.getActivity().getBaseContext())
                    .unregisterReceiver(broadcastReceiver);
        }
    }

    private static List<View> getViewsByTag(ViewGroup root, String tag){
        List<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

}
