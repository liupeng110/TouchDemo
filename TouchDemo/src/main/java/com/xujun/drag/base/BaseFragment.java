package com.xujun.drag.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.xujun.drag.utils.LUtils;

/**
 * @ explain:
 * @ author：xujun on 2016-8-11 16:16
 * @ email：gdutxiaoxu@163.com
 */
public abstract class BaseFragment extends Fragment {
    protected View mView;

    protected boolean isViewInitiated;//view是否被初始化
    protected boolean mIsVisibleToUser;//对用户是否可见
    protected boolean isDataInitiated;//数据是否初始化
    private Context mContext;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override  public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);  }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.i("进入"+this.getClass().getSimpleName()+","+"<<<<<<<<<<<<   onCreateView");
        if(mView==null){
            mView=View.inflate(mContext,getLayoutId(),null);
        }else{
            ViewGroup parent =(ViewGroup) mView.getParent();
            if(parent!=null){
                parent.removeView(mView);
            }
        }
        return mView;
    }

    protected abstract void initView(View view);

    protected abstract int getLayoutId();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LUtils.i(this.getClass().getSimpleName()+"<<<<<<<<<<<<   onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        initView(mView);
        initListener();
        initData();
        prepareFetchData();
    }

    protected void initData() {
    }

    protected void initListener() {
    }

    @Override public void onPause() {
        super.onPause();
        Logger.i("进入"+this.getClass().getSimpleName()+"<<<<<<<<<<<<   onPause");
    }

    @Override public void onResume() {
        super.onResume();
        Logger.i("进入"+this.getClass().getSimpleName()+"<<<<<<<<<<<<   onResume");
    }

    @Override public void onStop() {
        super.onStop();
        Logger.i("进入"+this.getClass().getSimpleName()+"<<<<<<<<<<<<   onStop");
         }

    @Override  public void onDestroyView() {
        super.onDestroyView();
        Logger.i("进入"+this.getClass().getSimpleName()+"<<<<<<<<<<<<   onDestroyView");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Logger.i("进入"+this.getClass().getSimpleName()+"<<<<<<<<<<<<   setUserVisibleHint"+isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        this.mIsVisibleToUser = isVisibleToUser;
        prepareFetchData();
    }

    public abstract void fetchData();//取数据

    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }//准备取数据

    public boolean prepareFetchData(boolean forceUpdate) {
        if (mIsVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            fetchData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }//更新数据

    @Override  public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.i("进入"+this.getClass().getSimpleName()+"<<<<<<<<<<<<   onHiddenChanged"+hidden);
    }
}