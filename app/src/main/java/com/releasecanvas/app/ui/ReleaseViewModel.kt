package com.releasecanvas.app.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.releasecanvas.app.data.location.LocationRepository
import com.releasecanvas.app.data.model.ExportResult
import com.releasecanvas.app.data.model.FormErrors
import com.releasecanvas.app.data.model.HistoryEntry
import com.releasecanvas.app.data.model.LocationStatus
import com.releasecanvas.app.data.model.PhotographerProfile
import com.releasecanvas.app.data.model.ReleaseDraft
import com.releasecanvas.app.data.pdf.PdfCompiler
import com.releasecanvas.app.data.pdf.ReleaseTemplate
import com.releasecanvas.app.data.prefs.PreferencesStore
import com.releasecanvas.app.data.storage.DocumentStore
import com.releasecanvas.app.util.Formatters
import com.releasecanvas.app.util.Validation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class ReleaseUiState(
    val draft: ReleaseDraft = ReleaseDraft(),
    val formErrors: FormErrors = FormErrors(),
    val hasSignatureStrokes: Boolean = false,
    val attestationAccepted: Boolean = false,
    val locationPreviewStatus: LocationStatus = LocationStatus.Acquiring,
    val locationPreviewText: String = "Acquiring GPS…",
    val isExporting: Boolean = false,
    val exportError: String? = null,
    val lastExport: ExportResult? = null,
    val profile: PhotographerProfile = PhotographerProfile(),
    val profileSavedMessage: String? = null,
)

