package com.bor96dev.homework2client

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import com.bor96dev.homework2server.IFileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FileManager(val context: Context) {
    private var fileService: IFileManager? = null
    private var listener: FileManagerStateListener? = null
    val isServiceConnected: Boolean get() = fileService != null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            fileService = IFileManager.Stub.asInterface(iBinder)
            listener?.onServiceConnected()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            fileService = null
            listener?.onServiceDisconnected()
        }
    }

    fun setFileManagerStateListener(listener: FileManagerStateListener) {
        this.listener = listener
    }

    fun switchConnectionToServer() {
        if (isServiceConnected) {
            disconnectFromServer()
        } else {
            connectToServer()
        }
    }

    private fun connectToServer() {
        val intent = Intent("com.bor96dev.homework2server.FileService").apply {
            setPackage("com.bor96dev.homework2server")
        }
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    suspend fun loadImage(): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val afd: AssetFileDescriptor? = fileService?.getFile()
            afd?.use { descriptor ->
                descriptor.createInputStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun disconnectFromServer() {
        if (isServiceConnected) {
            context.unbindService(serviceConnection)
            fileService = null
            listener?.onServiceDisconnected()
        }
    }

    fun removeListener() {
        this.listener = null
    }

    interface FileManagerStateListener {
        fun onServiceConnected()
        fun onServiceDisconnected()
    }
}