package com.example.ExpenseTracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;


public class SignupActivity extends AppCompatActivity {
    EditText UsernameEditText;
    EditText PasswordEditText;
    EditText RetypePassEditText;
    EditText EmailEditText;
    EditText PhoneEditText;
    Button SignUpbutton;
    Context context;
    private AwesomeValidation usernameValidation;
    private AwesomeValidation passwordValidation;
    private AwesomeValidation passwordReValidation;
    private AwesomeValidation emailValidation;
    private AwesomeValidation phoneValidation;
    DatabaseHelper databaseHelper;
    String UsernameValue;
    String RetypePassValue;
    String EmailValue;
    String PhoneValue;
    public SignupActivity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context=getApplicationContext();
        UsernameEditText=findViewById(R.id.etUsername);
        PasswordEditText=findViewById(R.id.etSignupPassword);
        RetypePassEditText=findViewById(R.id.etRetypePass);
        EmailEditText=findViewById(R.id.etEmail);
        PhoneEditText=findViewById(R.id.etPhone);
        SignUpbutton=findViewById(R.id.btnSignUp);

        usernameValidation = new AwesomeValidation(ValidationStyle.BASIC);
        passwordValidation = new AwesomeValidation(ValidationStyle.BASIC);
        passwordReValidation = new AwesomeValidation(ValidationStyle.BASIC);
        emailValidation = new AwesomeValidation(ValidationStyle.BASIC);
        phoneValidation = new AwesomeValidation(ValidationStyle.BASIC);
        databaseHelper = new DatabaseHelper(this);

        PhoneEditText.setVisibility(View.VISIBLE);
        SignUpbutton.setText("Sign me up!");
        EmailEditText.setVisibility(View.VISIBLE);
        addValidationToView();

        UsernameValue = UsernameEditText.getText().toString();
        if(!IsUserNameUnique(UsernameValue))
            UsernameEditText.setError("Username is already taken.");
        /*
            //Validation of usernanme on tab
        UsernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(!focus)
                {
                    //Check if the user name is already taken
                    UsernameValue = UsernameEditText.getText().toString();
                    if(!IsUserNameUnique(UsernameValue))
                        UsernameEditText.setError("Username is already taken.");
                    else
                    {
                        if(!usernameValidation.validate())
                            UsernameEditText.clearFocus();
                    }

                }
            }
        });
        
        //One capital letter, One number, One symbol, length 6 chars
        PasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(!focus)
                {
                    if(!passwordValidation.validate())
                        PasswordEditText.clearFocus();
                }
            }
        });

        //Verify match of password and Retypepassword fields on tab
        RetypePassEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(!focus)
                    passwordReValidation.validate();
            }
        });

        //Validation of email on tab
        EmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(!focus)
                    emailValidation.validate();
            }
        });

        //Validation of phone on tab
        PhoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(!focus)
                    phoneValidation.validate();
            }
        });
        */

        // Validate every field and Store data in a Hashmap , then redirect to Login Page by appropriate toast msg. If invalid credentials, then display respective toast msg.
        SignUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RetypePassValue=RetypePassEditText.getText().toString();
                UsernameValue=UsernameEditText.getText().toString();
                PhoneValue = PhoneEditText.getText().toString();
                EmailValue = EmailEditText.getText().toString();
                if (usernameValidation.validate() && emailValidation.validate() && passwordValidation.validate()
                        && phoneValidation.validate() && passwordReValidation.validate() && IsUserNameUnique(UsernameValue))
                {
                    ContentValues userContentValues = new ContentValues();
                    userContentValues.put("user_name", UsernameValue);
                    userContentValues.put("password", RetypePassValue);
                    userContentValues.put("user_email", EmailValue);
                    userContentValues.put("user_mobile", PhoneValue);
                    userContentValues.put("isNewUser", 1);
                    userContentValues.put("rememberMe", 0);
                    userContentValues.put("isActive",0);

                    if( databaseHelper.insertUser(userContentValues)) {

                        Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show();
                        Intent LogIn = new Intent(context, LoginPageActivity.class);
                        startActivity(LogIn);
                    }
                    else
                        Toast.makeText(context, "Beep bop...something went wrong. ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context, "Invalid credentials, could not sign up", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    // Check if username is already taken
    boolean IsUserNameUnique(String username)
    {
        return databaseHelper.isUserUnique(username);
    }

    private void addValidationToView()
    {
        //Regex from: https://stackoverflow.com/a/58771373
        usernameValidation.addValidation(this, R.id.etUsername,"^(?=.{2,20}$)(?:[a-zA-Z\\d]+(?:(?:\\.|-|_)[a-zA-Z\\d])*)+$", R.string.invalid_name);
        emailValidation.addValidation(this, R.id.etEmail, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        String regexPassword = ".{4,}";
        passwordValidation.addValidation(this, R.id.etSignupPassword, regexPassword, R.string.invalid_password);
        passwordReValidation.addValidation(this, R.id.etRetypePass, R.id.etSignupPassword, R.string.invalid_confirm_password);
        phoneValidation.addValidation(this, R.id.etPhone, "^[+]?[0-9]{10,13}$", R.string.invalid_phone);
    }
}
