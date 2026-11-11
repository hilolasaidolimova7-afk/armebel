package uz.arfurniture

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionScreen(onRequest: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AR ishlashi uchun kameraga ruxsat kerak.", textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRequest) { Text("Ruxsat berish") }
    }
}
