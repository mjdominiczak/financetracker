package com.mancode.financetracker.database.workers;

import android.content.Context;

import com.mancode.financetracker.database.FTDatabase;
import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.pojos.BalanceMini;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdateStateWorker extends Worker {

    public UpdateStateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        FTDatabase db = FTDatabase.getInstance(getApplicationContext());
        List<Date> dates = db.balanceDao().getDateKeys();
        List<NetValue> netValues = new ArrayList<>();
        for (Date date : dates) {
            List<BalanceMini> balances = db.balanceDao().getBalancesForDate(date);
            double value = 0.0;
            for (BalanceMini balance :
                    balances) {
                value += ((double) balance.accountType) * balance.value;
            }
            netValues.add(new NetValue(date, value));
        }

        db.netValueDao().insertAll(netValues);

        return Result.success();
    }
}
