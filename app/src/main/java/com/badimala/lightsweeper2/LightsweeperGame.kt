package com.badimala.lightsweeper

import java.util.Random

const val GRID_SIZE = 9
const val MINES_TOTAL = 10

class LightsweeperGame {

    // 2D list of booleans
    private val lightsGrid = MutableList(GRID_SIZE) { MutableList(GRID_SIZE) { true } }
    private val mines = MutableList(GRID_SIZE) { MutableList(GRID_SIZE) { true } }
    private val directionX = arrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val directionY = arrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

    fun newGame() {
        val randomNumGenerator = Random()

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                mines[row][col] = false
                lightsGrid[row][col] = false
            }
        }
        // Continue until all random mines have been created
        for (total in 0 until MINES_TOTAL) {
            var randomX = randomNumGenerator.nextInt(GRID_SIZE)
            var randomY = randomNumGenerator.nextInt(GRID_SIZE)
            if (mines[randomX][randomY] == false) {
                mines[randomX][randomY] = true
            }
        }
    }

    fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (direction in 0 until directionX.size) {
            var newRow = row + directionX[direction]
            var newCol = col + directionY[direction]
            if (isValidCell(newRow, newCol) == true &&
                isMine(newRow, newCol) == true) {
                count++
            }
        }
        return count
    }

    fun isMine(row: Int, col: Int): Boolean {
        return mines[row][col]
    }

    fun isLightOn(row: Int, col: Int): Boolean {
        return lightsGrid[row][col]
    }

    fun isNonLightOn(row: Int, col: Int): Boolean {
        return false
    }

    fun isValidCell(row: Int, col: Int): Boolean {
        return (row >= 0) && (row < GRID_SIZE) && (col >= 0) &&
                (col < GRID_SIZE)
    }

    fun selectLight(row: Int, col: Int) {
        lightsGrid[row][col] = !lightsGrid[row][col]
        if (countAdjacentMines(row, col) == 0 &&
            lightsGrid[row][col] == true) {
            selectAdjacentLights(row, col)
        }
    }

    fun selectNonLight(row: Int, col: Int) {

    }

    private fun selectAdjacentLights(row: Int, col: Int) {
        for (direction in 0 until directionX.size) {
            var newRow = row + directionX[direction]
            var newCol = col + directionY[direction]
            if (isValidCell(newRow, newCol) == true &&
                isMine(newRow, newCol) == false &&
                lightsGrid[row][col] == false) {
                selectLight(newRow, newCol)
            }
        }
    }

    val isGameOver: Boolean
        get() {
            for (row in 0 until GRID_SIZE) {
                for (col in 0.until(GRID_SIZE)) {
                    if (lightsGrid[row][col] != mines[row][col]) {
                        return false
                    }
                }
            }
            return true
        }

    var state: String
        get() {
            val boardString = StringBuilder()
            for (row in 0 until GRID_SIZE) {
                for (col in 0 until GRID_SIZE) {
                    val value = if (lightsGrid[row][col]) 'T' else 'F'
                    boardString.append(value)
                }
            }
            return boardString.toString()
        }
        set(value) {
            var index = 0
            for (row in 0 until GRID_SIZE) {
                for (col in 0 until GRID_SIZE) {
                    lightsGrid[row][col] = value[index] == 'T'
                    index++
                }
            }
        }

    var mineState: String
        get() {
            val boardString = StringBuilder()
            for (row in 0 until GRID_SIZE) {
                for (col in 0 until GRID_SIZE) {
                    val value = if (mines[row][col]) 'T' else 'F'
                    boardString.append(value)
                }
            }
            return boardString.toString()
        }
        set(value) {
            var index = 0
            for (row in 0 until GRID_SIZE) {
                for (col in 0 until GRID_SIZE) {
                    mines[row][col] = value[index] == 'T'
                    index++
                }
            }
        }
}