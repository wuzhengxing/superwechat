package cn.ucai.superwechat.net;

import android.content.Context;
import android.media.MediaPlayer;

import com.hyphenate.chat.EMGroup;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by Administrator on 2017/2/8.
 */

public class NetDao {
    public static void register(Context context, String userName, String nick, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,userName)
                .addParam(I.User.NICK,nick)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(String.class)
                .post()
                .execute(listener);
    }
    public static void unRegister(Context context, String userName, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void login(Context context, String userName,String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,userName)
                .addParam(I.User.PASSWORD,MD5.getMessageDigest(password))
                .targetClass(String.class)
                .execute(listener);
    }
    public static void getUserInfoByUserName(Context context, String userName,OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void updateUserNick(Context context, String userName,String userNick,OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME,userName)
                .addParam(I.User.NICK,userNick)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void updateUserAvatar(Context context, String userName, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID,userName)
                .addParam(I.AVATAR_TYPE,I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }
    public static void addContact(Context context, String userName, String cname, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME,userName)
                .addParam(I.Contact.CU_NAME,cname)
                .targetClass(String.class)
                .execute(listener);
    }

    public static void loadContact(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void removeContact(Context context, String userName, String cname, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_CONTACT)
                .addParam(I.Contact.USER_NAME,userName)
                .addParam(I.Contact.CU_NAME,cname)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void createGroup(Context context, EMGroup group, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,group.getGroupId())
                .addParam(I.Group.NAME,group.getGroupName())
                .addParam(I.Group.DESCRIPTION,group.getDescription())
                .addParam(I.Group.OWNER,group.getOwner())
                .addParam(I.Group.IS_PUBLIC,String.valueOf(group.isPublic()))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(group.isAllowInvites()))
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }
    public static void addGroupMembers(Context context, String members, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.USER_NAME,members)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void removeGroupMembers(Context context, String hxId, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_GROUP_MEMBER)
                .addParam(I.Member.GROUP_HX_ID,hxId)
                .addParam(I.Member.USER_NAME,username)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void updateGroupName(Context context, String hxId, String newname, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_GROUP_NAME)
                .addParam(I.Member.GROUP_HX_ID,hxId)
                .addParam(I.Member.USER_NAME,newname)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void deleteGroup(Context context, String hxId, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_GROUP)
                .addParam(I.Member.GROUP_HX_ID,hxId)
                .targetClass(String.class)
                .execute(listener);
    }
}
