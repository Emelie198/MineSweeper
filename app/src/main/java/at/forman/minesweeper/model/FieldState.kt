package at.forman.minesweeper.model

enum class FieldState(val number: Int = 0) {
    HIDDEN, VISIBLE, FLAGGED, BOMB, ZERO, ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8)
}
