package uz.arfurniture

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

/** Mebel katalogi. scaleToUnits = modelning eng katta o'lchami (metr). */
data class Furniture(val name: String, val asset: String, val scaleToUnits: Float)

val catalog = listOf(
    Furniture("Antik stul", "models/antique_chair.glb", 0.9f),
    Furniture("Zamonaviy javon", "models/modern_small_shelf.glb", 0.8f),
)

@Composable
fun ArScreen() {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val view = rememberView(engine)
    val cameraNode = rememberARCameraNode(engine)
    val childNodes = rememberNodes()
    val collisionSystem = rememberCollisionSystem(view)

    var selected by remember { mutableStateOf(catalog.first()) }
    var frame by remember { mutableStateOf<Frame?>(null) }
    var showPlanes by remember { mutableStateOf(true) }
    var trackingFailure by remember { mutableStateOf<TrackingFailureReason?>(null) }

    Box(Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            cameraNode = cameraNode,
            planeRenderer = showPlanes,
            sessionConfiguration = { session, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
                        Config.DepthMode.AUTOMATIC else Config.DepthMode.DISABLED
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
            },
            onTrackingFailureChanged = { trackingFailure = it },
            onSessionUpdated = { _, updatedFrame -> frame = updatedFrame },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, node ->
                    // Bo'sh joyga bosilsa - yangi mebel qo'yamiz
                    if (node == null) {
                        val hit = frame?.hitTest(motionEvent.x, motionEvent.y)
                            ?.firstOrNull { it.isValid(depthPoint = false, point = false) }
                        hit?.createAnchorOrNull()?.let { anchor ->
                            showPlanes = false
                            childNodes += buildAnchorNode(engine, modelLoader, anchor, selected)
                        }
                    }
                }
            )
        )

        // Yuqorida ko'rsatma
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(12.dp),
            shape = MaterialTheme.shapes.large,
            color = Color.Black.copy(alpha = 0.55f)
        ) {
            Text(
                text = when {
                    trackingFailure != null -> "Telefonni asta harakatlantiring, pol/devorni skanerlang…"
                    childNodes.isEmpty() -> "Yuzaga bosing — mebel o'rnatiladi"
                    else -> "1 barmoq: surish • 2 barmoq: aylantirish / kattalashtirish"
                },
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Pastda mebel tanlash paneli
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(catalog) { item ->
                        FilterChip(
                            selected = item == selected,
                            onClick = { selected = item },
                            label = { Text(item.name) }
                        )
                    }
                }
                FilledTonalIconButton(onClick = {
                    childNodes.toList().forEach { it.destroy() }
                    childNodes.clear()
                    showPlanes = true
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hammasini o'chirish")
                }
            }
        }
    }
}

private fun buildAnchorNode(
    engine: com.google.android.filament.Engine,
    modelLoader: io.github.sceneview.loaders.ModelLoader,
    anchor: Anchor,
    item: Furniture
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(assetFileLocation = item.asset),
        scaleToUnits = item.scaleToUnits
    ).apply {
        isEditable = true // surish, aylantirish, kattalashtirish
    }
    anchorNode.addChildNode(modelNode)
    return anchorNode
}
