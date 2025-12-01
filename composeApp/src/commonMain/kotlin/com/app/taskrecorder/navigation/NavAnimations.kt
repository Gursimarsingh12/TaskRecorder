package com.app.taskrecorder.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

object NavAnimations {
    private const val ANIMATION_DURATION = 300
    private const val SLOW_ANIMATION_DURATION = 500

    fun slideInFromRight() = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(ANIMATION_DURATION)
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

    fun slideOutToLeft() = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(ANIMATION_DURATION)
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

    fun slideInFromLeft() = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(ANIMATION_DURATION)
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

    fun slideOutToRight() = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(ANIMATION_DURATION)
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

    fun slideInFromBottom() = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(SLOW_ANIMATION_DURATION)
    ) + fadeIn(animationSpec = tween(SLOW_ANIMATION_DURATION))

    fun slideOutToBottom() = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(SLOW_ANIMATION_DURATION)
    ) + fadeOut(animationSpec = tween(SLOW_ANIMATION_DURATION))

    fun defaultEnterTransition(): ContentTransform = slideInFromRight() togetherWith slideOutToLeft()

    fun defaultExitTransition(): ContentTransform = slideInFromLeft() togetherWith slideOutToRight()

    fun slideUpEnterTransition() = slideInFromBottom() togetherWith fadeOut(animationSpec = tween(SLOW_ANIMATION_DURATION))

    fun slideDownExitTransition() = fadeIn(animationSpec = tween(SLOW_ANIMATION_DURATION)) togetherWith slideOutToBottom()
}