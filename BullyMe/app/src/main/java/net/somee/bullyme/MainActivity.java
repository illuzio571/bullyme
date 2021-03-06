package net.somee.bullyme;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NotificationReceiver notification = new NotificationReceiver();

        ArrayList<String> answers = new ArrayList<>();

        try {
            FileInputStream in = openFileInput("answers");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                answers.add(line);
            }

            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TypedValue outValue = new TypedValue();
            getResources().getValue(R.dimen.notification_intervalInHours, outValue, true);
            double hours = outValue.getFloat();

            notification.SetAlarm(this, hours);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(this.getPackageName(), 0);
            if (info != null) {
                long firstInstallTime = info.firstInstallTime;
                long currentTime = new Date().getTime();
                long difference = currentTime - firstInstallTime;
                int days = (int) (difference / (1000*60*60*24));

                TextView txtDays = findViewById(R.id.txtDays);
                txtDays.setText(days + " day(s)");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        List<String> files = Arrays.asList(this.fileList());

        if (!files.contains("answers")) {
            Intent intent = new Intent(this, Questionnaire.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        } /*else if (id == R.id.nav_questionnaire) {
            Intent intent = new Intent(this, Questionnaire.class);
            startActivity(intent);
            this.finish();
        } else if (id == R.id.nav_schedule) {
            Intent intent = new Intent(this, Schedule.class);
            startActivity(intent);
            this.finish();
        }*/ else if (id == R.id.nav_stop) {
            Intent intent = new Intent(this, Stop.class);
            startActivity(intent);
            this.finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
