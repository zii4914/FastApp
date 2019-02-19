package com.zii.fastapp.net;

import com.zii.fastapp.entity.HomePageBean;
import com.zii.fastapp.entity.ResultResp;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface TestApi {

  @GET("/article/list/0/json")
  Observable<ResultResp<HomePageBean>> homePage();
}
