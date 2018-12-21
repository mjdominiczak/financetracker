package com.mancode.financetracker.database.viewmodel;

import android.app.Application;

import com.mancode.financetracker.database.DataRepository;
import com.mancode.financetracker.database.entity.CategoryEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * Created by Manveru on 17.02.2018.
 */

public class CategoryViewModel extends AndroidViewModel {

    private DataRepository mRepository;
    private LiveData<List<CategoryEntity>> mAllCategories;


    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = DataRepository.getInstance(application);
        mAllCategories = mRepository.getAllCategories();
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return mAllCategories;
    }

    public List<CategoryEntity> getIncomeCategories() {
        return mRepository.getIncomeCategories();
    }

    public List<CategoryEntity> getOutcomeCategories() {
        return mRepository.getOutcomeCategories();
    }
}
