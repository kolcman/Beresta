package com.madmaxgames.beresta.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.madmaxgames.beresta.presentation.screens.notes.NotesScreen
import com.madmaxgames.beresta.presentation.ui.theme.BerestaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BerestaTheme {
                NotesScreen(
                    onNoteClick = {
                        Log.d("MainActivity", "Note clicked: ${it}")
                    },
                    onAddNoteClick = {
                        Log.d("MainActivity", "Floating clicked!")
                    }
                )
            }
        }
    }
}

