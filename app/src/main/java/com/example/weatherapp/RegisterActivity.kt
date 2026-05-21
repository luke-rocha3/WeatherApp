package com.example.weatherapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activity = LocalContext.current as Activity
            RegisterPage(activity)
        }
    }
}

@Composable
fun RegisterPage(activity: Activity) {
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var repSenha by rememberSaveable { mutableStateOf("") }

    // Condição de habilitação do botão (Passo 6)
    val isEnabled = nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && senha == repSenha

    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val modifierInput = Modifier.fillMaxWidth(0.9f)

        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = modifierInput)
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = modifierInput)
        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = modifierInput, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(value = repSenha, onValueChange = { repSenha = it }, label = { Text("Repetir Senha") }, modifier = modifierInput, visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.size(16.dp))

        Row {
            Button(
                enabled = isEnabled,
                onClick = {
                    Toast.makeText(activity, "Registro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    activity.finish()
                }
            ) { Text("Registrar") }

            Spacer(modifier = Modifier.size(16.dp))

            Button(onClick = { nome = ""; email = ""; senha = ""; repSenha = "" }) {
                Text("Limpar")
            }
        }
    }
}