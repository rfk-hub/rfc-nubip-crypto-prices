package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Page()
            }
        }
    }
}

@Composable
fun Page() {
    var symbol by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = symbol,
            onValueChange = { symbol = it },
            label = { Text("Введіть символ") }
        )
        Button(onClick = {
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        val client = OkHttpClient()
                        val symbolUpper = symbol.uppercase()
                        val url = "https://www.binance.com/api/v3/ticker/price?symbol=${symbolUpper}"
                        val request = Request.Builder()
                            .url(url)
                            .build()
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val jsonObject = responseBody?.let { JSONObject(it) }
                            val priceValue = jsonObject?.getString("price")
                            priceValue
                        } else {
                            "Помилка: ${response.code}"
                        }
                    } catch (e: Exception) {
                        "Виняток: ${e.message}"
                    }
                }
                price = result.toString()
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Взяти ціну")
        }
        Text(
            text = price,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
