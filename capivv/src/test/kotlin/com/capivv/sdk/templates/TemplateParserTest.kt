package com.capivv.sdk.templates

import org.junit.Assert.*
import org.junit.Test
import kotlinx.serialization.json.Json

/**
 * Unit tests for template parsing.
 */
class TemplateParserTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `parse valid template with all required fields`() {
        val jsonStr = """
        {
            "id": "template-123",
            "name": "Test Template",
            "version": "1.0.0",
            "components": [
                {
                    "type": "headline",
                    "props": { "text": "Welcome!" }
                }
            ]
        }
        """.trimIndent()

        val template = parseTemplate(jsonStr)

        assertNotNull(template)
        assertEquals("template-123", template?.id)
        assertEquals("Test Template", template?.name)
        assertEquals("1.0.0", template?.version)
        assertEquals(1, template?.components?.size)
    }

    @Test
    fun `parse template with multiple components`() {
        val jsonStr = """
        {
            "id": "template-456",
            "name": "Full Template",
            "version": "1.0.0",
            "components": [
                { "type": "headline", "props": { "text": "Title" } },
                { "type": "subtitle", "props": { "text": "Subtitle" } },
                { "type": "featureList", "props": { "features": [] } },
                { "type": "cta", "props": { "text": "Subscribe" } }
            ]
        }
        """.trimIndent()

        val template = parseTemplate(jsonStr)

        assertNotNull(template)
        assertEquals(4, template?.components?.size)
        assertEquals(ComponentType.HEADLINE, template?.components?.get(0)?.type)
        assertEquals(ComponentType.SUBTITLE, template?.components?.get(1)?.type)
        assertEquals(ComponentType.FEATURE_LIST, template?.components?.get(2)?.type)
        assertEquals(ComponentType.CTA, template?.components?.get(3)?.type)
    }

    @Test
    fun `parse template with background`() {
        val jsonStr = """
        {
            "id": "themed-template",
            "name": "Themed",
            "version": "1.0.0",
            "components": [],
            "background": {
                "type": "solid",
                "color": "#5469d4"
            }
        }
        """.trimIndent()

        val template = parseTemplate(jsonStr)

        assertNotNull(template)
        assertNotNull(template?.background)
        assertEquals("solid", template?.background?.type)
        assertEquals("#5469d4", template?.background?.color)
    }

    @Test
    fun `parse template with component props`() {
        val jsonStr = """
        {
            "id": "styled-template",
            "name": "Styled",
            "version": "1.0.0",
            "components": [
                {
                    "type": "headline",
                    "props": {
                        "text": "Styled Text",
                        "fontSize": 28.0,
                        "alignment": "center",
                        "padding": 16.0
                    }
                }
            ]
        }
        """.trimIndent()

        val template = parseTemplate(jsonStr)

        assertNotNull(template)
        val component = template?.components?.get(0)
        assertNotNull(component?.props)
        assertEquals("Styled Text", component?.props?.text)
        assertEquals(28.0f, component?.props?.fontSize)
        assertEquals("center", component?.props?.alignment)
        assertEquals(16.0f, component?.props?.padding)
    }

    @Test
    fun `return null for invalid JSON`() {
        val jsonStr = "not valid json"
        val template = parseTemplate(jsonStr)
        assertNull(template)
    }

    @Test
    fun `parse template with settings`() {
        val jsonStr = """
        {
            "id": "settings-template",
            "name": "With Settings",
            "version": "1.0.0",
            "components": [],
            "settings": {
                "show_close_button": false,
                "allow_swipe_dismiss": true
            }
        }
        """.trimIndent()

        val template = parseTemplate(jsonStr)

        assertNotNull(template)
        assertEquals(false, template?.settings?.showCloseButton)
        assertEquals(true, template?.settings?.allowSwipeDismiss)
    }

    private fun parseTemplate(jsonStr: String): TemplateDefinition? {
        return try {
            json.decodeFromString<TemplateDefinition>(jsonStr)
        } catch (e: Exception) {
            null
        }
    }
}
