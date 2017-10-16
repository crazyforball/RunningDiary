package sydney.edu.au.runningdiary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.model.Record;
import sydney.edu.au.runningdiary.utils.FileUtil;


/**
 * Created by yang on 8/19/17.
 */

public class RecordAdapter extends ArrayAdapter<Record> {

    private static class ViewHolder {
        TextView tv_date;
        TextView tv_distance;
        TextView tv_duration;
        TextView tv_pace;
    }


    public RecordAdapter(Context context, int layout, ArrayList<Record> records) {
        super(context, layout, records);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record record = getItem(position);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_record, parent, false);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_createDate);
            viewHolder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_pace = (TextView) convertView.findViewById(R.id.tv_pace);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_date.setText(record.getCreateDate());
        viewHolder.tv_distance.setText(record.getDistance() + " km");
        viewHolder.tv_duration.setText(FileUtil.toHMS(record.getDuration()));
        viewHolder.tv_pace.setText(record.getPace() + " km/h");
        // Return the completed view to render on screen
        return convertView;
    }
}
