package com.zii.fastapp;

import android.app.Application;
import android.util.Log;

/**
 * MyApplication
 * Create by zii at 2019/2/14.
 */
public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("zii-", "MyApp onCreate");
  }
}
