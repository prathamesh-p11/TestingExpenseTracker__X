package com.example.ExpenseTracker;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class AddCategory extends Fragment {
    TextView txt_display;
    EditText txt_enter_here;
    Button btn_add_category;
    Button btn_cancel;
    private ArrayList<String> items_list;

    public AddCategory() {
        // Required empty public constructor
    }

    public static int userID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        txt_display = view.findViewById(R.id.add_category_text);
        txt_enter_here = view.findViewById(R.id.category_from_user);
        btn_add_category = view.findViewById(R.id.submit_category);
        btn_cancel = view.findViewById(R.id.cancel_add);
        userID=databaseHelper.getActiveUserId();
        items_list = databaseHelper.getAllCategoriesofUser(userID);

        btn_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String custom_category = txt_enter_here.getText().toString();

                if(custom_category.isEmpty()){
                    txt_enter_here.setError("Field can't be empty!");
                }
                else{

                    if(items_list.contains(custom_category)){
                        Toast.makeText(getContext(), "Category already exists!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        ContentValues userContentValues = new ContentValues();
                        userContentValues.put("user_id", userID);
                        userContentValues.put("item_name", custom_category);
                        databaseHelper.insertCategory(userContentValues);

                        Toast.makeText(getContext(), "Category added!", Toast.LENGTH_LONG).show();

                        Fragment fragment = new CategoryLayout();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.category_layout, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CategoryLayout();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.category_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }
}
