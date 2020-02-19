package com.mebooth.mylibrary.main.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.home.activity.NewsPublishActivity;
import com.mebooth.mylibrary.main.home.activity.PublishActivity;

public class ConfirmPopWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private View ll_chat, ll_friend;

    public ConfirmPopWindow(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        ll_chat = view.findViewById(R.id.ll_chat);//发起群聊
        ll_friend = view.findViewById(R.id.ll_friend);//添加好友
        ll_chat.setOnClickListener(this);
        ll_friend.setOnClickListener(this);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        this.setWidth((int) (d.widthPixels * 0.35));
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.8f);//0.0-1.0
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) context, 1f);
            }
        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
        //弹窗位置设置
//        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 10);
//        showAsDropDown(view, -view.getWidth()-110, 10);
//        showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差

        if (Build.VERSION.SDK_INT >= 24) {
//            Rect visibleFrame = new Rect();
//            view.getGlobalVisibleRect(visibleFrame);
//            int height = view.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
//            setHeight(height);
            showAtLocation(view,Gravity.TOP|Gravity.RIGHT ,22, 180);
        } else {
            showAsDropDown(view, -110, 0);
        }

    }
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.ll_chat) {

            Intent intent = new Intent(context, PublishActivity.class);
            context.startActivity(intent);
        } else if (i == R.id.ll_friend) {
            Intent intent = new Intent(context, NewsPublishActivity.class);
            context.startActivity(intent);
        }
    }
}
