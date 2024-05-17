package deakin.gopher.guardian.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

import deakin.gopher.guardian.DataBase.DataClasses.Login;

public class DataBase {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    public DataBase(Context context) {
        dbHelper = new MySQLiteHelper(context, MySQLiteHelper.DATABASE_NAME,
                null, MySQLiteHelper.DATABASE_VERSION);
    }



    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }




    public long insert_tbl_Login(Login login) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.LoginID, login.getLoginID());
        contentValues.put(MySQLiteHelper.Password, login.getPassword());
        contentValues.put(MySQLiteHelper.UserType, login.getUserType());
        contentValues.put(MySQLiteHelper.ISLogin, login.getISLogin());
        contentValues.put(MySQLiteHelper.IsResetPassword, login.getIsResetPassword());
        contentValues.put(MySQLiteHelper.IsActive, "true");
        long insertId = database.insert(MySQLiteHelper.tbl_Login, null, contentValues);
        return insertId;
    }


    public long delete_PreviousLogin() {
        long i;
        i = database.delete(MySQLiteHelper.tbl_Login, null, null);
        return i;
    }


    @SuppressLint("Range")
    public Login get_Login_Details(String UserID) {
       Login login = new Login();

        String sql = "select * from tbl_Login  where LoginID='"+UserID+"' and IsActive='true'";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            login.setLoginID(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LoginID)));
            login.setISLogin(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ISLogin)));
            login.setIsResetPassword(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.IsResetPassword)));
            login.setPassword(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.Password)));
            login.setUserType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.UserType)));
            cursor.moveToNext();
        }
        cursor.close();
        return login;

    }


    public long LogOut()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.ISLogin,"false");
        return database.update("tbl_Login",contentValues,null,null);

    }

    public long UpdateIsActive()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.IsActive,"false");
        return database.update("tbl_Login",contentValues,null,null);

    }
    public long IsUpdatePassword()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.IsResetPassword,"true");
        String whereClause=MySQLiteHelper.IsActive+"=? ";
        String[]  whereArgs={"true"};
        return database.update("tbl_Login",contentValues,whereClause,whereArgs);

    }

    @SuppressLint("Range")
    public Login check_Login_Status() {
        Login login = new Login();

        String sql = "select * from tbl_Login where IsActive='true'";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            login.setISLogin(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.ISLogin)));
            login.setUserType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.UserType)));
            login.setIsResetPassword(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.IsResetPassword)));
            cursor.moveToNext();
        }
        cursor.close();
        return login;

    }




}
