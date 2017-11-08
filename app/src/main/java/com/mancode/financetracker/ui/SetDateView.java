package com.mancode.financetracker.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

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
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(year, month, dayOfMonth);
                mDateSet = true;
                updateText();
            }
        };
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        getContext(),
                        R.style.AppTheme_DatePicker,
                        mDateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    public Date getDate() {
        return mDateSet ? mCalendar.getTime() : null;
    }

    private void updateText() {
        Date date = mCalendar.getTime();
        DateFormat df = DateFormat.getDateInstance();
        this.setText(df.format(date));
    }
}
