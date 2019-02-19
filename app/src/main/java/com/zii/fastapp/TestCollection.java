package com.zii.fastapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.zii.base.util.AppUtils;
import com.zii.base.util.SysActionUtils;
import com.zii.base.util.TimeUtils;
import com.zii.base.util.ToastUtils;
import com.zii.base.widget.dialog.PopupDialog;
import com.zii.base.widget.dialog.PopupItemsDialog;
import com.zii.base.widget.dialog.TranslucenceDialog;
import com.zii.base.widget.picker.NumberPickerDialog;
import com.zii.business.net.RetrofitClient;
import com.zii.business.utils.RetrofitUtils;
import com.zii.fastapp.entity.HomePageBean;
import com.zii.fastapp.entity.ResultResp;
import com.zii.fastapp.net.TestApi;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * TestCollection
 * Create by zii at 2019/1/23.
 */
public class TestCollection {

  private Activity mActivity;

  private Uri mPictureUri;
  private String mPicturePath;

  private static final String URL_WANDROID_ARTICLE = "http://www.wanandroid.com/article/list/0/json";

  public TestCollection(Activity activity) {
    mActivity = activity;
  }

  public void testQuick(View view) {
    //new AlertDialog.Builder(mActivity)
    //  .setMessage("测试内容拉")
    //  .setTitle("标题")
    //  .show();

  }

  public void testRetrofitUtils() {
    RetrofitClient.getInstance().create(TestApi.class).homePage()
      .compose(RetrofitUtils.applyCommon())
      .subscribe(new Observer<ResultResp<HomePageBean>>() {
        @Override
        public void onSubscribe(Disposable d) {
          Log.d("zii-" + "", "onSubscribe() called with: d = [" + d + "]");
        }

        @Override
        public void onNext(ResultResp<HomePageBean> s) {
          Log.d("zii-" + "", "onNext() called with: s = [" + s + "]");
        }

        @Override
        public void onError(Throwable e) {
          Log.d("zii-" + "", "onError() called with: e = [" + e + "]");
        }

        @Override
        public void onComplete() {
          Log.d("zii-" + "", "onComplete() called");
        }
      });
  }

  public void testShowDatePicker(View view) {
    int[] dateNow = TimeUtils.date2IntArray(new Date());
    int[] showDate = new int[] { 2018, 2, 12 };

    new NumberPickerDialog(view.getContext())
      .title("测试标题")
      .enableDatePicker()
      .setStartDate(new int[] { dateNow[0] - 100, dateNow[1], dateNow[2] })
      .setEndDate(dateNow)
      .setShowDate(showDate)
      .buildDatePicker()
      .setResultListener(new NumberPickerDialog.PickerResultListener() {
        @Override
        public void onResult(int value1, int value2, int value3) {
          ToastUtils.showShort(value1 + "  " + value2 + "  " + value3);
        }
      }).show();
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
      default:
        break;
    }
    return null;
  }

  public String getVersions() {
    return AppUtils.getAppVersionCode() + "   " + AppUtils.getAppVersionName();
  }
}
