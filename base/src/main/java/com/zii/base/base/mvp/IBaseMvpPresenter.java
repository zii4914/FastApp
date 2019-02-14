package com.zii.base.base.mvp;

import androidx.lifecycle.LifecycleObserver;

/**
 * Description:<br>
 *
 * <p> IBasePresenter Create by Zii at 2018/6/14. </p>
 */
public abstract class IBaseMvpPresenter<V extends IBaseMvpView> implements LifecycleObserver {

  protected V mView;

  protected void attachView(V v) {
    mView = v;
  }

  protected void detachView() {
    mView = null;
  }
}
