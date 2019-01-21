package com.zii.base.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import androidx.core.content.FileProvider;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * AppUtils
 * Create by zii at 2018/11/11.
 */
public class Utils {

  static final AdaptScreenArgs ADAPT_SCREEN_ARGS = new AdaptScreenArgs();
  private static final ActivityLifecycleImpl ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();

  private final static String PERMISSION_ACTIVITY_CLASS_NAME =
      "com.blankj.utilcode.util.PermissionUtils$PermissionActivity";
  // TODO td-zii : 2018/11/11  修改为当前的PermissionActivity位置
  @SuppressLint("StaticFieldLeak")
  private static Application sApplication;

  private Utils() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  /**
   * Init utils.
   * <p>Init it in the class of Application.</p>
   *
   * @param context context
   */
  public static void init(final Context context) {
    if (context == null) {
      init(getApplicationByReflect());
      return;
    }
    init((Application) context.getApplicationContext());
  }

  /**
   * Init utils.
   * <p>Init it in the class of Application.</p>
   *
   * @param app application
   */
  public static void init(final Application app) {
    if (sApplication == null) {
      if (app == null) {
        sApplication = getApplicationByReflect();
      } else {
        sApplication = app;
      }
      sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
    }
  }

  /**
   * Return the context of Application object.
   *
   * @return the context of Application object
   */
  public static Application getApp() {
    if (sApplication != null) return sApplication;
    Application app = getApplicationByReflect();
    init(app);
    return app;
  }

  public static Resources getSystemResources() {
    return Resources.getSystem();
  }

  public static Resources getAppResources() {
    return getApp().getResources();
  }

