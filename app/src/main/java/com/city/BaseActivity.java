package com.city;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(getLayoutId());
        ButterKnife.bind(this);

        getPreIntent();
        initView();
        initData();
        initListener();
    }

    /**
     * @return 布局文件id
     */
    abstract int getLayoutId();

    /**
     * 获取上一个页面传递来的 intent 数据
     */
    void getPreIntent() {
    }

    /**
     * 初始化View
     */
    void initView() {
    }


    /**
     * 初始化界面数据
     */
    void initData() {
    }


    /**
     * 绑定监听器与适配器
     */
    void initListener() {
    }

}