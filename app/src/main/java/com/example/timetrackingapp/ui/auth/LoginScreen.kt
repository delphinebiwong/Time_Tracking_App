package com.example.timetrackingapp.ui.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val currentUser by viewModel.currentUser.collectAsState()
    val signInError by viewModel.signInError.collectAsState()
    
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            isLoading = false
            onLoginSuccess()
        }
    }

    LaunchedEffect(signInError) {
        signInError?.let {
            isLoading = false
            errorMessage = "Firebase Error: $it"
            viewModel.clearError()
        }
    }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("443218553527-u87t3jbrd2pjtbejbjjkv7dviuab80q4.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            isLoading = false
            if (result.resultCode != Activity.RESULT_CANCELED) {
                errorMessage = "Google Sign-In failed (Code: ${result.resultCode})."
            }
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                errorMessage = null
                viewModel.signInWithGoogle(token)
            } ?: run {
                isLoading = false
                errorMessage = "Error: Google did not provide an ID Token."
            }
        } catch (e: ApiException) {
            isLoading = false
            errorMessage = when (e.statusCode) {
                10 -> "Developer Error (10): Check SHA-1 in Firebase and google-services.json."
                7 -> "Network Error: Please check your internet connection."
                12500 -> "Sign-In Error (12500): Check your Firebase configuration."
                else -> "Google Error: ${e.statusCode}"
            }
            Log.e("LoginScreen", "Google API Error: ${e.statusCode}", e)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Refresh, // Using a standard icon to avoid unresolved reference
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Time Tracker Pro", 
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Track time perfectly",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (errorMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Signing in...", style = MaterialTheme.typography.labelMedium)
        } else {
            Button(
                onClick = { 
                    isLoading = true
                    errorMessage = null
                    // Force account picker by signing out first
                    googleSignInClient.signOut().addOnCompleteListener {
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(text = "Sign in with Google", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}