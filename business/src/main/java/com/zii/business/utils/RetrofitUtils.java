package com.zii.business.utils;

import android.app.Activity;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.zii.base.util.ActivityUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.lang.ref.WeakReference;

public class RetrofitUtils {

  public static <T> ObservableTransformer<T, T> applyCommon() {
    Log.d("zii-" + "", "applyCommon: Outer");
    return new ObservableTransformer<T, T>() {

      @Override
      public ObservableSource<T> apply(Observable<T> upstream) {
        Log.d("zii-" + "", "applyCommon: Inner");
        return upstream
          .compose(RetrofitUtils.<T>applyScheduler())
          .compose(RetrofitUtils.<T>applyLoading());
      }
    };
  }

  public static <T> ObservableTransformer<T, T> applyCommon(final Activity activity) {
    Log.d("zii-" + "", "applyCommon: Outer");
    return new ObservableTransformer<T, T>() {

      @Override
      public ObservableSource<T> apply(Observable<T> upstream) {
        Log.d("zii-" + "", "applyCommon: Inner");
        return upstream
          .compose(RetrofitUtils.<T>applyLoading(activity))
          .compose(RetrofitUtils.<T>applyScheduler());
      }
    };
  }

  public static <T> ObservableTransformer<T, T> applyScheduler() {
    Log.d("zii-" + "", "applyScheduler: Outer");
    return new ObservableTransformer<T, T>() {

      @Override
      public ObservableSource<T> apply(Observable<T> upstream) {
        Log.d("zii-" + "", "applyScheduler: Inner");
        return upstream
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  private static <T> ObservableTransformer<T, T> applyLoading() {
    return applyLoading(ActivityUtils.getTopActivity());
  }

  /**
   * 注意需要在main线程中执行，可以在后面添加subscribeOn(Main)来确保调用的线程为主线程
   */
  private static <T> ObservableTransformer<T, T> applyLoading(@NonNull final Activity activity) {
    Log.d("zii-" + "", "applyLoading: Outer");

    final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
    final AlertDialog loadingDialog = new AlertDialog.Builder(activity).setTitle("Loading").create();
    return new ObservableTransformer<T, T>() {
      @Override
      public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.doOnSubscribe(new Consumer<Disposable>() {
          @Override
          public void accept(Disposable disposable) {
            Log.d("zii-" + "", "applyLoading: Inner");
            Log.d("zii-", "on subscribe");
            Log.d("zii-", "Loading Show");
            Activity context;
            if ((context = activityWeakReference.get()) != null && !context.isFinishing()) {
              loadingDialog.show();
            }
          }
        }).doOnTerminate(new Action() {
          @Override
          public void run() {
            Activity context;
            Log.d("zii-", "on terminate");
            if ((context = activityWeakReference.get()) != null && !context.isFinishing()) {
              Log.d("zii-", "Loading Dismiss Terminate");
              loadingDialog.dismiss();
            }
          }
        }).doOnDispose(new Action() {
          @Override
          public void run() {
            Activity context;
            Log.d("zii-", "on dispose");
            if ((context = activityWeakReference.get()) != null && !context.isFinishing()) {
              Log.d("zii-", "Loading Dismiss Dispose");
              loadingDialog.dismiss();
            }
          }
        });
      }
    };
  }
}
