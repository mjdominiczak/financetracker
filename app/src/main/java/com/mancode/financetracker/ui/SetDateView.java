package com.mancode.financetracker.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;

import com.mancode.financetracker.R;

import org.threeten.bp.LocalDate;

import androidx.appcompat.widget.AppCompatButton;

/**
 * Created by Manveru on 04.11.2017.
 */

public class SetDateView extends AppCompatButton {

    private boolean mDateSet = false;
    private LocalDate mDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public SetDateView(Context context) {
        this(context, null);
    }

    public SetDateView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.setDateViewStyle);
        setDate(LocalDate.now());
        mDateSetListener = (view, year, month, dayOfMonth) -> setDate(year, month, dayOfMonth);
        this.setOnClickListener(v -> new DatePickerDialog(
                getContext(),
                R.style.AppTheme_DatePicker,
                mDateSetListener,
                mDate.getYear(),
                mDate.getMonthValue(),
                mDate.getDayOfMonth())
                .show());
    }

    public LocalDate getDate() {
        return mDateSet ? mDate : null;
    }

    private void setDate(int year, int month, int dayOfMonth) {
        mDate = LocalDate.of(year, month, dayOfMonth);
        mDateSet = true;
        updateText();
    }

    public void setDate(LocalDate date) {
        mDate = date;
        mDateSet = true;
        updateText();
    }

    public void resetDate() {
        mDateSet = false;
        updateText();
    }

    private void updateText() {
        if (mDateSet) {
            this.setText(mDate.toString());
        } else {
            this.setText("");
        }
    }
}
