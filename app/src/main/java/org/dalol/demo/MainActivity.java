package org.dalol.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.dalol.stepindicatorview.StepIndicatorView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StepIndicatorView siv = findViewById(R.id.step_indicator_view);
        siv.setStepsCount(3);
        siv.setCurrentStepPosition(1);

        //siv.setAllTicked(); Use this method if you want all to be selected

    }
}
