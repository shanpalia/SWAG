package com.paliaapk.swag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Purple = Color(0xFF831BEE)
private val Ink = Color(0xFF171421)
private val Muted = Color(0xFF777184)
private val Canvas = Color(0xFFF8F7FC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(248, 247, 252)
        window.navigationBarColor = android.graphics.Color.rgb(248, 247, 252)
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContent { SwagHome() }
    }
}

@Composable
private fun SwagHome() {
    var page by remember { mutableStateOf("Home") }
    var editor by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Purple, background = Canvas, surface = Color.White)) {
        if (editor) {
            Column(Modifier.fillMaxSize().background(Canvas)) {
                Row(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { editor = false }) { Icon(Icons.Filled.ArrowBack, "Back") }
                    Text("New Project", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                }
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Editor workspace\nVideo import and editing engine will be added next", color = Muted)
                }
            }
        } else {
            Scaffold(
                containerColor = Canvas,
                bottomBar = {
                    Row(Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding().padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        BottomItem("Home", Icons.Filled.Home, page == "Home") { page = "Home" }
                        BottomItem("Templates", Icons.Filled.Collections, page == "Templates") { page = "Templates" }
                        Surface(Modifier.size(56.dp).clickable { editor = true }, shape = CircleShape, color = Purple, shadowElevation = 8.dp) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Add, "New Project", tint = Color.White, modifier = Modifier.size(34.dp)) }
                        }
                        BottomItem("Projects", Icons.Filled.VideoLibrary, page == "Projects") { page = "Projects" }
                        BottomItem("AI Studio", Icons.Filled.AutoAwesome, page == "AI Studio") { page = "AI Studio" }
                    }
                }
            ) { padding ->
                if (page == "Home") HomeContent(Modifier.padding(padding), onCreate = { editor = true })
                else SimplePage(page, Modifier.padding(padding)) { editor = true }
            }
        }
    }
}

