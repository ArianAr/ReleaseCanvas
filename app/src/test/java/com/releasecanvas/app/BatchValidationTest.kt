package com.releasecanvas.app

import com.releasecanvas.app.data.model.BatchModelInput
import com.releasecanvas.app.data.model.BatchSession
import com.releasecanvas.app.data.model.BatchRosterError
import com.releasecanvas.app.util.Validation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BatchValidationTest {

    @Test
    fun validateBatchModels_requiresTwoFilledModels() {
        val err = Validation.validateBatchModels(
            names = listOf("A", ""),
            emails = listOf("a@example.com", ""),
        )
        assertTrue(err is BatchRosterError.TooFew)
    }

    @Test
    fun validateBatchModels_acceptsTwoValid() {
        val err = Validation.validateBatchModels(
            names = listOf("Alex", "Sam"),
            emails = listOf("a@example.com", "s@example.com"),
        )
        assertNull(err)
    }

    @Test
    fun validateBatchModels_rejectsBadEmail() {
        val err = Validation.validateBatchModels(
            names = listOf("Alex", "Sam"),
            emails = listOf("a@example.com", "not-an-email"),
        )
        assertTrue(err is BatchRosterError.InvalidEmail)
        assertEquals(2, (err as BatchRosterError.InvalidEmail).index)
    }

    @Test
    fun validateSharedShoot_requiresPhotographerAndDescription() {
        val bad = Validation.validateSharedShoot("", "")
        assertTrue(bad.hasErrors)
        val ok = Validation.validateSharedShoot("Pat", "Portrait set")
        assertFalse(ok.hasErrors)
    }

    @Test
    fun batchSession_progressAndHasNext() {
        val models = listOf(
            BatchModelInput("1", "A", "a@x.com"),
            BatchModelInput("2", "B", "b@x.com"),
            BatchModelInput("3", "C", "c@x.com"),
        )
        val s0 = BatchSession(models, currentIndex = 0)
        assertEquals(1, s0.humanIndex)
        assertEquals(3, s0.total)
        assertTrue(s0.hasNext)
        assertEquals("A", s0.currentModel?.modelName)

        val s2 = s0.copy(currentIndex = 2)
        assertFalse(s2.hasNext)
        assertEquals("C", s2.currentModel?.modelName)
    }
}
