package com.zii.business.net;

import android.text.TextUtils;
import com.orhanobut.logger.Logger;
import com.zii.base.util.PathUtils;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
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
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit 封装
 * toImpl：
 * 1.BaseUrl需要修改
 * 2.AppInterceptor根据需要调整
 */
public class RetrofitClient {
  private static final String BASE_URL = "http://www.wanandroid.com"; //host
  private static final int TIMEOUT_SECOND = 15;
  private static final String CACHE_PATH = PathUtils.getInternalAppCachePath() + File.separator + "OkHttpCache";
  private static final int CACHE_SIZE = 50 * 1024 * 1024; //50M

  private final HashMap<String, Retrofit> mClients = new HashMap<>();

  private static volatile RetrofitClient sInstance;

  private RetrofitClient() {
  }

  public static RetrofitClient getInstance() {
    if (sInstance == null) {
      synchronized (RetrofitClient.class) {
        if (sInstance == null)
          sInstance = new RetrofitClient();
      }
    }
    return sInstance;
  }

  public <T> T create(Class<T> cls) {
    return create(BASE_URL, cls);
  }

  public <T> T create(String baseUrl, Class<T> cls) {
    return getRetrofit(baseUrl).create(cls);
  }

  private Retrofit getRetrofit(String baseUrl) {
    Retrofit client = mClients.get(baseUrl);
    if (client == null) {
      client = newRetrofit(baseUrl);
      mClients.put(baseUrl, client);
    }
    return client;
  }

  private Retrofit newRetrofit(String baseUrl) {
    return new Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl(baseUrl)
      .client(getOkHttpClient())
      .build();
  }

  private OkHttpClient getOkHttpClient() {
    Cache cache = new Cache(new File(CACHE_PATH), CACHE_SIZE);
    return new OkHttpClient.Builder()
      .addInterceptor(new AppInterceptor())
      .addInterceptor(new LoggingInterception())
      .connectTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
      .readTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
      .cache(cache)
      .build();
  }

  private class AppInterceptor implements Interceptor {

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

    private final Charset ut8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
      Request request = chain.request();

      RequestBody requestBody = request.body();
      String reqBodyContent = "";
      if (requestBody != null) {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        if (isPlaintext(buffer)) {

          reqBodyContent = buffer.readString(ut8);
        }
      }
      String requestMsg =
        "Request Url: "
          + request.url()
          + "\n"
          + (!TextUtils.isEmpty(reqBodyContent) ? "RequestBody:" + (reqBodyContent.length() > 5000
          ? reqBodyContent.substring(0, 5000) : reqBodyContent) : "No RequestBody " + "")
          + "\n"
          + (request.headers().size() != 0 ? "Headers:" + request.headers() : "No Headers \n");

      Logger.d(requestMsg);
      //Log.d("zii-net", requestMsg);

      Response response = chain.proceed(request);
      ResponseBody responseBody = response.body();
      String respBodyContent = "";
      if (responseBody != null) {
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        if (isPlaintext(buffer)) {
          respBodyContent = buffer.clone().readString(ut8);
        }
      }

      String responseMsg = "Response Url: "
        + request.url()
        + "\n"
        //"" + "Url:" + request.url() + "\n"
        + "ResponseCode:"
        + response.code()
        + "\n"
        + (!TextUtils.isEmpty(respBodyContent) ? "ResponseBody:\n" + respBodyContent : "No ResponseBody ")
        + "\n";
      Logger.d(responseMsg);
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
