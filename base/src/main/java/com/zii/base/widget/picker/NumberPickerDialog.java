package com.zii.base.widget.picker;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.StringRes;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.zii.base.R;
import com.zii.base.R2;
import com.zii.base.widget.dialog.TranslucenceDialog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * NumberPickerView
 * Create by zii at 2018/10/14.
 */
public class NumberPickerDialog extends TranslucenceDialog {

  private final ArrayList<BaseNumberPicker> mPickers = new ArrayList<>();
  private final ArrayList<TextView> mSuffixTvs = new ArrayList<>();
  private final List<PickerData> mPickerDataList = new ArrayList<>();
  private final SparseIntArray mCurrentValues = new SparseIntArray(3);
  @BindView(R2.id.layout_top)
  LinearLayout mLayoutTop;
  @BindView(R2.id.divider)
  View mDivider;
  @BindView(R2.id.layout_picker)
  LinearLayout mLayoutPicker;
  @BindView(R2.id.picker_1)
  BaseNumberPicker mPicker1;
  @BindView(R2.id.picker_2)
  BaseNumberPicker mPicker2;
  @BindView(R2.id.picker_3)
  BaseNumberPicker mPicker3;
  @BindView(R2.id.tv_suffix_1)
  TextView mTvSuffix1;
  @BindView(R2.id.tv_suffix_2)
  TextView mTvSuffix2;
  @BindView(R2.id.tv_suffix_3)
  TextView mTvSuffix3;
  @BindView(R2.id.tv_center)
  TextView mTvTitle;
  private PickerChangeListener mChangeListener;
  private PickerResultListener mResultListener;

  public NumberPickerDialog(Context context) {
    super(context, R.layout.dialog_number_picker);

    ButterKnife.bind(this, mContentView);

    mPickers.add(mPicker1);
    mPickers.add(mPicker2);
    mPickers.add(mPicker3);

    mSuffixTvs.add(mTvSuffix1);
    mSuffixTvs.add(mTvSuffix2);
    mSuffixTvs.add(mTvSuffix3);

    defaultStyle();
    setGravity(Gravity.BOTTOM);
  }

  public NumberPickerDialog title(@StringRes int res) {
    mTvTitle.setText(res);
    return this;
  }

  public NumberPickerDialog title(String title) {
    mTvTitle.setText(title);
    return this;
  }

  public NumberPickerDialog addPickerData(String[] displayValues, String suffixText) {
    mPickerDataList.add(new PickerData(displayValues, 0, displayValues.length - 1, suffixText));
    return this;
  }

  public NumberPickerDialog addPickerData(String[] displayValues, int minValue, int maxValue, String suffixText) {
    mPickerDataList.add(new PickerData(displayValues, minValue, maxValue, suffixText));
    return this;
  }

  public NumberPickerDialog addPickerData(int minValue, int maxValue, String suffixText) {
    mPickerDataList.add(new PickerData(minValue, maxValue, suffixText));
    return this;
  }

  public NumberPickerDialog setLoop1(boolean canLoop) {
    mPicker1.setWrapSelectorWheel(canLoop);
    return this;
  }

  public NumberPickerDialog setLoop2(boolean canLoop) {
    mPicker2.setWrapSelectorWheel(canLoop);
    return this;
  }

  public NumberPickerDialog setLoop3(boolean canLoop) {
    mPicker3.setWrapSelectorWheel(canLoop);
    return this;
  }

  public NumberPickerDialog setLoop(boolean picker1b, boolean picker2b, boolean picker3b) {
    mPicker1.setWrapSelectorWheel(picker1b);
    mPicker2.setWrapSelectorWheel(picker2b);
    mPicker3.setWrapSelectorWheel(picker3b);
    return this;
  }

  public NumberPickerDialog setValues1(int v) {
    mCurrentValues.put(0, v);
    return this;
  }

  public NumberPickerDialog setValues2(int v) {
    mCurrentValues.put(1, v);
    return this;
  }

  public NumberPickerDialog setValues3(int v) {
    mCurrentValues.put(2, v);
    return this;
  }

