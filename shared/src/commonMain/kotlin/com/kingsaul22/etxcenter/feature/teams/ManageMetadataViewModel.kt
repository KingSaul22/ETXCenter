package com.kingsaul22.etxcenter.feature.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IMetadataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageMetadataViewModel(
    private val metadataRepository: IMetadataRepository
) : ViewModel() {

    val metadata: StateFlow<Map<String, String>> = metadataRepository
        .getMetadataFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun setEntry(key: String, value: String, isNumeric: Boolean) {
        if (key.isBlank()) return
        viewModelScope.launch {
            metadataRepository.setMetadataValue(key.trim(), value, isNumeric)
        }
    }

    fun deleteEntry(key: String) {
        viewModelScope.launch {
            metadataRepository.deleteMetadata(key)
        }
    }
}
