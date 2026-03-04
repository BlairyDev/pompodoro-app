package com.example.pomopodorotimer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomopodorotimer.viewmodel.AuthState
import com.example.pomopodorotimer.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onHomeClicked: (String) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val email by authViewModel.email.collectAsStateWithLifecycle()
    val password by authViewModel.password.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Authenticated -> onHomeClicked("test")// must change later talk to group how to approach this backend
            is AuthState.Error -> Unit
            else -> {}
        }
    }


    LoginContent(
        email = email,
        password = password,
        onLoginClicked = authViewModel::logIn,
        onHomeClicked = onHomeClicked,
        onEmailChanged = authViewModel::onEmailChanged,
        onPasswordChanged = authViewModel::onPasswordChange,
        modifier = modifier
    )
}


@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    onLoginClicked: (String, String) -> Unit,
    onHomeClicked: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String)-> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
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
            onLoginClicked(email, password)
        }) {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(modifier: Modifier = Modifier) {
}