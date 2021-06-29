package com.example.ExpenseTracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    static String name = "expense_manager_database";
    static int version = 1;

    private static final String CreateTable_USER = "CREATE TABLE if not exists 'user' ('user_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'user_name' TEXT," +
            "'password' TEXT, 'user_email' TEXT, 'user_mobile' TEXT,'isNewUser' BIT,'rememberMe' BIT NOT NULL,'isActive' BIT NOT NULL)";

    private static final String createTable_EXPENSE_ITEMS = "CREATE TABLE if not exists 'expense_items' ('item_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'Item_name' TEXT,'user_id' INTEGER,FOREIGN KEY('user_id') REFERENCES user(user_id))";
    private static final String createTable_EXPENSE="CREATE TABLE if not exists 'user_expenses' ('user_expense_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'user_id' INTEGER  ,'expense_item_id' INTEGER , 'expense' MONEY,'expense_date' DATE, 'inserted_date' DATE,FOREIGN KEY('user_id') REFERENCES user(user_id), FOREIGN KEY('expense_item_id') REFERENCES expense_items(item_id) )";
    private static final String createTable_USER_GOAL_ANNUAL_SAVINGS="CREATE TABLE if not exists 'user_goal_for_annual_savings'( 'goal_id' INTEGER NOT NULL PRIMARY KEY UNIQUE,'user_id' INTEGER,'year' YEAR,'desired_savings_for_year' MONEY,'annual_income' MONEY,'max_daily_expense' MONEY,FOREIGN KEY('user_id') REFERENCES user(user_id), CONSTRAINT USER_INCOME_BY_YEAR UNIQUE(user_id,year))";


    public DatabaseHelper(Context context) {
        super(context, name, null, version);
        getWritableDatabase().execSQL(CreateTable_USER);
        getWritableDatabase().execSQL(createTable_EXPENSE_ITEMS);
        getWritableDatabase().execSQL(createTable_EXPENSE);
        getWritableDatabase().execSQL(createTable_USER_GOAL_ANNUAL_SAVINGS);
    }

    public int getExpenseId(String item_name, int user_id){
        String sql_QUERY="Select item_id from expense_items where user_id='"+user_id+"' and Item_name='"+item_name+"'";
        SQLiteStatement sqlite_STATEMENT=getReadableDatabase().compileStatement(sql_QUERY);
        int id;
        id=Integer.parseInt(sqlite_STATEMENT.simpleQueryForString());
        sqlite_STATEMENT.close();

        return id;
    }

    public void insertExpense(int user_id, int item_id, double exp,String date,String current_date){
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", user_id);
            contentValues.put("expense_item_id", item_id);
            contentValues.put("expense", exp);
            contentValues.put("expense_date", date);
            contentValues.put("inserted_date", current_date);
            getWritableDatabase().insert("user_expenses", "", contentValues);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
    }
    public HashMap<String,String> getUserdetails( int user_id){
        HashMap<String ,String> details = null;
        String sql_QUERY="Select * from user where user_id='"+user_id+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery(sql_QUERY, null);

        if(cursor.moveToFirst()){
            do{
                details=new HashMap<>();
                details.put("user_name",cursor.getString(cursor.getColumnIndex("user_name")));
                details.put("password",cursor.getString(cursor.getColumnIndex("password")));
                details.put("user_email",cursor.getString(cursor.getColumnIndex("user_email")));
                details.put("user_mobile",cursor.getString(cursor.getColumnIndex("user_mobile")));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return details;
    }

    public boolean updateUserDetails(String email, String phoneNo,int userId)
    {
        boolean isSuccessfull = false;
        try {

            ContentValues UsercontentValues = new ContentValues();

            UsercontentValues.put("user_email", email);
            UsercontentValues.put("user_mobile", phoneNo);

            isSuccessfull = getWritableDatabase().update("user", UsercontentValues,"user_id=?",new String[]{String.valueOf(userId)}) <1 ? false : true;
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return isSuccessfull;
    }

    public double getDailyExpenses(DatabaseHelper dbhelper, String date, int user_id){
        double expense = 0;
        String sql_QUERY="Select * from user_expenses where expense_date='"+date+"' and user_id='"+user_id+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery(sql_QUERY, null);

        if(cursor.moveToFirst()){
            do{
                expense = expense + Double.parseDouble(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return expense;
    }

    public int getSaving(int user_id){
        String sql_QUERY="Select max_daily_expense from user_goal_for_annual_savings where user_id='"+user_id+"'";
        SQLiteStatement sqlite_STATEMENT=getReadableDatabase().compileStatement(sql_QUERY);
        int exp;
        exp=Integer.parseInt(sqlite_STATEMENT.simpleQueryForString());
        sqlite_STATEMENT.close();
        return exp;
    }


    public boolean insertUser(ContentValues userContentValues){
     Boolean flag=   getWritableDatabase().insert("user","", userContentValues)==-1?false:true;
   return flag;
   }


    public boolean isLoginValid(String username, String password) {
        String sql_QUERY = "Select count(*) from user where user_name='" + username + "' and password='" + password + "'";
        SQLiteStatement sqlite_STATEMENT = getReadableDatabase().compileStatement(sql_QUERY);
        long l = sqlite_STATEMENT.simpleQueryForLong();
        sqlite_STATEMENT.close();

        if (l == 1) {
            return true;

        } else {
            return false;
        }
    }

    public boolean isUserUnique(String username){
        String sql_QUERY = "Select count(*) from user where user_name='" + username + "'";
        SQLiteStatement sqlite_STATEMENT = getReadableDatabase().compileStatement(sql_QUERY);
        long l = sqlite_STATEMENT.simpleQueryForLong();
        sqlite_STATEMENT.close();

        if(l == 0){
            return true;
        } else {
            return false;
        }
    }
    public String getActiveUserEmail()
    {
        String sql_QUERY="Select user_email from user where user_id='"+getActiveUserId()+"'";
        SQLiteStatement sqlite_STATEMENT=getReadableDatabase().compileStatement(sql_QUERY);
        String email=sqlite_STATEMENT.simpleQueryForString();
        sqlite_STATEMENT.close();

        return email;

    }
    public String getActiveUserName()
    {
        String sql_QUERY="Select user_name from user where user_id='"+getActiveUserId()+"'";
        SQLiteStatement sqlite_STATEMENT=getReadableDatabase().compileStatement(sql_QUERY);
        String name=sqlite_STATEMENT.simpleQueryForString();
        sqlite_STATEMENT.close();

        return name;

    }

    public int get_isNewUser(String username)
    {
        String sqlQuery="select isNewUser from user where user_name='"+username+"'";
        SQLiteStatement sqlite_STATEMENT=getReadableDatabase().compileStatement(sqlQuery);
        int flag=Integer.parseInt(sqlite_STATEMENT.simpleQueryForString());
        sqlite_STATEMENT.close();
        return flag;
    }

    public ArrayList<String> getAllCategoriesofUser(int userid) {
        ArrayList<String> cat = new ArrayList<>();
        String sql_QUERY = "Select Item_name from expense_items where user_id='" + userid + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery(sql_QUERY, null);

        if(cursor.moveToFirst()){
            do{
                cat.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return cat;
    }


    public void addDefaultCategories(String username){
        int i = getUserId(username);

        ContentValues userContentValues = new ContentValues();
        userContentValues.put("user_id", i);
        userContentValues.put("item_name", "Food");
        insertCategory(userContentValues);
        userContentValues.put("user_id", i);
        userContentValues.put("item_name", "Bills");
        insertCategory(userContentValues);
        userContentValues.put("user_id", i);
        userContentValues.put("item_name", "Shopping");
        insertCategory(userContentValues);
        userContentValues.put("user_id", i);
        userContentValues.put("item_name", "Movie");
        insertCategory(userContentValues);
        userContentValues.put("user_id", i);
        userContentValues.put("item_name", "Drinks");
        insertCategory(userContentValues);
    }

    void insertCategory(ContentValues userContentValues) {
        try{
            getWritableDatabase().insert("expense_items", "", userContentValues);
        }
        catch (SQLiteConstraintException e){

        }
    }
    void removeCategory(String category){
        try {
            getWritableDatabase().delete("expense_items", "Item_name=?", new String[]{category});
        }catch (SQLiteConstraintException e){

        }
    }

    public boolean isCategoryInExpenses (String item, int user_id){

        String sql_QUERY = "Select item_id from expense_items where Item_name='" + item + "' and user_id='"+user_id+"'";
        SQLiteStatement sqlite_STATEMENT = getReadableDatabase().compileStatement(sql_QUERY);
        long item_id = sqlite_STATEMENT.simpleQueryForLong();
        sqlite_STATEMENT.close();

        String sql2 = "Select count(*) from user_expenses where expense_item_id='"+item_id+"'";
        SQLiteStatement statement1 = getReadableDatabase().compileStatement(sql2);
        long count = statement1.simpleQueryForLong();
        statement1.close();
        if(count==0){
            return false;
        }
        else
            return true;
    }



    public boolean setGoals(Double AnnualIncome, Double desired_annual_savings, Double max_daily_expense,int userId)
    {
        boolean isSuccessfull = false;
        try {

            ContentValues GoalscontentValues = new ContentValues();
            GoalscontentValues.put("user_id", userId);
            GoalscontentValues.put("year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            GoalscontentValues.put("desired_savings_for_year", desired_annual_savings);
            GoalscontentValues.put("annual_income", AnnualIncome);
            GoalscontentValues.put("max_daily_expense", max_daily_expense);
            //returns number of rows affected..else returns -1
            isSuccessfull = getWritableDatabase().insert("user_goal_for_annual_savings", "", GoalscontentValues) == -1 ? false : true;
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return isSuccessfull;
    }



    public boolean updateGoals(Double AnnualIncome, Double desired_annual_savings, Double max_daily_expense,int userId)
    {
        boolean isSuccessfull = false;
        try {

            ContentValues GoalscontentValues = new ContentValues();
           // GoalscontentValues.put("user_id", userId);
            GoalscontentValues.put("year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            GoalscontentValues.put("desired_savings_for_year", desired_annual_savings);
            GoalscontentValues.put("annual_income", AnnualIncome);
            GoalscontentValues.put("max_daily_expense", max_daily_expense);
            //returns number of rows affected..else returns -1
            isSuccessfull = getWritableDatabase().update("user_goal_for_annual_savings", GoalscontentValues,"user_id=?",new String[]{String.valueOf(userId)}) <1 ? false : true;
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return isSuccessfull;
    }

    public int getUserId(String username)
    {
        int id=-1;
        try {
            String sqlQuery="select user_id from user where user_name='"+username+"'";
            SQLiteStatement sqlite_STATEMENT=getReadableDatabase().compileStatement(sqlQuery);
            id=Integer.parseInt(sqlite_STATEMENT.simpleQueryForString());
            sqlite_STATEMENT.close();

        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return id;
    }

    public int getActiveUserId()
    {
        int id=-1;
        try {
            String sqlQuery="select user_id from user where isActive=1";
            SQLiteStatement sqlite_STATEMENT=getReadableDatabase().compileStatement(sqlQuery);
            id=Integer.parseInt(sqlite_STATEMENT.simpleQueryForString());
            sqlite_STATEMENT.close();

        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return id;
    }

    public  void UpdateisNewUser(int userId)
    {
        try
        {
            String query="UPDATE user SET isNewUser=0 WHERE user_id= "+userId;
            getWritableDatabase().execSQL(query);
        }
            catch(SQLiteException e)
            {
                e.printStackTrace();
            }

    }

    public  void setisActive(int userId)
    {
        try
        {
            String queryFirst=" UPDATE user SET isActive=0 WHERE user_id in (SELECT user_id from user WHERE isActive=1) ";
            getWritableDatabase().execSQL(queryFirst);
            String query="UPDATE user SET isActive=1 WHERE user_id= "+userId;
            getWritableDatabase().execSQL(query);
        }
        catch(SQLiteException e)
        {
            e.printStackTrace();
        }

    }
    public void userInactive(int userId)
    {
        try
        {
            String query="UPDATE user SET isActive=0 WHERE user_id= "+userId;
            getWritableDatabase().execSQL(query);
        }
        catch(SQLiteException e)
        {
            e.printStackTrace();
        }
    }

    public Cursor getUserNameNPassword()
    {
        Cursor data=null;
        try {
             data=getWritableDatabase().rawQuery("select * from user where rememberMe=1",null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return data;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'user'");
        db.execSQL("DROP TABLE IF EXISTS 'expense_items'");
        db.execSQL("DROP TABLE IF EXISTS 'user_expenses'");
        db.execSQL("DROP TABLE IF EXISTS 'user_goal_for_annual_savings'");
        onCreate(db);

    }

    public Cursor getUserGoals(int userId)
    {
        Cursor data=null;
        try {
            data=getWritableDatabase().rawQuery("select * from user_goal_for_annual_savings where user_id='"+userId+"'",null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public HashMap<String,String> getTodayStatus(int activeUserId) {
        HashMap<String ,String > todaysdata=new HashMap<>();
        Double TodayTotalExpenses= Double.valueOf(0);
        Double YearTotalExpenses=Double.valueOf(0);
        Double AnnualIncome=Double.valueOf(0);
        Double DailyIncome=Double.valueOf(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        int CurrentYear=localDate.getYear();
        String LastYearDate= String.valueOf(CurrentYear-1)+"-12-31";
        String CurrentDate=dtf.format(localDate);


        try {
          Cursor  Todayexpenses=getWritableDatabase().rawQuery("select expense from user_expenses where user_id='"+activeUserId+"' and expense_date= '"+CurrentDate+"'",null);
          if(Todayexpenses!=null ||Todayexpenses.getCount()>0)
          {
              if(Todayexpenses.moveToFirst())
              {
                  do {
                      TodayTotalExpenses = TodayTotalExpenses + Double.parseDouble(Todayexpenses.getString(0));
                  }while (Todayexpenses.moveToNext());

              }
          }
          Todayexpenses.close();
          String query="SELECT expense FROM user_expenses where expense_date >"+LastYearDate+" AND user_id='"+activeUserId+"' ";
          Cursor YearlyExpenditure=getWritableDatabase().rawQuery(query,null);

            if(YearlyExpenditure!=null ||YearlyExpenditure.getCount()>0)
            {
                if(YearlyExpenditure.moveToFirst())
                {
                    do {
                        YearTotalExpenses = YearTotalExpenses + Double.parseDouble(YearlyExpenditure.getString(0));
                    }while (YearlyExpenditure.moveToNext());

                }
            }
            YearlyExpenditure.close();
            String queryForIncome= "select annual_income from user_goal_for_annual_savings where user_id='"+activeUserId+"'";
            Cursor IncomeCursor=getWritableDatabase().rawQuery(queryForIncome,null);
            if(IncomeCursor!=null|| IncomeCursor.getCount()>0)
            {
                if(IncomeCursor.moveToFirst())
                {
                    do{
                        AnnualIncome=AnnualIncome+Double.parseDouble(IncomeCursor.getString(0));
                    }while (IncomeCursor.moveToNext());
                }
            }
            IncomeCursor.close();
            Calendar cal = Calendar.getInstance();
            double dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
            double total_days_till_now = dayOfYear;

            DailyIncome=AnnualIncome/365;
            todaysdata.put("TodayExpenses",TodayTotalExpenses.toString());
            todaysdata.put("YearlyExpenses",YearTotalExpenses.toString());
            todaysdata.put("TodaySavings",String.valueOf(DailyIncome-TodayTotalExpenses));
            todaysdata.put("ThisyearSavings",String.valueOf((DailyIncome*total_days_till_now)-YearTotalExpenses));
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        return todaysdata;
    }
}
