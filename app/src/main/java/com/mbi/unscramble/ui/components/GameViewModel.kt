package com.mbi.unscramble.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mbi.unscramble.data.MAX_NO_OF_WORDS
import com.mbi.unscramble.data.SCORE_INCREASE
import com.mbi.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GameViewModel: ViewModel() {
    private  val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)){
            updateGameState(_uiState.value.score.plus(SCORE_INCREASE))
        }else {
            _uiState.update { current -> current.copy(isGuessedWordWrong = true) }
        }
        updateUserGuess("")
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    private fun updateGameState(updateScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState -> currentState.copy(
                isGuessedWordWrong = false,
                score = updateScore,
                isGameOver = true
            ) }
        }else {
            _uiState.update { currentState -> currentState.copy(
                isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                currentWordCount =  currentState.currentWordCount.inc(),
                score = updateScore
            )
            }
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}