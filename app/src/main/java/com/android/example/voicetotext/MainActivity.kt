package com.android.example.voicetotext

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.example.voicetotext.ui.theme.VoiceTotextTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceTotextTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() }) { innerPadding ->
                    VoiceRecognitionScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(
        title = {
            Text(text = "Voice to Text App", color = Color.White)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun VoiceRecognitionScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf("") }

    // Launcher for speech recognition
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (spokenText != null) {
                prompt = spokenText  // Update prompt with recognized text
            } else {
                Toast.makeText(context, "Failed to recognize speech", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center  // Center the Row in the middle of the screen
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)  // Horizontal padding for better alignment
        ) {

            BasicTextField(
                value = prompt,
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (prompt.isEmpty()) {
                        Text("Type or speak your message...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )


            // Add a button to start voice recognition
            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                        intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                        speechRecognizerLauncher.launch(intent)
                    } else {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            100
                        )
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Speak")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VoiceTotextTheme {
        VoiceRecognitionScreen()
    }
}