@Composable
private fun HomeContent(modifier: Modifier = Modifier, onCreate: () -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Menu, "Menu", modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF05D9F5), Purple, Color(0xFFFF20C8)))),
                    contentAlignment = Alignment.Center) {
                    Text("S", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("SWAG", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Ink, letterSpacing = 1.sp)
                    Text("VIDEO EDITOR", fontSize = 9.sp, color = Purple, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFF17121F)) {
                    Row(Modifier.padding(horizontal = 9.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFD34E), modifier = Modifier.size(14.dp))
                        Text(" PRO", color = Color(0xFFFFD34E), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.NotificationsNone, "Notifications", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Surface(shape = CircleShape, color = Color(0xFFF0E9FF), modifier = Modifier.size(34.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Person, "Profile", tint = Purple) }
                }
            }
        }
        item {
            Box(Modifier.fillMaxWidth().height(178.dp).clip(RoundedCornerShape(25.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF16063D), Color(0xFF4010A3), Color(0xFF9D13E8))))
                .padding(18.dp)) {
                Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Edit Like a Pro", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Professional Video Editor\nwith AI Tools", color = Color.White.copy(alpha = .92f), fontSize = 14.sp, lineHeight = 19.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HeroChip("Effects", Icons.Filled.AutoAwesome)
                        HeroChip("Templates", Icons.Filled.Collections)
                        HeroChip("AI Edit", Icons.Filled.Bolt)
                    }
                }
                Icon(Icons.Filled.Movie, null, tint = Color.White.copy(alpha = .20f),
                    modifier = Modifier.align(Alignment.CenterEnd).size(88.dp))
            }
        }
        item {
            Row(Modifier.fillMaxWidth().height(108.dp).clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF9C13F5), Color(0xFF5B0BEA), Color(0xFFE329D7))))
                .clickable { onCreate() }.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(17.dp), color = Color.White, modifier = Modifier.size(62.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Add, null, tint = Purple, modifier = Modifier.size(42.dp)) }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("New Project", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Start Editing Now", color = Color.White.copy(alpha = .9f), fontSize = 14.sp)
                }
                Icon(Icons.Filled.Movie, null, tint = Color.White.copy(alpha = .25f), modifier = Modifier.size(58.dp))
            }
        }
        item {
            val tools = listOf(
                "Trim & Cut" to Icons.Filled.ContentCut, "Merge" to Icons.Filled.Layers,
                "Audio" to Icons.Filled.MusicNote, "Text" to Icons.Filled.TextFields,
                "Effects" to Icons.Filled.AutoAwesome, "Filters" to Icons.Filled.Palette,
                "Transition" to Icons.Filled.CompareArrows, "Overlay" to Icons.Filled.Image,
                "Speed" to Icons.Filled.Speed, "More" to Icons.Filled.Apps
            )
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                tools.chunked(5).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { (name, icon) ->
                            Column(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White)
                                .clickable { onCreate() }.padding(vertical = 11.dp),
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Icon(icon, name, tint = Purple, modifier = Modifier.size(26.dp))
                                Text(name, fontSize = 9.sp, color = Ink, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureCard("AI Studio", "AI Removal, Auto Captions, Enhance & More", Icons.Filled.Memory,
                    listOf(Color(0xFF071A71), Color(0xFF3D0B9C), Color(0xFF00BFD8)), Modifier.weight(1f), onCreate)
                FeatureCard("Templates", "Reels, Status, Attitude & Trending", Icons.Filled.Layers,
                    listOf(Color(0xFF241078), Color(0xFF4D0BC0), Color(0xFFB522F2)), Modifier.weight(1f), onCreate)
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("My Projects", Modifier.weight(1f), fontSize = 21.sp, color = Ink, fontWeight = FontWeight.ExtraBold)
                Text("View All  ›", color = Purple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProjectTile("Project_01", "00:15", listOf(Color(0xFFFFB36B), Color(0xFF6B3C9B)), Icons.Filled.Person, Modifier.weight(1f), onCreate)
                ProjectTile("Project_02", "00:30", listOf(Color(0xFF203C50), Color(0xFF111827)), Icons.Filled.TwoWheeler, Modifier.weight(1f), onCreate)
                ProjectTile("Project_03", "00:20", listOf(Color(0xFFB8C8CC), Color(0xFF263B45)), Icons.Filled.DirectionsWalk, Modifier.weight(1f), onCreate)
                ProjectTile("Project_04", "00:28", listOf(Color(0xFFFFB44D), Color(0xFF4A2415)), Icons.Filled.DirectionsCar, Modifier.weight(1f), onCreate)
            }
        }
        item {
            Text("By PALIAAPK HUB  •  Developer By shanpalia", color = Muted, fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun HeroChip(label: String, icon: ImageVector) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color.Black.copy(alpha = .28f)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(13.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, icon: ImageVector, colors: List<Color>, modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.clip(RoundedCornerShape(19.dp)).background(Brush.linearGradient(colors))
        .clickable { onClick() }.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFFBFFBFF), modifier = Modifier.size(31.dp))
        Spacer(Modifier.width(6.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = Color.White.copy(alpha = .9f), fontSize = 9.sp, lineHeight = 12.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Filled.ChevronRight, null, tint = Color.White, modifier = Modifier.size(17.dp))
    }
}

@Composable
private fun ProjectTile(title: String, time: String, colors: List<Color>, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clickable { onClick() }) {
        Box(Modifier.fillMaxWidth().height(78.dp).clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(colors)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White.copy(alpha = .9f), modifier = Modifier.size(34.dp))
            Text(time, color = Color.White, fontSize = 9.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).background(Color.Black.copy(alpha = .65f), RoundedCornerShape(5.dp)).padding(horizontal = 4.dp, vertical = 2.dp))
        }
        Text(title, color = Ink, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun BottomItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.clip(RoundedCornerShape(14.dp)).clickable { onClick() }.padding(horizontal = 7.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, label, tint = if (selected) Purple else Ink, modifier = Modifier.size(23.dp))
        Text(label, color = if (selected) Purple else Ink, fontSize = 9.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun SimplePage(title: String, modifier: Modifier = Modifier, onCreate: () -> Unit) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("Explore SWAG $title", color = Muted)
        Button(onClick = onCreate) { Text("Start a project") }
    }
}
