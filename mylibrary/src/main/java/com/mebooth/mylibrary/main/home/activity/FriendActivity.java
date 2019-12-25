package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.bean.GetIMUserInfoJson;
import com.mebooth.mylibrary.main.home.fragment.NewMainFragment;
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
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class FriendActivity extends BaseFragment {
    private Fragment mConversationFragment = null;
    private ImageView back;
    private TextView title;
    private TextView right;
    private String uids = "";
    private UserInfo userInfo;

    public static FriendActivity newInstance() {
        return new FriendActivity();
    }
    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
        StatusBarUtil.setLightMode(getActivity()); //黑色图标
    }

    public void switchContent() {

//        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
//
//            @Override
//
//            public void onSuccess(List<Conversation> conversations) {
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < conversations.size(); i++) {
//                    if (sb.length() > 0) {//该步即不会第一位有逗号，也防止最后一位拼接逗号！
//                        sb.append(",");
//                    }
//                    sb.append(conversations.get(i).getTargetId());
//                }
//
//                getIMInfo(sb.toString());
//
//            }
//
//            @Override
//
//            public void onError(RongIMClient.ErrorCode errorCode) {
//
//            }
//
//        });

        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String userId) {
                return getIMInfo(userId);//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
            }

        }, true);

        //必需继承FragmentActivity,嵌套fragment只需要这行代码
        getChildFragmentManager().beginTransaction().replace(R.id.onef, initConversationList()).commitAllowingStateLoss();
    }

    private Fragment initConversationList() {
        /**
         * appendQueryParameter对具体的会话列表做展示
         */
        if (mConversationFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationList")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                    // .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                    //.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务号
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置私聊会是否聚合显示
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return mConversationFragment;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.friend_layout;
    }

    @Override
    protected void initView(View view) {
        title = view.findViewById(R.id.public_title);
        back = view.findViewById(R.id.public_back);
        right = view.findViewById(R.id.public_right);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        title.setText("朋友");
        back.setVisibility(View.GONE);
        right.setVisibility(View.VISIBLE);
        right.setText("我关注的人");

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),FriendListActivity.class);
                startActivity(intent);

            }
        });
        switchContent();
    }

    private UserInfo getIMInfo(String uidStr) {

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
                                userInfo = new UserInfo(String.valueOf(users.get(j).getUid()), users.get(j).getNickname(), Uri.parse(users.get(j).getAvatar()));
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
            return userInfo;

    }

}
