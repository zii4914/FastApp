package com.zii.base.util;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;

/**
 * DialogUtils
 * Create by zii at 2019/1/23.
 */
public class DialogUtils {

  public static void alert(String message) {
    alert(null, message);
  }

  public static void alert(String title, String message) {
    alert(ActivityUtils.getTopActivity(), title, message);
  }

  public static void alert(Context context, String title, String message) {
    alert(context, title, message, "确定", null);
  }

  public static void alert(Context context, String title, String message, String positive,
    DialogInterface.OnClickListener positiveClickListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    if (!TextUtils.isEmpty(title)) {
      builder.setTitle(title);
    }
    builder.setMessage(message);
    builder.setPositiveButton(positive, positiveClickListener);
    if (positiveClickListener != null) {
      builder.setNegativeButton("取消", null);
    }
    builder.show();
  }
}
