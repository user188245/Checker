package com.user.checker;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.user.checker.models.BaseModel;
import com.user.checker.models.SidModel;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;



public class DBManager {
    private SQLiteDatabase db;
    private Context context;
    private final static String dbName = "private_db";

    public DBManager(Context context){
        this.context = context;
    }

    public String getSID(){
        db = context.openOrCreateDatabase(dbName,context.MODE_PRIVATE,null);
        db.execSQL("create table if not exists Player_Info (s_id    char(32)  primary key   not null)");
        String s_id = null;
        Cursor cursor = db.rawQuery("select * from Player_Info",null);
        if(!cursor.moveToPosition(0)) {
            s_id = requestSID();
            db.execSQL("insert into Player_Info values ('" + s_id + "')");
        }else {
            s_id = cursor.getString(0);
        }
        Toast.makeText(context,"s_id : " + s_id,Toast.LENGTH_SHORT).show();
        db.close();
        return s_id;
    }

    private String requestSID(){
        Log.d("in","requestSID");
        SidModel sidModel;
        String json = "";
        try {
            json = HttpManager.post(HttpManager.webServer+"sid.php","privateKey",HttpManager.privateKey);
            sidModel = new SidModel(json);
            if(sidModel.errorCode != 0)
                Toast.makeText(context,"err : " + sidModel.cause,Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            System.err.println(json);
            sidModel = null;
        }
        return sidModel.s_id;
    }

    @Deprecated
    public void dropTable(){
        db = context.openOrCreateDatabase(dbName,context.MODE_PRIVATE,null);
        db.execSQL("drop table if exists Player_Info");
        db.close();
    }


}
