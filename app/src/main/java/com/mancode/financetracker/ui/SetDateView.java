package com.mancode.financetracker.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.mancode.financetracker.R;

import org.threeten.bp.LocalDate;

/**
 * Created by Manveru on 04.11.2017.
 */

public class SetDateView extends AppCompatAutoCompleteTextView {

    private boolean mDateSet = false;
    private boolean isShowingDialog = false;
    private LocalDate mDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public SetDateView(Context context) {
        this(context, null);
    }

    public SetDateView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.setDateViewStyle);
        setDate(LocalDate.now());
        mDateSetListener = (view, year, month, dayOfMonth) -> setDate(year, month + 1, dayOfMonth);
        setOnClickListener(v -> {
            if (isShowingDialog) return;
            DatePickerDialog dialog = new DatePickerDialog(
                    SetDateView.this.getContext(),
                    mDateSetListener,
                    mDate.getYear(),
                    mDate.getMonthValue() - 1,
                    mDate.getDayOfMonth());
            dialog.setOnDismissListener(d -> isShowingDialog = false);
            dialog.show();
            isShowingDialog = true;
        });
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
            setText(mDate.toString());
        } else {
            setText("");
        }
    }
}
