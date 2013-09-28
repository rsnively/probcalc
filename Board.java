package probcalc;

/**
 * Class storing information about the state of the game (from the
 * perspective of the player).
 */
public class Board {
	
	///////////////////////////////////////////////////////////////////////////
	//								MEMBERS									 //
	///////////////////////////////////////////////////////////////////////////
	
	// The number of rows in the game.
	private int m_rows;
	// The number of columns in the game.
	private int m_cols;
	// The total number of mines in the game.
	private int m_mines;
	// The number of flags currently placed.
	private int m_flags;
	// The number of covered squares.
	private int m_unknown;
	
	// The actual game grid. We will be using the age-old convention of having
	// the top-left corner be (0,0) and the bottom-right corner being
	// (m_rows - 1, m_cols - 1).
	private Square[][] m_grid;
	
	
	///////////////////////////////////////////////////////////////////////////
	//							CONSTRUCTORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Default Constructor: Sets up grid for a new "Beginner" minesweeper
	 * game.
	 */
	public Board() {
		m_rows = 9;
		m_cols = 9;
		m_mines = 10;
		m_flags = 0;
		m_unknown = m_rows * m_cols;
		m_grid = new Square[m_rows][m_cols];
		
		// Initially all squares are unknown.
		for(int r = 0; r < m_rows; r++) {
			for (int c = 0; c < m_cols; c++) {
				m_grid[r][c] = Square.UNKNOWN;
			}
		}
	}
	
