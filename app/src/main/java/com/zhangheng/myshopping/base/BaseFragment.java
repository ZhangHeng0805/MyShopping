package com.zhangheng.myshopping.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {
    //上下文
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mapView.onSaveInstanceState(savedInstanceState);
        return initView();

    }

    /*
    * 强制子类实现，实现子类特有的UI
    * */
    protected abstract View initView();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /*
    * 初始化数据，绑定数据，可以重写此方法
    * */
    protected void initData(){

    }


}
