package pt.ipleiria.estg.dei.pi.mymultiprev.ui.login

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.LoginResponse
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.DarkRed
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.myColors
import retrofit2.HttpException
import java.net.HttpURLConnection


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (String) -> Unit
) {

    val TAG = "LoginComposable"

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isErrorUsername by rememberSaveable { mutableStateOf(false) }
    var isErrorPassword by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var netWorkIssueMessage by remember { mutableStateOf("") }
    var isNetworkIssue by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val response = viewModel.loginResponse.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.myColors.background)
    ) {

        Text(
            text = "Bem-Vindo",
            modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 32.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 32.dp)

        ) {
            if (!isLoading) {

                val customTextSelectionColors = TextSelectionColors(
                    handleColor = Teal,
                    backgroundColor = Teal
                )

                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    OutlinedTextField(
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Teal,
                            cursorColor = Teal,
                            focusedLabelColor = Teal,
                            textColor = MaterialTheme.colors.onBackground
                        ),
                        value = username,
                        onValueChange = {
                            username = it
                            isErrorUsername = it.isEmpty()
                        },
                        isError = isErrorUsername,
                        singleLine = true,
                        label = {
                            Text(text = "Nome de Utilizador")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )

                    Text(
                        text = if (isErrorUsername) "Nome de Utilizador está vazio!" else "",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Teal,
                            cursorColor = Teal,
                            focusedLabelColor = Teal,
                            textColor = MaterialTheme.colors.onBackground
                        ),
                        isError = isErrorPassword,
                        value = password,
                        onValueChange = {
                            password = it
                            isErrorPassword = it.isEmpty()
                        },
                        label = {
                            Text(text = "Palavra-Passe")
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff,
                                    if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )

                    Text(
                        text = if (isErrorPassword) "Palavra-Passe está vazia!" else "",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center
                    )

                    if (isErrorUsername and isErrorPassword and username.isNotEmpty() and password.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                color = MaterialTheme.colors.error,
                                text = "Nome de Utilizador / Palavra-Passe inválidos",
                                maxLines = 1
                            )
                        }
                    }
                }

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                    border = BorderStroke(1.dp, Teal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp), onClick = {
                        Log.i(TAG, "Button Login Clicked: $username / $password")
                        if (isLoading) {
                            return@Button
                        }

                        if (username.isEmpty() and password.isNotEmpty()) {
                            isErrorUsername = true
                        }
                        if (password.isEmpty() and username.isNotEmpty()) {
                            isErrorPassword = true
                        }
                        if (username.isEmpty() and password.isEmpty()) {
                            isErrorUsername = true
                            isErrorPassword = true

                        }
                        if (username.isNotEmpty() and password.isNotEmpty()) {
                            isErrorUsername = false
                            isErrorPassword = false
                            isLoading = true
                            focusManager.clearFocus()
                            viewModel.login(username.trim(), password.trim())
                        }
                    }) {
                    Text(
                        "ENTRAR",
                        fontSize = 20.sp,
                        color = MaterialTheme.myColors.onBackground
                    )
                }


                if (isNetworkIssue) {
                    Text(
                        modifier = Modifier.padding(top = 70.dp),
                        color = DarkRed,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        text = netWorkIssueMessage
                    )
                }


            } else {
                CircularProgressIndicator(
                    color = Teal,
                    modifier = Modifier
                        .size(68.dp)
                        .fillMaxSize()
                )
            }
        }
    }


    fun treatHTTPException(errorCode: Int) {
        Log.i(TAG, "HTTP Exception - $errorCode")
        when (errorCode) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                isErrorUsername = true
                isErrorPassword = true
            }
        }
    }

    fun treatErrorResponse() {
        Log.i(TAG, "Timeout while connecting to API")
        netWorkIssueMessage = if (!viewModel.isNetworkAvailable())
            "Sem conexão à internet."
        else
            "Não foi possível conectar. Tente novamente mais tarde."

        isNetworkIssue = true

        Log.i(TAG, "Internet Connection - $viewModel.isNetworkAvailable()")
    }

    fun treatLoginResponse(response: Resource<LoginResponse>) {
        Log.d(TAG, "response: ${response.data}")
        when (response) {
            is Resource.Success -> {
                viewModel.savePatientId()
                viewModel.isLoggedIn = true
                netWorkIssueMessage = "LOGGED SUCCESSFULLY"
                isLoading = false

                onLoginSuccess("")
            }
            is Resource.Error -> {
                if (response.error is HttpException) treatHTTPException(
                    response.error.code()
                ) else treatErrorResponse()

                isLoading = false
            }
            else -> {
            }
        }
    }

    if (response.value != null) {
        treatLoginResponse(response.value!!)
    }
}