package com.mbi.unscramble

import com.mbi.unscramble.ui.components.GameViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test


class GameViewModelTest {
    private  val viewModel = GameViewModel()

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        /**
         *  Warning: This way to retrieve the uiState works because MutableStateFlow is used. In the
         *  upcoming units you will learn about advanced usages of StateFlow that creates a stream
         *  of data and you need to react to handle the stream. For those scenarios you will write
         *  unit tests using different methods/approaches. This applies to all the usages of
         *  viewModel.uiState.value in this class.
         **/
        val gameUiState = viewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // Assert that current word is scrambled.
        assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)
        // Assert that current word count is set to 1.
        assertTrue(gameUiState.currentWordCount == 1)
        // Assert that initially the score is 0.
        assertTrue(gameUiState.score == 0)
        // Assert that wrong word guessed is false.
        assertFalse(gameUiState.isGuessedWordWrong)
        // Assert that game is not over.
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModelIncorrectGuessErrorFlagSet() {
        // Given an incorrect word as input
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value
        // Assert that score is unchanged
        assertEquals(0, currentGameUiState.score)
        // Assert that checkUserGuess() method updates isGuessedWordWrong correctly
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModelCorrectWordGuessedScoreUpdatedAndErrorFlagUnset(){
        val gameUiState = viewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)
        // Assert that current word is scrambled.
        assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)
        // Assert that current word count is set to 1.
        assertTrue(gameUiState.currentWordCount == 1)
        // Assert that initially the score is 0.
        assertTrue(gameUiState.score == 0)
        // Assert that the wrong word guessed is false.
        assertFalse(gameUiState.isGuessedWordWrong)
        // Assert that game is not over.
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModelAllWordsGuessedUiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            // Assert that after each correct answer, score is updated correctly
            assertNotEquals(expectedScore, currentGameUiState.score)
        }
        // Assert that after all questions are answered, the current word count is up-to-date.
        assertNotEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        // Assert that after 10 questions are answered, the game is over.
        assertFalse(currentGameUiState.isGameOver)
    }



    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}