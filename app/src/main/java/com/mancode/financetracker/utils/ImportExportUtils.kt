package com.mancode.financetracker.utils

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mancode.financetracker.database.DatabaseJson
import com.mancode.financetracker.ui.MainActivityNav

const val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0
const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1

fun MainActivityNav.checkPermissionAndExport() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // TODO rationale for permissions
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
        }
    } else {
        exportJson()
    }
}

fun MainActivityNav.exportJson() {
    if (DatabaseJson.exportJson()) {
        Toast.makeText(this,
                "Database exported to " + DatabaseJson.JSON_FILE.path,
                Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(this,
                "Database export failed",
                Toast.LENGTH_SHORT).show()
    }
}

fun MainActivityNav.checkPermissionAndImport() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // TODO rationale for permissions
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }
    } else {
        importJson()
    }
}

fun MainActivityNav.importJson() {
    if (DatabaseJson.importJson()) {
        Toast.makeText(this,
                "Import started with " + DatabaseJson.JSON_FILE.path,
                Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(this,
                "Database restore failed",
                Toast.LENGTH_SHORT).show()
    }
}

