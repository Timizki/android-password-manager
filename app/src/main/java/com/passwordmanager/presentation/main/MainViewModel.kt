package com.passwordmanager.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passwordmanager.domain.model.Password
import com.passwordmanager.domain.usecase.PasswordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    val passwords: StateFlow<List<Password>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        when {
            query.isNotBlank() -> passwordUseCases.searchPasswords(query)
            category != null -> passwordUseCases.getPasswordsByCategory(category)
            else -> passwordUseCases.getAllPasswords()
        }
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val categories: StateFlow<List<String>> = passwordUseCases.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }
    
    fun deletePassword(password: Password) {
        viewModelScope.launch {
            passwordUseCases.deletePassword(password)
        }
    }
}