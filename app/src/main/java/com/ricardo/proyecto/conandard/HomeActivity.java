package com.ricardo.proyecto.conandard;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ricardo.proyecto.conandard.arduino.ArduinoManager;
import com.ricardo.proyecto.conandard.tabs.ConectFragment;
import com.ricardo.proyecto.conandard.tabs.LogFragment;
import com.ricardo.proyecto.conandard.tabs.OperateFragment;
import com.ricardo.proyecto.conandard.tabs.ViewPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onStart() {
        ArduinoManager.getInstance().setContext(this);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArduinoManager.getInstance().getArduino().unsetArduinoListener();
        ArduinoManager.getInstance().getArduino().close();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OperateFragment(), "ONE");
        adapter.addFragment(new ConectFragment(), "TWO");
        adapter.addFragment(new LogFragment(), "LOG");
        viewPager.setAdapter(adapter);
    }

}