	/**
	 * Allows for a board of the desired dimensions to be specified. 
	 * 
	 * No squares may be revealed initially, if one wishes to do so for testing
	 * purposes, use the Board.set(...) function.
	 * 
	 * @param rows The number of rows in the board (must be greater than 0
	 *             - set to 1 if not)
	 * @param cols The number of columns in the board (must be greater than
	 * 			   0 - set to 1 if not)
	 * @param mines The number of mines in the board (must be greater than
	 * 			    -1 - set to 0 if not - and less than the total number of
	 * 				squares on the grid - set to that number if not)
	 */
	public Board(int rows, int cols, int mines) {
		
		m_rows = rows;
		if (m_rows <= 0) {
			m_rows = 1;
		}
		
		m_cols = cols;
		if (m_cols <= 0) {
			m_cols = 1;
		}
		
		m_unknown = m_cols * m_rows;
		m_mines = mines;
		if (m_mines < 0) {
			m_mines = 0;
		}
		else if (m_mines > m_unknown) {
			m_mines = m_unknown;
		}
		
		m_flags = 0;
		m_grid = new Square[m_rows][m_cols];
		
		// Initially all squares are unknown.
		for (int r = 0; r < m_rows; r++) {
			for (int c = 0; c < m_cols; c++) {
				m_grid[r][c] = Square.UNKNOWN;
			}
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//								ACCESSORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Accessor for the number of rows in the board.
	 */
	public int rows() {
		return m_rows;
	}
	
	/**
	 * Accessor for the number of columns in the board.
	 */
	public int cols() {
		return m_cols;
	}
	
	/**
	 * Returns the contents of the grid at (r, c) or UNKNOWN if the coordinates
	 * are out of bounds.
	 */
	public Square at(int r, int c) {
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return Square.UNKNOWN;
		}
		return m_grid[r][c];
	}
	
	/**
	 * Returns the number of flags currently set for the board (note that
	 * this does not have to be less than the number of mines - it's not our
	 * fault if the player is being silly).
	 */
	public int flags() {
		return m_flags;
	}
	
	/**
	 * Returns the total number of mines in the board.
	 */
	public int mines() {
		return m_mines;
	}
	
	/**
	 * Returns the number of mines left to be found (this does not guarantee
	 * that the flags which are set are correct, nor does it necessarily return
	 * a positive value).
	 */
	public int mines_left() {
		return m_mines - m_flags;
	}
	
	/**
	 * Returns the number of unknown squares left. Only squares with the
	 * UNKNOWN value count, FLAG squares do not.
	 */
	public int unknown() {
		return m_unknown;
	}
	
	/**
	 * Returns true if any of the squares adjacent to (r,c) are "known."
	 * Otherwise, this function returns false.
	 * 
	 * If (r,c) is out-of-bounds for the board, this function returns false.
	 */
	public boolean known_adjacent(int r, int c) {
		
		// Square is out of bounds.
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		// Top-Left
		if (known(r-1,c-1)) {
			return true;
		}
		// Top
		if (known(r-1,c)) {
			return true;
		}
		// Top-Right
		if (known(r-1,c+1)) {
			return true;
		}
		// Left
		if (known(r,c-1)) {
			return true;
		}
		// Right
		if (known(r,c+1)) {
			return true;
		}
		// Bottom-Left
		if (known(r+1,c-1)) {
			return true;
		}
		// Bottom
		if (known(r+1,c)) {
			return true;
		}
		// Bottom Right
		if (known(r+1,c+1)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if square (r,c) is "known" - ie, if the player has
	 * uncovered it (flags do not count). Otherwise returns false.
	 * 
	 * Also returns false if the given square is out of bounds.
	 */
	public boolean known(int r, int c) {
		// Square is out of bounds.
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		return (at(r, c) != Square.UNKNOWN && at(r,c) != Square.FLAG);
	}
	
	/**
	 * Returns true if any of the squares adjacent to (r,c) are "unknown."
	 * Otherwise, this function returns false.
	 * 
	 * If (r,c) is out-of-bounds for the board, this function returns false.
	 */
	public boolean unknown_adjacent(int r, int c) {
		
		// Square is out of bounds.
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		// Top-Left
		if (unknown(r-1,c-1)) {
			return true;
		}
		// Top
		if (unknown(r-1,c)) {
			return true;
		}
		// Top-Right
		if (unknown(r-1,c+1)) {
			return true;
		}
		// Left
		if (unknown(r,c-1)) {
			return true;
		}
		// Right
		if (unknown(r,c+1)) {
			return true;
		}
		// Bottom-Left
		if (unknown(r+1,c-1)) {
			return true;
		}
		// Bottom
		if (unknown(r+1,c)) {
			return true;
		}
		// Bottom Right
		if (unknown(r+1,c+1)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if square (r,c) is "unknown" - ie, if the player has
	 * yet to uncover it (flags count as uncovering). Otherwise returns false.
	 * 
	 * Also returns false if the given square is out of bounds.
	 */
	public boolean unknown(int r, int c) {
		// Square is out of bounds.
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		return (at(r, c) == Square.UNKNOWN);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//								MUTATORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Sets the contents of the board at (r,c) to s. This only does so for the
     * player's perspective. It does not have any effect on the actual layout
	 * of the mines in the game.
	 * 
	 * Note that this function also increments/decrements m_flags and m_unknown
	 * in accordance with the type of square being set.
	 * 
	 * This function returns false if square (r,c) is out of bounds, or true
	 * otherwise.
	 */
	public boolean set(int r, int c, Square s) {
		
		// If the coordinates are out of bounds, return false.
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		// Unflagging a square: decrement number of flags.
		if (m_grid[r][c] == Square.FLAG && s != Square.FLAG) {
			m_flags--;
		}
		// Flagging a square: increment number of flags.
		else if (s == Square.FLAG && m_grid[r][c] != Square.FLAG) {
			m_flags++;
		}
		
		// Turning a square from known to unknown (this should never happen in
		// a normal game...): increment number of unknown squares.
		if (m_grid[r][c] != Square.UNKNOWN && s == Square.UNKNOWN) {
			m_unknown++;
		}
		// Uncovering or flagging an unknown square: decrement number of
		// unknown squares.
		else if (m_grid[r][c] == Square.UNKNOWN && s != Square.UNKNOWN) {
			m_unknown--;
		}
		
		// Modify the grid and return true.
		m_grid[r][c] = s;
		return true;
	}
}
