package com.example.ExpenseTracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ExpenseTracker.ui.home.HomeFragment;

public class popUpWin_forInputs extends DialogFragment {

    DatabaseHelper databaseHelper;

    EditText txt_AnnualIncome;
    EditText txt_AnnualSavings;
    EditText txt_MaxDailyExpense;
    Double AnnualIncome,DesiredAnnualSavings,MaxDailyExpenseLimit;
    int userId;
    boolean isEditingGoals=false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view=inflater.inflate(R.layout.dialog_window_for_inputs,null);
        final Bundle bundle=getArguments();
        databaseHelper=new DatabaseHelper(getContext());
        txt_AnnualIncome=view.findViewById(R.id.et_annual_income);
        txt_AnnualSavings=view.findViewById(R.id.et_desired_annual_savings);
        txt_MaxDailyExpense=view.findViewById(R.id.et_max_daily_expense);


        Cursor goals=databaseHelper.getUserGoals(databaseHelper.getActiveUserId());
        if(goals.getCount()>0) {
            goals.moveToFirst();
            String AnnualIncome = goals.getString(goals.getColumnIndex("annual_income"));
            String DesiredAnnualSavings=goals.getString(goals.getColumnIndex("desired_savings_for_year"));
            String max_daily_expense = goals.getString(goals.getColumnIndex("max_daily_expense"));
            txt_AnnualIncome.setText(String.valueOf(Math.round(Double.parseDouble(AnnualIncome))));
            txt_AnnualSavings.setText(String.valueOf(Math.round(Double.parseDouble(DesiredAnnualSavings))));
            txt_MaxDailyExpense.setText(String.valueOf(Math.round(Double.parseDouble(max_daily_expense))));
            isEditingGoals=true;

        }
        goals.close();
        builder.setView(view);


        txt_AnnualSavings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if( txt_AnnualIncome.getText().length()>0 && txt_MaxDailyExpense.getText().length()>0 ) {
                        AnnualIncome = Double.parseDouble(txt_AnnualIncome.getText().toString());
                        DesiredAnnualSavings = Double.parseDouble(txt_AnnualSavings.getText().toString());
                        MaxDailyExpenseLimit=Double.parseDouble(txt_MaxDailyExpense.getText().toString());
                        if(MaxDailyExpenseLimit!=Math.round((AnnualIncome - DesiredAnnualSavings) / 365))
                        {
                            txt_MaxDailyExpense.setText(String.valueOf(Math.round((AnnualIncome - DesiredAnnualSavings) / 365)));
                        }
                    }
                }
            }
        });
        txt_MaxDailyExpense.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && txt_AnnualIncome.getText().length()>0 && txt_AnnualSavings.getText().length()>0)
                {
                    AnnualIncome=Double.parseDouble(txt_AnnualIncome.getText().toString());
                    DesiredAnnualSavings= Double.parseDouble(txt_AnnualSavings.getText().toString());
                    MaxDailyExpenseLimit=Double.parseDouble(txt_MaxDailyExpense.getText().toString());
                    if(Double.parseDouble(txt_MaxDailyExpense.getText().toString())>Math.round((Double.parseDouble(txt_AnnualIncome.getText().toString()) - Double.parseDouble(txt_AnnualSavings.getText().toString())) / 365))
                    {
                        txt_MaxDailyExpense.setError("Expenses seems to be more.Your savings will be reduced to"+String.valueOf(Math.round((Double.parseDouble(txt_AnnualIncome.getText().toString()) -Double.parseDouble(txt_AnnualSavings.getText().toString()) ) / 365)));
                       Toast.makeText(getContext(),"Maximum expense must tally with Income and Savings!",Toast.LENGTH_SHORT);

                    }
                    else if(Double.parseDouble(txt_MaxDailyExpense.getText().toString())<Math.round((Double.parseDouble(txt_AnnualIncome.getText().toString()) - Double.parseDouble(txt_AnnualSavings.getText().toString())) / 365))
                    {
                        Double Savings=(Double.parseDouble(txt_AnnualIncome.getText().toString())-(Double.parseDouble(txt_MaxDailyExpense.getText().toString()) *365));
                        txt_AnnualSavings.setError("You can save to up "+Savings+".To set the goal edit savings or expenses");
                    }
                    else
                    {
                    }
                }
            }
        });
                // Add action buttons
            builder .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AnnualIncome=Double.parseDouble(txt_AnnualIncome.getText().toString());
                        DesiredAnnualSavings= Double.parseDouble(txt_AnnualSavings.getText().toString());
                        MaxDailyExpenseLimit=Double.parseDouble(txt_MaxDailyExpense.getText().toString());

                       // username=bundle.getString("username");
                        userId=databaseHelper.getActiveUserId();
                        Boolean insertSuccessful=false;
                        Boolean UpdateSuccessful=false;
                        if(MaxDailyExpenseLimit==Math.round((AnnualIncome - DesiredAnnualSavings) / 365))
                        {
                            if (isEditingGoals) {
                                UpdateSuccessful = databaseHelper.updateGoals(AnnualIncome, DesiredAnnualSavings, MaxDailyExpenseLimit, userId);

                            } else {
                                insertSuccessful = databaseHelper.setGoals(AnnualIncome, DesiredAnnualSavings, MaxDailyExpenseLimit, userId);
                            }


                            if (insertSuccessful) {
                                databaseHelper.addDefaultCategories(databaseHelper.getActiveUserName());
                                Toast.makeText(getContext(), "Goals have been set!", Toast.LENGTH_SHORT).show();
                                databaseHelper.UpdateisNewUser(userId);
                                HomeFragment homeFragment = new HomeFragment();
                                getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
                                getActivity().setTitle("Home");
                                Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();


                            } else if (UpdateSuccessful) {
                                Toast.makeText(getContext(), "Goals have been updated!", Toast.LENGTH_SHORT).show();
                                HomeFragment homeFragment = new HomeFragment();
                                getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
                                getActivity().setTitle("Home");

                            } else
                                Toast.makeText(getContext(), "Oops! Something went wrong. Please try again later. ", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            long temp = Math.round((AnnualIncome - DesiredAnnualSavings) / 365);
                            Log.e("Wtf", String.valueOf(temp));
                            Toast.makeText(getContext(), "Maximum expense must tally with Income and Savings! ", Toast.LENGTH_LONG).show();
                            popUpWin_forInputs popUp=new popUpWin_forInputs();
                            popUp.show(getParentFragmentManager(),"dialog");


                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        popUpWin_forInputs.this.getDialog().cancel();
                        HomeFragment homeFragment=new HomeFragment();
                        getActivity().setTitle("Home");
                        getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
                        Toast.makeText(getContext(),"Home",Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }
}