package com.example.pomopodorotimer.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomopodorotimer.ui.theme.PomopodoroTimerTheme
import com.example.pomopodorotimer.viewmodel.AuthState
import com.example.pomopodorotimer.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onHomeClicked: (String) -> Unit,
    onBackClicked: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val authState by authViewModel.authState.collectAsState()
    val email by authViewModel.email.collectAsStateWithLifecycle()
    val password by authViewModel.password.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Authenticated -> onHomeClicked("test") //talk about group how we should do this
            is AuthState.Error -> Toast.makeText(context,
                (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {

                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackClicked()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                } ,
                actions = {},
            )
        }
    ) { innerPadding ->
        RegisterContent(
            modifier = Modifier.padding(innerPadding),
            email = email,
            password = password,
            onRegisterClicked = authViewModel::signUp,
            onEmailChanged = authViewModel::onEmailChanged,
            onPasswordChanged = authViewModel::onPasswordChange
        )
    }
}


@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    onRegisterClicked: (String, String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String)-> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pompodoro App")
        Text("Register")
        OutlinedTextField(
            value = email,
            onValueChange ={
                onEmailChanged(it)
            },
            label = {
                Text("Email")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange ={
                onPasswordChanged(it)
            },
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(onClick = {
            onRegisterClicked(email, password)
        }) {
            Text("Register")
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    PomopodoroTimerTheme {
        RegisterContent(
            email = "",
            password = "",
            onRegisterClicked = { _, _ ->

            },
            onEmailChanged = {},
            onPasswordChanged = {}
        )
    }
}