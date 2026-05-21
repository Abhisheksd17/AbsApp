package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val onBoardingGradient: Brush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF43116A),
        Color(0xFF0A1832)
    )
)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

//Gray
val SageGray = Color(0xFFB9C1BE)
val CoolSlateGray = Color(0xFFA8B0AF)
val TimeColor = Color(0xFF8696A0)
val LightSageGray = Color(0xFFCDD1D0)
val PaleMintGray = Color(0xFFD6E4E0)
val SlateGray = Color(0xFF797C7B)
val IceGray = Color(0xFFF3F6F6)
val SoftLightGray = Color(0xFFE6E6E6)
val LightGrayBackground = Color(0xFFF5F6F6)

   // teal
 val BubbleIncoming = Color(0xFFFFFFFF)   // white
 val TextOutgoing   = Color.White
 val TextIncoming   = Color(0xFF1A1A1A)
 val TimeOutgoing   = Color.White.copy(alpha = 0.7f)
 val TimeIncoming   = Color(0xFF999999)


//Green
val TealGreen = Color(0xFF24786D)
 val BubbleOutgoing = Color(0xFF20A090)
val CoolSlateGreen = Color(0xFF363F3B)
val OnlineGreen = Color(0xFF2BEF83)

//Red
val ErrorRed = Color(0xFFFF2D1B)

//Blue
val ReadBlue = Color(0xFF4FC3F7)


val GradientStart = Color(0xFF1a1a2e)
val GradientEnd = Color(0xFF0f0f1e)

// Status bar colors
val StatusBarBackground = Color.Black.copy(alpha = 0.4f)
val StatusBarTextPrimary = Color.White
val StatusBarTextSecondary = Color.White.copy(alpha = 0.7f)
val ConnectingText = Color(0xFF64B5F6)
val ConnectingDot = Color(0xFF64B5F6)

// Waiting state colors
val AvatarBackground = Color(0xFF6C5CE7).copy(alpha = 0.3f)
val AvatarText = Color.White
val PeerNameText = Color.White
val WaitingStateText = Color.White.copy(alpha = 0.7f)
val AnimatedCircleOuter = Color(0xFF6C5CE7).copy(alpha = 0.15f)
val AnimatedCircleInner = Color(0xFF6C5CE7).copy(alpha = 0.2f)

// Control button colors
val ControlBarBackground = Color.Black.copy(alpha = 0.5f)
val ControlButtonActive = Color.White.copy(alpha = 0.3f)
val ControlButtonInactive = Color.White.copy(alpha = 0.15f)
val ControlButtonIconActive = Color.White
val ControlButtonIconInactive = Color.White.copy(alpha = 0.9f)
val ControlButtonLabel = Color.White.copy(alpha = 0.7f)

// Hang up button colors
val HangUpButton = Color(0xFFE74C3C)
val HangUpIcon = Color.White

// Local video preview
val LocalVideoBackground = Color.Black


 val AvatarOuterRing = Color(0xFF6C5CE7).copy(alpha = 0.15f)
 val AvatarMiddleRing = Color(0xFF6C5CE7).copy(alpha = 0.2f)
 val AvatarInnerRing = Color(0xFF6C5CE7).copy(alpha = 0.3f)
 val AcceptButtonColor = Color(0xFF34C759)
 val DeclineButtonColor = Color(0xFFE74C3C)
