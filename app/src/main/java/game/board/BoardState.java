package game.board;

/**
 * Enumeration of the Board state
 */
public enum BoardState {
    PLAYER_A_TURN,
    PLAYER_B_TURN,

    PLAYER_A_HAS_NO_VALID_MOVE,
    PLAYER_B_HAS_NO_VALID_MOVE,

    PLAYER_A_GETS_ANOTHER_TURN,
    PLAYER_B_GETS_ANOTHER_TURN,

    PLAYER_A_WAS_ROBBED,
    PLAYER_B_WAS_ROBBED,

    PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE,
    PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE,

    GAME_OVER
}
