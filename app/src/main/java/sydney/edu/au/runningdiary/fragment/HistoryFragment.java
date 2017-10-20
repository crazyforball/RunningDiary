package sydney.edu.au.runningdiary.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.activity.LoginActivity;
import sydney.edu.au.runningdiary.activity.MainActivity;
import sydney.edu.au.runningdiary.adapter.RecordAdapter;
import sydney.edu.au.runningdiary.model.Record;
import sydney.edu.au.runningdiary.model.TrackPoint;
import sydney.edu.au.runningdiary.service.RecordManager;
import sydney.edu.au.runningdiary.service.TrackPointManager;
import sydney.edu.au.runningdiary.utils.FileUtil;

/**
 * Created by yang on 10/14/17.
 */

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private SwipeRefreshLayout refreshLayout;
    private ListView lv_history;
    private TextView tv_monthly_distance;
    private TextView tv_monthly_duration;
    private TextView tv_monthly_pace;

    private ArrayList<Record> records;
    private  RecordAdapter recordAdapter;

    private static final long MON_IN_MILLISEC = 259200000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        lv_history = (ListView) view.findViewById(R.id.lv_history);
        tv_monthly_distance = (TextView) view.findViewById(R.id.tv_month_km);
        tv_monthly_duration = (TextView) view.findViewById(R.id.tv_month_hms);
        tv_monthly_pace = (TextView) view.findViewById(R.id.tv_month_km_h);

        lv_history.setOnItemLongClickListener(this);
        lv_history.setOnItemClickListener(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(true);
        onRefresh();

        onActionResult();
        return view;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_msg)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Record recordToDelete = records.get(position);

                        List<TrackPoint> trackPointsToDelete = TrackPointManager.getTrackPoints(recordToDelete.getId());

                        //delete from local DB
                        RecordManager.deleteRecord(recordToDelete);

                        if (trackPointsToDelete != null) {
                            for (Iterator<TrackPoint> iterator = trackPointsToDelete.iterator(); iterator.hasNext();) {
                                TrackPoint trackPointToDelete = iterator.next();
                                TrackPointManager.deleteTrackPoint(trackPointToDelete);
                            }
                        }

                        update();
                        MapsFragment.resetMap();
                        MapsFragment.setupMap();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Record recordToShow = records.get(position);
        List<TrackPoint> trackPointsToShow = TrackPointManager.getTrackPoints(recordToShow.getId());
        if (trackPointsToShow != null) {
            List<LatLng> track = new ArrayList<LatLng>();
            for (Iterator<TrackPoint> iterator = trackPointsToShow.iterator(); iterator.hasNext();) {
                TrackPoint trackPoint = iterator.next();
                LatLng location_point = new LatLng(trackPoint.getLatitude(), trackPoint.getLongitude());
                track.add(location_point);
            }
            MainActivity.trackPoints = track;
        }
        MainActivity.viewPager.setCurrentItem(1);
        MapsFragment.resetMap();
        MapsFragment.updateMap();
    }

    @Override
    public void onRefresh() {
        update();
        refreshLayout.setRefreshing(false);
    }

    private void update() {
        records = new ArrayList<Record>();
        Date queryDate = new Date(new Date().getTime() - MON_IN_MILLISEC);
        records.addAll(RecordManager.getRecords(LoginActivity.user.getUsername(), queryDate));
        if (recordAdapter != null) {
            recordAdapter.notifyDataSetChanged();
        }
        onActionResult();
    }

    private void onActionResult() {
        if (records != null) {
            if (!records.isEmpty()) {
                double monthly_distance = 0.00;
                int monthly_duration = 0;
                double monthly_pace = 0.00;

                for (Iterator<Record> iterator = records.iterator(); iterator.hasNext(); ) {
                    Record record = iterator.next();
                    monthly_distance += record.getDistance();
                    monthly_duration += record.getDuration();
                }

                recordAdapter = new RecordAdapter(getActivity(), R.layout.item_record, records);
                lv_history.setAdapter(recordAdapter);

                tv_monthly_distance.setText(Math.round((monthly_distance * 100)) / 100.0 + " km");
                tv_monthly_duration.setText(FileUtil.toHMS(monthly_duration));

                monthly_pace = (monthly_distance * 1000 / (monthly_duration * 1.0)) * 3.6;
                tv_monthly_pace.setText(Math.round((monthly_pace * 100)) / 100.0 + " km/h");
            }
        }
    }


}
