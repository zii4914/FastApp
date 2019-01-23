package com.zii.base.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.zii.base.BuildConfig;
import com.zii.base.util.CrashUtils;
import com.zii.base.util.Utils;

/**
 * BaseApplication
 * Create by Zii at 2018/6/14.
 */
public class BaseApplication extends Application {

  private static final String TAG = "BaseApplication";

  private static Application sApplication;
  private ActivityLifecycleCallbacks mCallbacks = new ActivityLifecycleCallbacks() {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
      Log.d(TAG, "onActivityCreated() called with: activity = ["
          + activity
          + "], savedInstanceState = ["
          + savedInstanceState
          + "]");
    }

    @Override
    public void onActivityStarted(Activity activity) {
      Log.d(TAG, "onActivityStarted() called with: activity = [" + activity + "]");
    }

    @Override
    public void onActivityResumed(Activity activity) {
      Log.d(TAG, "onActivityResumed() called with: activity = [" + activity + "]");
    }

    @Override
    public void onActivityPaused(Activity activity) {
      Log.d(TAG, "onActivityPaused() called with: activity = [" + activity + "]");
    }

    @Override
    public void onActivityStopped(Activity activity) {
      Log.d(TAG, "onActivityStopped() called with: activity = [" + activity + "]");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
      Log.d(TAG,
          "onActivitySaveInstanceState() called with: activity = ["
              + activity
              + "], outState = ["
              + outState
              + "]");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
      Log.d(TAG, "onActivityDestroyed() called with: activity = [" + activity + "]");
    }
  };

  public static Context getContext() {
    return sApplication;
  }

  public Application getApplication() {
    return sApplication;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    sApplication = this;
    Log.d("zii-app", "BaseApplication onCreate");

    registerActivityLifecycleCallbacks(mCallbacks);
    Utils.init(this);
    CrashUtils.init();
    FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
        .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
        .methodCount(1)         // (Optional) How many method line to show. Default 2
        //.methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
        //.logStrategy(new ) // (Optional) Changes the log strategy to print out. Default LogCat
        .tag("zii-")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
        .build();
    Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
      @Override
      public boolean isLoggable(int priority, @Nullable String tag) {
        return BuildConfig.DEBUG;
      }
    });
  }
}
