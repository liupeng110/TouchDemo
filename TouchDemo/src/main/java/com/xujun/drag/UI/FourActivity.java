package com.xujun.drag.UI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xujun.drag.ItemAdapter;
import com.xujun.drag.R;
import com.xujun.drag.base.BaseFragmentAdapter;
import com.xujun.drag.base.RecyclerUtils;

import java.util.ArrayList;
import java.util.List;

public class FourActivity extends AppCompatActivity {
    ViewPager mViewPager;
    TextView mTextView;

    RecyclerView mRecyclerView;
    private List<Fragment> mFragments;

    private ArrayList<String> mList;
    private ItemAdapter mItemAdapter;

    ScrollView mNoHorizontalScrollView;
    private boolean first = true;
    private BaseFragmentAdapter mBaseFragmentAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);
        initView();
        initListener();
        initData();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && first) { first = false; mNoHorizontalScrollView.scrollTo(0, 0); }//首次 滑动到起点处
    }
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTextView = (TextView) findViewById(R.id.tv_page);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mNoHorizontalScrollView = (ScrollView) findViewById(R.id.NoHorizontalScrollView);
    }
    private void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onPageSelected(int position) {
                mTextView.setText(String.format("%d/8", position + 1));
            }
        });
    }
    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 30; i++) { @SuppressLint("DefaultLocale")
             String s = String.format("我是第%d个测试Item", i);
            mList.add(s);
        }
        mItemAdapter = new ItemAdapter(this, mList);
        RecyclerUtils.init(mRecyclerView);
        mRecyclerView.setAdapter(mItemAdapter);

        mFragments = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ImageFragment imageFragment = ImageFragment.newInstance(R.drawable.huoying);
            mFragments.add(imageFragment);//8个fragment
        }

        mBaseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mBaseFragmentAdapter);

    }      //初始化数据结束


}
