package com.mancode.financetracker.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;

import com.mancode.financetracker.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Manveru on 04.11.2017.
 */

public class SetDateView extends AppCompatButton {

    private boolean mDateSet = false;
    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public SetDateView(Context context) {
        this(context, null);
    }

    public SetDateView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.setDateViewStyle);
        mCalendar = Calendar.getInstance();
        setDate(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        mDateSetListener = (view, year, month, dayOfMonth) -> setDate(year, month, dayOfMonth);
        this.setOnClickListener(v -> new DatePickerDialog(
                getContext(),
                R.style.AppTheme_DatePicker,
                mDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH))
                .show());
    }

    public Date getDate() {
        return mDateSet ? mCalendar.getTime() : null;
    }

    private void setDate(int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        mDateSet = true;
        updateText();
    }

    public void setDate(Date date) {
        mCalendar.setTime(date);
        mDateSet = true;
        updateText();
    }

    public void resetDate() {
        mDateSet = false;
        updateText();
    }

    private void updateText() {
        if (mDateSet) {
            Date date = mCalendar.getTime();
            DateFormat df = DateFormat.getDateInstance();
            this.setText(df.format(date));
        } else {
            this.setText("");
        }
    }
}
