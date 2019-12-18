package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetIMUserInfoJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ConversationListActivity extends BaseTransparentActivity {


    private String uids = "";

    @Override
    protected int getContentViewId() {
        return R.layout.conversationlist;
    }

    @Override
    protected void initData() {
        super.initData();

        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {

            @Override

            public void onSuccess(List<Conversation> conversations) {

                for (int i = 0; i < conversations.size(); i++) {
                    uids += "," + conversations.get(i);
                }

                getIMInfo(uids);

                String si = conversations.get(0).getTargetId();

                Conversation.ConversationType type = conversations.get(0).getConversationType();

                //从本地缓存中获取（只针对IMKit）

                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(si);

            }

            @Override

            public void onError(RongIMClient.ErrorCode errorCode) {

            }

        });

    }

    private void getIMInfo(String uidStr) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getIMUserInfo(uidStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetIMUserInfoJson>() {
                    @Override
                    public void onNext(GetIMUserInfoJson getIMUserInfoJson) {
                        super.onNext(getIMUserInfoJson);

                        if (null != getIMUserInfoJson && getIMUserInfoJson.getErrno() == 0) {
                            ArrayList<GetIMUserInfoJson.IMUserData.IMUser> users = getIMUserInfoJson.getData().getUsers();

                            for (int j = 0; j < users.size(); j++) {
                                UserInfo userInfo = new UserInfo(String.valueOf(users.get(j).getUid()), users.get(j).getNickname(), Uri.parse(users.get(j).getAvatar()));
                                RongIM.getInstance().refreshUserInfoCache(userInfo);
                            }

                        } else if (null != getIMUserInfoJson && getIMUserInfoJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getIMUserInfoJson && getIMUserInfoJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getIMUserInfoJson.getErrmsg()) ? "数据加载失败" : getIMUserInfoJson.getErrmsg());
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
}
