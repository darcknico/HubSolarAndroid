package com.ricardo.proyecto.conandard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ricardo.proyecto.conandard.manager.ArduinoManager;
import com.ricardo.proyecto.conandard.tabs.ConectFragment;
import com.ricardo.proyecto.conandard.tabs.LogFragment;
import com.ricardo.proyecto.conandard.tabs.OperateFragment;
import com.ricardo.proyecto.conandard.tabs.ViewPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArduinoManager arduinoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_conandard);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        arduinoManager = ArduinoManager.getInstance();
    }

    @Override
    protected void onStart() {
        arduinoManager.setContext(getBaseContext());
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (arduinoManager.getUsbHelper()!=null) {
            if(arduinoManager.getUsbHelper().isOpened()) {
                arduinoManager.getUsbHelper().unsetArduinoListener();
                arduinoManager.getUsbHelper().close();
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_home, menu);
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.toogle:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor prefsEditor = sp.edit();
                toggleTheme = !toggleTheme;
                prefsEditor.putBoolean("theme",toggleTheme);
                prefsEditor.apply();
                setTheme(toggleTheme?R.style.AppTheme_Blue:R.style.AppTheme_Red);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
    */

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OperateFragment(), "INICIO");
        adapter.addFragment(new ConectFragment(), "CONEXION");
        adapter.addFragment(new LogFragment(), "LOG");
        viewPager.setAdapter(adapter);
    }

}
