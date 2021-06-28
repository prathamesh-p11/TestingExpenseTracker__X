package com.example.ExpenseTracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Pattern;


public class ProfileFragment extends Fragment {

    DatabaseHelper databaseHelper;
    HashMap<String,String > userDetails=new HashMap<>();
    EditText txt_Username,txt_Password,txt_ReTypePassword,txt_Email,txt_Phone;
    String str_Email,str_Phone;
    Button btn_Update;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
         View root =inflater.inflate(R.layout.fragment_profile, container, false);
        databaseHelper=new DatabaseHelper(getContext());
        txt_Username=root.findViewById(R.id.et_update_Username);
        txt_Password=root.findViewById(R.id.et_update_Password);
        txt_ReTypePassword=root.findViewById(R.id.et_update_RetypePass);
        txt_Email=root.findViewById(R.id.et_update_Email);
        txt_Phone=root.findViewById(R.id.et_update_Phone);
        btn_Update=root.findViewById(R.id.btnUpdateProfile);
        userDetails=databaseHelper.getUserdetails(databaseHelper.getActiveUserId());
        if(userDetails.size()>0)
        {


            txt_Username.setText(userDetails.get("user_name"));
            txt_Password.setText(userDetails.get("password"));
            txt_ReTypePassword.setText(userDetails.get("password"));
            txt_Email.setText(userDetails.get("user_email"));
            txt_Phone.setText(userDetails.get("user_mobile"));

            txt_Username.setEnabled(false);
            txt_Password.setEnabled(false);
            txt_ReTypePassword.setEnabled(false);
            txt_Username.setAlpha((float) 0.5);
            txt_Password.setAlpha((float) 0.5);
            txt_ReTypePassword.setAlpha((float) 0.5);

        }
        //Validation of email on tab
        txt_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(!focus)
                {
                    str_Email=txt_Email.getText().toString();

                    if(!IsEmailValid(str_Email))
                    {
                        txt_Email.setError("Invalid email!");
                    }
                }
            }
        });

        //Validation of phone on tab
        txt_Phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(!focus)
                {
                    str_Phone=txt_Phone.getText().toString();

                    if(!IsPhoneValid(str_Phone))
                    {
                        txt_Phone.setError("Invalid phone number!");
                    }
                }
            }
        });

btn_Update.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        str_Email=  txt_Email.getText().toString();
        str_Phone=txt_Phone.getText().toString();
        if(IsEmailValid(str_Email) && IsPhoneValid(str_Phone))
        {
          if(  databaseHelper.updateUserDetails( str_Email,str_Phone, databaseHelper.getActiveUserId()))
          {
              Toast.makeText(getContext(),"User details updated! Please login again.",Toast.LENGTH_LONG).show();
              Intent intent=new Intent(getContext(),LoginPageActivity.class);
              startActivity(intent);

          }
          else
          {
              Toast.makeText(getContext(),"db ",Toast.LENGTH_SHORT).show();
          }
        }
        else {
            Toast.makeText(getContext(),"Enter valid credentials",Toast.LENGTH_SHORT).show();
        }

    }
});
    return root;
    }

    boolean IsEmailValid(String email)
    {
        Pattern EmailFormat= Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return EmailFormat.matcher(email).find();

    }
    //Check if the number has exactly 10 digits
    boolean IsPhoneValid(String ph)
    {
        Pattern PhoneFormat=Pattern.compile("^\\d{10}$");
        return PhoneFormat.matcher(ph).find();

    }
}