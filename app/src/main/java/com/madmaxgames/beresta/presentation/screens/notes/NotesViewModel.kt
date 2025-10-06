package com.madmaxgames.beresta.presentation.screens.notes

import androidx.lifecycle.ViewModel
import com.madmaxgames.beresta.data.TestNotesRepositoryImpl
import com.madmaxgames.beresta.domain.AddNoteUseCase
import com.madmaxgames.beresta.domain.DeleteNoteUseCase
import com.madmaxgames.beresta.domain.EditNoteUseCase
import com.madmaxgames.beresta.domain.GetAllNotesUseCase
import com.madmaxgames.beresta.domain.GetNoteUseCase
import com.madmaxgames.beresta.domain.Note
import com.madmaxgames.beresta.domain.SearchNoteUseCase
import com.madmaxgames.beresta.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel : ViewModel() {

    private val repository = TestNotesRepositoryImpl

    private val addNoteUseCase = AddNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNoteUseCase = SearchNoteUseCase(repository)
    private val switchPinnedUseCase = SwitchPinnedStatusUseCase(repository)

    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    private val query = MutableStateFlow("")

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        addSomeNotes()
        query
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest { input ->
                if (input.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNoteUseCase(input)
                }
            }
            .onEach { notes ->
                val pinnedNotes = notes.filter { it.isPinned }
                val otherNotes = notes.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, other = otherNotes) }
            }
            .launchIn(scope)
    }

    // TODO: don't forget remove it
    private fun addSomeNotes() {
        repeat(5_000){
            addNoteUseCase(title = "title $it", content = "content $it")
        }
    }

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.DeleteNote -> {
                deleteNoteUseCase(command.noteId)
            }

            is NotesCommand.EditNote -> {
                val note = getNoteUseCase(command.note.id)
                val noteTitle = note.title
                editNoteUseCase(note.copy(title = "$noteTitle - edited"))
            }

            is NotesCommand.InputSearchQuery -> {
                query.update { command.query.trim() }
            }

            is NotesCommand.SwitchPinnedStatus -> {
                switchPinnedUseCase(command.noteId)
            }
        }
    }

}

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

    //Temp
    data class DeleteNote(val noteId: Int) : NotesCommand
    data class EditNote(val note: Note) : NotesCommand


}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val other: List<Note> = listOf()
)