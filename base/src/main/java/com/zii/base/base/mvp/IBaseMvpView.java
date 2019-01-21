package com.zii.base.base.mvp;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * Description:<br>
 *
 * <p> IBaseView Create by Zii at 2018/6/14. </p>
 */
public interface IBaseMvpView extends View.OnClickListener {

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
   * 视图点击事件
   *
   * @param view 视图
   */
  void onWidgetClick(final View view);
}
