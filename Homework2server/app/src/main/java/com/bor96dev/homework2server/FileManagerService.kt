package com.bor96dev.homework2server

import android.content.res.AssetFileDescriptor
import android.content.res.Resources
import android.os.RemoteException

class FileManagerService(
    private val resources: Resources
) : IFileManager.Stub() {
    @Throws(RemoteException::class)
    override fun getFile(): AssetFileDescriptor {
        return resources.openRawResourceFd(R.raw.london)
    }
}