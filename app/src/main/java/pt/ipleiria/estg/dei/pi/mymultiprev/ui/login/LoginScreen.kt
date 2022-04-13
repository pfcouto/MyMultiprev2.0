package pt.ipleiria.estg.dei.pi.mymultiprev.ui.login

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import retrofit2.HttpException
import java.net.HttpURLConnection


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginClick: (String) -> Unit
) {

    val TAG = "LoginComposable"

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }

    var test by remember { mutableStateOf("") }

    Text(
        text = "Bem-Vindo",
        modifier = Modifier.padding(start = 32.dp, top = 32.dp),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = test,
        modifier = Modifier.padding(start = 32.dp, top = 96.dp),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 32.dp)
    ) {

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = {
                Text(text = "Username")
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(

            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),

            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible.value) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, description)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp), onClick = {
            Log.i(TAG, "Button Login Clicked: $username / $password")

            isLoading.value = !isLoading.value

            if (username.isEmpty() and password.isNotEmpty()) {
                Toast.makeText(context, "Username is Empty", Toast.LENGTH_SHORT).show()
            }
            if (password.isEmpty() and username.isNotEmpty()) {
                Toast.makeText(context, "Password is Empty", Toast.LENGTH_SHORT).show()
            }
            if (username.isEmpty() and password.isEmpty()) {
                Toast.makeText(context, "Username and Password are Empty", Toast.LENGTH_SHORT)
                    .show()
            }
            if (username.isNotEmpty() and password.isNotEmpty()) {
                viewModel.login(username, password)
                test = username + " - " + password
                test += viewModel.isLoggedIn.toString()
                viewModel.loginResponse
//                test = viewModel.loginResponse.value.toString()
            }
        }) {
            if (!isLoading.value) {
                Text(
                    "LOGIN",
                    fontSize = 20.sp
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(27.dp), color = Color.White)
            }
        }
    }





    fun treatHTTPException(errorCode: Int) {
//        binding.viewFlipper.showPrevious()
//        Log.i(TAG, "HTTP Exception - $errorCode")
//        when (errorCode) {
//            HttpURLConnection.HTTP_UNAUTHORIZED -> {
//                binding.loginForm.textViewErrors.apply {
//                    text = getString(R.string.login_invalid_credentials)
//                    visibility = View.VISIBLE
//                }
//            }
//        }
    }

    fun treatErrorResponse() {
//        Log.i(TAG, "Timeout while connecting to API")
//        binding.viewFlipper.showPrevious()
//        val errorMsg = if (!viewModel.isNetworkAvailable())
//            getString(R.string.login_no_internet)
//        else
//            getString(R.string.login_connection_timeout)
//
//        Log.i(TAG, "Internet Connection - $viewModel.isNetworkAvailable()")
//
//        com.google.android.material.snackbar.Snackbar.make(
//            binding.root,
//            errorMsg,
//            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
//        )
//            .setAction(getString(R.string.OK)) {}.show()
    }

    fun treatSuccessResponse() {
        if (viewModel.patient.value != null) {
            Log.i(TAG, "Logged In Successfully!")
        }

        viewModel.apply {
            savePatientId()
            isLoggedIn = true
        }
    }

    when (val loginResponseResource = viewModel.loginResponse.observeAsState()) {
        is Resource.Success<*> -> {
            Log.i(TAG, "loginResponseResource is Resource.Success")
            treatSuccessResponse()
            isLoading.value = false
        }
        is Resource.Error<*> -> {
            Log.i(TAG, "loginResponseResource is Resource.Error")
            if (loginResponseResource.error is HttpException)
                treatHTTPException(loginResponseResource.error.code())
            else {
                treatErrorResponse()
            }
            isLoading.value = false
        }
    }
}
