package com.badimala.lightsweeper2

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.badimala.lightsweeper2.GRID_SIZE
import com.badimala.lightsweeper2.LightsweeperGame

const val GAME_STATE = "gameState"
const val GAME_MINES = "gameMines"

class GameFragment : Fragment() {//, NewGameDialogFragment.OnNewGameSelectedListener {
    private lateinit var game: LightsweeperGame
    private lateinit var lightGridLayout: GridLayout
    private var lightOnColor = 0
    private var lightOffColor = 0
    private var lightWrongColor = 0
    private var nonLightColor = 0
    private var nonLightWrongColor = 0
    private var selectedCell = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val parentView = inflater.inflate(R.layout.fragment_game, container, false)

        // Add the same click handler and context menu to all grid buttons
        lightGridLayout = parentView.findViewById(R.id.light_grid)
        var count = 0
        for (gridButton in lightGridLayout.children) {
            gridButton.setOnClickListener(this::onLightButtonClick)
//            registerForContextMenu(gridButton)
            gridButton.tag = count
            count++
        }

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val onColorId = sharedPref.getInt("color", R.color.yellow)

        lightOnColor = ContextCompat.getColor(requireActivity(), onColorId)
        lightOffColor = ContextCompat.getColor(requireActivity(), R.color.black)
        lightWrongColor = ContextCompat.getColor(requireActivity(), R.color.red)
        nonLightColor = ContextCompat.getColor(requireActivity(), R.color.gray)
        nonLightWrongColor = ContextCompat.getColor(requireActivity(), R.color.darker_red)

        game = LightsweeperGame()

        if (savedInstanceState == null) {
            startGame()
        } else {
            game.mineState = savedInstanceState.getString(GAME_MINES)!!
            game.state = savedInstanceState.getString(GAME_STATE)!!
            setButtonColors()
        }

        val newGameBtn = parentView.findViewById<Button>(R.id.new_game_button)
        newGameBtn.setOnClickListener {
            createNewGameDialog()
        }

        return parentView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(GAME_STATE, game.state)
        outState.putString(GAME_MINES, game.mineState)
    }

    private fun startGame() {
        game.newGame()
        setButtonColors()
    }

    private fun createNewGameDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.new_game)
            .setMessage(R.string.new_game_warning)
            .setPositiveButton(R.string.yes, { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    startGame()
                }
            })
            .setNegativeButton(R.string.no, null)
            .create()
            .show()
    }

    private fun onLightButtonClick(view: View) {

        // Find the button's row and col
        val buttonIndex = lightGridLayout.indexOfChild(view)
        val row = buttonIndex / GRID_SIZE
        val col = buttonIndex % GRID_SIZE

        game.selectLight(row, col)
        setButtonColors()

        // Congratulate the user if the game is over
        if (game.isGameOver) {
            Toast.makeText(requireActivity(), R.string.congrats, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setButtonColors() {

        // Set all buttons' background color
        for (buttonIndex in 0 until lightGridLayout.childCount) {
            val gridButton = lightGridLayout.getChildAt(buttonIndex)

            // Find the button's row and col
            val row = buttonIndex / GRID_SIZE
            val col = buttonIndex % GRID_SIZE

            if (game.isLightOn(row, col)) {
                gridButton.setBackgroundColor(lightOnColor)
                (gridButton as Button).text = game.countAdjacentMines(row, col).toString()
                println((gridButton as Button).text)
                if (game.isMine(row, col) == false) {
                    (gridButton as Button).setBackgroundColor(lightWrongColor)
                }
            } else if (game.isNonLightOn(row, col)) {
                gridButton.setBackgroundColor(nonLightColor)
                (gridButton as Button).text = game.countAdjacentMines(row, col).toString()
                println((gridButton as Button).text)
                if (game.isMine(row, col) == true) {
                    (gridButton as Button).setBackgroundColor(lightWrongColor)
                }
            } else {
                gridButton.setBackgroundColor(lightOffColor)
            }
        }
    }
}