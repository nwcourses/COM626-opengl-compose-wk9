package com.example.openglcompose

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.openglcompose.ui.theme.OpenGLComposeTheme

class MainActivity : ComponentActivity() {

    private var surfaceTexture: SurfaceTexture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkPermissions()

        val viewModel: OpenGLAppViewModel by viewModels()
        setContent {
            OpenGLComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    OpenGLComposable(
                        modifier = Modifier.padding(innerPadding),
                        floatArrayOf(0f, 0f, 0f),
                        onSurfaceTextureAvailable = {
                           // TODO set the surfaceTexture to the parameter passed back from OpenGLView
                        })
                }
            }
        }
    }

    fun checkPermissions() {
        val permission = Manifest.permission.CAMERA
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    startCamera()
                }
            }
            launcher.launch(permission)
        } else {
            startCamera()
        }
    }

    private fun startCamera(): Boolean {
        // TODO add the camera initialisation code as shown in the notes
        return true
    }

}


// cameraPos will come from state and ViewModel via MutableStateFlow
@Composable
fun OpenGLComposable(
    modifier: Modifier, cameraPos: FloatArray = floatArrayOf(0f, 0f, 0f),
    onSurfaceTextureAvailable: (SurfaceTexture) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            OpenGLView(ctx) {
                /*
                 TODO, this is the callback specified in the OpenGLView.  Handle the parameter - the SurfaceTexture - appropriately.
                 */
            }
        },
        update = {
            // Nothing need be done here for today
        }
    )
}