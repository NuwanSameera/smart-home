package lk.smarthome.smarthomeagent.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

    private View rootView;

    public ListViewFragment(){
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
        int regionIndex = getArguments().getInt(ARG_REGION_INDEX);
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

}
