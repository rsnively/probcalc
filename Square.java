package probcalc;

/**
 * From the player's perspective, these are the possible values for a single
 * square in the grid of a minesweeper game.
 * 
 * The enumerations ONE through EIGHT represent the number of mines in
 * surrounding squares when the player has revealed that square (and EMPTY
 * represents when there are no such mines).
 * 
 * The UNKNOWN enumeration represents squares that have not yet been clicked,
 * while the FLAG enumeration represents unknown squares that the player
 * believes to be mines.
 * 
 * The MINE and WRONGFLAG enumerations are only used when the player has lost
 * the game. The first reveals the locations of all the mines in the game, and
 * the second tells players where they may have flagged a truly safe square.
 */
public enum Square {
	EMPTY, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT,
	FLAG, UNKNOWN, MINE, WRONGFLAG;
	
	/**
	 * Return an integer value equal to the number of mines surrounding a
	 * square. Hopefully this will only be called on numbered squares, but a 
	 * -1 is returned if this is not the case.
	 */
	public static int toInt(Square s) {
		switch (s) {
			case EMPTY: return 0;
			case ONE: return 1;
			case TWO: return 2;
			case THREE: return 3;
			case FOUR: return 4;
			case FIVE: return 5;
			case SIX: return 6;
			case SEVEN: return 7;
			case EIGHT: return 8;
			default: break;
		}
		
		return -1;
	}
	
	/**
	 * Given an integer value, returns the appropriate Square constant. If
	 * given a value greater than eight or less than zero, returns the
	 * UNKNOWN constant.
	 */
	public static Square toSquare(int n) {
		switch (n) {
			case 0: return EMPTY;
			case 1: return ONE;
			case 2: return TWO;
			case 3: return THREE;
			case 4: return FOUR;
			case 5: return FIVE;
			case 6: return SIX;
			case 7: return SEVEN;
			case 8: return EIGHT;
			default: return UNKNOWN;
		}
	}
}
