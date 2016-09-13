package lk.smarthome.smarthomeagent.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import lk.smarthome.smarthomeagent.R;
import lk.smarthome.smarthomeagent.controller.DbHandler;
import lk.smarthome.smarthomeagent.controller.DeviceArrayAdaptor;
import lk.smarthome.smarthomeagent.controller.RegionArrayAdaptor;

public class ListViewFragment extends Fragment {

    private static final String ARG_REGION_INDEX = "list_number";

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
        updateDocumentsList();
        return rootView;
    }

    private void updateDocumentsList() {
        int regionIndex = getArguments().getInt(ARG_REGION_INDEX);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        DbHandler dbHandler = DbHandler.getInstance(getActivity().getApplicationContext());

        switch (regionIndex) {
            case 0:
                RegionArrayAdaptor regionAdaptor = new RegionArrayAdaptor(this
                        .getActivity().getBaseContext(), dbHandler.getRegions());
                listView.setAdapter(regionAdaptor);
                break;
            default:
                DeviceArrayAdaptor adapter = new DeviceArrayAdaptor(this
                        .getActivity().getBaseContext(), dbHandler.getDevices(regionIndex));
                listView.setAdapter(adapter);
                break;
        }

    }

}
