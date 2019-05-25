package com.city.utils;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.city.bean.DaoMaster;
import com.city.bean.DaoSession;
import com.lzy.okgo.OkGo;

public class MyApplication extends Application {
    private static DaoSession daoSession;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //必须调用初始化
        OkGo.init(this);

        //配置数据库
        setupDatabase();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        LogUtils.e("onCreate: 配置数据库 ");
        //创建数据库shop.db"
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "shop.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        // 获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        // 获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }

}