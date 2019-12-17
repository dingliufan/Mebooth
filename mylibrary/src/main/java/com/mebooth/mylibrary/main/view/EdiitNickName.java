package com.mebooth.mylibrary.main.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import razerdp.basepopup.BasePopupWindow;

public class EdiitNickName extends BasePopupWindow {

    private Context context;
    private String nickName;
    private EditText nickNameEdit;
    private TextView sureUpdate;
    private TextView cancel;
    private MyListener myListener;

    public EdiitNickName(Context context,String nickName,MyListener myListener) {
        super(context);
        this.context = context;
        this.nickName = nickName;
        this.myListener = myListener;
        setPopupGravity(Gravity.CENTER);

        bindEvent();
    }

    private void bindEvent() {
        nickNameEdit = (EditText)findViewById(R.id.edit_nickname);
        sureUpdate = (TextView)findViewById(R.id.edit_sure);
        cancel = (TextView)findViewById(R.id.edit_cancel);

        nickNameEdit.setText(nickName);

        sureUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceFactory.getNewInstance()
                        .createService(YService.class)
                        .udateNickName(nickNameEdit.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CommonObserver<PublicBean>() {
                            @Override
                            public void onNext(PublicBean publicBean) {
                                super.onNext(publicBean);

                                if (null != publicBean && publicBean.getErrno() == 0) {

                                    ToastUtils.getInstance().showToast("昵称已修改");
                                    myListener.setNickName(nickNameEdit.getText().toString());
                                    dismiss();

                                } else if (null != publicBean && publicBean.getErrno() != 200) {

                                    ToastUtils.getInstance().showToast(TextUtils.isEmpty(publicBean.getErrmsg()) ? "数据加载失败" : publicBean.getErrmsg());
                                } else {

                                    ToastUtils.getInstance().showToast("数据加载失败");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);

                                ToastUtils.getInstance().showToast("数据加载失败");
                            }
                        });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public interface MyListener {

        void setNickName(String nickName);

    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.ediynickname_layout);
    }
}
