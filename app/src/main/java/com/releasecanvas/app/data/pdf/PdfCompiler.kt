package com.releasecanvas.app.data.pdf

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.releasecanvas.app.data.locale.AppLocale
import com.releasecanvas.app.data.model.CustomTemplate
import com.releasecanvas.app.data.model.LocationStatus
import com.releasecanvas.app.data.model.PhotographerProfile
import com.releasecanvas.app.data.model.ReleaseDraft
import com.releasecanvas.app.data.model.SigningMetadata
import com.releasecanvas.app.util.Formatters
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Builds a US Letter PDF. Content that exceeds the bottom margin flows onto
 * additional pages automatically. Labels and legal body use [releaseLanguageTag].
 */
class PdfCompiler {

    fun compile(
        draft: ReleaseDraft,
        metadata: SigningMetadata,
        attestationAccepted: Boolean = false,
        profile: PhotographerProfile = PhotographerProfile(),
        customTemplates: List<CustomTemplate> = emptyList(),
        releaseLanguageTag: String = "en",
    ): ByteArray {
        val labels = PdfLabelCatalog.forLanguage(releaseLanguageTag)
        val rtl = AppLocale.isRtl(releaseLanguageTag)
        val document = PdfDocument()
        val accent = parseAccent(profile.brandAccentHex)
        val writer = PaginatedWriter(document, accentColor = accent, rtl = rtl, labels = labels)

        val customs = customTemplates
        val option = TemplateResolver.resolveOption(draft.templateId, customs)
        if (profile.brandingEnabled) {
            writer.drawBrandingHeader(profile)
        }
        writer.drawTitle(labels.title)
        writer.drawMetaLine(
            "${labels.generated} · ${option.displayName} · ${option.version}",
        )
        writer.gap()
        writer.drawHeading(labels.parties)
        writer.drawBody("${labels.model}: ${draft.modelName}")
        writer.drawBody("${labels.model_email}: ${draft.modelEmail}")
        writer.drawBody("${labels.photographer}: ${draft.shooterName}")
        if (profile.brandingEnabled) {
            optionalLine(profile.studioName)?.let { writer.drawBody("${labels.studio}: $it") }
        }
        optionalLine(draft.photographerEmail)?.let {
            writer.drawBody("${labels.photographer_email}: $it")
        }
        optionalLine(draft.photographerPhone)?.let {
            writer.drawBody("${labels.photographer_phone}: $it")
        }
        optionalLine(draft.clientAgency)?.let { writer.drawBody("${labels.client}: $it") }
        writer.gap()
        writer.drawHeading(labels.shoot)
        optionalLine(draft.shootId)?.let { writer.drawBody("${labels.shoot_id}: $it") }
        optionalLine(draft.locationName)?.let { writer.drawBody("${labels.shoot_location}: $it") }
        writer.drawBody("${labels.description}: ${draft.description.trim()}")
        optionalLine(draft.notes)?.let { writer.drawWrapped("${labels.notes}: $it", writer.bodyPaint) }
        writer.gap()
        writer.drawHeading(labels.terms)
        writer.drawBody("${labels.template}: ${option.displayName}")
        val terms = TemplateResolver.resolveBody(
            draft.templateId,
            customs,
            draft.modelName,
            draft.shooterName,
            releaseLanguageTag,
        )
        writer.drawWrapped(terms, writer.smallPaint)
        writer.gap()
        writer.drawHeading(labels.signature)
        writer.drawSignature(draft.signatureBitmap)
        writer.gap()
        writer.drawHeading(labels.metadata)
        writer.drawBody("${labels.signed_utc}: ${Formatters.formatUtc(metadata.signedAtUtc)}")
        Formatters.formatPlaceLabel(metadata)?.let { place ->
            val source = Formatters.formatPlaceSource(metadata.placeSource)?.let { " ($it)" }.orEmpty()
            writer.drawBody("${labels.place}: $place$source")
        }
        val coords = Formatters.formatCoordinates(metadata)
        writer.drawBody(
            "${labels.gps}: ${coords ?: unavailableGpsReason(metadata.locationStatus, labels)}",
        )
        writer.drawBody(
            "${labels.location_status}: ${statusLabel(metadata.locationStatus, labels)}",
        )
        if (attestationAccepted) {
            writer.drawWrapped(labels.attestation, writer.bodyPaint)
        }
        writer.gap()
        writer.drawWrapped(labels.footer, writer.smallPaint)
        if (profile.brandingEnabled) {
            writer.gap()
            val studio = profile.studioName.trim().ifBlank { profile.displayName.trim() }
            if (studio.isNotEmpty()) {
                writer.drawMetaLine(labels.prepared.replace("%s", studio))
            }
        }

        writer.finish()
        val out = ByteArrayOutputStream()
        document.writeTo(out)
        document.close()
        return out.toByteArray()
    }

