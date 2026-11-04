package com.paliaapk.swag

import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay

private val Purple = Color(0xFF831BEE)
private val Ink = Color(0xFF171421)
private val Muted = Color(0xFF777184)
private val Canvas = Color(0xFFF8F7FC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(248, 247, 252)
        window.navigationBarColor = android.graphics.Color.rgb(248, 247, 252)
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContent { SwagApp() }
    }
}

@Composable
private fun SwagApp() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var showBrandSplash by remember { mutableStateOf(true) }
    var permissionDenied by remember { mutableStateOf(false) }
    var pendingGallery by remember { mutableStateOf(false) }
    var page by remember { mutableStateOf("Home") }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    val videoPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedVideo = uri
            page = "Editor"
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            permissionDenied = false
            if (pendingGallery) videoPicker.launch("video/*")
        } else {
            permissionDenied = true
        }
        pendingGallery = false
    }
    val launchGallery = {
        if (ContextCompat.checkSelfPermission(context, videoPermission) == PackageManager.PERMISSION_GRANTED) {
            videoPicker.launch("video/*")
        } else {
            pendingGallery = true
            permissionLauncher.launch(videoPermission)
        }
    }
    BackHandler(enabled = !showBrandSplash) {
        when {
            page == "Editor" -> page = "Home"
            page != "Home" -> page = "Home"
            else -> activity?.moveTaskToBack(true)
        }
    }

    LaunchedEffect(Unit) {
        delay(1700)
        showBrandSplash = false
        if (ContextCompat.checkSelfPermission(context, videoPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(videoPermission)
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = Purple, background = Canvas, surface = Color.White)) {
        when {
            showBrandSplash -> BrandSplash()
            page == "Editor" -> EditorScreen(
                videoUri = selectedVideo,
                onBack = { page = "Home" },
                onPickVideo = { launchGallery() }
            )
            else -> Scaffold(
                containerColor = Canvas,
                bottomBar = {
                    Row(
                        Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding().padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomItem("Home", Icons.Filled.Home, page == "Home") { page = "Home" }
                        BottomItem("Templates", Icons.Filled.Collections, page == "Templates") { page = "Templates" }
                        Surface(
                            Modifier.size(56.dp).clickable { launchGallery() },
                            shape = CircleShape, color = Purple, shadowElevation = 8.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Add, "New Project", tint = Color.White, modifier = Modifier.size(34.dp))
                            }
                        }
                        BottomItem("Projects", Icons.Filled.VideoLibrary, page == "Projects") { page = "Projects" }
                        BottomItem("AI Studio", Icons.Filled.AutoAwesome, page == "AI Studio") { page = "AI Studio" }
                    }
                }
            ) { padding ->
                when (page) {
                    "Home" -> HomeContent(Modifier.padding(padding), onCreate = { launchGallery() })
                    "Templates" -> FeaturePage("Templates", "Choose a video first to start creating with a template.", "Pick Video") { launchGallery() }
                    "Projects" -> FeaturePage("My Projects", "Your imported videos will open in the editor. Project saving is coming in a later update.", "Import Video") { launchGallery() }
                    "AI Studio" -> FeaturePage("AI Studio", "AI tools are being added. You can already import a video and preview it in SWAG.", "Import Video") { launchGallery() }
                }
            }
        }
        if (permissionDenied) {
            AlertDialog(
                onDismissRequest = { permissionDenied = false },
                title = { Text("Video permission required") },
                text = { Text("SWAG needs video access to show videos from your Gallery. Allow video access in Android Settings and try again.") },
                confirmButton = { TextButton(onClick = { permissionDenied = false }) { Text("OK") } }
            )
        }
    }
}

@Composable
private fun BrandSplash() {
    Box(
        Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF080416), Color(0xFF241052), Color(0xFF4C087B)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(116.dp).clip(RoundedCornerShape(32.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF05D9F5), Purple, Color(0xFFFF20C8)))),
                contentAlignment = Alignment.Center
            ) {
                Text("S", color = Color.White, fontSize = 86.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(18.dp))
            Text("SWAG", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Text("VIDEO EDITOR", color = Color(0xFFB8F7FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
            Spacer(Modifier.height(38.dp))
            Text("By PALIAAPK HUB", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text("Developer By shanpalia", color = Color.White.copy(alpha = .75f), fontSize = 12.sp)
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
                        Text("Professional Video Editor\\nwith AI Tools", color = Color.White.copy(alpha = .92f), fontSize = 14.sp, lineHeight = 19.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HeroChip("Effects", Icons.Filled.AutoAwesome)
                        HeroChip("Templates", Icons.Filled.Collections)
                        HeroChip("AI Edit", Icons.Filled.Bolt)
                    }
                }
                Icon(Icons.Filled.Movie, null, tint = Color.White.copy(alpha = .20f), modifier = Modifier.align(Alignment.CenterEnd).size(88.dp))
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
                    Text("Choose a video from Gallery", color = Color.White.copy(alpha = .9f), fontSize = 13.sp)
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
private fun EditorScreen(videoUri: Uri?, onBack: () -> Unit, onPickVideo: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFF100C19))) {
        Row(Modifier.fillMaxWidth().background(Color(0xFF1B1428)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White) }
            Text("SWAG Editor", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onPickVideo) { Text("Change video", color = Color(0xFFDC9BFF)) }
        }
        if (videoUri == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.VideoLibrary, null, tint = Color.White, modifier = Modifier.size(58.dp))
                    Text("Select a video to begin", color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onPickVideo) { Text("Choose from Gallery") }
                }
            }
        } else {
            AndroidView(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp).clip(RoundedCornerShape(16.dp)),
                factory = { context ->
                    VideoView(context).apply {
                        setVideoURI(videoUri)
                        val controller = MediaController(context)
                        controller.setAnchorView(this)
                        setMediaController(controller)
                        setOnPreparedListener { mp -> mp.isLooping = false }
                    }
                },
                update = { it.setVideoURI(videoUri) }
            )
        }
        Column(Modifier.fillMaxWidth().background(Color(0xFF1B1428)).padding(14.dp)) {
            Text("Basic tools", color = Color.White, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                EditorTool("Trim", Icons.Filled.ContentCut)
                EditorTool("Audio", Icons.Filled.MusicNote)
                EditorTool("Text", Icons.Filled.TextFields)
                EditorTool("Speed", Icons.Filled.Speed)
                EditorTool("Filter", Icons.Filled.Palette)
            }
            Text("Preview & playback are available. Editing operations and MP4 export are not implemented yet.",
                color = Color(0xFFB8AFC5), fontSize = 11.sp)
        }
    }
}

@Composable
private fun EditorTool(label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 10.dp)) {
        Icon(icon, null, tint = Color(0xFFD59AFF), modifier = Modifier.size(24.dp))
        Text(label, color = Color.White, fontSize = 10.sp)
    }
}

@Composable
private fun FeaturePage(title: String, description: String, action: String, onAction: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, color = Ink, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text(description, color = Muted, fontSize = 15.sp)
        Button(onClick = onAction, shape = RoundedCornerShape(14.dp)) { Text(action) }
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
