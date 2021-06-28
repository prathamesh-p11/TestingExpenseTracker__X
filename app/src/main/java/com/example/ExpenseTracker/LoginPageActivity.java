package com.example.ExpenseTracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPageActivity extends AppCompatActivity {
    Context context;
    Button btn_SignUp,btn_Login;
    EditText txt_Username,txt_Password;
    Toast t;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        context=getApplicationContext();
        btn_Login= findViewById(R.id.btnLogin);
        txt_Username=findViewById(R.id.txtUsername);
        txt_Password=findViewById(R.id.txtPassword);
        btn_SignUp=findViewById(R.id.btnSignup);
        databaseHelper = new DatabaseHelper(this);
        Cursor UsernameNPassword= databaseHelper.getUserNameNPassword();

        if(UsernameNPassword.getCount()>0)
        {
            UsernameNPassword.moveToFirst();
            txt_Username.setText(UsernameNPassword.getString(1));//username
            txt_Password.setText(UsernameNPassword.getString(2));//password
            UsernameNPassword.close();
        }
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = txt_Username.getText().toString();
                String password = txt_Password.getText().toString();
                int userid=databaseHelper.getUserId(username);
                databaseHelper.setisActive(userid);


                if(!databaseHelper.isUserUnique(username)){
                    if(databaseHelper.isLoginValid(username,password)){
                        t =Toast.makeText(context,"Login Successful.",Toast.LENGTH_LONG);
                        Intent MainScreenIntent=new Intent(context,MainScreen.class);
                        MainScreenIntent.putExtra("UserName", username);
                        startActivity(MainScreenIntent);

                    }
                    else if(password.equals("")){
                        txt_Password.setError("Password can't be empty!");
                    }
                    else
                    {
                        t =Toast.makeText(context,"Incorrect Password!",Toast.LENGTH_LONG);
                        t.show();
                        txt_Password.setText("");
                    }
                } else if (username.equals("")){
                    Toast toast =Toast.makeText(context,"Username can't be empty!",Toast.LENGTH_LONG);
                    toast.show();
                    txt_Username.setError("Username can't be empty!");
                    txt_Username.setText("");
                    txt_Password.setText("");
                }
                else{
                    txt_Username.setError("User not found!");
                    Toast toast = Toast.makeText(context, "User not found! Click SignUp to register.", Toast.LENGTH_LONG);
                    toast.show();
                    txt_Password.setText("");
                }

            }
        });
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(LoginPageActivity.this,SignupActivity.class);
                startActivity(signup);
            }
        });
    }
}
