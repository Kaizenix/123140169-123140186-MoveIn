package com.example.noteai.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.components.*
import com.example.noteai.presentation.theme.MoveInTheme
import com.example.noteai.presentation.theme.NoteAITheme

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    NoteAITheme(darkTheme = true) {
        MoveInScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MoveIn.",
                    style = MoveInTheme.typography.displayLarge.copy(
                        fontSize = 34.sp,
                        color = MoveInTheme.colors.textPrimary
                    )
                )

                Text(
                    text = "Your Anti-Toxic Productivity Space",
                    style = MoveInTheme.typography.bodyMedium.copy(
                        color = MoveInTheme.colors.textSecondary
                    )
                )

                Spacer(modifier = Modifier.height(48.dp))

                MoveInCard(
                    contentPadding = 24.dp
                ) {
                    Text(
                        text = "Masuk dulu, lalu pilih mood kamu hari ini.",
                        style = MoveInTheme.typography.bodyLarge.copy(
                            color = MoveInTheme.colors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MoveInTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email atau username",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MoveInTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    MoveInPrimaryButton(
                        text = "Masuk ke MoveIn",
                        onClick = {
                            val userName = email.substringBefore("@")
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            onLoginSuccess(userName)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Belum punya akun? Register",
                            style = MoveInTheme.typography.labelLarge.copy(
                                color = MoveInTheme.colors.accentBlue
                            )
                        )
                    }
                }
            }
        }
    }
}
