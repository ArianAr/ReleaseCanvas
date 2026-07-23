package com.releasecanvas.app.data.pdf

import com.releasecanvas.app.data.model.CustomTemplate
import com.releasecanvas.app.data.model.TemplateOption
import java.util.UUID

object TemplateResolver {

    fun builtInOptions(): List<TemplateOption> =
        ReleaseTemplate.entries.map {
            TemplateOption(
                id = it.id,
                displayName = it.displayName,
                version = it.version,
                shortDescription = it.shortDescription,
                isCustom = false,
            )
        }

    fun customOptions(customs: List<CustomTemplate>): List<TemplateOption> =
        customs.map {
            val jurisdiction = it.jurisdiction.trim()
            TemplateOption(
                id = it.id,
                displayName = it.name,
                version = it.version,
                shortDescription = when {
                    jurisdiction.isNotEmpty() -> "Custom · $jurisdiction"
                    else -> "Custom (editable)"
                },
                isCustom = true,
            )
        }

    fun allOptions(customs: List<CustomTemplate>): List<TemplateOption> =
        builtInOptions() + customOptions(customs)

    fun resolveBody(
        templateId: String,
        customs: List<CustomTemplate>,
        modelName: String,
        photographerName: String,
        languageTag: String = "en",
    ): String {
        val custom = customs.firstOrNull { it.id == templateId }
        if (custom != null) {
            return applyPlaceholders(custom.body, modelName, photographerName)
        }
        val builtIn = ReleaseTemplate.fromId(templateId)
        return ReleaseTerms.body(builtIn, modelName, photographerName, languageTag)
    }

    fun resolveOption(
        templateId: String,
        customs: List<CustomTemplate>,
    ): TemplateOption {
        customs.firstOrNull { it.id == templateId }?.let {
            val jurisdiction = it.jurisdiction.trim()
            return TemplateOption(
                id = it.id,
                displayName = it.name,
                version = it.version,
                shortDescription = when {
                    jurisdiction.isNotEmpty() -> "Custom · $jurisdiction"
                    else -> "Custom (editable)"
                },
                isCustom = true,
            )
        }
        val builtIn = ReleaseTemplate.fromId(templateId)
        return TemplateOption(
            id = builtIn.id,
            displayName = builtIn.displayName,
            version = builtIn.version,
            shortDescription = builtIn.shortDescription,
            isCustom = false,
        )
    }

    fun applyPlaceholders(body: String, modelName: String, photographerName: String): String {
        return body
            .replace("{{MODEL}}", modelName.trim())
            .replace("{{PHOTOGRAPHER}}", photographerName.trim())
            .replace("{{model}}", modelName.trim())
            .replace("{{photographer}}", photographerName.trim())
            .trim()
            .ifBlank { body.trim() }
    }

    fun newCustomId(): String = "custom_${UUID.randomUUID()}"
}
