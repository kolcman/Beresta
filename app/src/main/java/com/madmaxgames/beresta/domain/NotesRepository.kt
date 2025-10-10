package com.madmaxgames.beresta.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun addNotes(
        title: String,
        content: String,
        isPinned: Boolean,
        updatedAt: Long
    )
    suspend fun editNote(note: Note)
    suspend fun deleteNote(noteId: Int)
    suspend fun getNote(noteId: Int): Note
    fun getAllNotes(): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun switchPinnedStatus(noteId: Int)

}