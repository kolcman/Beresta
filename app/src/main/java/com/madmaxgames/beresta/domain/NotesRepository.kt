package com.madmaxgames.beresta.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun addNotes(title: String, content: String)
    fun editNote(note: Note)
    fun deleteNote(noteId: Int)
    fun getNote(noteId: Int): Note
    fun getAllNotes(): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    fun switchPinnedStatus(noteId: Int)

}