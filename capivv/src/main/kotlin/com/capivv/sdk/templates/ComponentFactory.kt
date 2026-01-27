package com.capivv.sdk.templates

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capivv.sdk.l10n.CapivvL10n
import com.capivv.sdk.models.Offering
import com.capivv.sdk.models.Product
import com.capivv.sdk.ui.components.ProductCard
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Factory for rendering dynamic template components.
 */
@OptIn(ExperimentalMaterial3Api::class)
object ComponentFactory {

    /**
     * Render a component and its children.
     */
    @Composable
    fun RenderComponent(
        component: TemplateComponent,
        offering: Offering?,
        selectedProduct: Product?,
        locale: String,
        onProductSelected: (Product) -> Unit,
        onPurchase: () -> Unit,
        onRestore: () -> Unit,
        onDismiss: () -> Unit,
        onUrlClick: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        when (component.type) {
            ComponentType.HEADLINE -> HeadlineComponent(component.props, locale, modifier)
            ComponentType.SUBTITLE -> SubtitleComponent(component.props, locale, modifier)
            ComponentType.BODY -> BodyComponent(component.props, locale, modifier)
            ComponentType.PRICE -> PriceComponent(selectedProduct, modifier)
            ComponentType.FEATURE_LIST -> FeatureListComponent(component.props, modifier)
            ComponentType.CTA -> CTAComponent(component.props, locale, onPurchase, onRestore, onDismiss, onUrlClick, modifier)
            ComponentType.IMAGE -> ImageComponent(component.props, modifier)
            ComponentType.SPACER -> SpacerComponent(component.props, modifier)
            ComponentType.CONTAINER -> ContainerComponent(component, offering, selectedProduct, locale, onProductSelected, onPurchase, onRestore, onDismiss, onUrlClick, modifier)
            ComponentType.PRODUCT_SELECTOR -> ProductSelectorComponent(offering, selectedProduct, onProductSelected, modifier)
            ComponentType.BADGE -> BadgeComponent(component.props, locale, modifier)
            ComponentType.DIVIDER -> DividerComponent(component.props, modifier)
            ComponentType.RESTORE_BUTTON -> RestoreButtonComponent(component.props, locale, onRestore, modifier)
            ComponentType.LEGAL_TEXT -> LegalTextComponent(component.props, locale, modifier)
            ComponentType.VIDEO -> VideoComponent(component.props, modifier)
            ComponentType.SOCIAL_PROOF -> SocialProofComponent(component.props, modifier)
            ComponentType.FAQ -> FAQComponent(component.props, modifier)
            ComponentType.CAROUSEL -> CarouselComponent(component.props, modifier)
            ComponentType.PROGRESS_INDICATOR -> ProgressIndicatorComponent(component.props, modifier)
            ComponentType.COUNTDOWN -> CountdownComponent(component.props, locale, modifier)
        }
    }

