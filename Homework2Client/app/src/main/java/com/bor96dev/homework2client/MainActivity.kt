package com.bor96dev.homework2client

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bor96dev.homework2client.ui.theme.Homework2ClientTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var fileManager: FileManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileManager = FileManager(this)

        enableEdgeToEdge()
        setContent {
            Homework2ClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        fileManager = fileManager
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, fileManager: FileManager) {
    var isServiceBound by remember { mutableStateOf(false) }
    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingImage by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        fileManager.setFileManagerStateListener(object :
            FileManager.FileManagerStateListener {
            override fun onServiceConnected() {
                isServiceBound = true
            }

            override fun onServiceDisconnected() {
                isServiceBound = false
            }
        })
        onDispose {
            fileManager.removeListener()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isServiceBound) "Связь установлена" else "Связь с сервером отсутствует",
            fontSize = 18.sp,
            color = if (isServiceBound) MaterialTheme.colorScheme.primary else
                MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                fileManager.switchConnectionToServer()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isServiceBound) "Отключиться от сервера" else "Подключиться к серверу")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = isServiceBound && !isLoadingImage,
            onClick = {
                scope.launch {
                    isLoadingImage = true
                    val result = fileManager.loadImage()
                    if (result != null) {
                        loadedBitmap = result
                    }
                    isLoadingImage = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Показать картинку")
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp)
        ) {
            if (isLoadingImage) {
                CircularProgressIndicator()
            } else {
                loadedBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Text("Здесь появится картинка")
            }
        }
    }
}