package net.somee.bullyme;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Questionnaire extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //TODO: Next Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        List<String> files = Arrays.asList(this.fileList());

        try {
            FileOutputStream outputStream = openFileOutput("answers", MODE_PRIVATE);
            outputStream.write("text".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] questions = getResources().getStringArray(R.array.Questions);

        for (String question: questions) {
            String[] questionData = question.split(":");
            String name = questionData[0];
            String type = questionData[1];
            String content = questionData[2];

            TextView questionText = findViewById(R.id.txtQuestion);
            questionText.setText(content);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            if (type.equals("text")) {
                EditText editText = new EditText(this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(editText);
            }
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
        } else if (id == R.id.nav_questionnaire) {
            Intent intent = new Intent(this, Questionnaire.class);
            startActivity(intent);
            this.finish();
        } else if (id == R.id.nav_schedule) {
            Intent intent = new Intent(this, Schedule.class);
            startActivity(intent);
            this.finish();
        } else if (id == R.id.nav_stop) {
            Intent intent = new Intent(this, Stop.class);
            startActivity(intent);
            this.finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
