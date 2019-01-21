package com.zii.base.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;

/**
 * BaseRelativeLayout
 * Create by zii at 2018/11/11.
 */
public abstract class BaseRelativeLayout extends RelativeLayout implements IBaseWidgetView {

  public BaseRelativeLayout(Context context) {
    super(context);
    init(null);
  }

  public BaseRelativeLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public BaseRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    inflaterLayout();
    initAttrs();
    int[] styleable = bindStyleable();
    if (attrs != null && styleable != null && styleable.length > 0) {
      TypedArray typedArray = getContext().obtainStyledAttributes(attrs, styleable);
      for (int i = 0; i < typedArray.getIndexCount(); i++) {
        int index = typedArray.getIndex(i);
        setStyleAttr(typedArray, index);
      }
      typedArray.recycle();
    }
    initPaints();
    initViews();
  }
}