    private fun parseAccent(hex: String): Int = runCatching {
        Color.parseColor(hex.trim().ifBlank { "#3A86FF" })
    }.getOrDefault(Color.parseColor("#3A86FF"))

    private fun optionalLine(value: String): String? =
        value.trim().takeIf { it.isNotEmpty() }

    private fun statusLabel(status: LocationStatus, labels: PdfLabels): String = when (status) {
        LocationStatus.Available -> labels.status_available
        LocationStatus.PermissionDenied -> labels.status_denied
        LocationStatus.Unavailable -> labels.status_unavailable
        LocationStatus.Timeout -> labels.status_timeout
        LocationStatus.Acquiring -> labels.status_acquiring
    }

    private fun unavailableGpsReason(status: LocationStatus, labels: PdfLabels): String =
        when (status) {
            LocationStatus.PermissionDenied -> labels.gps_denied
            LocationStatus.Timeout -> labels.gps_timeout
            LocationStatus.Unavailable -> labels.gps_unavailable
            LocationStatus.Acquiring -> labels.gps_acquiring
            LocationStatus.Available -> labels.gps_unavailable
        }

    private class PaginatedWriter(
        private val document: PdfDocument,
        private val accentColor: Int = Color.BLACK,
        private val rtl: Boolean = false,
        private val labels: PdfLabels,
    ) {
        private var pageNumber = 0
        private var page: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var y = MARGIN.toFloat()

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = if (rtl) Paint.Align.RIGHT else Paint.Align.LEFT
        }
        val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            textSize = 13f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = if (rtl) Paint.Align.RIGHT else Paint.Align.LEFT
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.LEFT
        }
        val smallPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 9.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.LEFT
        }

        private val textAlign: Layout.Alignment =
            if (rtl) Layout.Alignment.ALIGN_OPPOSITE else Layout.Alignment.ALIGN_NORMAL

        private val textX: Float
            get() = if (rtl) (PAGE_WIDTH - MARGIN).toFloat() else MARGIN.toFloat()

        init {
            startPage()
        }

        fun gap() {
            ensureSpace(SECTION_GAP + 4f)
            y += SECTION_GAP
        }

        fun drawTitle(text: String) {
            ensureSpace(titlePaint.textSize + 12f)
            canvas!!.drawText(text, textX, y + titlePaint.textSize, titlePaint)
            y += titlePaint.textSize + 8f
        }

        fun drawMetaLine(text: String) {
            ensureSpace(smallPaint.textSize + 8f)
            val paint = TextPaint(smallPaint).apply {
                textAlign = if (rtl) Paint.Align.RIGHT else Paint.Align.LEFT
            }
            canvas!!.drawText(text, textX, y + paint.textSize, paint)
            y += smallPaint.textSize + 4f
        }

        fun drawBrandingHeader(profile: PhotographerProfile) {
            val logoFile = profile.logoPath.takeIf { it.isNotBlank() }?.let { File(it) }
            val logo = if (logoFile != null && logoFile.exists()) {
                BitmapFactory.decodeFile(logoFile.absolutePath)
            } else {
                null
            }
            if (logo != null && !logo.isRecycled) {
                val maxH = 48f
                val maxW = 120f
                val scale = minOf(maxW / logo.width, maxH / logo.height, 1f)
                val w = logo.width * scale
                val h = logo.height * scale
                ensureSpace(h + 16f)
                val left = if (rtl) PAGE_WIDTH - MARGIN - w else MARGIN.toFloat()
                canvas!!.drawBitmap(
                    logo,
                    null,
                    android.graphics.RectF(left, y, left + w, y + h),
                    Paint(Paint.FILTER_BITMAP_FLAG),
                )
                y += h + 6f
            }
            val studio = profile.studioName.trim().ifBlank { profile.displayName.trim() }
            if (studio.isNotEmpty()) {
                ensureSpace(headingPaint.textSize + 10f)
                canvas!!.drawText(studio, textX, y + headingPaint.textSize, headingPaint)
                y += headingPaint.textSize + 4f
            }
            ensureSpace(8f)
            val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accentColor
                strokeWidth = 2f
            }
            canvas!!.drawLine(
                MARGIN.toFloat(),
                y,
                (PAGE_WIDTH - MARGIN).toFloat(),
                y,
                line,
            )
            y += 10f
        }

        fun drawHeading(text: String) {
            ensureSpace(headingPaint.textSize + 10f)
            canvas!!.drawText(text, textX, y + headingPaint.textSize, headingPaint)
            y += headingPaint.textSize + 6f
        }

        fun drawBody(text: String) = drawWrapped(text, bodyPaint)

        fun drawWrapped(text: String, paint: TextPaint) {
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, CONTENT_WIDTH)
                .setAlignment(textAlign)
                .setLineSpacing(0f, 1.15f)
                .setIncludePad(false)
                .build()
            val maxBlock = PAGE_HEIGHT - MARGIN * 2
            if (layout.height > maxBlock) {
                drawLayoutPaginated(layout)
                return
            }
            ensureSpace(layout.height + 4f)
            canvas!!.save()
            canvas!!.translate(MARGIN.toFloat(), y)
            layout.draw(canvas!!)
            canvas!!.restore()
            y += layout.height + 4f
        }

        private fun drawLayoutPaginated(layout: StaticLayout) {
            var line = 0
            while (line < layout.lineCount) {
                val lineBottom = layout.getLineBottom(line) - layout.getLineTop(line)
                ensureSpace(lineBottom + 2f)
                val top = layout.getLineTop(line)
                canvas!!.save()
                canvas!!.translate(MARGIN.toFloat(), y - top)
                canvas!!.clipRect(0, top, CONTENT_WIDTH, layout.getLineBottom(line))
                layout.draw(canvas!!)
                canvas!!.restore()
                y += lineBottom + 2f
                line++
            }
            y += 2f
        }

        fun drawSignature(signature: Bitmap?) {
            if (signature == null || signature.isRecycled) {
                drawBody(labels.no_sig)
                return
            }
            val maxWidth = 220f
            val maxHeight = 90f
            val scale = minOf(maxWidth / signature.width, maxHeight / signature.height, 1f)
            val width = signature.width * scale
            val height = signature.height * scale
            ensureSpace(height + 10f)
            val destLeft = if (rtl) PAGE_WIDTH - MARGIN - width else MARGIN.toFloat()
            val destTop = y
            val borderPaint = Paint().apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas!!.drawRect(destLeft, destTop, destLeft + width, destTop + height, borderPaint)
            canvas!!.drawBitmap(
                signature,
                null,
                android.graphics.RectF(destLeft, destTop, destLeft + width, destTop + height),
                Paint(Paint.FILTER_BITMAP_FLAG),
            )
            y = destTop + height + 6f
        }

        fun finish() {
            page?.let { document.finishPage(it) }
            page = null
            canvas = null
        }

        private fun ensureSpace(needed: Float) {
            val bottom = PAGE_HEIGHT - MARGIN
            if (y + needed > bottom) {
                startPage()
            }
        }

        private fun startPage() {
            page?.let { document.finishPage(it) }
            pageNumber += 1
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = document.startPage(info)
            canvas = page!!.canvas
            y = MARGIN.toFloat()
            if (pageNumber > 1) {
                val continued = labels.continued.replace("%d", pageNumber.toString())
                val paint = TextPaint(smallPaint).apply {
                    textAlign = if (rtl) Paint.Align.RIGHT else Paint.Align.LEFT
                }
                canvas!!.drawText(continued, textX, y + paint.textSize, paint)
                y += smallPaint.textSize + 10f
            }
        }
    }

    companion object {
        const val PAGE_WIDTH = 612
        const val PAGE_HEIGHT = 792
        const val MARGIN = 48
        val CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2
        const val SECTION_GAP = 10f
    }
}
