package model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(private val savedStateHandle: SavedStateHandle) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Game::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return Game(savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}