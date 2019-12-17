package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.fragment.NewMainFragment;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class FriendActivity extends BaseFragment {
    private Fragment mConversationFragment = null;
    private ImageView back;
    private TextView title;
    private TextView right;

    public static FriendActivity newInstance() {
        return new FriendActivity();
    }


    public void switchContent() {
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
}
