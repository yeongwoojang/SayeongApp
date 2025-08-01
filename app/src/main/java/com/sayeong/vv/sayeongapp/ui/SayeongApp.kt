package com.sayeong.vv.sayeongapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sayeong.vv.designsystem.component.SayeongBackground
import com.sayeong.vv.designsystem.component.SayeongGradientBackground
import com.sayeong.vv.designsystem.theme.LocalGradientColors
import com.sayeong.vv.home.HomeScreen

@Composable
fun SayeongApp(
    modifier: Modifier = Modifier
) {
    SayeongBackground(modifier = Modifier) {
        SayeongGradientBackground(gradientColors = LocalGradientColors.current) {
            HomeScreen()
        }
    }
}