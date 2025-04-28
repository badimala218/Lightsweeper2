package com.badimala.lightsweeper2

import java.util.Random

const val GRID_SIZE = 9
const val MINES_TOTAL = 10

class LightsweeperGame {

    // 2D list of booleans
    private val lightsGrid = MutableList(GRID_SIZE) { MutableList(GRID_SIZE) { true } }
    private val mines = MutableList(GRID_SIZE) { MutableList(GRID_SIZE) { true } }
    private val visited = MutableList(GRID_SIZE) { MutableList(GRID_SIZE) { true } }
    private val directionX = arrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val directionY = arrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

    fun newGame() {
        val randomNumGenerator = Random()

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                mines[row][col] = false
                lightsGrid[row][col] = false
                visited[row][col] = false
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
        return !lightsGrid[row][col] && visited[row][col]
    }

    fun isVisited(row: Int, col: Int): Boolean {
        return visited[row][col]
    }

    fun isValidCell(row: Int, col: Int): Boolean {
        return (row >= 0) && (row < GRID_SIZE) && (col >= 0) &&
                (col < GRID_SIZE)
    }

    fun selectLight(row: Int, col: Int) {
        if (visited[row][col] == false) {
            visited[row][col] = true
            lightsGrid[row][col] = !lightsGrid[row][col]
            if (countAdjacentMines(row, col) == 0) {
                selectAdjacentLights(row, col)
            }
        }
    }

    fun selectAsLight(row: Int, col: Int) {
        if (visited[row][col] == false) {
            visited[row][col] = true
            lightsGrid[row][col] = true
            if (countAdjacentMines(row, col) == 0) {
                selectAdjacentLights(row, col)
            }
        }
    }

    fun selectAsNonLight(row: Int, col: Int) {
        if (visited[row][col] == false) {
            visited[row][col] = true
            lightsGrid[row][col] = false
            if (countAdjacentMines(row, col) == 0) {
                selectAdjacentLights(row, col)
            }
        }
    }

    private fun selectAdjacentLights(row: Int, col: Int) {
        for (direction in 0 until directionX.size) {
            var newRow = row + directionX[direction]
            var newCol = col + directionY[direction]
            if (isValidCell(newRow, newCol) == true &&
                isMine(newRow, newCol) == false &&
                visited[row][col] == false) {
                selectAsNonLight(newRow, newCol)
            }
        }
    }

    val isGameOver: Boolean
        get() {
            for (row in 0 until GRID_SIZE) {
                for (col in 0.until(GRID_SIZE)) {
                    if (visited[row][col] == false) {
                        return false
                    }
                }
            }
            return true
        }

    val score: Int
        get() {
            var count = 0
            for (row in 0 until GRID_SIZE) {
                for (col in 0.until(GRID_SIZE)) {
                    if (lightsGrid[row][col] == mines[row][col]) {
                        count++
                    }
                }
            }
            return count
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