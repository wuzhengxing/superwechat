package cn.ucai.superwechat.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.domain.Result;
import cn.ucai.superwechat.net.NetDao;
import cn.ucai.superwechat.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.utils.ResultUtils;

import static cn.ucai.superwechat.R.id.username;

public class UserProfileActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "UserProfileActivity";
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    private ProgressDialog dialog;
    String username;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.bind(this);
        DisplayUtils.initBackWithTitle(this, "个人资料");
        initListener();
    }


    private void initListener() {
        username = EMClient.getInstance().getCurrentUser();
        tvUsername.setText(username);
        EaseUserUtils.setAppUserNick(username, tvNick);
        EaseUserUtils.setAppUserAvatar(this, username, ivAvatar);
        asyncFetchUserInfo(username);


    }


    public void asyncFetchUserInfo(String username) {
        NetDao.getUserInfoByUserName(this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String str) {
                if (str != null) {
                    Result result = ResultUtils.getResultFromJson(str, User.class);
                    if (result != null && result.isRetMsg()) {
                        Log.e(TAG, "result:" + result);
                        User user = (User) result.getRetData();
                        if (user != null) {
                            SuperWeChatHelper.getInstance().saveAppContact(user);
                            tvNick.setText(user.getMUserNick());
                            if (!TextUtils.isEmpty(user.getAvatar())) {
                                Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.default_hd_avatar).into(ivAvatar);
                            } else {
                                Glide.with(UserProfileActivity.this).load(R.drawable.default_hd_avatar).into(ivAvatar);
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error:" + error);
            }
        });

    }


    private void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, I.REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        NetDao.updateUserNick(this, username, nickName, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String str) {
                dialog.dismiss();
                if (str != null) {
                    Result result = ResultUtils.getResultFromJson(str, User.class);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            User user = (User) result.getRetData();
                            if (user != null) {
                                Log.e(TAG, "user:" + user);
                                CommonUtils.showShortToast(R.string.toast_updatenick_success);
                                PreferenceManager.getInstance().setCurrentUserNick(nickName);
                                SuperWeChatHelper.getInstance().saveAppContact(user);
                                tvNick.setText(nickName);
                            }
                        } else {
                            if (result.getRetCode() == I.MSG_USER_SAME_NICK) {
                                CommonUtils.showShortToast("昵称未修改");
                            } else {
                                CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                            }
                        }
                    } else {
                        CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                    }
                } else {
                    CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error:" + error);
                dialog.dismiss();
                CommonUtils.showShortToast(R.string.toast_updatenick_fail);
            }
        });
       /* new Thread(new Runnable() {

            @Override
            public void run() {
                boolean updatenick = SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
                if (UserProfileActivity.this.isFinishing()) {
                    return;
                }
                if (!updatenick) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                    .show();
                            tvNick.setText(nickName);
                        }
                    });
                }
            }
        }).start();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case I.REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case I.REQUESTCODE_CUTTING:
                if (data != null) {
                    //setPicToView(data);
                    uploadAppUserAvatar(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, I.REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            ivAvatar.setImageDrawable(drawable);
            uploadUserAvatar(Bitmap2Bytes(photo));
            uploadAppUserAvatar(picdata);
        }

    }

    private void uploadAppUserAvatar(Intent picdata) {
        File file = saveBitmapFile(picdata);
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        NetDao.updateUserAvatar(this, username, file, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String str) {
                dialog.dismiss();
                if (str != null) {
                    Result result = ResultUtils.getResultFromJson(str, User.class);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            User user = (User) result.getRetData();
                            PreferenceManager.getInstance().setCurrentUserAvatar(user.getAvatar());
                            SuperWeChatHelper.getInstance().saveAppContact(user);
                            EaseUserUtils.setAppUserAvatar(UserProfileActivity.this,username,ivAvatar);
                            CommonUtils.showShortToast(R.string.toast_updatephoto_success);
                        } else {
                            if (result.getRetCode() == I.MSG_REGISTER_UPLOAD_AVATAR_FAIL) {
                                CommonUtils.showShortToast("上传头像失败");
                            } else {
                                CommonUtils.showShortToast(R.string.toast_updatephoto_fail);
                            }
                        }
                    }else {
                        CommonUtils.showShortToast(R.string.toast_updatephoto_fail);
                    }
                } else {
                    CommonUtils.showShortToast(R.string.toast_updatephoto_fail);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error:" + error);
                CommonUtils.showShortToast(R.string.toast_updatephoto_fail);
                dialog.dismiss();
            }
        });
    }

    private File saveBitmapFile(Intent picdata) {
        Bundle extras=picdata.getExtras();
        if(extras!=null){
            Bitmap bitmap = extras.getParcelable("data");
            String imagePath= EaseImageUtils.getImagePath(username+I.AVATAR_SUFFIX_JPG);
            File file = new File(imagePath);//将要保存头像的路径
            Log.e(TAG, "file:" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }

        return null;
    }

    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();

        dialog.show();
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @OnClick({R.id.layout_avatar, R.id.layout_nick, R.id.layout_username})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_avatar:
                uploadHeadPhoto();
                break;
            case R.id.layout_nick:
                final EditText editText = new EditText(this);
                new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();

                break;
            case R.id.layout_username:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        asyncFetchUserInfo(EMClient.getInstance().getCurrentUser());
    }
}
