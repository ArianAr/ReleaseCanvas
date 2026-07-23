package com.releasecanvas.app

import com.releasecanvas.app.data.model.LocationStatus
import com.releasecanvas.app.data.model.PlaceSource
import com.releasecanvas.app.data.model.SigningMetadata
import com.releasecanvas.app.util.Formatters
import com.releasecanvas.app.util.Validation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class FormattersTest {

    @Test
    fun sanitizeForFileName_stripsIllegalCharacters() {
        val result = Formatters.sanitizeForFileName("Jane / Doe: Studio?")
        assertEquals("Jane_Doe_Studio", result)
    }

    @Test
    fun buildFileName_includesModelAndStamp() {
        val instant = Instant.parse("2026-07-23T15:30:00Z")
        val name = Formatters.buildFileName("Alex Model", instant)
        assertEquals("Release_Alex_Model_20260723_153000.pdf", name)
    }

    @Test
    fun formatLocation_availableIncludesAccuracy() {
        val meta = SigningMetadata(
            signedAtUtc = Instant.parse("2026-07-23T15:30:00Z"),
            latitude = 37.7749,
            longitude = -122.4194,
            locationAccuracyM = 12f,
            locationStatus = LocationStatus.Available,
        )
        val text = Formatters.formatLocation(meta)
        assertTrue(text.contains("37.774900"))
        assertTrue(text.contains("-122.419400"))
        assertTrue(text.contains("±12m"))
    }

    @Test
    fun formatLocation_includesPlaceAndSource() {
        val meta = SigningMetadata(
            signedAtUtc = Instant.parse("2026-07-23T15:30:00Z"),
            latitude = 37.7749,
            longitude = -122.4194,
            locationAccuracyM = 12f,
            locationStatus = LocationStatus.Available,
            placeCity = "San Francisco",
            placeCountry = "United States",
            placeSource = PlaceSource.ReverseGeocode,
        )
        val text = Formatters.formatLocation(meta)
        assertTrue(text.contains("San Francisco, United States"))
        assertTrue(text.contains("reverse geocode"))
        assertTrue(text.contains("37.774900"))
    }

    @Test
    fun formatPlaceLabel_manualCityOnly() {
        val meta = SigningMetadata(
            signedAtUtc = Instant.parse("2026-07-23T15:30:00Z"),
            latitude = null,
            longitude = null,
            locationAccuracyM = null,
            locationStatus = LocationStatus.Unavailable,
            placeCity = "Lisbon",
            placeCountry = null,
            placeSource = PlaceSource.Manual,
        )
        assertEquals("Lisbon", Formatters.formatPlaceLabel(meta))
        assertEquals("Lisbon [manual]", Formatters.formatLocation(meta))
    }

    @Test
    fun formatLocation_permissionDeniedMessage() {
        val meta = SigningMetadata(
            signedAtUtc = Instant.parse("2026-07-23T15:30:00Z"),
            latitude = null,
            longitude = null,
            locationAccuracyM = null,
            locationStatus = LocationStatus.PermissionDenied,
        )
        assertEquals("unavailable (permission denied)", Formatters.formatLocation(meta))
        assertNull(Formatters.formatPlaceLabel(meta))
    }

    @Test
    fun emailValidation_pure() {
        assertTrue(Validation.isValidEmailPure("model@example.com"))
        assertFalse(Validation.isValidEmailPure("not-an-email"))
        assertFalse(Validation.isValidEmailPure(""))
    }
}
