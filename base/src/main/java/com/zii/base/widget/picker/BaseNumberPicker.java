package com.zii.base.widget.picker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import com.zii.base.R;
import java.lang.reflect.Field;

/**
 * BaseNumberPicker
 * Create by iDoctor-ZII at 2018/9/5.
 */
public class BaseNumberPicker extends NumberPicker {

  private static final int DefaultDividerColor = Color.parseColor("#eeeeee");
  private static final int DefaultTextColor = Color.parseColor("#333333");
  private static final int DefaultTextSize = 15;

  private int mDividerColor;
  private int mTextColor;
  private float mTextSizeDp;

  public BaseNumberPicker(Context context) {
    super(context);
    init(null);
  }

  public BaseNumberPicker(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public BaseNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    if (attrs == null) {
      return;
    }

    TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BaseNumberPicker);
    for (int i = 0; i < typedArray.getIndexCount(); i++) {
      int index = typedArray.getIndex(i);
      if (index == R.styleable.BaseNumberPicker_BaseNumberPicker_divider_color) {
        mDividerColor = typedArray.getColor(index, mDividerColor);
      } else if (index == R.styleable.BaseNumberPicker_BaseNumberPicker_text_color) {
        mTextColor = typedArray.getColor(index, mTextColor);
      } else if (index == R.styleable.BaseNumberPicker_BaseNumberPicker_text_size) {
        mTextSizeDp = typedArray.getDimension(index, mTextSizeDp);
      }
    }
    typedArray.recycle();
  }

  @Override
  public void addView(View child) {
    this.addView(child, null);
  }

  @Override
  public void addView(View child, ViewGroup.LayoutParams params) {
    this.addView(child, -1, params);
  }

  @Override
  public void addView(View child, int index, ViewGroup.LayoutParams params) {
    super.addView(child, index, params);

    if (child instanceof EditText) {
      ((EditText) child).setTextColor(mTextColor == 0 ? DefaultTextColor : mTextColor);
      ((EditText) child).setTextSize(mTextSizeDp == 0 ? DefaultTextSize : mTextSizeDp);
    }
    setDividerColor(mDividerColor == 0 ? DefaultDividerColor : mDividerColor);
  }

  /**
   * 使用反射改变分割线的值
   *
   * @param dividerColor 分割线颜色
   */
  public void setDividerColor(int dividerColor) {
    NumberPicker picker = this;
    Field[] pickerFields = NumberPicker.class.getDeclaredFields();
    for (Field pf : pickerFields) {
      if (pf.getName().equals("mSelectionDivider")) {
        pf.setAccessible(true);
        try {
          pf.set(picker, new ColorDrawable(dividerColor));
        } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
          e.printStackTrace();
        }
        break;
      }
    }
  }

  /**
   * 设置显示的内容
   */
  public void setDisplayData(String[] displayValues, int minValue, int maxValue) {
    setDisplayedValues(displayValues);
    setMinValue(minValue);
    setMaxValue(maxValue);
  }

  public void setDisplayData(int minValue, int maxValue) {
    setMinValue(minValue);
    setMaxValue(maxValue);
  }
}
