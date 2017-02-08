package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import cn.ucai.superwechat.R;


/**
 * Created by Administrator on 2017/1/17.
 */

public class DisplayUtils {
    public static void  initBack(final Activity activity){
      activity.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              activity.finish();
          }
      });
    }
    public static void  initBackWithTitle(final Activity activity, String title){
       TextView tv = (TextView) activity.findViewById(R.id.tvTitle);
        tv.setText(title);
        initBack(activity);

    }
}
