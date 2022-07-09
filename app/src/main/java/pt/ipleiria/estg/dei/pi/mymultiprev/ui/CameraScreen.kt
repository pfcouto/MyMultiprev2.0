package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    prescriptionId: String,
    navController: NavHostController
) {

    var isTakingPhoto by remember { mutableStateOf(false) }

    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    viewModel.setPrescriptionItemId(prescriptionId)


    LaunchedEffect(lensFacing) {
        val cameraProvider = ProcessCameraProvider.getInstance(context)
        cameraProvider.get().unbindAll()
        cameraProvider.get().bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }


    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    fun Context.getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    fun takePhoto() {
        val imageCapture = imageCapture
        val photoFile: File = File(
            context.getOutputDirectory(),
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss-SSS", Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
//                    setResult(CameraResult.RESULT_ERROR)
                    this.onError(exc)
                    Log.i("ERROR", "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    val savedUri = Uri.fromFile(photoFile)

                    viewModel.setPrescriptionItemPhoto(
                        viewModel.prescriptionItemId.value!!,
                        savedUri
                    )
                    navController.popBackStack()
                }
            })

    }

    var counter by remember { mutableStateOf(0) }

    when (cameraPermissionState.status) {

        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

                if (isTakingPhoto) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Teal, modifier = Modifier.size(56.dp))
                    }
                } else {
                    IconButton(
                        modifier = Modifier.padding(bottom = 20.dp),
                        enabled = !isTakingPhoto,
                        onClick = {
                            Log.i("PhotoScreen", "Button Clicked")
                            isTakingPhoto = true
                            takePhoto()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Sharp.Lens,
                                contentDescription = "Take picture",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(1.dp)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    )
                }
            }
        }
        is PermissionStatus.Denied -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val textToShow =
                    if ((cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                        // If the user has denied the permission but the rationale can be shown,
                        // then gently explain why the app requires this permission
                        "A Câmera é importante para esta aplicação. Por favor dê permissão."
                    } else {
                        // If it's the first time the user lands on this feature, or the user
                        // doesn't want to be asked again for this permission, explain that the
                        // permission is required
                        "É necessário dar permissão para esta funcionalidade ficar disponível. Por favaor dê permissão."
                    }
                Text(textToShow, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    counter += 1
                    if (counter != 2) {
                        cameraPermissionState.launchPermissionRequest()
                    } else {
                        navController.popBackStack()
                        Toast.makeText(
                            context,
                            "Terá de ir às definições do telemóvel para dar permissão. Obrigado.", Toast.LENGTH_LONG
                        ).show()
                    }

                }) {
                    Text("OK")
                }
            }
        }
        else -> {
            Toast.makeText(context, "Esteve AQUI", Toast.LENGTH_LONG).show()
        }
    }
}