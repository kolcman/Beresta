package com.madmaxgames.beresta.domain

class SwitchPinnedStatusUseCase(
    private val repository: NotesRepository
) {
    operator fun invoke(noteId: Int) {
        repository.switchPinnedStatus(noteId)

    }
}