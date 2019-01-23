package com.zii.fastapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.zii.base.util.SysActionUtils;
import com.zii.base.util.ToastUtils;
import com.zii.base.widget.dialog.PopupDialog;
import com.zii.base.widget.dialog.PopupItemsDialog;
import com.zii.base.widget.dialog.TranslucenceDialog;

import static android.app.Activity.RESULT_OK;

/**
 * TestCollection
 * Create by zii at 2019/1/23.
 */
public class TestCollection {

  private Activity mActivity;

  private Uri mPictureUri;
  private String mPicturePath;

  public TestCollection(Activity activity) {
    mActivity = activity;
  }

  public void testQuick(View view) {
    new AlertDialog.Builder(mActivity)
        .setMessage("测试内容拉")
        .show();
  }

  public void testTakePicture(View view) {
    Object[] objects = SysActionUtils.takePictureWithoutSave(mActivity, 111);
    if (objects != null) {
      mPictureUri = (Uri) objects[0];
      mPicturePath = (String) objects[1];
    } else {
      mPictureUri = null;
      mPicturePath = null;
    }
  }

  public void testPopupDialog(View view) {
    PopupItemsDialog dialog = new PopupItemsDialog(mActivity, 180, 0)
        .addItem("哈哈", new TranslucenceDialog.OnClickChild() {
          @Override
          public void onClick(TranslucenceDialog dialog, View view) {
            dialog.dismiss();
            ToastUtils.showShort(((TextView) view).getText());
          }
        })
        .addItem("Gou", new TranslucenceDialog.OnClickChild() {
          @Override
          public void onClick(TranslucenceDialog dialog, View view) {
            dialog.dismiss();
            ToastUtils.showShort(((TextView) view).getText());
          }
        });
    dialog.setLocation(view, "bottom", PopupDialog.ALIGN_LEFT, 0, 0);
    dialog.show();
  }

  public Object onReceiverResult(int requestCode, int resultCode, @Nullable Intent data) {
    switch (requestCode) {
      case 111:
        if (resultCode == RESULT_OK) {

          Log.d("zii-",
              "uri:" + mPictureUri + "       " + mPicturePath);
          SysActionUtils.galleryAddPicture(mPicturePath);
          return mPictureUri;
        }
        break;
    }
    return null;
  }
}
