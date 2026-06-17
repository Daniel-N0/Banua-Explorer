package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.viewinterop.AndroidView

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi


@OptIn(UnstableApi::class)
@Composable
fun SplashVideoScreen(
    videoResId: Int,
    onVideoFinished: () -> Unit
) {

    val context = LocalContext.current

    val exoPlayer = remember(videoResId) {

        ExoPlayer.Builder(context).build().apply {

            val videoUri = Uri.parse(
                "android.resource://${context.packageName}/$videoResId"
            )

            val mediaItem = MediaItem.fromUri(videoUri)

            setMediaItem(mediaItem)

            prepare()

            playWhenReady = true

            repeatMode = Player.REPEAT_MODE_OFF

            addListener(object : Player.Listener {

                override fun onPlayerError(
                    error: androidx.media3.common.PlaybackException
                ) {
                    android.util.Log.e(
                        "SPLASH_VIDEO",
                        "ERROR = ${error.message}"
                    )
                }

                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {
                    if (playbackState == Player.STATE_ENDED) {
                        onVideoFinished()
                    }
                }
            })
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(it).apply {
                useController = false

                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)

                layoutParams =
                    android.widget.FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                    )
            }
        },
        update = {
            it.player = exoPlayer
        }
    )
}