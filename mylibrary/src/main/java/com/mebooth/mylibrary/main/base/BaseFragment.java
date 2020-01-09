package com.mebooth.mylibrary.main.base;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mebooth.mylibrary.main.view.GetDecorationPopup;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public abstract class BaseFragment extends Fragment {


    protected String TAG = getClass().getSimpleName();
    protected View rootView;
    protected Activity activity;
    private Toast mToast = null;
    private Unbinder mUnbinder;

    private TextMessage rongMsg;
    private JSONArray data;
    private GetDecorationPopup getDecorationPopup;

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

        /**
         * 设置接收消息的监听器。
         *
         * 所有接收到的消息、通知、状态都经由此处设置的监听器处理。包括私聊消息、群组消息、聊天室消息以及各种状态。
         */
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageWrapperListener() {
            @Override
            public boolean onReceived(final Message message, final int left, boolean hasPackage, boolean offline) {

                if (message.getSenderUserId().equals("12358336")) {
                    rongMsg = (TextMessage) message.getContent();
                    JSONObject obj = null;//最外层的JSONObject对象
                    JSONObject content = null;
                    try {
                        obj = new JSONObject(rongMsg.getContent());
                        content = obj.getJSONObject("extends");//通过user字段获取其所包含的JSONObject对象
                        data = content.getJSONArray("content");

                        if (data.length() > 1) {
                            JSONObject value = data.getJSONObject(0);
//                            String title = ;
                            if (getDecorationPopup != null) {
                                getDecorationPopup.dismiss();
                                getDecorationPopup = null;
                            }
                            android.os.Message msg = new android.os.Message();
                            msg.obj = value;
                            mhandler.sendMessage(msg);

                        } else {
                            JSONObject value = data.getJSONObject(0);
//                            String title = ;
                            if (getDecorationPopup != null) {
                                getDecorationPopup.dismiss();
                                getDecorationPopup = null;
                            }
                            android.os.Message msg = new android.os.Message();
                            msg.obj = value;
                            mhandler.sendMessage(msg);

                        }

//                        GetDecorationPopup getDecorationPopup = new GetDecorationPopup();
//                        ToastUtils.getInstance().showToast(content.getString("nickname"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(1000);//休眠3秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            /**
                             * 要执行的操作
                             */
                            RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, "12358336");

                        }
                    }.start();

                }
                return false;
            }
        });

    }

    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            JSONObject json = (JSONObject) msg.obj;
            try {
                if(data.length()>1){
                    getDecorationPopup = new GetDecorationPopup(activity, json.getString("image"), json.getString("title"), "您获得了" + json.getString("title") + "等" + json.length() + "枚勋章");

                }else{
                    getDecorationPopup = new GetDecorationPopup(activity, json.getString("image"), json.getString("title"), json.getString("restrict"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getDecorationPopup.showPopupWindow();
        }

    };


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
