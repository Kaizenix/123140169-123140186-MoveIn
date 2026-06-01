package com.example.noteai.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.AppState
import com.example.noteai.presentation.JourneyLog
import com.example.noteai.presentation.MentalTheme
import com.example.noteai.presentation.components.AiValidationDialog
import com.example.noteai.presentation.components.BentoCard
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    appState: AppState,
    setAppState: (AppState) -> Unit,
    modifier: Modifier = Modifier
) {
    var yapText by rememberSaveable { mutableStateOf("") }
    var isBurning by remember { mutableStateOf(false) }
    var tinyWinDone by rememberSaveable { mutableStateOf(false) }
    var quoteMode by rememberSaveable { mutableStateOf("gentle") }
    
    var aiModalOpen by remember { mutableStateOf(false) }
    var aiMessage by remember { mutableStateOf("") }
    
    val tinyWins = listOf("Minum Segelas Air", "Tarik Napas 3x", "Renggangkan Tangan", "Pejam Mata 1 Menit")
    var currentTinyWinIndex by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(appState) {
        if (appState == AppState.NEUTRAL) {
            tinyWinDone = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(theme.gap)
    ) {
        // Mental Space Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isLight) theme.accentLight.copy(alpha = 0.1f) else theme.accentDark.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "MENTAL SPACE: ${theme.name}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )
            }
        }

        // YAPPING SPACE
        BentoCard(theme = theme, isLight = isLight, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = if (isLight) theme.accentLight else theme.accentDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Yapping Space",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) theme.accentLight else theme.accentDark
                    )
                }
                Text(
                    text = "${yapText.length}/250",
                    fontSize = 10.sp,
                    color = if (isLight) Color(0xFFA3A3A3) else Color(0xFF737373)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) {
                if (isBurning) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = yapText,
                        onValueChange = { if (it.length <= 250) yapText = it },
                        placeholder = {
                            Text(
                                "Ruangan ini aman. Tulis sepuasnya (max 250 karakter). Aplikasi akan bereaksi sesuai curhatanmu...",
                                fontSize = 14.sp,
                                color = Color(0xFF737373)
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (isLight) Color.Black.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.2f),
                            unfocusedContainerColor = if (isLight) Color.Black.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.2f),
                            focusedBorderColor = if (isLight) theme.accentLight else theme.accentDark,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = if (isLight) Color(0xFF262626) else Color.White,
                            unfocusedTextColor = if (isLight) Color(0xFF262626) else Color.White
                        )
                    )

                    if (yapText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                isBurning = true
                                val response = getDynamicAiResponse(yapText)
                                // Simulation
                                setAppState(AppState.OVERWHELMED)
                                addJourneyLog(JourneyLog(
                                    time = "Baru saja",
                                    mood = "Overwhelmed",
                                    task = "Yapping Space",
                                    result = "Divalidasi AI",
                                    type = "yapping",
                                    appState = AppState.OVERWHELMED,
                                    color = Color(0xFFF87171),
                                    bgColor = Color(0xFFF87171).copy(alpha = 0.1f)
                                ))
                                addMomentum(3)
                                
                                // Delay then show modal
                                aiMessage = response
                                yapText = ""
                                isBurning = false
                                aiModalOpen = true
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isLight) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = if (isLight) theme.accentLight else theme.accentDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // AMBIENCE & TINY WIN ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(theme.gap)
        ) {
            // Ambience Card
            BentoCard(
                theme = theme,
                isLight = isLight,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = if (isLight) theme.accentLight else theme.accentDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ambience",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) theme.accentLight else theme.accentDark
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (appState) {
                        AppState.OVERWHELMED -> "Feels Heavy."
                        AppState.RECOVERING -> "Breathing."
                        else -> "Neutral."
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (appState) {
                        AppState.OVERWHELMED -> "Ruang visual menyempit sesuai beban pikiranmu."
                        AppState.RECOVERING -> "Sedang memberi ruang untuk pulih."
                        else -> "Semua berjalan normal."
                    },
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }

            // Tiny Win Card
            BentoCard(
                theme = theme,
                isLight = isLight,
                modifier = Modifier.weight(1f).aspectRatio(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (tinyWinDone) {
                                    if (isLight) Color(0xFF10B981) else Color.White
                                } else {
                                    if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.1f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = if (tinyWinDone) (if (isLight) Color.White else Color.Black) else (if (isLight) theme.accentLight else theme.accentDark),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (!tinyWinDone) {
                        IconButton(
                            onClick = { currentTinyWinIndex = (currentTinyWinIndex + 1) % tinyWins.size },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = Color(0xFFA3A3A3), modifier = Modifier.size(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (tinyWinDone) "Selesai!" else tinyWins[currentTinyWinIndex],
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (!tinyWinDone) {
                            tinyWinDone = true
                            setAppState(AppState.RECOVERING)
                            addJourneyLog(JourneyLog(
                                time = "Baru saja",
                                mood = "Lelah",
                                task = tinyWins[currentTinyWinIndex],
                                result = "Recovery",
                                type = "tinywin",
                                appState = AppState.RECOVERING,
                                color = Color(0xFF60A5FA),
                                bgColor = Color(0xFF60A5FA).copy(alpha = 0.1f)
                            ))
                            addMomentum(10)
                        }
                    },
                    enabled = !tinyWinDone,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tinyWinDone) (if (isLight) Color(0xFFD1FAE5) else Color.White.copy(alpha = 0.2f)) else (if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.05f)),
                        contentColor = if (tinyWinDone) (if (isLight) Color(0xFF059669) else theme.accentDark) else (if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3))
                    ),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (tinyWinDone) "Tuntas (+10)" else "Selesaikan", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // QUOTE CARD
        BentoCard(theme = theme, isLight = isLight, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ktor Quote",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLight) Color.Black.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.5f))
                        .padding(2.dp)
                ) {
                    QuoteModeButton("gentle", quoteMode == "gentle", isLight) { quoteMode = "gentle" }
                    QuoteModeButton("roast", quoteMode == "roast", isLight) { quoteMode = "roast" }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (quoteMode == "gentle") 
                    "\"It's okay to do nothing today. Your worth isn't tied to productivity.\"" 
                else 
                    "\"Tugas nggak akan beres pakai sihir. Berhenti scroll, kerjakan sekarang.\"",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
                color = if (isLight) Color(0xFF262626) else Color.White.copy(alpha = 0.9f)
            )
        }
    }

    if (aiModalOpen) {
        AiValidationDialog(
            message = aiMessage,
            theme = theme,
            isLight = isLight,
            onDismiss = { aiModalOpen = false }
        )
    }
}

