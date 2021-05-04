package com.mancode.financetracker.workers

import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import org.threeten.bp.LocalDate
import timber.log.Timber

const val TAG_UPDATE = "updateState"
const val TAG_UPDATE_PARTIAL = "updateStatePartial"
const val TAG_IMPORT = "import"
const val TAG_EXPORT = "export"

fun Context.runUpdateWorker(accountIds: IntArray? = null, date: LocalDate? = null, force: Boolean = false) {
    val isPartial = accountIds != null || date != null
    if (!isPartial) {
        val lastUpdate = PreferenceAccessor.lastUpdateDate
        if (lastUpdate != null && lastUpdate.isEqual(LocalDate.now()) && !force) {
            Timber.i("Full update already done today")
            return
        }
    }
    val request = OneTimeWorkRequest.Builder(UpdateStateWorker::class.java).apply {
        addTag(TAG_UPDATE)
        if (isPartial) addTag(TAG_UPDATE_PARTIAL)
        setInputData(
            Data.Builder().apply {
                if (accountIds != null) putIntArray(UpdateStateWorker.KEY_ACCOUNT_IDS, accountIds)
                if (date != null) putString(
                    UpdateStateWorker.KEY_DATE,
                    DateConverter.toString(date)
                )
            }.build()
        )
    }.build()
    val policy = if (!isPartial) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.APPEND
    WorkManager.getInstance(this)
        .enqueueUniqueWork("UpdateStateWorker", policy, request)
}

fun Context.runExportToUri(uri: Uri) {
    val request = OneTimeWorkRequest.Builder(ExportToJsonWorker::class.java)
        .addTag(TAG_EXPORT)
        .setInputData(
            Data.Builder().apply {
                putString(ExportToJsonWorker.KEY_URI_ARG, uri.toString())
            }.build()
        )
        .build()
    WorkManager.getInstance(this).enqueue(request)
}

fun Context.runImportFromUri(uri: Uri) {
    val importRequest = OneTimeWorkRequest.Builder(ImportFromJsonWorker::class.java)
        .addTag(TAG_IMPORT)
        .setInputData(
            Data.Builder().apply {
                putString(ImportFromJsonWorker.KEY_URI_ARG, uri.toString())
            }.build()
        )
        .build()
    val updateRequest = OneTimeWorkRequest.Builder(UpdateStateWorker::class.java)
        .addTag(TAG_UPDATE)
        .build()
    WorkManager.getInstance(this).apply {
        cancelAllWorkByTag(TAG_UPDATE)
        beginWith(importRequest)
            .then(updateRequest)
            .enqueue()
    }
}