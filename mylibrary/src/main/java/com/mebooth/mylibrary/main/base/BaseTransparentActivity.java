package com.mebooth.mylibrary.main.base;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.mebooth.mylibrary.main.utils.ActivityCollectorUtil;
import com.mebooth.mylibrary.main.view.GetDecorationPopup;
import com.mebooth.mylibrary.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

import static com.mebooth.mylibrary.utils.UIUtils.getContext;

/**
 * 全透明BaseTransparentActivity
 */
public abstract class BaseTransparentActivity extends AppCompatActivity {
    private TextMessage rongMsg;
    private JSONArray data;
    private GetDecorationPopup getDecorationPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        setStatusBar();
        initExtraBundle();
        initView();
        initListener();
        initData();
        initData(savedInstanceState);
        ActivityCollectorUtil.addActivity(this);
    }

    protected abstract int getContentViewId();

    protected void initExtraBundle() {

    }

    protected void initView() {

    }

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

    protected void initData() {

    }

    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    protected void setStatusBar() {

        //type1:展示顶部状态栏
//        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
//        StatusBarUtil.setLightMode(this); //黑色图标
//        StatusBarUtil.setDarkMode(this); //白色图标

        //type2：全透明
//        StatusBarUtil.setTransparentForImageViewInFragment(this, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (dispatchTouchEvent() && ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected boolean dispatchTouchEvent() {
        return true;
    }

    //根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    //多种隐藏软件盘方法的其中一种
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ActivityCollectorUtil.removeActivity(this);

    }

    protected void onResume() {
        super.onResume();
    }

    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            JSONObject json = (JSONObject) msg.obj;
            try {
                if(data.length()>1){
                    getDecorationPopup = new GetDecorationPopup(BaseTransparentActivity.this, json.getString("image"), json.getString("title"), "您获得了" + json.getString("title") + "等" + json.length() + "枚勋章");

                }else{
                    getDecorationPopup = new GetDecorationPopup(BaseTransparentActivity.this, json.getString("image"), json.getString("title"), json.getString("restrict"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getDecorationPopup.showPopupWindow();
        }

    };
}
