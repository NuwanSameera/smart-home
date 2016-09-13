package lk.smarthome.smarthomeagent.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import lk.smarthome.smarthomeagent.R;
import lk.smarthome.smarthomeagent.model.SmartRegion;

public class RegionArrayAdaptor extends ArrayAdapter<SmartRegion> {
    private final Context context;
    private final List<SmartRegion> values;

    public RegionArrayAdaptor(Context context, List<SmartRegion> values) {
        super(context, R.layout.row_region, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder {
        private TextView textViewName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_region,
                    parent, false);
            mViewHolder.textViewName = (TextView) convertView
                    .findViewById(R.id.textViewName);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.textViewName.setText(values.get(position).getName());
        return convertView;
    }
}