package com.mebooth.mylibrary.main.base;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mebooth.mylibrary.utils.StringUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {


    protected String TAG = getClass().getSimpleName();
    protected View rootView;
    protected Activity activity;
    private Toast mToast = null;
    private Unbinder mUnbinder;

    public BaseFragment() {
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        mToast = Toast.makeText(activity,"", Toast.LENGTH_SHORT);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(getLayoutResId(), container, false);
            initButterKnife();
            setStatusBar();
            initView(rootView);
            initExtraBundle();
            initData(savedInstanceState);
            initListener();
        }


        return rootView;
    }

    /**
     * 获取布局的Id
     */
    protected abstract int getLayoutResId();

    /**
     * 查找控件
     */
    protected abstract void initView(View view);

    protected void setStatusBar() {

        //type1:展示顶部状态栏
//        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
//        StatusBarUtil.setLightMode(this); //黑色图标
//        StatusBarUtil.setDarkMode(this); //白色图标

        //type2：全透明
//        StatusBarUtil.setTransparentForImageViewInFragment(this, null);
    }

    private void initButterKnife() {

        //绑定并且返回一个Unbinder值用来解绑

        mUnbinder = ButterKnife.bind(this, rootView);

    }


    /**
     * 获取bundle数据
     */
    protected void initExtraBundle() {
    }

    /**
     * 初始化数据操作
     *
     * @param savedInstanceState
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 设置监听
     */
    protected void initListener() {
    }


    protected final void  toast(String msg){
        if (mToast!=null&& !StringUtil.isEmptyWithTrim(msg)){
            mToast.setText(msg);
            mToast.show();
        }
    }
    protected final void toast(int strId){
        if (mToast!=null){
            mToast.setText(strId);
            mToast.show();
        }
    }

    @Override
    public void onDestroyView() {
         super.onDestroyView();

        //解绑
        try {

            mUnbinder.unbind();
        }catch (Exception e){

        }

    }

}
