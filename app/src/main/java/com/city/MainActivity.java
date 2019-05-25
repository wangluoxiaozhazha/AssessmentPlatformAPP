package com.city;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.city.assessmentplatformapp.R;
import com.city.loader.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Banner banner;
    private Integer[] images={R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d};
    private String[] titles={"学会说话是一件非常重要的事情","从菜鸟到砖家","脑洞是个无底洞","office的高级应用"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner=(Banner)findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(Arrays.asList(images));
        //banner设置方法全部调用完毕时最后调用
        banner.setBannerTitles(Arrays.asList(titles));
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        banner.setDelayTime(4000);
        banner.start();
    }
}
