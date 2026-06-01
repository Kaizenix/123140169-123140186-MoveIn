package com.example.noteai.presentation.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.AppState
import com.example.noteai.presentation.JourneyLog
import com.example.noteai.presentation.MentalTheme
import com.example.noteai.presentation.components.BentoCard
import kotlinx.coroutines.delay

@Composable
fun DetoxScreen(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    setAppState: (AppState) -> Unit,
    modifier: Modifier = Modifier
) {
    var bubbles by remember { mutableStateOf(List(12) { false }) }
    var isBreathing by rememberSaveable { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("idle") }
    var isDoomLock by rememberSaveable { mutableStateOf(false) }
    var timeLeft by rememberSaveable { mutableIntStateOf(30) } // Reduced to 30s for demo as requested

    // Breathing Logic
    LaunchedEffect(isBreathing) {
        if (!isBreathing) {
            breathPhase = "idle"
            return@LaunchedEffect
        }
        while (isBreathing) {
            breathPhase = "inhale"
            delay(4000)
            if (!isBreathing) break
            breathPhase = "hold"
            delay(2000)
            if (!isBreathing) break
            breathPhase = "exhale"
            delay(4000)
        }
    }

    // Doomscroll Lock Logic
    LaunchedEffect(isDoomLock) {
        if (isDoomLock) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isDoomLock = false
            addMomentum(20)
            timeLeft = 30 // Reset for next time
        }
    }

    // Pop-it Reset Logic
    LaunchedEffect(bubbles) {
        if (bubbles.all { it }) {
            delay(800)
            bubbles = List(12) { false }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = null,
                    tint = if (isLight) Color(0xFF6366F1) else Color(0xFF818CF8),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detox Space",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )
            }
            Text(
                text = "Alihkan pikiran dari doomscrolling.",
                fontSize = 14.sp,
                color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
            )
        }

        // ZEN BREATHING
        BentoCard(
            theme = theme,
            isLight = isLight,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Air, null, tint = if (isLight) theme.accentLight else theme.accentDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Zen Breathing",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) theme.accentLight else theme.accentDark
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val scale by animateFloatAsState(
                        targetValue = when (breathPhase) {
                            "inhale" -> 2.5f
                            "hold" -> 2.5f
                            "exhale" -> 1f
                            else -> 1f
                        },
                        animationSpec = tween(
                            durationMillis = when (breathPhase) {
                                "inhale" -> 4000
                                "hold" -> 2000
                                "exhale" -> 4000
                                else -> 500
                            },
                            easing = LinearEasing
                        )
                    )
                    val opacity by animateFloatAsState(
                        targetValue = when (breathPhase) {
                            "inhale" -> 0.2f
                            "hold" -> 0.4f
                            "exhale" -> 0f
                            else -> 0f
                        },
                        animationSpec = tween(
                            durationMillis = when (breathPhase) {
                                "inhale" -> 4000
                                "hold" -> 2000
                                "exhale" -> 4000
                                else -> 500
                            },
                            easing = LinearEasing
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize(scale)
                            .clip(CircleShape)
                            .background((if (isLight) Color(0xFF3B82F6) else Color.White).copy(alpha = opacity))
                    )

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isLight) Color.White else Color.Black.copy(alpha = 0.8f))
                            .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable {
                                if (isBreathing) {
                                    isBreathing = false
                                    setAppState(AppState.RECOVERING)
                                    addJourneyLog(JourneyLog(
                                        time = "Baru saja",
                                        mood = "Cemas",
                                        task = "Zen Breathing",
                                        result = "Tenang",
                                        type = "breathing",
                                        appState = AppState.RECOVERING,
                                        color = Color(0xFF60A5FA),
                                        bgColor = Color(0xFF60A5FA).copy(alpha = 0.1f)
                                    ))
                                    addMomentum(5)
                                } else {
                                    isBreathing = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBreathing) Icons.Default.CheckCircle else Icons.Default.Air,
                            contentDescription = null,
                            tint = if (isLight) Color(0xFF3B82F6) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = when (breathPhase) {
                        "idle" -> "Tap angin untuk mulai"
                        "inhale" -> "Tarik Napas..."
                        "hold" -> "Tahan..."
                        "exhale" -> "Hembuskan..."
                        else -> ""
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )

                Text(
                    text = if (isBreathing) "Tap centang untuk selesai (+5 Momentum)." else "Ritme 4-7-8",
                    fontSize = 10.sp,
                    color = if (isLight) Color(0xFF737373) else Color.White.copy(alpha = 0.5f)
                )
            }
        }

        // DIGITAL POP-IT
        BentoCard(theme = theme, isLight = isLight, modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Digital Pop-It", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isLight) Color(0xFFE0E7FF) else Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Fidgeting", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF4F46E5) else Color.White.copy(alpha = 0.7f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (row in 0 until 3) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (col in 0 until 4) {
                                val index = row * 4 + col
                                val isPopped = bubbles[index]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isPopped) {
                                                if (isLight) Color(0xFFE5E7EB) else Color.Black.copy(alpha = 0.8f)
                                            } else {
                                                if (isLight) Color.White else Color.White.copy(alpha = 0.1f)
                                            }
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isPopped) (if (isLight) Color(0xFFD1D5DB) else Color.White.copy(alpha = 0.05f)) else (if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.2f)),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (!isPopped) {
                                                val newBubbles = bubbles.toMutableList()
                                                newBubbles[index] = true
                                                bubbles = newBubbles
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isPopped) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (isLight) Color(0xFF9CA3AF) else Color.White.copy(alpha = 0.2f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // DOOMSCROLL LOCK
        BentoCard(theme = theme, isLight = isLight, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isLight) Color(0xFFFEF2F2) else Color(0xFF262626)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDoomLock) Icons.Default.Lock else Icons.Default.MonitorWeight, // MonitorOff equivalent
                        contentDescription = null,
                        tint = if (isDoomLock) Color(0xFFEF4444) else Color(0xFFA3A3A3),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Doomscroll Lock", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
                    if (isDoomLock) {
                        Text(
                            text = "Terkunci: ${timeLeft / 60}:${(timeLeft % 60).toString().padStart(2, '0')}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) Color(0xFFEF4444) else Color(0xFFF87171)
                        )
                    } else {
                        Text(
                            text = "Blokir UI & Medsos 30 detik. Dapatkan +20 Momentum.",
                            fontSize = 10.sp,
                            color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                        )
                    }
                }
                if (!isDoomLock) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isLight) Color(0xFF171717) else Color.White)
                            .clickable { isDoomLock = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Mulai",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}
