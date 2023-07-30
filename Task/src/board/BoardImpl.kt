package board

import board.Direction.*

fun createSquareBoard(width: Int): SquareBoard = BoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(createSquareBoard(width))

class BoardImpl(override val width: Int) : SquareBoard {

    private val cells: List<Cell> = (1..width)
        .flatMap { firstIndex -> (1..width).map { Cell(firstIndex, it) } }
        .toList()

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return cells.firstOrNull { cell -> cell.i == i && cell.j == j }
    }

    override fun getCell(i: Int, j: Int): Cell {
        val cell = getCellOrNull(i, j)
        requireNotNull(cell)
        return cell
    }

    override fun getAllCells(): Collection<Cell> {
        return cells
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        return jRange.mapNotNull { getCellOrNull(i, it) }
            .toList()
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        return iRange.mapNotNull { getCellOrNull(it, j) }.toList()
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            DOWN -> getCellOrNull(i + 1, j)
            UP -> getCellOrNull(i - 1, j)
            LEFT -> getCellOrNull(i, j - 1)
            RIGHT -> getCellOrNull(i, j + 1)
        }
    }

}

class GameBoardImpl<T>(private val squareBoard: SquareBoard): GameBoard<T>, SquareBoard by squareBoard {

    private val boardCellValueMap: MutableMap<Cell, T?> = squareBoard.getAllCells()
        .associateWith { null }
        .toMutableMap()

    override fun get(cell: Cell): T? {
        return boardCellValueMap[cell]
    }

    override fun set(cell: Cell, value: T?) {
        boardCellValueMap[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return boardCellValueMap.filterValues(predicate).keys
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return boardCellValueMap.filterValues(predicate).keys.firstOrNull()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return boardCellValueMap.any { (_, value) -> predicate.invoke(value) }
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return boardCellValueMap.all { (_, value) -> predicate.invoke(value) }
    }
}