package com.capivv.sdk.templates

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Types of components supported in dynamic templates.
 */
@Serializable
enum class ComponentType {
    @SerialName("headline")
    HEADLINE,

    @SerialName("subtitle")
    SUBTITLE,

    @SerialName("body")
    BODY,

    @SerialName("price")
    PRICE,

    @SerialName("featureList")
    FEATURE_LIST,

    @SerialName("cta")
    CTA,

    @SerialName("image")
    IMAGE,

    @SerialName("spacer")
    SPACER,

    @SerialName("container")
    CONTAINER,

    @SerialName("productSelector")
    PRODUCT_SELECTOR,

    @SerialName("badge")
    BADGE,

    @SerialName("divider")
    DIVIDER,

    @SerialName("restoreButton")
    RESTORE_BUTTON,

    @SerialName("legalText")
    LEGAL_TEXT,

    // Phase E components
    @SerialName("video")
    VIDEO,

    @SerialName("socialProof")
    SOCIAL_PROOF,

    @SerialName("faq")
    FAQ,

    @SerialName("carousel")
    CAROUSEL,

    @SerialName("progressIndicator")
    PROGRESS_INDICATOR,

    @SerialName("countdown")
    COUNTDOWN
}

/**
 * Background style for templates.
 */
@Serializable
data class BackgroundStyle(
    val type: String = "solid", // solid, gradient, image
    val color: String? = null,
    @SerialName("gradient_colors")
    val gradientColors: List<String>? = null,
    @SerialName("gradient_angle")
    val gradientAngle: Float? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
)

/**
 * A feature item in a feature list.
 */
@Serializable
data class FeatureItemModel(
    val text: String,
    val icon: String? = null,
    val highlighted: Boolean = false
)

/**
 * An FAQ item.
 */
@Serializable
data class FAQItemModel(
    val question: String,
    val answer: String,
    @SerialName("initially_expanded")
    val initiallyExpanded: Boolean = false
)

/**
 * A carousel item.
 */
@Serializable
data class CarouselItemModel(
    val title: String? = null,
    val description: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    val icon: String? = null
)

/**
 * Countdown style configuration.
 */
@Serializable
data class CountdownStyle(
    val style: String = "boxed", // boxed, circular, labeled
    @SerialName("show_labels")
    val showLabels: Boolean = true,
    @SerialName("label_position")
    val labelPosition: String = "below" // below, inside
)

/**
 * Props for a template component.
 */
@Serializable
data class ComponentProps(
    // Text content
    val text: String? = null,
    val localizedText: Map<String, String>? = null,

    // Styling
    val alignment: String? = null, // left, center, right
    val fontSize: Float? = null,
    val fontWeight: String? = null, // normal, medium, semibold, bold
    val color: String? = null,
    val backgroundColor: String? = null,
    val padding: Float? = null,
    val margin: Float? = null,
    val cornerRadius: Float? = null,

    // Image
    @SerialName("image_url")
    val imageUrl: String? = null,
    val width: Float? = null,
    val height: Float? = null,
    @SerialName("aspect_ratio")
    val aspectRatio: Float? = null,
    @SerialName("content_mode")
    val contentMode: String? = null, // fill, fit

    // Feature list
    val features: List<FeatureItemModel>? = null,
    @SerialName("icon_color")
    val iconColor: String? = null,

    // CTA
    val action: String? = null, // purchase, restore, dismiss, url
    val url: String? = null,
    val style: String? = null, // primary, secondary, text

    // Spacer
    @SerialName("spacer_height")
    val spacerHeight: Float? = null,

    // Video
    @SerialName("video_url")
    val videoUrl: String? = null,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null,
    val autoplay: Boolean? = null,
    val muted: Boolean? = null,

    // Social proof
    val rating: Float? = null,
    @SerialName("review_count")
    val reviewCount: Int? = null,
    @SerialName("download_count")
    val downloadCount: String? = null,
    val testimonials: List<String>? = null,

    // FAQ
    @SerialName("faq_items")
    val faqItems: List<FAQItemModel>? = null,

    // Carousel
    @SerialName("carousel_items")
    val carouselItems: List<CarouselItemModel>? = null,
    @SerialName("auto_scroll")
    val autoScroll: Boolean? = null,
    @SerialName("scroll_interval")
    val scrollInterval: Int? = null,

    // Progress
    @SerialName("current_step")
    val currentStep: Int? = null,
    @SerialName("total_steps")
    val totalSteps: Int? = null,
    @SerialName("step_labels")
    val stepLabels: List<String>? = null,

    // Countdown
    @SerialName("end_time")
    val endTime: String? = null, // ISO 8601 timestamp
    @SerialName("countdown_style")
    val countdownStyle: CountdownStyle? = null,
    @SerialName("expired_text")
    val expiredText: String? = null
)

/**
 * A component in a template.
 */
@Serializable
data class TemplateComponent(
    val type: ComponentType,
    val props: ComponentProps = ComponentProps(),
    val children: List<TemplateComponent>? = null
)

/**
 * Settings for a template.
 */
@Serializable
data class TemplateSettings(
    @SerialName("show_close_button")
    val showCloseButton: Boolean = true,
    @SerialName("close_button_style")
    val closeButtonStyle: String = "icon", // icon, text
    @SerialName("allow_swipe_dismiss")
    val allowSwipeDismiss: Boolean = true,
    @SerialName("background_dismissible")
    val backgroundDismissible: Boolean = false
)

/**
 * A complete template definition.
 */
@Serializable
data class TemplateDefinition(
    val id: String,
    val name: String,
    val version: String,
    val components: List<TemplateComponent>,
    val background: BackgroundStyle? = null,
    val settings: TemplateSettings = TemplateSettings(),
    val metadata: Map<String, String>? = null
)

/**
 * Result of loading a template.
 */
@Serializable
data class TemplateLoadResult(
    val template: TemplateDefinition?,
    val version: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("cache_ttl_seconds")
    val cacheTtlSeconds: Int? = null
)
