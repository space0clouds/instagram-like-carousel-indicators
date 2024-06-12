package clouds.space.carousel.indicators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Carousel(
    colors: List<Color> = listOf(
        Color(0xFF354499),
        Color(0xFF0195A8),
        Color(0xFF6D9E35),
        Color(0xFFBDAD23),
        Color(0xFFA86808),
        Color(0xFF5B8827),
        Color(0xFF0E6F65),
        Color(0xFF752583)
    ),
) {
    val state: PagerState = rememberPagerState {
        colors.size
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = Modifier.height(280.dp),
            state = state
        ) { page ->
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .background(color = colors[page])
                        .fillMaxSize()
                )
            }
        }

        InstagramLikeCarouselIndicators(
            pagerState = state,
            onPageSelected = { page ->
                coroutineScope.launch {
                    state.scrollToPage(page)
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewCarousel() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Carousel()
    }
}