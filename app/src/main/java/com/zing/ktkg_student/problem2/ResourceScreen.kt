package com.zing.ktkg_student.problem2

import android.graphics.Color as AndroidColor
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun ResourceScreen(resourceViewModel: ResourceViewModel = viewModel()) {
    val resourceResponse = resourceViewModel.resourceResponse
    val isLoading = resourceViewModel.isLoading
    val errorMessage = resourceViewModel.errorMessage
    // Quản lý trạng thái cuộn của danh sách
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // Animatable để theo dõi trạng thái cuộn (chỉ số đầu tiên)
    val scrolledProgress = remember { Animatable(0f) }
    val density = LocalDensity.current

    // Hiệu ứng cuộn: cập nhật giá trị scrolledProgress dựa trên vị trí cuộn của danh sách
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect {
                scrolledProgress.animateTo(
                    if (it > 0) 1f else 0f,
                    animationSpec = tween(300)
                )
            }
    }
    val appBarElevation by animateDpAsState(
        targetValue = if (scrolledProgress.value > 0.5f) 8.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            // hiệu ứng xoay
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 8.dp)
                                .graphicsLayer {
                                    rotationZ = scrolledProgress.value * 90
                                }
                        )
                        Text(
                            "API Resources",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.95f),
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = appBarElevation,
                modifier = Modifier.graphicsLayer {
                    shadowElevation = appBarElevation.toPx()
                }
            )
        },
        backgroundColor = MaterialTheme.colors.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colors.primary.copy(alpha = 0.08f),
                            MaterialTheme.colors.background.copy(alpha = 0.95f),
                            MaterialTheme.colors.background
                        )
                    )
                )
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    LoadingAnimation(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage.isNotEmpty() -> {
                    ErrorView(
                        errorMessage = errorMessage,
                        modifier = Modifier.align(Alignment.Center),
                        onRetry = {}
                    )
                }
                else -> {
                    resourceResponse?.let { response ->
                        // Hiển thị danh sách dữ liệu dưới dạng LazyColumn
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            // Card hiển thị thông tin phân trang
                            item {
                                PaginationInfoCard(response)
                            }
                            items(response.data) { resource ->
                                ResourceCard(resource)
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SupportFooter(support = response.support)
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                        AnimatedVisibility(
                            visible = listState.firstVisibleItemIndex > 0,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                },
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowUpward,
                                    contentDescription = "Scroll to top"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaginationInfoCard(response: ResourceResponse) {
    // Card hiển thị thông tin phân trang như số trang hiện tại
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colors.primary.copy(alpha = 0.1f),
                            MaterialTheme.colors.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                            append("Page ")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                            append("${response.page}")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(" of ${response.total_pages}")
                        }
                    },
                    style = MaterialTheme.typography.h6
                )

                // Hiển thị các chấm chỉ trang hiện tại
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    repeat(min(response.total_pages, 5)) { index ->
                        val isCurrentPage = index + 1 == response.page
                        val size = if (isCurrentPage) 10.dp else 6.dp
                        Box(
                            modifier = Modifier
                                .size(size)
                                .clip(CircleShape)
                                .background(
                                    if (isCurrentPage) MaterialTheme.colors.primary
                                    else MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                )
                        )
                    }
                    if (response.total_pages > 5) {
                        Text(
                            "...",
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatisticItem(title = "Total Items", value = "${response.total}")
                StatisticItem(title = "Per Page", value = "${response.per_page}")
            }
        }
    }
}

@Composable
fun StatisticItem(title: String, value: String) {
    // Hiển thị một mục thống kê (tiêu đề và giá trị)
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
    }
}

@Composable
fun ResourceCard(resource: ColorResource) {
    // Phân tích màu nền dựa trên giá trị màu của resource
    val backgroundColor = try {
        Color(AndroidColor.parseColor(resource.color))
    } catch (e: Exception) {
        MaterialTheme.colors.primary
    }
    val textColor = if (isColorBright(backgroundColor)) Color.Black else Color.White
    var expanded by remember { mutableStateOf(false) }

    // Card hiển thị thông tin của resource
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { expanded = !expanded }  // Nhấn vào card để mở rộng/thu gọn thông tin
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = resource.name,
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Year: ${resource.year}",
                        style = MaterialTheme.typography.body2,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }

                // Hiển thị badge ID với background đối lập
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.9f), CircleShape)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "#${resource.id}",
                        style = MaterialTheme.typography.subtitle2.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = backgroundColor
                    )
                }
            }

            // chi tiết mở rộng khi nhấn vào card
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(16.dp)
                ) {
                    DetailItem(label = "ID", value = "${resource.id}", textColor)
                    DetailItem(label = "Name", value = resource.name, textColor)
                    DetailItem(label = "Year", value = "${resource.year}", textColor)
                    DetailItem(label = "Color", value = resource.color, textColor)
                    DetailItem(label = "Pantone", value = resource.pantone_value, textColor)

                    Spacer(modifier = Modifier.height(8.dp))
                    // tạo ô chứa mã màu và màu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(8.dp, RoundedCornerShape(8.dp))
                                .background(backgroundColor, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        ) {
                            Text(
                                text = resource.color,
                                style = MaterialTheme.typography.caption,
                                color = textColor,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, textColor: Color) {
    // Hiển thị một dòng thông tin chi tiết với nhãn và giá trị
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun SupportFooter(support: Support) {
    val uriHandler: UriHandler = LocalUriHandler.current

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(support.url) }
    ) {
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary.copy(alpha = 0.05f),
                                MaterialTheme.colors.primary.copy(alpha = 0.1f),
                                MaterialTheme.colors.primary.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Support",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = support.text,
                    style = MaterialTheme.typography.body1,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = support.url,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tap to visit website",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}

@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {
    // Hiển thị hoạt ảnh loading khi dữ liệu đang được tải
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )

        CircularProgressIndicator(
            modifier = Modifier
                .size(60.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            color = MaterialTheme.colors.primary,
            strokeWidth = 5.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading resources...",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ErrorView(errorMessage: String, modifier: Modifier = Modifier, onRetry: () -> Unit) {
    // Hiển thị thông báo lỗi cùng với nút Retry khi có lỗi xảy ra
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Retry",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.button
            )
        }
    }
}

// công thức tính độ sáng
fun isColorBright(color: Color): Boolean {
    val luminance = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue)
    return luminance > 0.5
}
