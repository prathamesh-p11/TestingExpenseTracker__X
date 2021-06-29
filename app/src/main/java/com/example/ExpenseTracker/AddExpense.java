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
    EditText txt_expense;

    ArrayList<String> list_categories;
    ImageButton btn_select_date_button;
    Button btn_submit_button;
    public static TextView show_date;

    boolean IsFuture(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_1 = simpleDateFormat.parse(s);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,1);
        Date date_2 = cal.getTime();
        if(date_1.compareTo(date_2) > 0){
            return true;
        }
        else
            return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        final DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final int user_id = databaseHelper.getActiveUserId();
        list_categories = databaseHelper.getAllCategoriesofUser(user_id);
        final Spinner spinner = (Spinner)findViewById(R.id.CategoryListLayout);
        btn_submit_button = findViewById(R.id.btnsubmit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddExpense.this, android.R.layout.simple_list_item_1, list_categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        show_date = findViewById(R.id.Date_text);
        btn_select_date_button = findViewById(R.id.slect_date_button);
        txt_expense = findViewById(R.id.edText1);
        btn_select_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new AddExpense.DatePickerFragmentC();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        btn_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String item = spinner.getSelectedItem().toString();
                String date = show_date.getText().toString();
                int expense_id = databaseHelper.getExpenseId(item, user_id);

                Date c_date = new Date();
                String current_date = new SimpleDateFormat("yyyy-MM-dd").format(c_date);

                try {
                    if(show_date.getText().toString().equals("Date")){
                        Toast toast3 =Toast.makeText(AddExpense.this,"Please select a date to continue!",Toast.LENGTH_LONG);
                        toast3.show();
                    }
                    else if(IsFuture(date)){
                        Toast toast3 =Toast.makeText(AddExpense.this,"Time machine is under maintenance, future dates not available! ",Toast.LENGTH_LONG);
                        toast3.show();
                    }
                    else if(txt_expense.getText().toString().equals("")){
                        txt_expense.setError("Expense field required!");
                    }
                    else{
                        double expense = Double.parseDouble(txt_expense.getText().toString());
                        databaseHelper.insertExpense(user_id, expense_id, expense, date, current_date);
                        Toast toast1 =Toast.makeText(AddExpense.this,"Expense added! ",Toast.LENGTH_LONG);
                        toast1.show();
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
        private String str_formattedDate;
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int int_Year = c.get(Calendar.YEAR);
            int int_Month = c.get(Calendar.MONTH);
            int int_Day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, int_Year, int_Month,int_Day);
        }

        @Override
        public void onDateSet(DatePicker view, int int_Year, int int_Month, int int_Day) {
            Calendar c = Calendar.getInstance();
            c.set(int_Year, int_Month,int_Day);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            str_formattedDate = simpleDateFormat.format(c.getTime());

            AddExpense.show_date.setText(str_formattedDate);
        }
    }
}