@Composable
private fun QuoteModeButton(label: String, selected: Boolean, isLight: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) (if (isLight) Color.White else Color(0xFF262626)) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.replaceFirstChar { it.uppercase() },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) (if (isLight) Color.Black else Color.White) else Color(0xFF737373)
        )
    }
}

private fun getDynamicAiResponse(text: String): String {
    val lowerText = text.lowercase()
    return when {
        lowerText.contains("tugas") || lowerText.contains("kerja") || lowerText.contains("dosen") -> 
            "Tugas yang numpuk emang bikin napas terasa sesak. Valid banget kalau kamu capek. Tinggalkan layarmu 5 menit, dunia nggak akan kiamat kok."
        lowerText.contains("sepi") || lowerText.contains("sendiri") || lowerText.contains("sedih") -> 
            "Kadang rasa sepi itu datang tiba-tiba dan berat banget. Aku di sini dengerin kamu. Coba peluk dirimu sendiri atau minum air hangat ya."
        lowerText.contains("bingung") || lowerText.contains("overthinking") || lowerText.contains("pusing") -> 
            "Terlalu banyak isi kepala emang bikin bising. Yuk kita pause sebentar. Nggak semua harus dipikirkan jawabannya malam ini."
        else -> "Aku dengar keluh kesahmu. Nggak apa-apa merasa seperti ini. Validasi emosimu, tarik napas pelan-pelan, kamu sudah bertahan dengan baik hari ini."
    }
}
