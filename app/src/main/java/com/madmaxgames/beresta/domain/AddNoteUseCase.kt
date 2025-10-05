package com.madmaxgames.beresta.domain

class AddNoteUseCase(
    private val repository: NotesRepository
) {

    fun addNote(note: Note) {
        repository.addNotes(note)
    }
}