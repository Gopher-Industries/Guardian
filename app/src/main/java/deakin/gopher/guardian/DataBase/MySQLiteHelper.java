package deakin.gopher.guardian.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLiteHelper extends SQLiteOpenHelper {

    Context context;

    public MySQLiteHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static final String DATABASE_NAME = "Guardiandb.db";

    public static final int DATABASE_VERSION = 1;
   public static final String UserType = "UserType";

    public static final String Common_Id = "Common_Id";
    public static final String LoginID = "LoginID";
    public static final String Password = "Password";
    public static final String ISLogin = "ISLogin";
    public static final String IsResetPassword = "IsResetPassword";
    public static final String tbl_Login = "tbl_Login";
    public static final String IsActive = "IsActive";

    public static final String create_tbl_Login = "create table "
            + tbl_Login + " (" + Common_Id + " integer primary key autoincrement,"
            + LoginID + " text,"
            + Password + " text,"
            + UserType + " text,"
            + IsResetPassword + " text,"
            + IsActive + " text,"
            + ISLogin + " text)";



// tbl for Content

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(create_tbl_Login);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