    @Composable
    private fun HeadlineComponent(props: ComponentProps, locale: String, modifier: Modifier) {
        val text = props.localizedText?.get(locale) ?: props.text ?: ""
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = getFontWeight(props.fontWeight),
            color = parseColor(props.color) ?: MaterialTheme.colorScheme.onBackground,
            textAlign = getTextAlign(props.alignment),
            modifier = modifier
                .fillMaxWidth()
                .padding(props.padding?.dp ?: 0.dp)
        )
    }

    @Composable
    private fun SubtitleComponent(props: ComponentProps, locale: String, modifier: Modifier) {
        val text = props.localizedText?.get(locale) ?: props.text ?: ""
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = getFontWeight(props.fontWeight),
            color = parseColor(props.color) ?: MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = getTextAlign(props.alignment),
            modifier = modifier
                .fillMaxWidth()
                .padding(props.padding?.dp ?: 0.dp)
        )
    }

    @Composable
    private fun BodyComponent(props: ComponentProps, locale: String, modifier: Modifier) {
        val text = props.localizedText?.get(locale) ?: props.text ?: ""
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = parseColor(props.color) ?: MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = getTextAlign(props.alignment),
            modifier = modifier
                .fillMaxWidth()
                .padding(props.padding?.dp ?: 0.dp)
        )
    }

    @Composable
    private fun PriceComponent(product: Product?, modifier: Modifier) {
        product?.let {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = it.priceString,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                it.getFormattedPeriod()?.let { period ->
                    Text(
                        text = " / ${period.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    @Composable
    private fun FeatureListComponent(props: ComponentProps, modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            props.features?.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = parseColor(props.iconColor) ?: MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = feature.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (feature.highlighted) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }

    @Composable
    private fun CTAComponent(
        props: ComponentProps,
        locale: String,
        onPurchase: () -> Unit,
        onRestore: () -> Unit,
        onDismiss: () -> Unit,
        onUrlClick: (String) -> Unit,
        modifier: Modifier
    ) {
        val text = props.localizedText?.get(locale) ?: props.text ?: CapivvL10n.get("subscribe_now", locale)
        val onClick: () -> Unit = when (props.action) {
            "purchase" -> onPurchase
            "restore" -> onRestore
            "dismiss" -> onDismiss
            "url" -> {{ props.url?.let { onUrlClick(it) } }}
            else -> onPurchase
        }

        when (props.style) {
            "secondary" -> OutlinedButton(
                onClick = onClick,
                modifier = modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(text)
            }
            "text" -> TextButton(
                onClick = onClick,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(text)
            }
            else -> Button(
                onClick = onClick,
                modifier = modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(text, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    @Composable
    private fun ImageComponent(props: ComponentProps, modifier: Modifier) {
        // Placeholder for image loading - in real implementation use Coil/Glide
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(props.height?.dp ?: 200.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(props.cornerRadius?.dp ?: 8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    private fun SpacerComponent(props: ComponentProps, modifier: Modifier) {
        Spacer(modifier = modifier.height(props.spacerHeight?.dp ?: 16.dp))
    }

    @Composable
    private fun ContainerComponent(
        component: TemplateComponent,
        offering: Offering?,
        selectedProduct: Product?,
        locale: String,
        onProductSelected: (Product) -> Unit,
        onPurchase: () -> Unit,
        onRestore: () -> Unit,
        onDismiss: () -> Unit,
        onUrlClick: (String) -> Unit,
        modifier: Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (component.props.backgroundColor != null) {
                        Modifier.background(
                            parseColor(component.props.backgroundColor)!!,
                            RoundedCornerShape(component.props.cornerRadius?.dp ?: 0.dp)
                        )
                    } else Modifier
                )
                .padding(component.props.padding?.dp ?: 0.dp),
            horizontalAlignment = getHorizontalAlignment(component.props.alignment)
        ) {
            component.children?.forEach { child ->
                RenderComponent(
                    component = child,
                    offering = offering,
                    selectedProduct = selectedProduct,
                    locale = locale,
                    onProductSelected = onProductSelected,
                    onPurchase = onPurchase,
                    onRestore = onRestore,
                    onDismiss = onDismiss,
                    onUrlClick = onUrlClick
                )
            }
        }
    }

    @Composable
    private fun ProductSelectorComponent(
        offering: Offering?,
        selectedProduct: Product?,
        onProductSelected: (Product) -> Unit,
        modifier: Modifier
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            offering?.products?.forEach { product ->
                ProductCard(
                    product = product,
                    isSelected = product.identifier == selectedProduct?.identifier,
                    onClick = { onProductSelected(product) }
                )
            }
        }
    }

    @Composable
    private fun BadgeComponent(props: ComponentProps, locale: String, modifier: Modifier) {
        val text = props.localizedText?.get(locale) ?: props.text ?: ""
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(props.cornerRadius?.dp ?: 6.dp),
            color = parseColor(props.backgroundColor) ?: MaterialTheme.colorScheme.secondary
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = parseColor(props.color) ?: MaterialTheme.colorScheme.onSecondary
            )
        }
    }

    @Composable
    private fun DividerComponent(props: ComponentProps, modifier: Modifier) {
        Divider(
            modifier = modifier.padding(vertical = props.margin?.dp ?: 8.dp),
            color = parseColor(props.color) ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }

    @Composable
    private fun RestoreButtonComponent(props: ComponentProps, locale: String, onRestore: () -> Unit, modifier: Modifier) {
        val text = props.localizedText?.get(locale) ?: props.text ?: CapivvL10n.get("restore_purchases", locale)
        TextButton(
            onClick = onRestore,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }

    @Composable
    private fun LegalTextComponent(props: ComponentProps, locale: String, modifier: Modifier) {
        val text = props.localizedText?.get(locale) ?: props.text ?: CapivvL10n.get("subscription_disclaimer", locale)
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = parseColor(props.color) ?: MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth().padding(props.padding?.dp ?: 8.dp)
        )
    }

    @Composable
    private fun VideoComponent(props: ComponentProps, modifier: Modifier) {
        // Video placeholder with thumbnail
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(props.height?.dp ?: 200.dp)
                .background(Color.Black, RoundedCornerShape(props.cornerRadius?.dp ?: 12.dp))
                .clip(RoundedCornerShape(props.cornerRadius?.dp ?: 12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Play button overlay
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play video",
                    tint = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    @Composable
    private fun SocialProofComponent(props: ComponentProps, modifier: Modifier) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating
            props.rating?.let { rating ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", rating),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    props.reviewCount?.let {
                        Text(
                            text = "$it reviews",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Downloads
            props.downloadCount?.let { count ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = count,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "downloads",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    @Composable
    private fun FAQComponent(props: ComponentProps, modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            props.faqItems?.forEach { item ->
                var expanded by remember { mutableStateOf(item.initiallyExpanded) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { expanded = !expanded }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.question,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (expanded) "Collapse" else "Expand"
                            )
                        }

                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.answer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun CarouselComponent(props: ComponentProps, modifier: Modifier) {
        val items = props.carouselItems ?: return
        val pagerState = rememberPagerState(pageCount = { items.size })

        // Auto-scroll
        if (props.autoScroll == true) {
            LaunchedEffect(pagerState) {
                while (true) {
                    delay((props.scrollInterval ?: 3000).toLong())
                    val nextPage = (pagerState.currentPage + 1) % items.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }

        Column(modifier = modifier) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val item = items[page]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item.title?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        item.description?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Page indicators
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(items.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .background(
                                if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                },
                                CircleShape
                            )
                    )
                }
            }
        }
    }

    @Composable
    private fun ProgressIndicatorComponent(props: ComponentProps, modifier: Modifier) {
        val currentStep = props.currentStep ?: 1
        val totalSteps = props.totalSteps ?: 3

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (step in 1..totalSteps) {
                    val isCompleted = step < currentStep
                    val isCurrent = step == currentStep

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                when {
                                    isCompleted -> MaterialTheme.colorScheme.primary
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = step.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (step < totalSteps) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .padding(horizontal = 8.dp)
                                .background(
                                    if (step < currentStep) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    }
                                )
                        )
                    }
                }
            }

            // Step labels
            props.stepLabels?.let { labels ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    labels.take(totalSteps).forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CountdownComponent(props: ComponentProps, locale: String, modifier: Modifier) {
        val endTimeString = props.endTime ?: return
        val endTime = try {
            Instant.parse(endTimeString)
        } catch (e: Exception) {
            return
        }

        var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(endTime)) }

        LaunchedEffect(endTime) {
            while (timeRemaining.totalSeconds > 0) {
                delay(1000)
                timeRemaining = calculateTimeRemaining(endTime)
            }
        }

        val style = props.countdownStyle ?: CountdownStyle()

        if (timeRemaining.totalSeconds <= 0) {
            Text(
                text = props.expiredText ?: CapivvL10n.get("offer_expired", locale),
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier
            )
            return
        }

        when (style.style) {
            "boxed" -> BoxedCountdown(timeRemaining, style, modifier)
            "circular" -> CircularCountdown(timeRemaining, style, modifier)
            else -> LabeledCountdown(timeRemaining, style, locale, modifier)
        }
    }

    @Composable
    private fun BoxedCountdown(time: TimeRemaining, style: CountdownStyle, modifier: Modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                time.days to "d",
                time.hours to "h",
                time.minutes to "m",
                time.seconds to "s"
            ).forEach { (value, label) ->
                Card(
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%02d", value),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (style.showLabels) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CircularCountdown(time: TimeRemaining, style: CountdownStyle, modifier: Modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format(
                    "%02d:%02d:%02d",
                    time.hours + (time.days * 24),
                    time.minutes,
                    time.seconds
                ),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun LabeledCountdown(time: TimeRemaining, style: CountdownStyle, locale: String, modifier: Modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center
        ) {
            if (time.days > 0) {
                Text("${time.days} ${CapivvL10n.get("days", locale)} ")
            }
            Text(
                "${time.hours} ${CapivvL10n.get("hours", locale)} " +
                "${time.minutes} ${CapivvL10n.get("minutes", locale)} " +
                "${time.seconds} ${CapivvL10n.get("seconds", locale)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // Helper functions

    private fun parseColor(colorString: String?): Color? {
        return colorString?.let {
            try {
                Color(android.graphics.Color.parseColor(
                    if (it.startsWith("#")) it else "#$it"
                ))
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun getFontWeight(weight: String?): FontWeight {
        return when (weight) {
            "bold" -> FontWeight.Bold
            "semibold" -> FontWeight.SemiBold
            "medium" -> FontWeight.Medium
            else -> FontWeight.Normal
        }
    }

    private fun getTextAlign(alignment: String?): TextAlign {
        return when (alignment) {
            "left" -> TextAlign.Start
            "right" -> TextAlign.End
            else -> TextAlign.Center
        }
    }

    private fun getHorizontalAlignment(alignment: String?): Alignment.Horizontal {
        return when (alignment) {
            "left" -> Alignment.Start
            "right" -> Alignment.End
            else -> Alignment.CenterHorizontally
        }
    }

    private data class TimeRemaining(
        val days: Int,
        val hours: Int,
        val minutes: Int,
        val seconds: Int
    ) {
        val totalSeconds: Long
            get() = days * 86400L + hours * 3600L + minutes * 60L + seconds
    }

    private fun calculateTimeRemaining(endTime: Instant): TimeRemaining {
        val now = Instant.now()
        if (now.isAfter(endTime)) {
            return TimeRemaining(0, 0, 0, 0)
        }

        var remaining = ChronoUnit.SECONDS.between(now, endTime)
        val days = (remaining / 86400).toInt()
        remaining %= 86400
        val hours = (remaining / 3600).toInt()
        remaining %= 3600
        val minutes = (remaining / 60).toInt()
        val seconds = (remaining % 60).toInt()

        return TimeRemaining(days, hours, minutes, seconds)
    }
}
