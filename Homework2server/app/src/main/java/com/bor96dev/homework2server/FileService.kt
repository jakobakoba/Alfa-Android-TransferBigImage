package com.bor96dev.homework2server

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FileService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return FileManagerService(resources)
    }
}