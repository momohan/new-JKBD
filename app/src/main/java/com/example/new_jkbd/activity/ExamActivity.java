package com.example.new_jkbd.activity;

import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.new_jkbd.ExamApplication;
import com.example.new_jkbd.R;
import com.example.new_jkbd.bean.Exam;
import com.example.new_jkbd.bean.ExamInfo;
import com.example.new_jkbd.biz.ExamBiz;
import com.example.new_jkbd.biz.IExamBiz;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by &&&&& on 2017/7/4.
 */

public class ExamActivity extends AppCompatActivity {
    TextView tvExamInfo,tvExamTitle,tv_op1,tv_op2,tv_op3,tv_op4,tvLoadingText,tvNo;
    CheckBox cb01,cb02,cb03,cb04;
    ImageView imageView;
    IExamBiz biz;
    boolean isLoadExamInfo=false;
    boolean isLoadQuestion=false;
    LinearLayout LayoutLoading,Layout03,Layout04;
    boolean isLoadExamInfoReceiver=false;
    boolean isLoadQuestionReceiver=false;
    ProgressBar dialog;

    LoadExamBroadcast mLoadExamBroadcast;
    LoadQuestionBroadcast mLoadQuestionBroadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        mLoadExamBroadcast=new LoadExamBroadcast();
        mLoadQuestionBroadcast=new LoadQuestionBroadcast();
        setListenter();
        initView();
        biz=new ExamBiz();
        loadData();
    }

    private void setListenter() {
        registerReceiver(mLoadExamBroadcast,new IntentFilter(ExamApplication.LOAD_EXAM_INFO));
        registerReceiver(mLoadQuestionBroadcast,new IntentFilter(ExamApplication.LOAD_EXAM_QUESTION));
    }

    private void loadData() {
        LayoutLoading.setEnabled(false);
        dialog.setVisibility(View.VISIBLE);
        tvLoadingText.setText("试题下载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                biz.beginExam();
            }
        }).start();
    }


    private void initView() {
        LayoutLoading=(LinearLayout)findViewById(R.id.layout_loading);
        Layout03=(LinearLayout)findViewById(R.id.Layout03);
        Layout04=(LinearLayout)findViewById(R.id.Layout04);
        tvExamInfo= (TextView) findViewById(R.id.tv_examinfo);
        tvExamTitle= (TextView) findViewById(R.id.tv_exam_title);
        tvNo=(TextView)findViewById(R.id.tv_exam_no);
        tv_op1= (TextView) findViewById(R.id.tv_op1);
        tv_op2= (TextView) findViewById(R.id.tv_op2);
        tv_op3= (TextView) findViewById(R.id.tv_op3);
        tv_op4= (TextView) findViewById(R.id.tv_op4);
        cb01= (CheckBox) findViewById(R.id.cb01);
        cb02= (CheckBox) findViewById(R.id.cb02);
        cb03= (CheckBox) findViewById(R.id.cb03);
        cb04= (CheckBox) findViewById(R.id.cb04);
        imageView= (ImageView) findViewById(R.id.im_exam_image);
        tvLoadingText= (TextView) findViewById(R.id.tv_loadingtext);
        dialog= (ProgressBar) findViewById(R.id.load_dialog);
        LayoutLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    private void initData() {
        if(isLoadQuestionReceiver&&isLoadExamInfoReceiver)
        {
            if(isLoadQuestion&&isLoadExamInfo)
            {
                LayoutLoading.setVisibility(View.GONE);
                ExamInfo examInfo=ExamApplication.getInstance().getmExamInfo();
                if(examInfo!=null)
                {
                    showData(examInfo);
                }

                    showExam(biz.getExam());

            }
            else
            {
                LayoutLoading.setEnabled(true);
                dialog.setVisibility(View.GONE);
                tvLoadingText.setText("下载失败，点击重新下载！");
            }

        }

    }

    private void showExam(Exam exam) {
        if(exam!=null)
        {
            tvNo.setText(biz.getExamIndex());
            tvExamTitle.setText(exam.getQuestion());
            tv_op1.setText(exam.getItem1());
            tv_op2.setText(exam.getItem2());
            tv_op3.setText(exam.getItem3());
            tv_op4.setText(exam.getItem4());
            Layout03.setVisibility(exam.getItem3().equals("")?View.GONE:View.INVISIBLE);
            cb03.setVisibility(exam.getItem3().equals("")?View.GONE:View.INVISIBLE);
            Layout04.setVisibility(exam.getItem3().equals("")?View.GONE:View.INVISIBLE);
            cb04.setVisibility(exam.getItem3().equals("")?View.GONE:View.INVISIBLE);
            if(exam.getUrl()!=null && !exam.getUrl().equals(""))
            {
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(ExamActivity.this)
                        .load(exam.getUrl())
                        .into(imageView);
            }
            else
            {
                imageView.setVisibility(View.GONE);
            }
        }
    }

    private void showData(ExamInfo examInfo) {
        tvExamInfo.setText(examInfo.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLoadExamBroadcast!=null)
        {
            unregisterReceiver(mLoadExamBroadcast);
        }
        if(mLoadQuestionBroadcast!=null)
        {
            unregisterReceiver(mLoadQuestionBroadcast);
        }
    }

    public void preQuestion(View view) {
        showExam(biz.preQuestion());
    }

    public void nextQuestion(View view) {
        showExam(biz.nextQuestion());
    }

    class LoadExamBroadcast extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSuccess=intent.getBooleanExtra(ExamApplication.LOAD_DATA_SUCCESS,false);
            if(isSuccess)
            {
                isLoadExamInfo=true;
            }
            isLoadExamInfoReceiver=true;
            initData();
        }
    }
    class LoadQuestionBroadcast extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isSuccess=intent.getBooleanExtra(ExamApplication.LOAD_DATA_SUCCESS,false);
            if(isSuccess)
            {
                isLoadQuestion=true;
            }
            isLoadQuestionReceiver=true;
            initData();
        }
    }
}
