package com.zii.base.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseFragment
 * Create by Zii at 2018/6/14.
 */
public abstract class BaseBusinessFragment extends BaseFragment implements IBaseView {

  private static final String TAG = "BaseFragment";
  private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

  protected View mContentView;
  protected Activity mActivity;

  protected Unbinder unbinder;
  private long lastClick = 0;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreate: ");
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
      FragmentTransaction ft = getFragmentManager().beginTransaction();
      if (isSupportHidden) {
        ft.hide(this);
      } else {
        ft.show(this);
      }
      ft.commitAllowingStateLoss();
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreateView: ");
    setBaseView(inflater, bindLayout());
    return mContentView;
  }

  protected void setBaseView(@NonNull LayoutInflater inflater, @LayoutRes int layoutId) {
    if (layoutId == 0) return;
    mContentView = inflater.inflate(layoutId, null);
    unbinder = ButterKnife.bind(this, mContentView);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onViewCreated: ");
    super.onViewCreated(view, savedInstanceState);
    Bundle bundle = getArguments();
    initData(bundle);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onActivityCreated: ");
    super.onActivityCreated(savedInstanceState);
    mActivity = getActivity();
    initView(savedInstanceState, mContentView);
    doBusiness();
  }

  @Override
  public void onDestroyView() {
    Log.d(TAG, "onDestroyView: ");
    if (mContentView != null) {
      ((ViewGroup) mContentView.getParent()).removeView(mContentView);
    }
    super.onDestroyView();
    if (unbinder != null) {
      unbinder.unbind();
    }
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy: ");
    super.onDestroy();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    Log.d(TAG, "onSaveInstanceState: ");
    super.onSaveInstanceState(outState);
    outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
  }

  private boolean isFastClick() {
    long now = System.currentTimeMillis();
    if (now - lastClick >= 200) {
      lastClick = now;
      return false;
    }
    return true;
  }

  @Override
  public void onClick(View view) {
    if (!isFastClick()) onWidgetClick(view);
  }

  public <T extends View> T findViewById(@IdRes int id) {
    if (mContentView == null) throw new NullPointerException("ContentView is null.");
    return mContentView.findViewById(id);
  }
}
