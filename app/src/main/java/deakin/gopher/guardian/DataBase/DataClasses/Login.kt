package deakin.gopher.guardian.DataBase.DataClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_Login")
data class Login(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "CommonId") var CommonId:Int=0,
    @ColumnInfo(name = "LoginID") var LoginID :String?="",
    @ColumnInfo(name = "Password") var Password :String?="",
    @ColumnInfo(name = "UserType") var UserType :String?="",

    )