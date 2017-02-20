package cn.ucai.superwechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.domain.Result;
import cn.ucai.superwechat.net.NetDao;
import cn.ucai.superwechat.net.OnCompleteListener;
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class FriendProfileActivity extends BaseActivity {
    private static final String TAG = "FriendProfileActivity";
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.bt_send_msg)
    Button btSendMsg;
    @BindView(R.id.bt_add_card)
    Button btAddCard;
    @BindView(R.id.bt_record_video)
    Button btRecordVideo;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        DisplayUtils.initBackWithTitle(this,"详细信息");
        initData();
    }

    private void initData() {
         user = (User) getIntent().getSerializableExtra(I.User.TABLE_NAME);
        if (user != null) {
           showUserInfo();
        } else {
            String username = getIntent().getStringExtra(I.User.USER_NAME);
            if(username==null){
                MFGT.finish(this);
            }else {
                syncUserInfo(username);
            }
        }

    }

    private void syncUserInfo(String username) {
        NetDao.getUserInfoByUserName(this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String str) {
                if(str!=null){
                    Result result = ResultUtils.getResultFromJson(str, User.class);
                    if(result!=null){
                        if (result.isRetMsg()) {
                            User u = (User) result.getRetData();
                            if(u!=null){
                                user=u;
                                showUserInfo();
                                Log.e(TAG, "user:" + user);
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void showUserInfo() {
        tvNick.setText(user.getMUserNick());
        EaseUserUtils.setAppUserAvatarBypath(this,user.getAvatar(),ivAvatar,null);
        tvUsername.setText("微信号:"+user.getMUserName());
        if(isFriend()){
            btRecordVideo.setVisibility(View.VISIBLE);
            btSendMsg.setVisibility(View.VISIBLE);
        }else {
            btAddCard.setVisibility(View.VISIBLE);
        }
    }
    public boolean isFriend(){
        User u= SuperWeChatHelper.getInstance().getAppContactList().get(user.getMUserName());
        if(u==null){
            return false;
        }else {
            SuperWeChatHelper.getInstance().saveAppContact(user);
            return true;
        }
    }

    @OnClick({R.id.bt_send_msg, R.id.bt_add_card, R.id.bt_record_video})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send_msg:
                MFGT.gotoChat(this,user.getMUserName());
                break;
            case R.id.bt_add_card:
                MFGT.gotoAddFriend(this,user.getMUserName());
                break;
            case R.id.bt_record_video:
                startActivity(new Intent(this, VideoCallActivity.class)
                        .putExtra("username", user.getMUserName())
                        .putExtra("isComingCall", false));
        }
    }
}
