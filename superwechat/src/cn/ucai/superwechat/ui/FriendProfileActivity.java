package cn.ucai.superwechat.ui;

import android.app.Activity;
import android.os.Bundle;
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
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.MFGT;

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
         user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if (user != null) {
           showUserInfo();
        } else {
            MFGT.finish(this);
        }

    }

    private void showUserInfo() {
        tvNick.setText(user.getMUserNick());
        EaseUserUtils.setAppUserAvatarBypath(this,user.getAvatar(),ivAvatar);
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
                break;
            case R.id.bt_add_card:
                MFGT.gotoAddFriend(this,user.getMUserName());
                break;
            case R.id.bt_record_video:
                break;
        }
    }
}
