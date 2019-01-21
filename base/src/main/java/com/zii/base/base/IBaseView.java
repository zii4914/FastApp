package com.zii.base.base;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * IBaseView
 * Create by zii at 2018/11/11.
 */
public interface IBaseView extends View.OnClickListener {

  /**
   * 初始化数据
   *
   * @param bundle 传递过来的 bundle
   */
  void initData(@Nullable final Bundle bundle);

  /**
   * 绑定布局
   *
   * @return 布局 Id
   */
  int bindLayout();

  /**
   * 初始化 view
   */
  void initView(final Bundle savedInstanceState, final View contentView);

  /**
   * 业务操作
   */
  void doBusiness();

  /**
   * 视图点击事件
   *
   * @param view 视图
   */
  void onWidgetClick(final View view);
}