  public NumberPickerDialog setChangeListener(PickerChangeListener changeListener) {
    mChangeListener = changeListener;
    mPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
      @Override
      public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (mChangeListener != null) {
          mChangeListener.onValueChange(mPicker1, mPicker2, mPicker3, 1);
        }
      }
    });
    mPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
      @Override
      public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (mChangeListener != null) {
          mChangeListener.onValueChange(mPicker1, mPicker2, mPicker3, 2);
        }
      }
    });
    mPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
      @Override
      public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (mChangeListener != null) {
          mChangeListener.onValueChange(mPicker1, mPicker2, mPicker3, 3);
        }
      }
    });
    return this;
  }

  public NumberPickerDialog setResultListener(PickerResultListener resultListener) {
    mResultListener = resultListener;
    return this;
  }

  public NumberPickerDialog setValues(int v1, int v2, int v3) {
    mCurrentValues.put(0, v1);
    mCurrentValues.put(1, v2);
    mCurrentValues.put(2, v3);
    return this;
  }

  private NumberPickerDialog build() {
    int size = mPickerDataList.size();

    for (int i = 0; i < mPickers.size(); i++) {
      mPickers.get(i).setVisibility(View.GONE);
      mSuffixTvs.get(i).setVisibility(View.GONE);
    }

    for (int i = 0; i < size; i++) {
      if (i > mPickers.size()) {
        //超出可以显示的数据不处理
        break;
      }
      PickerData pickerData = mPickerDataList.get(i);
      BaseNumberPicker picker = mPickers.get(i);
      TextView tvSuffix = mSuffixTvs.get(i);

      picker.setVisibility(View.VISIBLE);
      tvSuffix.setVisibility(View.VISIBLE);

      //数据
      if (pickerData.displayValues != null) {
        picker.setDisplayData(pickerData.displayValues, pickerData.minValue, pickerData.maxValue);
      } else {
        picker.setDisplayData(pickerData.minValue, pickerData.maxValue);
      }
      //后缀
      if (TextUtils.isEmpty(pickerData.suffixText)) {
        tvSuffix.setVisibility(View.GONE);
      } else {
        tvSuffix.setText(pickerData.suffixText);
      }
      //当前数值
      if (mCurrentValues.get(i, -99999) != -99999) {
        picker.setValue(mCurrentValues.get(i));
      }
    }

    return this;
  }

  @OnClick({ R2.id.tv_left, R2.id.tv_right })
  public void onViewClicked(View view) {
    int i = view.getId();
    if (i == R.id.tv_left) {
      dismiss();
    } else if (i == R.id.tv_right) {
      if (mResultListener != null) {
        mResultListener.onResult(mPicker1.getValue(), mPicker2.getValue(), mPicker3.getValue());
      }
      dismiss();
    }
  }

  public DateBuilder enableDatePicker() {
    return new DateBuilder(this);
  }

  @Override
  public void show() {
    build();
    super.show();
  }

  private int getMaxDay(int year, int month) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, 1);
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  private int[] getDateArray(int year, int month, int day) {
    return new int[] { year, month, day };
  }

  public interface PickerChangeListener {

    void onValueChange(BaseNumberPicker picker1, BaseNumberPicker picker2, BaseNumberPicker picker3, int changeIndex);
  }

  public interface PickerResultListener {

    void onResult(int value1, int value2, int value3);
  }

  public class DateBuilder {

    private NumberPickerDialog mDialog;
    private int[] mStartDate;
    private int[] mEndDate;
    private int[] mShowDate;
    private boolean mIsOnlyYearMonth;

    public DateBuilder(NumberPickerDialog dialog) {
      mDialog = dialog;
    }

    /**
     * 设置开始时间
     *
     * @param startDate 容器数量为3的数组，分别对应年月日
     */
    public DateBuilder setStartDate(int[] startDate) {
      mStartDate = startDate;
      return this;
    }

    /**
     * 设置开始时间
     *
     * @param endDate 容器数量为3的数组，分别对应年月日
     */
    public DateBuilder setEndDate(int[] endDate) {
      mEndDate = endDate;
      return this;
    }

    public DateBuilder setShowDate(int[] showDate) {
      mShowDate = showDate;
      return this;
    }

    public DateBuilder onlyYearMonth() {
      mIsOnlyYearMonth = true;
      return this;
    }

    public NumberPickerDialog buildDatePicker() {
      buildDateParams();
      buildDateDate(mStartDate, mEndDate, mShowDate);
      return mDialog;
    }

    private void buildDateParams() {
      if (mStartDate == null && mEndDate == null) {
        Calendar calendar = Calendar.getInstance();
        //三个都没设置，则范围为当前日期上下100年，显示时间为当前时间
        mStartDate = getDateArray(calendar.get(Calendar.YEAR) - 100, 1, 1);
        mEndDate = getDateArray(calendar.get(Calendar.YEAR) + 100, 1, 1);
        //若显示时间为空，则显示当前日期时间
        mShowDate = getDateArray(
          calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
      } else if (mStartDate != null && mEndDate == null) {
        //起始时间不为空，则范围为起始时间+100年
        mEndDate = getDateArray(mStartDate[0] + 100, 1, 1);
        if (mShowDate == null) {
          //若显示时间为空，则显示起始时间
          mShowDate = mStartDate;
        }
      } else if (mStartDate == null && mEndDate != null) {
        //结束时间不为空，则范围为结束时间-100年 至 结束时间
        mStartDate = getDateArray(mEndDate[0] - 100, 1, 1);
        if (mShowDate == null) {
          //若显示时间为空，则显示结束时间
          mShowDate = mEndDate;
        }
      } else if (mShowDate == null) {
        mShowDate = mEndDate;
      }
    }

    private void buildDateDate(final int[] startDate, final int[] endDate, int[] currentDate) {

      //年，范围，startDate 至 endDate
      mDialog.addPickerData(startDate[0], endDate[0], "年");

      //月
      //若当前选择的年份==起始年份，应该先显示最小月份为 起始月份，否则为1月；
      //若当前选择的年份==结束年份，应该先显示最大月份为 结束月份，否则为12月；
      mDialog.addPickerData(currentDate[0] == startDate[0] ? startDate[1] : 1,
        currentDate[0] == endDate[0] ? endDate[1] : 12,
        "月");

      //日
      //若当前选择的年份==起始年份，并且当前月份==起始月份，则应该先显示最小日为 起始日，否则为1日
      //若当前选择的年份==结束年份，并且当前月份==结束月份，则应该先显示最大日为 结束日，否则为当前选择年月下最大日
      boolean isStartYearMonth = currentDate[0] == startDate[0] && currentDate[1] == startDate[1];
      boolean isEndYearMonth = currentDate[0] == endDate[0] && currentDate[1] == endDate[1];
      if (!mIsOnlyYearMonth) {
        mDialog.addPickerData(isStartYearMonth ? startDate[2] : 1,
          isEndYearMonth ? endDate[2] : getMaxDay(currentDate[0], currentDate[1]), "日");
      }

      mDialog.setValues(currentDate[0], currentDate[1], currentDate[2]);

      mDialog.setTitle("请选择日期");
      mDialog.setLoop(false, false, false);

      mDialog.setChangeListener(new PickerChangeListener() {
        @Override
        public void onValueChange(BaseNumberPicker picker1, BaseNumberPicker picker2, BaseNumberPicker picker3,
          int changeIndex) {
          if (changeIndex == 1) {
            //年份发生变化时，月份要产生对应变化
            if (picker1.getValue() == endDate[0]) {
              //若当前年份==结束年份，同时当前年份==起始年份，则最小月份为设置的起始月份，否则为1月
              //最大月份应该结束月份
              picker2.setDisplayData(picker1.getValue() == startDate[0] ? startDate[1] : 1, endDate[1]);
            } else if (picker1.getValue() == startDate[0]) {
              //若选择年份为起始年份，则月份最小为起始月份，若同时选择年份==结束年份，则最大月份为结束月份，否则为12月
              picker2.setDisplayData(startDate[1], picker1.getValue() == endDate[0] ? endDate[1] : 12);
            } else {
              //若选择年份在开始及结束年份之间
              picker2.setDisplayData(1, 12);
            }
          }

          if (changeIndex != 3) {
            //若年或月发生变化，则日要产生对应变化
            int showYear = picker1.getValue();
            int showMonth = picker2.getValue();
            int showDay = picker3.getValue();

            boolean isStartYearMonth = showYear == mStartDate[0] && showMonth == mStartDate[1];
            boolean isEndYearMonth = showYear == endDate[0] && showMonth == endDate[1];

            picker3.setDisplayData(isStartYearMonth ? mStartDate[2] : 1,
              isEndYearMonth ? endDate[2] : getMaxDay(showYear, showMonth));
            picker3.setValue(showDay);
          }
        }
      });
    }
  }

  private class PickerData {

    public int minValue;
    public int maxValue;
    public String suffixText;
    public String[] displayValues;

    public PickerData() {
    }

    public PickerData(int minValue, int maxValue, String suffixText) {
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.suffixText = suffixText;
    }

    public PickerData(String[] displayValues, int minValue, int maxValue, String suffixText) {
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.suffixText = suffixText;
      this.displayValues = displayValues;
    }
  }
}
