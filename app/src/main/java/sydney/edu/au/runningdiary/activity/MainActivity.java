package sydney.edu.au.runningdiary.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.adapter.ViewPagerAdapter;
import sydney.edu.au.runningdiary.fragment.HistoryFragment;
import sydney.edu.au.runningdiary.fragment.MapsFragment;
import sydney.edu.au.runningdiary.fragment.RunFragment;
import sydney.edu.au.runningdiary.utils.BottomNavigationViewHelper;

/**
 * Created by yang on 9/25/17.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPagerAdapter adapter;
    public static ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;

    public static List<LatLng> trackPoints;
    public static LatLng current_position;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
//        bottomNavigationView.setVisibility(View.GONE);
        viewPager.addOnPageChangeListener(this);

        setupViewPager(viewPager);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.nav_weather:
                viewPager.setCurrentItem(0);
                break;*/
            case R.id.nav_run:
                viewPager.setCurrentItem(0);
                break;
            case R.id.nav_map:
                viewPager.setCurrentItem(1);
                break;
            case R.id.nav_history:
                viewPager.setCurrentItem(2);
                break;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (menuItem != null) {
            menuItem.setChecked(false);
        } else {
            bottomNavigationView.getMenu().getItem(0).setChecked(false);
        }
        menuItem = bottomNavigationView.getMenu().getItem(position);
        menuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new WeatherFragment(), getString(R.string.nav_weather));
        adapter.addFragment(new RunFragment(), getString(R.string.nav_run));
        adapter.addFragment(new MapsFragment(), getString(R.string.nav_map));
        adapter.addFragment(new HistoryFragment(), getString(R.string.nav_history));
        viewPager.setAdapter(adapter);
    }

}
