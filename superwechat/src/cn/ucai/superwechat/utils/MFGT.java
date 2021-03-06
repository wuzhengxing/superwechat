package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.net.OnCompleteListener;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.AddFriendActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.FriendProfileActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;


/**
 * Created by Administrator on 2017/1/10.
 */

public class MFGT {
    public static void finish(Activity activity){
        activity.finish();
        activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
    public static void startActivity(Activity context, Class<?> cls){
        context.startActivity(new Intent(context,cls));
        context.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);

    }
    public static void startActivity(Activity context, Intent intent){
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);

    }

   public static void gotoLogin(Activity activity) {
       startActivity(activity, LoginActivity.class);
   }
   public static void gotoRegister(Activity activity) {
       startActivity(activity, RegisterActivity.class);
   }

    public static void gotoSetting(FragmentActivity activity) {
        startActivity(activity, SettingActivity.class);
    }

    public static void gotoLoginCleanTask(Activity activity) {
        startActivity(activity,new Intent(activity,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public static void gotoUserProfile(Activity activity) {
        startActivity(activity, UserProfileActivity.class);
    }

    public static void gotoAddContact(Activity activity) {
        startActivity(activity, AddContactActivity.class);
    }

    public static void gotoFriend(Activity activity, User user) {
       startActivity(activity,new Intent(activity, FriendProfileActivity.class).putExtra(I.User.TABLE_NAME,user));
    }
    public static void gotoFriend(Activity activity, String username) {
        if (username.equals(EMClient.getInstance().getCurrentUser())) {
            gotoUserProfile(activity);
        }else {
            startActivity(activity,new Intent(activity, FriendProfileActivity.class).putExtra(I.User.USER_NAME,username));
        }
    }

    public static void gotoAddFriend(Activity activity, String mUserName) {
        startActivity(activity,new Intent(activity, AddFriendActivity.class).putExtra(I.User.USER_NAME,mUserName));
    }

    public static void gotoChat(Activity activity, String mUserName) {
        startActivity(activity,new Intent(activity, ChatActivity.class).putExtra("userId", mUserName));
    }

    public static void gotoMain(Activity activity,boolean isChat) {
        startActivity(activity,new Intent(activity,MainActivity.class).
                putExtra(I.BACK_MAIN_FROM_CHAT,isChat));
    }
}
