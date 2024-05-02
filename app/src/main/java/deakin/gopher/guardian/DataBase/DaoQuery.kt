package deakin.gopher.guardian.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import deakin.gopher.guardian.DataBase.DataClasses.Login

@Dao
interface DaoQuery {

    @Insert(onConflict = OnConflictStrategy.NONE)
     fun insertLogin(data: Login):Long=0

    @Query("select * from tbl_Login where LoginID=:LoginID and Password=:Password")
    fun getCheckLogin(LoginID:String,Password:String):Login

}