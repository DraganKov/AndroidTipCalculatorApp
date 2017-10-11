package com.kovacevic.dragan.androidassignment1tipcalculator;
//By Dragan Kovacevic, October 2016
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    Spinner tipspinner;
    Spinner guestcountspinner;

    Button CalculateButton;
    Button ClearButton;

    EditText BillInput;
    EditText CustomTipInput;

    CheckBox HSTcheck;

    TextView TipTotal;
    TextView BillTotal;
    TextView PerPersonTotal;

    int tipindex;
    int guestnum = 1;

    double hst = 0.13;
    double total;
    double tiptotal;
    double hsttotal;

    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onCreateSpinners(savedInstanceState);

        onCreateButtons(savedInstanceState);

        onCreateEditTextBoxes(savedInstanceState);

        onCreateTextViews(savedInstanceState);

        HSTcheck = (CheckBox) findViewById(R.id.checkboxaddhst);
        PerPersonTotal.setVisibility(View.INVISIBLE);

        //clear all input for each reboot incase app is reloaded
        onClear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Method to create the spinners in the UI
     */
    public void onCreateSpinners(Bundle savedInstanceState){

        tipspinner = (Spinner) findViewById(R.id.tipSpinner);
        guestcountspinner = (Spinner) findViewById(R.id.guestcountspinner);

        ArrayAdapter<CharSequence> tipadapter =
                ArrayAdapter.createFromResource(this, R.array.tipchoices, android.R.layout.simple_spinner_item);
        tipadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tipspinner.setAdapter(tipadapter);

        ArrayAdapter<CharSequence> guestcountadapter =
                ArrayAdapter.createFromResource(this, R.array.guestcounts, android.R.layout.simple_spinner_item);
        guestcountadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        guestcountspinner.setAdapter(guestcountadapter);

        tipspinner.setOnItemSelectedListener(this);
        guestcountspinner.setOnItemSelectedListener(this);

    }
    private void onCreateTextViews(Bundle savedInstanceState) {

        TipTotal = (TextView) findViewById(R.id.tiptotal);
        BillTotal = (TextView) findViewById(R.id.totaltotal);
        PerPersonTotal = (TextView) findViewById(R.id.splittotal);
        PerPersonTotal.setVisibility(View.INVISIBLE);
    }

    private void onCreateEditTextBoxes(Bundle savedInstanceState) {

        BillInput = (EditText) findViewById(R.id.billInput);
        BillInput.setText("");

        CustomTipInput = (EditText) findViewById(R.id.customTipInput);
        CustomTipInput.setHint("Enter Tip %:");
        CustomTipInput.setVisibility(View.INVISIBLE);
    }

    public void onCreateButtons(Bundle savedInstanceState){

        CalculateButton = (Button) findViewById(R.id.calculatebutton);
        ClearButton = (Button) findViewById(R.id.clearbutton);

        CalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((BillInput.getText().toString().isEmpty())||(CustomTipInput.getText().toString().isEmpty())&&
                        (tipspinner.getSelectedItemPosition() == 3)){

                    //Toast.makeText"Not All Fields are Populated")
                    context = getApplicationContext();
                    CharSequence text = "Must Fill in all Fields to Calculate!";
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                }
                else{
                    onCalculate();
                }
            }
        });

        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        //Get the number of guests on each update
        guestnum = Integer.parseInt(guestcountspinner.getItemAtPosition(pos).toString());

        //if spinner is on Other, show the custom tip input
        if(tipspinner.getSelectedItemPosition() == 3){
            CustomTipInput.setVisibility(View.VISIBLE);
        }
        else{
            CustomTipInput.setVisibility(View.INVISIBLE);
            CustomTipInput.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }

    /*
    Calulation Function for the app
     */
    public void onCalculate(){
        //try catch incase of invalid input
        try {
            total = Double.parseDouble(BillInput.getText().toString());

            if (tipspinner.getSelectedItemPosition() == 3) {
                tiptotal = (Double.parseDouble(CustomTipInput.getText().toString()) / 100);
            } else {
                tipindex = tipspinner.getSelectedItemPosition();
                tiptotal = (Double.parseDouble(tipspinner.getItemAtPosition(tipindex).toString()) / 100);
            }

            //Add HST if checkbox is checked
            if (HSTcheck.isChecked()) {
                hsttotal = total*hst;

                total += hsttotal;
            }

            //Calculate and add tip to the total
            tiptotal = total*tiptotal;
            total += tiptotal;
        }
        catch(Exception e){
        }

        //Dipslay all the calculatons to the user.
        BillTotal.setText(String.format("Total is: $%.2f ($%.2f hst)", total, hsttotal));

        TipTotal.setText(String.format("Tip is: $%.2f",tiptotal));

        //Display the split cost to the user if more than 1 guest is selected
        if (guestnum > 1){
            PerPersonTotal.setVisibility(View.VISIBLE);
            PerPersonTotal.setText(String.format("Per Person: $%.2f",(total/guestnum)));
        }
        else{
            PerPersonTotal.setVisibility(View.INVISIBLE);
        }
    }

    /*
    Reset all UI back to original
     */
    public void onClear(){
        //Reset all edit texts to default
        BillInput.setHint("Enter Bill Amount");
        BillInput.setText("");
        CustomTipInput.setText("");

        //hide custom input
        CustomTipInput.setVisibility(View.INVISIBLE);

        //reset all textviews to default
        TipTotal.setText("Tip is: $");
        BillTotal.setText("Total is: $");
        PerPersonTotal.setText("Per Person: $");
        CustomTipInput.setHint("Enter Tip %:");

        PerPersonTotal.setVisibility(View.INVISIBLE);


        tiptotal = 0.00;
        total= 0.00;

        //Uncheck Tax box if it was checked
        if(HSTcheck.isChecked()){
            HSTcheck.toggle();
        }

        //reset spinners to zero
        guestcountspinner.setSelection(0);
        tipspinner.setSelection(0);
    }
}