  private static Application getApplicationByReflect() {
    try {
      @SuppressLint("PrivateApi")
      Class<?> activityThread = Class.forName("android.app.ActivityThread");
      Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
      Object app = activityThread.getMethod("getApplication").invoke(thread);
      if (app == null) {
        throw new NullPointerException("u should init first");
      }
      return (Application) app;
    } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
      e.printStackTrace();
    }
    throw new NullPointerException("u should init first");
  }

  static ActivityLifecycleImpl getActivityLifecycle() {
    return ACTIVITY_LIFECYCLE;
  }

  static LinkedList<Activity> getActivityList() {
    return ACTIVITY_LIFECYCLE.mActivityList;
  }

  static Context getTopActivityOrApp() {
    if (isAppForeground()) {
      Activity topActivity = ACTIVITY_LIFECYCLE.getTopActivity();
      return topActivity == null ? Utils.getApp() : topActivity;
    } else {
      return Utils.getApp();
    }
  }

  static boolean isAppForeground() {
    ActivityManager am =
        (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
    //noinspection ConstantConditions
    List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
    if (info == null || info.size() == 0) return false;
    for (ActivityManager.RunningAppProcessInfo aInfo : info) {
      if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
        return aInfo.processName.equals(Utils.getApp().getPackageName());
      }
    }
    return false;
  }

  static void restoreAdaptScreen() {
    final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
    final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
    final Activity activity = ACTIVITY_LIFECYCLE.getTopActivity();
    if (activity != null) {
      final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
      if (ADAPT_SCREEN_ARGS.isVerticalSlide) {
        activityDm.density = activityDm.widthPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
      } else {
        activityDm.density = activityDm.heightPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
      }
      activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
      activityDm.densityDpi = (int) (160 * activityDm.density);

      appDm.density = activityDm.density;
      appDm.scaledDensity = activityDm.scaledDensity;
      appDm.densityDpi = activityDm.densityDpi;
    } else {
      if (ADAPT_SCREEN_ARGS.isVerticalSlide) {
        appDm.density = appDm.widthPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
      } else {
        appDm.density = appDm.heightPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
      }
      appDm.scaledDensity = appDm.density * (systemDm.scaledDensity / systemDm.density);
      appDm.densityDpi = (int) (160 * appDm.density);
    }
  }

  static void cancelAdaptScreen() {
    final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
    final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
    final Activity activity = ACTIVITY_LIFECYCLE.getTopActivity();
    if (activity != null) {
      final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
      activityDm.density = systemDm.density;
      activityDm.scaledDensity = systemDm.scaledDensity;
      activityDm.densityDpi = systemDm.densityDpi;
    }
    appDm.density = systemDm.density;
    appDm.scaledDensity = systemDm.scaledDensity;
    appDm.densityDpi = systemDm.densityDpi;
  }

  static boolean isAdaptScreen() {
    final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
    final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
    return systemDm.density != appDm.density;
  }

  public interface OnAppStatusChangedListener {

    void onForeground();

    void onBackground();
  }

  static class AdaptScreenArgs {

    int sizeInPx;
    boolean isVerticalSlide;
  }

  static class ActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {

    final LinkedList<Activity> mActivityList = new LinkedList<>();
    final HashMap<Object, OnAppStatusChangedListener> mStatusListenerMap = new HashMap<>();

    private int mForegroundCount = 0;
    private int mConfigCount = 0;

    void addListener(final Object object, final OnAppStatusChangedListener listener) {
      mStatusListenerMap.put(object, listener);
    }

    void removeListener(final Object object) {
      mStatusListenerMap.remove(object);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
      setTopActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
      setTopActivity(activity);
      if (mForegroundCount <= 0) {
        postStatus(true);
      }
      if (mConfigCount < 0) {
        ++mConfigCount;
      } else {
        ++mForegroundCount;
      }
    }

    @Override
    public void onActivityResumed(Activity activity) {
      setTopActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {/**/}

    @Override
    public void onActivityStopped(Activity activity) {
      if (activity.isChangingConfigurations()) {
        --mConfigCount;
      } else {
        --mForegroundCount;
        if (mForegroundCount <= 0) {
          postStatus(false);
        }
      }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {/**/}

    @Override
    public void onActivityDestroyed(Activity activity) {
      mActivityList.remove(activity);
    }

    private void postStatus(final boolean isForeground) {
      if (mStatusListenerMap.isEmpty()) return;
      for (OnAppStatusChangedListener onAppStatusChangedListener : mStatusListenerMap.values()) {
        if (onAppStatusChangedListener == null) return;
        if (isForeground) {
          onAppStatusChangedListener.onForeground();
        } else {
          onAppStatusChangedListener.onBackground();
        }
      }
    }

    Activity getTopActivity() {
      if (!mActivityList.isEmpty()) {
        final Activity topActivity = mActivityList.getLast();
        if (topActivity != null) {
          return topActivity;
        }
      }
      Activity topActivityByReflect = getTopActivityByReflect();
      if (topActivityByReflect != null) {
        setTopActivity(topActivityByReflect);
      }
      return topActivityByReflect;
    }

    private void setTopActivity(final Activity activity) {
      if (PERMISSION_ACTIVITY_CLASS_NAME.equals(activity.getClass().getName())) return;
      if (mActivityList.contains(activity)) {
        if (!mActivityList.getLast().equals(activity)) {
          mActivityList.remove(activity);
          mActivityList.addLast(activity);
        }
      } else {
        mActivityList.addLast(activity);
      }
    }

    private Activity getTopActivityByReflect() {
      try {
        @SuppressLint("PrivateApi")
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivityList");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        if (activities == null) return null;
        for (Object activityRecord : activities.values()) {
          Class activityRecordClass = activityRecord.getClass();
          Field pausedField = activityRecordClass.getDeclaredField("paused");
          pausedField.setAccessible(true);
          if (!pausedField.getBoolean(activityRecord)) {
            Field activityField = activityRecordClass.getDeclaredField("activity");
            activityField.setAccessible(true);
            return (Activity) activityField.get(activityRecord);
          }
        }
      } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // interface
  ///////////////////////////////////////////////////////////////////////////

  public static final class FileProvider4UtilCode extends FileProvider {

    @Override
    public boolean onCreate() {
      Utils.init(getContext());
      return true;
    }
  }
}