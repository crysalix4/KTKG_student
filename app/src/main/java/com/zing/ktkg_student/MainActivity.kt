package com.zing.ktkg_student

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zing.ktkg_student.problem1.Problem1Activity
import com.zing.ktkg_student.problem2.Problem2Activity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                onProblem1Click = {
                    startActivity(Intent(this, Problem1Activity::class.java))
                },
                onProblem2Click = {
                    startActivity(Intent(this, Problem2Activity::class.java))
                }
            )
        }
    }
}

@Composable
fun MainScreen(
    onProblem1Click: () -> Unit,
    onProblem2Click: () -> Unit
) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Main Menu", style = MaterialTheme.typography.h6) },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 8.dp
                )
            }
        ) { innerPadding ->
            // Nền gradient cho toàn màn hình
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary.copy(alpha = 0.2f),
                                MaterialTheme.colors.background
                            )
                        )
                    )
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Problem",
                        style = MaterialTheme.typography.h4,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colors.surface)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = onProblem1Click,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text(text = "Problem 1")
                            }
                            Button(
                                onClick = onProblem2Click,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text(text = "Problem 2")
                            }
                        }
                    }
                }
            }
        }
    }
}
