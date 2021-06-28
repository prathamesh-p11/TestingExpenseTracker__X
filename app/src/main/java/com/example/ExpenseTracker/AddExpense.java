package com.example.ExpenseTracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddExpense extends AppCompatActivity
{
    EditText ed1;

    ArrayList<String> categories;
    ImageButton select_date_button;
    Button submit_button;
    public static TextView show_date;

    boolean IsFuture(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdf.parse(s);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,1);
        Date d2 = cal.getTime();
        if(d1.compareTo(d2) > 0){
            return true;
        }
        else
            return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        // databaseHelper=new DatabaseHelper();
        //CategoryFragment frag_obj = new CategoryFragment();
        //CategoryLayout frag_layout = new CategoryLayout();
        // AddCategory frag = AddCategory.newInstance(userName);
        // getSupportFragmentManager().beginTransaction().replace(R.id.CategoryListLayout, frag_layout).commit();
        final DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final int user_id = databaseHelper.getActiveUserId();
        categories = databaseHelper.getAllCategoriesofUser(user_id);
        final Spinner spinner = (Spinner)findViewById(R.id.CategoryListLayout);
        submit_button = findViewById(R.id.btnsubmit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddExpense.this, android.R.layout.simple_list_item_1, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        show_date = findViewById(R.id.Date_text);
        select_date_button = findViewById(R.id.slect_date_button);
        ed1 = findViewById(R.id.edText1);
        select_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new AddExpense.DatePickerFragmentC();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String item = spinner.getSelectedItem().toString();
                String date = show_date.getText().toString();
                int expense_id = databaseHelper.getExpenseId(item, user_id);

                Date c_date = new Date();
                String current_date = new SimpleDateFormat("yyyy-MM-dd").format(c_date);

                try {
                    if(show_date.getText().toString().equals("Date")){
                        Toast toast3 =Toast.makeText(AddExpense.this,"Please select a date!",Toast.LENGTH_LONG);
                        toast3.show();
                    }
                    else if(IsFuture(date)){
                        Toast toast3 =Toast.makeText(AddExpense.this,"Future date not allowed! ",Toast.LENGTH_LONG);
                        toast3.show();
                    }
                    else if(ed1.getText().toString().equals("")){
                        ed1.setError("Expense can't be blank!");
                    }
                    else{
                        double expense = Double.parseDouble(ed1.getText().toString());
                        databaseHelper.insertExpense(user_id, expense_id, expense, date, current_date);
                        Toast toast1 =Toast.makeText(AddExpense.this,"Expense added! ",Toast.LENGTH_LONG);
                        toast1.show();
                        //finish();
                       // HomeFragment fragment=new HomeFragment();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,fragment).commit();
                        Intent intent=new Intent(getApplicationContext(),MainScreen.class);
                        intent.putExtra("UserName",databaseHelper.getActiveUserName());
                        startActivity(intent);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static class DatePickerFragmentC extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        private String formattedDate;
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month,day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month,dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            formattedDate = sdf.format(c.getTime());

            AddExpense.show_date.setText(formattedDate);
        }
    }
}