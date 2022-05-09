package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
) {

    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    //TODO identificar numero da prescricao
//    viewModel.setPrescriptionItemId(intent.getStringExtra(Constants.PRESCRIPTION_ITEM_ID)!!)

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
        android.Manifest.permission.CAMERA
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
//                    scaffoldState.snackbarHostState.showSnackbar("An error occurred while trying to take a picture")
                    Log.i("ERROR", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    viewModel.setPrescriptionItemPhoto(
                        viewModel.prescriptionItemId.value!!,
                        savedUri
                    )
                }
            })
    }


    when (cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            // 3
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

                IconButton(
                    modifier = Modifier.padding(bottom = 20.dp),
                    onClick = {
                        Log.i("PhotoScreen", "Button Clicked")
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
                        "The camera is important for this app. Please grant the permission."
                    } else {
                        // If it's the first time the user lands on this feature, or the user
                        // doesn't want to be asked again for this permission, explain that the
                        // permission is required
                        "Camera permission required for this feature to be available. " +
                                "Please grant the permission"
                    }
                Text(textToShow, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("OK")
                }
            }
        }
    }
}

//    suspend fun Context.getCameraProvider(): ProcessCameraProvider =
//        suspendCoroutine { continuation ->
//            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
//                cameraProvider.addListener({
//                    continuation.resume(cameraProvider.get())
//                }, ContextCompat.getMainExecutor(this))
//            }
//        }


//private fun takePhoto(
//    filename: String,
//    imageCapture: ImageCapture,
//    outputDirectory: File,
//    executor: Executor,
//    onImageCaptured: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//) {
//
//    val photoFile = File(
//        outputDirectory,
//        filename
//    )
//
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
//        override fun onError(exception: ImageCaptureException) {
//            Log.i("kilo", "Take photo error:", exception)
//            onError(exception)
//        }
//
//        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//            val savedUri = Uri.fromFile(photoFile)
//            onImageCaptured(savedUri)
//        }
//    })
//}


//fun takePhoto() {
//    val imageCapture = imageCapture ?: return
//
//    val photoFile = File(
//        outputDirectory,
//        SimpleDateFormat(
//            "yyyy-MM-dd-HH-mm-ss-SSS", Locale.US
//        ).format(System.currentTimeMillis()) + ".jpg"
//    )
//
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//    imageCapture.takePicture(
//        outputOptions,
//        ContextCompat.getMainExecutor(LocalContext.current),
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onError(exc: ImageCaptureException) {
//                setResult(CameraResult.RESULT_ERROR)
//                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//            }
//
//            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                val savedUri = Uri.fromFile(photoFile)
//                viewModel.setPrescriptionItemPhoto(
//                    viewModel.prescriptionItemId.value!!,
//                    savedUri
//                )
//            }
//        })
//}
//
//fun ImageCapture.takePicture(
//    context: Context,
//    lensFacing: Int,
//    onImageCaptured: (Uri, Boolean) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//) {
//    val outputDirectory = context.getOutputDirectory()
//    // Create output file to hold the image
//    val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
//    val outputFileOptions = getOutputFileOptions(lensFacing, photoFile)
//
//    this.takePicture(
//        outputFileOptions,
//        Executors.newSingleThreadExecutor(),
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
//                // If the folder selected is an external media directory, this is
//                // unnecessary but otherwise other apps will not be able to access our
//                // images unless we scan them using [MediaScannerConnection]
//                val mimeType = MimeTypeMap.getSingleton()
//                    .getMimeTypeFromExtension(savedUri.toFile().extension)
//                MediaScannerConnection.scanFile(
//                    context,
//                    arrayOf(savedUri.toFile().absolutePath),
//                    arrayOf(mimeType)
//                ) { _, uri ->
//
//                }
//                onImageCaptured(savedUri, false)
//            }
//            override fun onError(exception: ImageCaptureException) {
//                onError(exception)
//            }
//        })
//}

