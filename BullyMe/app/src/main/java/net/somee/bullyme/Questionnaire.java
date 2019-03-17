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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Questionnaire extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int currentQuestion = 0;

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

        FloatingActionButton fab = findViewById(R.id.fab);

        final String[] questions = getResources().getStringArray(R.array.Questions);
        final LinearLayout linearLayout = findViewById(R.id.linearLayout);

        String[] questionData = questions[0].split(";");
        String name = questionData[0];
        String type = questionData[1];
        String content = questionData[2];

        TextView questionText = findViewById(R.id.txtQuestion);
        questionText.setText(content);

        if (type.equals("text")) {
            EditText editText = new EditText(this);
            editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(editText);
        } else if (type.equals("bool")) {
            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            RadioButton btnYes = new RadioButton(this);
            btnYes.setText("Yes");
            btnYes.setTextSize(24);
            btnYes.setPadding(10, 20, 10, 20);
            RadioButton btnNo = new RadioButton(this);
            btnNo.setText("No");
            btnNo.setTextSize(24);
            btnNo.setPadding(10, 20, 10, 20);
            btnYes.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            btnNo.setLayoutParams(new LinearLayout.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            radioGroup.addView(btnYes);
            radioGroup.addView(btnNo);
            linearLayout.addView(radioGroup);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion < questions.length) {
                    int prevQuestion = currentQuestion;
                    ++currentQuestion;

                    String[] prevQuestionData = questions[prevQuestion].split(";");
                    String prevName = prevQuestionData[0];
                    String prevType = prevQuestionData[1];
                    try {
                        FileOutputStream outputStream = openFileOutput("answers", MODE_APPEND);

                        if (prevType.equals("text")) {
                            EditText editText = (EditText) linearLayout.getChildAt(0);
                            String response = editText.getText().toString();
                            if ("".equals(response)) {
                                outputStream.close();
                                --currentQuestion;
                                return;
                            }

                            outputStream.write(("{" + prevName + "};" + response + '\n').getBytes());

                        } else if (prevType.equals("bool")) {
                            RadioGroup radioGroup = (RadioGroup) linearLayout.getChildAt(0);
                            RadioButton btnYes = (RadioButton) radioGroup.getChildAt(0);
                            RadioButton btnNo = (RadioButton) radioGroup.getChildAt(1);
                            String response;
                            if (btnYes.isChecked()) {
                                response = "true";
                            } else if (btnNo.isChecked()) {
                                response = "false";
                            } else {
                                outputStream.close();
                                --currentQuestion;
                                return;
                            }

                            outputStream.write(("{" + prevName + "};" + response + '\n').getBytes());
                        }

                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (currentQuestion < questions.length) {
                        String[] questionData = questions[currentQuestion].split(";");
                        String type = questionData[1];
                        String content = questionData[2];

                        linearLayout.removeAllViews();
                        TextView questionText = findViewById(R.id.txtQuestion);
                        questionText.setText(content);

                        if (type.equals("text")) {
                            EditText editText = new EditText(Questionnaire.this);
                            editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            linearLayout.addView(editText);
                        } else if (type.equals("bool")) {
                            RadioGroup radioGroup = new RadioGroup(Questionnaire.this);
                            radioGroup.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            RadioButton btnYes = new RadioButton(Questionnaire.this);
                            btnYes.setText("Yes");
                            btnYes.setTextSize(24);
                            btnYes.setPadding(10, 20, 10, 20);
                            RadioButton btnNo = new RadioButton(Questionnaire.this);
                            btnNo.setText("No");
                            btnNo.setTextSize(24);
                            btnNo.setPadding(10, 20, 10, 20);
                            btnYes.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                            btnNo.setLayoutParams(new LinearLayout.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                            radioGroup.addView(btnYes);
                            radioGroup.addView(btnNo);
                            linearLayout.addView(radioGroup);
                        }

                    } else {
                        try {
                            FileInputStream inputStream = openFileInput("answers");
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                sb.append(line + " ");
                            }
                            inputStreamReader.close();

                            Snackbar.make(v, sb.toString(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        Questionnaire.this.finish();
                    }

                }
            }
        });
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
