package pt.ipleiria.estg.dei.pi.mymultiprev.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Menu
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


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginClick: (String) -> Unit
) {

//    viewModel.apply {
//        checkIsLoggedIn()
//        treatLoginResponse()
//    }

    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val passwordVisible = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }

    Text(
        text = "Bem-Vindo",
        modifier = Modifier.padding(start = 32.dp, top = 32.dp),
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
                Toast.makeText(context, "Successfully Validated", Toast.LENGTH_SHORT).show()
            }
        }) {
            if (!isLoading.value) {
                Text(
                    "LOGIN",
                    fontSize = 20.sp
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(27.dp), color = Color.White)


//                viewModel.login(
//                    username,
//                    password
//                )

            }
        }
    }
//    Text(text = "Hello")

}
