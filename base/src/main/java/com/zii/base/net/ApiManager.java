package com.zii.base.net;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.orhanobut.logger.Logger;
import com.zii.base.util.PathUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiManager
 * Create by zii at 2018/11/24.
 */
public class ApiManager {

  public static final String BASE_URL = "";
  public static final int TIMEOUT_NET = 15;
  public static final String CACHE_PATH = PathUtils.getInternalAppCachePath() + File.separator + "OkHttpCache";
  public static final int CACHE_SIZE = 50 * 1024 * 1024; //10M
  private static volatile ApiManager sInstance;

  private ApiManager() {
  }

  public static ApiManager getInstance() {
    if (sInstance == null) {
      synchronized (ApiManager.class) {
        if (sInstance == null) {
          sInstance = new ApiManager();
        }
      }
    }
    return sInstance;
  }

  public static <T> ObservableTransformer<T, T> applyCommon(final Activity activity) {
    return new ObservableTransformer<T, T>() {

      @Override
      public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
          .compose(ApiManager.<T>applyLoadingShow(activity))
          .compose(ApiManager.<T>applyCommonScheduler());
      }
    };
  }

  public static <T> ObservableTransformer<T, T> applyCommonScheduler() {
    return new ObservableTransformer<T, T>() {

      @Override
      public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  /**
   * 注意需要在main线程中执行，可以在后面添加subscribeOn(Main)来确保调用的线程为主线程
   */
  private static <T> ObservableTransformer<T, T> applyLoadingShow(@NonNull final Activity activity) {
    final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
    final AlertDialog loadingDialog = new AlertDialog.Builder(activity).setTitle("Loading").create();
    return new ObservableTransformer<T, T>() {
      @Override
      public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.doOnSubscribe(new Consumer<Disposable>() {
          @Override
          public void accept(Disposable disposable) {
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

  public <T> T create(Class<T> cls) {
    return getRetrofit().create(cls);
  }

  private Retrofit getRetrofit() {
    return new Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl(BASE_URL)
      .client(getOkHttpClient())
      .build();
  }

  private OkHttpClient getOkHttpClient() {
    Cache cache = new Cache(new File(CACHE_PATH), CACHE_SIZE);
    return new OkHttpClient.Builder()
      .addInterceptor(new AppInterceptor())
      .addInterceptor(new LoggingInterception())
      .connectTimeout(TIMEOUT_NET, TimeUnit.SECONDS)
      .readTimeout(TIMEOUT_NET, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT_NET, TimeUnit.SECONDS)
      .cache(cache)
      .build();
  }

  public class AppInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();

      //Request.Builder newBuilder = request.newBuilder();
      //UserBean userBean = UserInfo.getInstance().getUserBean();
      //if (userBean != null) {
      //  newBuilder.addHeader("token", userBean.getToken());
      //}

      //return chain.proceed(newBuilder.build());
      return chain.proceed(request);
    }
  }

  private class LoggingInterception implements Interceptor {

    private final Charset UT8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
      Request request = chain.request();

      RequestBody requestBody = request.body();
      String reqBodyContent = "";
      if (requestBody != null) {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        if (isPlaintext(buffer)) {

          reqBodyContent = buffer.readString(UT8);
        }
      }
      String requestMsg =
        "Request Url: "
          + request.url()
          + "\n"
          //+ "  _______________________________________________________________________________________________________________________________________________________________________________\n"
          + (!TextUtils.isEmpty(reqBodyContent) ? "RequestBody:" + (reqBodyContent.length() > 5000 ?
          reqBodyContent.substring(0, 5000) : reqBodyContent) : "No RequestBody " + "")
          + "\n"
          + (request.headers().size() != 0 ? "Headers:" + request.headers() : "No Headers \n")
          + "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

      Logger.d("net", requestMsg);
      //Log.d("zii-net", requestMsg);

      Response response = chain.proceed(request);
      ResponseBody responseBody = response.body();
      String respBodyContent = "";
      if (responseBody != null) {
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        if (isPlaintext(buffer)) {
          respBodyContent = buffer.clone().readString(UT8);
        }
      }

      String responseMsg = "Response Url: "
        + request.url()
        + "\n"
        //+ "  _______________________________________________________________________________________________________________________________________________________________________________\n"
        //"" + "Url:" + request.url() + "\n"
        + "ResponseCode:"
        + response.code()
        + "\n"
        + (!TextUtils.isEmpty(respBodyContent) ? "ResponseBody:" + respBodyContent : "No ResponseBody ")
        + "\n"
        + "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
      Logger.d("net", responseMsg);
      //Log.d("zii-net", responseMsg);
      return response;
    }

    private boolean isPlaintext(Buffer buffer) {
      try {
        Buffer prefix = new Buffer();
        long byteCount = buffer.size() < 64 ? buffer.size() : 64;
        buffer.copyTo(prefix, 0, byteCount);
        for (int i = 0; i < 16; i++) {
          if (prefix.exhausted()) {
            break;
          }
          int codePoint = prefix.readUtf8CodePoint();
          if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
            return false;
          }
        }
        return true;
      } catch (EOFException e) {
        return false; // Truncated UTF-8 sequence.
      }
    }
  }
}