class ReleaseViewModel(
    application: Application,
    private val preferencesStore: PreferencesStore,
    private val locationRepository: LocationRepository,
    private val pdfCompiler: PdfCompiler,
    private val documentStore: DocumentStore,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ReleaseUiState())
    val uiState: StateFlow<ReleaseUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<HistoryEntry>> = preferencesStore.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val photographerProfile: StateFlow<PhotographerProfile> = preferencesStore.photographerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PhotographerProfile())

    init {
        viewModelScope.launch {
            preferencesStore.photographerProfile.collect { profile ->
                _uiState.update { state ->
                    val draft = state.draft
                    state.copy(
                        profile = profile,
                        draft = draft.copy(
                            shooterName = draft.shooterName.ifBlank { profile.displayName },
                            photographerEmail = draft.photographerEmail.ifBlank { profile.email },
                            photographerPhone = draft.photographerPhone.ifBlank { profile.phone },
                        ),
                    )
                }
            }
        }
        viewModelScope.launch {
            val templateId = preferencesStore.lastTemplateId.first()
            if (templateId.isNotBlank()) {
                _uiState.update {
                    it.copy(draft = it.draft.copy(template = ReleaseTemplate.fromId(templateId)))
                }
            }
        }
    }

    fun updateModelName(value: String) = updateDraft { copy(modelName = value) }
    fun updateModelEmail(value: String) = updateDraft { copy(modelEmail = value) }
    fun updateShooterName(value: String) = updateDraft { copy(shooterName = value) }
    fun updateDescription(value: String) = updateDraft { copy(description = value) }
    fun updateTemplate(template: ReleaseTemplate) {
        updateDraft { copy(template = template) }
        viewModelScope.launch { preferencesStore.setLastTemplateId(template.id) }
    }
    fun updateShootId(value: String) = updateDraft { copy(shootId = value) }
    fun updatePhotographerEmail(value: String) = updateDraft { copy(photographerEmail = value) }
    fun updatePhotographerPhone(value: String) = updateDraft { copy(photographerPhone = value) }
    fun updateClientAgency(value: String) = updateDraft { copy(clientAgency = value) }
    fun updateNotes(value: String) = updateDraft { copy(notes = value) }
    fun updateLocationName(value: String) = updateDraft { copy(locationName = value) }
    fun updateCity(value: String) = updateDraft { copy(city = value) }
    fun updateCountry(value: String) = updateDraft { copy(country = value) }

    private fun updateDraft(block: ReleaseDraft.() -> ReleaseDraft) {
        _uiState.update { state ->
            state.copy(draft = state.draft.block(), formErrors = FormErrors(), exportError = null)
        }
    }

    fun validateForm(): Boolean {
        val draft = _uiState.value.draft
        val errors = Validation.validateForm(
            modelName = draft.modelName,
            modelEmail = draft.modelEmail,
            shooterName = draft.shooterName,
            description = draft.description,
        )
        _uiState.update { it.copy(formErrors = errors) }
        if (!errors.hasErrors) {
            viewModelScope.launch {
                // Keep display name in profile if empty, otherwise leave profile as-is
                // and only persist last used photographer name for form convenience.
                preferencesStore.setShooterName(draft.shooterName)
                preferencesStore.setLastTemplateId(draft.template.id)
            }
        }
        return !errors.hasErrors
    }

    fun savePhotographerProfile(profile: PhotographerProfile) {
        viewModelScope.launch {
            preferencesStore.setPhotographerProfile(profile)
            _uiState.update {
                it.copy(
                    profile = profile,
                    profileSavedMessage = "Profile saved",
                    draft = it.draft.copy(
                        shooterName = it.draft.shooterName.ifBlank { profile.displayName },
                        photographerEmail = it.draft.photographerEmail.ifBlank { profile.email },
                        photographerPhone = it.draft.photographerPhone.ifBlank { profile.phone },
                    ),
                )
            }
        }
    }

    fun clearProfileSavedMessage() {
        _uiState.update { it.copy(profileSavedMessage = null) }
    }

    suspend fun importProfileLogo(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val app = getApplication<Application>()
            val dest = File(app.filesDir, "profile_logo.jpg")
            app.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dest).use { output -> input.copyTo(output) }
            } ?: return@withContext null
            dest.absolutePath
        }.getOrNull()
    }

    fun clearProfileLogo(current: PhotographerProfile) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                current.logoPath.takeIf { it.isNotBlank() }?.let { path ->
                    runCatching { File(path).delete() }
                }
            }
            savePhotographerProfile(current.copy(logoPath = ""))
        }
    }

    fun setSignature(bitmap: Bitmap?, hasStrokes: Boolean) {
        val previous = _uiState.value.draft.signatureBitmap
        if (previous != null && previous !== bitmap && !previous.isRecycled) {
            previous.recycle()
        }
        _uiState.update {
            it.copy(
                draft = it.draft.copy(signatureBitmap = bitmap),
                hasSignatureStrokes = hasStrokes,
            )
        }
    }

    fun clearSignatureFlag() {
        setSignature(null, false)
    }

    fun setAttestationAccepted(accepted: Boolean) {
        _uiState.update { it.copy(attestationAccepted = accepted, exportError = null) }
    }

    fun refreshLocationPreview() {
        viewModelScope.launch {
            val draft = _uiState.value.draft
            _uiState.update {
                it.copy(
                    locationPreviewStatus = LocationStatus.Acquiring,
                    locationPreviewText = "Acquiring GPS…",
                )
            }
            val meta = locationRepository.captureForSigning(
                timeoutMs = 8_000L,
                manualCity = draft.city,
                manualCountry = draft.country,
            )
            _uiState.update {
                it.copy(
                    locationPreviewStatus = meta.locationStatus,
                    locationPreviewText = Formatters.formatLocation(meta),
                )
            }
        }
    }

    fun export(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isExporting) return
        if (!state.hasSignatureStrokes || state.draft.signatureBitmap == null) {
            _uiState.update { it.copy(exportError = "Please provide a signature") }
            return
        }
        if (!state.attestationAccepted) {
            _uiState.update {
                it.copy(exportError = "Confirm age of majority / authority to sign before exporting")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, exportError = null) }
            runCatching {
                val metadata = locationRepository.captureForSigning(
                    manualCity = state.draft.city,
                    manualCountry = state.draft.country,
                )
                val bytes = pdfCompiler.compile(
                    draft = state.draft,
                    metadata = metadata,
                    attestationAccepted = true,
                )
                val result = documentStore.savePdf(bytes, state.draft.modelName, metadata)
                preferencesStore.addHistoryEntry(
                    HistoryEntry(
                        displayName = result.displayName,
                        uriString = result.contentUri.toString(),
                        signedAtUtc = Formatters.formatUtc(metadata.signedAtUtc),
                        modelName = state.draft.modelName.trim(),
                    ),
                )
                result
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        lastExport = result,
                        locationPreviewStatus = result.metadata.locationStatus,
                        locationPreviewText = Formatters.formatLocation(result.metadata),
                    )
                }
                onSuccess()
            }.onFailure { error ->
                val detail = error.message?.takeIf { it.isNotBlank() } ?: "Unknown error"
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportError = "Export failed: $detail. Your form and signature are still here — try again.",
                    )
                }
            }
        }
    }

    fun clearExportError() {
        _uiState.update { it.copy(exportError = null) }
    }

    fun resetForNewRelease() {
        val profile = _uiState.value.profile
        val template = _uiState.value.draft.template
        val oldSig = _uiState.value.draft.signatureBitmap
        if (oldSig != null && !oldSig.isRecycled) oldSig.recycle()
        _uiState.value = ReleaseUiState(
            profile = profile,
            draft = ReleaseDraft(
                shooterName = profile.displayName,
                photographerEmail = profile.email,
                photographerPhone = profile.phone,
                template = template,
            ),
        )
    }

    fun removeHistoryEntry(uriString: String) {
        viewModelScope.launch {
            preferencesStore.removeHistoryEntry(uriString)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            preferencesStore.clearHistory()
        }
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val prefs = PreferencesStore(app)
                    return ReleaseViewModel(
                        application = app,
                        preferencesStore = prefs,
                        locationRepository = LocationRepository(app),
                        pdfCompiler = PdfCompiler(),
                        documentStore = DocumentStore(app),
                    ) as T
                }
            }
    }
}
