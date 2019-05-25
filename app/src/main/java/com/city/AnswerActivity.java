package com.city;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Chronometer;

import com.city.assessmentplatformapp.R;
import com.city.bean.JsonQuestBean;
import com.city.bean.QuestBean;
import com.city.constants.Config;
import com.city.db.LoveDao;
import com.city.fragment.AnswerFragment;
import com.city.utils.LogUtils;
import com.city.utils.ToastUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


/**
 * @ 创建时间: 2017/6/13 on 17:08.
 * @ 描述：回答界面
 * @ 作者: 郑卫超 QQ: 2318723605
 */
public class AnswerActivity extends BaseActivity implements Chronometer.OnChronometerTickListener {

    @BindView(R.id.vp_answer)
    ViewPager vp_answer;
    @BindView(R.id._chro_exam)
    Chronometer chronometer;

    private ArrayList<Fragment> fragmentlists;
    private int minute = 0;
    private int second = 0;
    private AlertDialog.Builder builder;
    private ArrayList<String> a;
    private int nowpager = 0;
    private List<QuestBean> messages;
    private String type;

    @Override
    int getLayoutId() {
        return R.layout.activity_answer;
    }

    @Override
    void getPreIntent() {
//        获取传递来的变量
        type = getIntent().getExtras().get("type").toString().trim();
    }

    @Override
    void initView() {

//      联网获取数据
        initNet(type);

        vp_answer.setOnPageChangeListener(new MyOnPageChangeListener());
        setChronometer();
    }

    /**
     * 设置计时器
     */
    private void setChronometer() {
        chronometer.setText(nowtime());
        chronometer.start();
        chronometer.setOnChronometerTickListener(this);
    }

    /**
     * 计时器规则
     *
     * @param chronometer
     */
    @Override
    public void onChronometerTick(Chronometer chronometer) {
        second++;
        if (second == 59) {
            minute++;
            second = 00;
        }
    }

    /**
     * 现在时间
     *
     * @return
     */
    private String nowtime() {
        if (second < 10) {
            return (minute + ":0" + second);
        } else {
            return (minute + ":" + second);
        }
    }

    /**
     * 初始化网络连接
     *
     * @param type
     */
    private void initNet(String type) {
        a = new ArrayList<>();
        fragmentlists = new ArrayList<>();
        LogUtils.e("initNet: 开始联网…………");
        //进度条对话框
        final ProgressDialog progressDialog = new ProgressDialog(AnswerActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("获取题目中...");
        progressDialog.show();
//        联网
        OkGo.get(Config.URL_GET_QUESTION)
                .params("type", type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogUtils.e("onSuccess: ========---------======" + s);
                        //gson解析
                        Gson gson = new Gson();
                        JsonQuestBean jsonQuestBean = gson.fromJson(s, JsonQuestBean.class);
                        messages = jsonQuestBean.getMessages();
                        for (int i = 0; i < messages.size(); i++) {
                            QuestBean questBeanQ = messages.get(i);
                            questBeanQ.setId(i);
                            fragmentlists.add(new AnswerFragment(questBeanQ));
                            LoveDao.insertLove(questBeanQ);
                            a.add(questBeanQ.getId() + "");
                            LogUtils.e(i + "   onSuccess    : " + questBeanQ.getId() + questBeanQ.getTitle());
                        }
//                        设置适配器
                        vp_answer.setAdapter(new MainAdapter(getSupportFragmentManager()));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        LogUtils.e("onError  ");
                    }
                });
        LogUtils.e("initNet: 联网结束…………");
    }


    @OnClick({R.id._btn_previous, R.id._btn_submit, R.id._btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            点击上一题按钮
            case R.id._btn_previous:
//                如果是第一题，则谈吐司提醒，否则上移一道题
                if (nowpager == 0) {
                    ToastUtils.showShort("已经到头啦!");
                } else {
                    vp_answer.setCurrentItem(--nowpager);
                }
                break;
//            点击提交按钮
            case R.id._btn_submit:
//                简答题不进行提交评分
                if (type.equals("3")) {
                    ToastUtils.showShort("简答题目前暂不支持评分");
                    return;
                }
//                否则初始化并展示提交对话框
                initAlertDialog();
                builder.show();
                break;
//            点击下一题按钮
            case R.id._btn_next:
//                如果是最后一题，则谈吐司提醒，否则下移一道题
                if (nowpager == fragmentlists.size()) {
                    ToastUtils.showShort("已经是最后一题了!");
                } else {
                    vp_answer.setCurrentItem(++nowpager);
                }
                break;

        }
    }

    /**
     * viewpager适配器
     */
    class MainAdapter extends FragmentPagerAdapter {

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }


        //获取条目
        @Override
        public Fragment getItem(int position) {
            return fragmentlists.get(position);
        }

        //数目
        @Override
        public int getCount() {
            return fragmentlists.size();
        }
    }


    // 弹出是否确认交卷的对话框
    private void initAlertDialog() {
        //新建对话框
        builder = new AlertDialog.Builder(AnswerActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否确定交卷?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                计算分数
                int grade = 0;
//                判断题
                if (type.equals("2")) {
                    for (int i = 0; i < messages.size(); i++) {
//                        查询
                        QuestBean questBeenA = LoveDao.queryLove(Integer.parseInt(a.get(i)));
//                        判断
                        if (questBeenA.getAnswer().equals("对") && questBeenA.getMyanswer().equals("A") || questBeenA.getAnswer().equals("错") && questBeenA.getMyanswer().equals("B")) {
                            grade += 20;
                        }
                    }
                }
//                选择题
                else {
                    for (int i = 0; i < messages.size(); i++) {
                        QuestBean questBeenA = LoveDao.queryLove(Integer.parseInt(a.get(i)));
                        if (questBeenA.getAnswer().equals(questBeenA.getMyanswer())) {
                            grade += 20;
                        }
                    }
                }

//                传递分数
                Intent intent = new Intent(AnswerActivity.this, GradeActivity.class);
                intent.putExtra("grade", "" + grade);
//                传递题目列表
                intent.putStringArrayListExtra("timu", a);
                startActivity(intent);
                finish();
            }

        });
        builder.setNegativeButton("取消", null);
    }

    /**
     * viewpager监听事件
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            nowpager = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}