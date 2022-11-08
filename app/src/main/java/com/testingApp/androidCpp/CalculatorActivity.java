package com.testingApp.androidCpp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.testingApp.androidCpp.databinding.ActivityCalculatorBinding;

public class CalculatorActivity extends AppCompatActivity {

    private ActivityCalculatorBinding activityCalculatorBinding;

    static {
        System.loadLibrary("androidCpp");
    }

    public native Integer doSumCpp(Integer i, Integer j);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityCalculatorBinding = ActivityCalculatorBinding.inflate(getLayoutInflater());
        setContentView(activityCalculatorBinding.getRoot());
    }

    public EditText e1, e2;
    TextView t1;
    int num1, num2;

    public boolean getNumbers() {

        //checkAndClear();
        // defining the edit text 1 to e1
        e1 = (EditText) findViewById(R.id.num1);

        // defining the edit text 2 to e2
        e2 = (EditText) findViewById(R.id.num2);

        // defining the text view to t1
        t1 = (TextView) findViewById(R.id.result);

        // taking input from text box 1
        String s1 = e1.getText().toString();

        // taking input from text box 2
        String s2 = e2.getText().toString();

        if (s1.equals("Please enter value 1") && s2.equals(null)) {
            String result = "Please enter value 2";
            e2.setText(result);
            return false;
        }
        if (s1.equals(null) && s2.equals("Please enter value 2")) {
            String result = "Please enter value 1";
            e1.setText(result);
            return false;
        }
        if (s1.equals("Please enter value 1") || s2.equals("Please enter value 2")) {
            return false;
        }

        if ((!s1.equals(null) && s2.equals(null)) || (!s1.equals("") && s2.equals(""))) {

            String result = "Please enter value 2";

            e2.setText(result);
            return false;
        }
        if ((s1.equals(null) && !s2.equals(null)) || (s1.equals("") && !s2.equals(""))) {
            //checkAndClear();
            String result = "Please enter value 1";
            e1.setText(result);
            return false;
        }
        if ((s1.equals(null) && s2.equals(null)) || (s1.equals("") && s2.equals(""))) {
            //checkAndClear();
            String result1 = "Please enter value 1";
            e1.setText(result1);
            String result2 = "Please enter value 2";
            e2.setText(result2);
            return false;
        } else {
            // converting string to int.
            num1 = Integer.parseInt(s1);

            // converting string to int.
            num2 = Integer.parseInt(s2);
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    public void doSum(View v) {

        // get the input numbers
        if (getNumbers()) {
            t1.setText(Integer.toString(doSumCpp(num1, num2)));
        } else {
            t1.setText("Error Please enter Required Values");
        }

    }

    public void clearTextNum1(View v) {

        // get the input numbers
        e1.getText().clear();
    }

    public void clearTextNum2(View v) {

        // get the input numbers
        e2.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    public void doPow(View v) {

        //checkAndClear();
        // get the input numbers
        if (getNumbers()) {
            double sum = Math.pow(num1, num2);
            t1.setText(Double.toString(sum));
        } else {
            t1.setText("Error Please enter Required Values");
        }
    }

    // a public method to perform subtraction
    @SuppressLint("SetTextI18n")
    public void doSub(View v) {
        //checkAndClear();
        // get the input numbers
        if (getNumbers()) {
            int sum = num1 - num2;
            t1.setText(Integer.toString(sum));
        } else {
            t1.setText("Error Please enter Required Values");
        }
    }

    // a public method to perform multiplication
    @SuppressLint("SetTextI18n")
    public void doMul(View v) {
        //checkAndClear();
        // get the input numbers
        if (getNumbers()) {
            int sum = num1 * num2;
            t1.setText(Integer.toString(sum));
        } else {
            t1.setText("Error Please enter Required Values");
        }
    }

    // a public method to perform Division
    @SuppressLint("SetTextI18n")
    public void doDiv(View v) {
        //checkAndClear();
        // get the input numbers
        if (getNumbers()) {

            // displaying the text in text view assigned as t1
            double sum = num1 / (num2 * 1.0);
            t1.setText(Double.toString(sum));
        } else {
            t1.setText("Error Please enter Required Values");
        }
    }

    // a public method to perform modulus function
    @SuppressLint("SetTextI18n")
    public void doMod(View v) {
        //checkAndClear();
        // get the input numbers
        if (getNumbers()) {
            double sum = num1 % num2;
            t1.setText(Double.toString(sum));
        } else {
            t1.setText("Error Please enter Required Values");
        }
    }
}
