package com.madmaxgames.beresta.presentation.screens.creation

import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madmaxgames.beresta.data.TestNotesRepositoryImpl
import com.madmaxgames.beresta.domain.AddNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel : ViewModel() {
    private val repository = TestNotesRepositoryImpl
    private val addNoteUseCase = AddNoteUseCase(repository)
    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow();

    fun processCommand(command: CreateNoteCommand) {
        when (command) {
            CreateNoteCommand.Back -> {
                _state.update {
                    CreateNoteState.Finished
                }
            }

            is CreateNoteCommand.InputContent -> {
                _state.update { oldState ->
                    if (oldState is CreateNoteState.Creation) {
                        oldState.copy(
                            content = command.content,
                            isSaveEnabled = oldState.title.isNotBlank() && oldState.content.isNotBlank()
                        )
                    } else {
                        CreateNoteState.Creation(content = command.content)
                    }
                }

            }

            is CreateNoteCommand.InputTitle -> {
                _state.update { oldState ->
                    if (oldState is CreateNoteState.Creation) {
                        oldState.copy(
                            title = command.title,
                            isSaveEnabled = oldState.title.isNotBlank() && oldState.content.isNotBlank()
                        )
                    } else {
                        CreateNoteState.Creation(title = command.title)
                    }
                }
            }

            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { oldState ->
                        if (oldState is CreateNoteState.Creation) {
                            val title = oldState.title
                            val content = oldState.content
                            addNoteUseCase(title, content)
                            CreateNoteState.Finished
                        } else {
                            oldState
                        }
                    }
                }
            }
        }
    }


}

sealed interface CreateNoteCommand {
    data class InputTitle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String) : CreateNoteCommand
    data object Save : CreateNoteCommand
    data object Back : CreateNoteCommand
}

sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false,
    ) : CreateNoteState

    data object Finished : CreateNoteState
}