package com.zii.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import com.zii.base.R;
import com.zii.base.util.ResourceUtils;
import com.zii.base.util.ViewHelper;

/**
 * TranslucenceDialog
 * 半透明Dialog
 * Create by zii at 2018/10/14.
 */
public class TranslucenceDialog extends Dialog {

  protected boolean mIsTouchOutsideCancelable;//控制点击dialog外部是否dismiss
  protected boolean mIsBackCancelable;//控制返回键是否dismiss
  protected View mContentView;
  protected Context mContext;
  protected int mGravity;
  @StyleRes
  protected int mAnimations;
  protected int mWidthDp;
  protected int mHeightDp;
  protected int mLeftMargin;
  protected int mTopMargin;

  public TranslucenceDialog(@NonNull Context context) {
    super(context, R.style.TranslucenceDialog);
  }

  public TranslucenceDialog(Context context, @LayoutRes int layoutRes) {
    super(context, R.style.TranslucenceDialog);

    this.mContext = context;
    setLayout(layoutRes);
  }

  public TranslucenceDialog(Context context, @LayoutRes int layoutRes, int widthDp, int heightDp) {
    this(context, layoutRes);
    setLayoutSize(widthDp, heightDp);
  }

  public TranslucenceDialog(@NonNull Context context, int widthDp, int heightDp) {
    super(context, R.style.TranslucenceDialog);
    mWidthDp = widthDp;
    mHeightDp = heightDp;
  }

  public TranslucenceDialog setLayout(@LayoutRes int layout) {
    mContentView = LayoutInflater.from(getContext()).inflate(layout, null, false);
    return this;
  }

  public TranslucenceDialog setLayoutSize(int widthDp, int heightDp) {
    mWidthDp = widthDp;
    mHeightDp = heightDp;
    return this;
  }

  public TranslucenceDialog setCanceledOnTouchOutsideI(boolean cancelable) {
    mIsTouchOutsideCancelable = cancelable;
    return this;
  }

  public TranslucenceDialog setWindowAnimations(@StyleRes int windowAnimations) {
    mAnimations = windowAnimations;
    return this;
  }

  public TranslucenceDialog setCancelableI(boolean backCancelable) {
    mIsBackCancelable = backCancelable;
    return this;
  }

  /**
   * @param gravity 方向
   * @see Gravity
   */
  public TranslucenceDialog setGravity(int gravity) {
    mGravity = gravity;
    return this;
  }

  public TranslucenceDialog setButton(@IdRes int id, String text, OnClickChild onClickChild) {
    return setText(id, text)
      .setChildClickListener(id, onClickChild);
  }

  public TranslucenceDialog setButton(@IdRes int id, @StringRes int textRes, OnClickChild onClickChild) {
    return setText(id, textRes)
      .setChildClickListener(id, onClickChild);
  }

  public TranslucenceDialog setText(@IdRes int id, @StringRes int textRes) {
    return setText(id, ResourceUtils.getString(textRes));
  }

  public TranslucenceDialog setText(@IdRes int id, CharSequence text) {
    if (mContentView != null) {
      TextView tv = mContentView.findViewById(id);
      if (tv != null) {
        tv.setText(text);
      }
    }
    return this;
  }

  public <T> TranslucenceDialog setWidget(@IdRes int id, T data, OnBindDate<T> bindDate) {
    if (mContentView != null) {
      View view = mContentView.findViewById(id);
      if (view != null && bindDate != null) {
        bindDate.onBindData(view, data);
      }
    }
    return this;
  }

  protected TranslucenceDialog setChildClickListener(final View view, final OnClickChild listener) {
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.onClick(TranslucenceDialog.this, view);
        }
      }
    });
    return this;
  }

  public TranslucenceDialog setChildClickListener(@IdRes int id, final OnClickChild listener) {
    if (mContentView != null) {
      View view = mContentView.findViewById(id);
      if (view != null) {
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (listener != null) {
              listener.onClick(TranslucenceDialog.this, v);
            }
          }
        });
      }
    }
    return this;
  }

  public TranslucenceDialog defaultStyle() {
    setCancelableI(true);
    setCanceledOnTouchOutsideI(true);
    setWindowAnimations(R.style.anim_dialog_fade);
    return this;
  }

  public TranslucenceDialog disableCancelStyle() {
    setCancelableI(false);
    setCanceledOnTouchOutsideI(false);
    setWindowAnimations(R.style.anim_dialog_fade);
    return this;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(mContentView);//这行一定要写在前面
    setCancelable(mIsBackCancelable);//点击外部不可dismiss
    setCanceledOnTouchOutside(mIsTouchOutsideCancelable);

    Window window = this.getWindow();
    if (mGravity != 0) {
      window.setGravity(mGravity);
    }
    if (mAnimations != 0) {
      window.setWindowAnimations(mAnimations);
    }

    WindowManager.LayoutParams params = window.getAttributes();
    if (mWidthDp != 0 || mHeightDp != 0) {
      params.width = getLayoutParam(mWidthDp);
      params.height = getLayoutParam(mHeightDp);
    } else {
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    params.x = mLeftMargin;
    params.y = mTopMargin;
    window.setAttributes(params);
  }

  private int getLayoutParam(int srcDp) {
    int target = srcDp;
    if (srcDp != ViewGroup.LayoutParams.MATCH_PARENT && srcDp != ViewGroup.LayoutParams.WRAP_CONTENT) {
      target = ViewHelper.dp2px(srcDp);
    }
    return target;
  }

  public View getContentView() {
    return mContentView;
  }

  public interface OnClickChild {

    void onClick(TranslucenceDialog dialog, View view);
  }

  public interface OnBindDate<T> {

    void onBindData(View view, T data);
  }
}
