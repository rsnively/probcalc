package probcalc;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class generates the minefield that the player tries to guess throughout
 * the course of the game.
 */
public class Minefield {

	// The number of rows in the field.
	private int m_rows;
	// The number of columns in the field.
	private int m_cols;
	// The number of mines in the field.
	private int m_mines;
	
	// The mine grid. Square (0,0) is in the top left, and the square in the
	// bottom-right is (m_rows - 1, m_cols - 1). A value of true indicates that
	// the square is a mine. False indicates otherwise.
	private boolean m_grid[][];
	
	///////////////////////////////////////////////////////////////////////////
	//								CONSTRUCTORS							 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Allows for a minefield of the desired dimensions to be specified.  The
	 * first click is also specified, so that no mines are placed in or around
	 * it.
	 * 
	 * @param rows The number of rows in the field
	 * @param cols The number of columns in the field
	 * @param mines The number of mines in the field
	 * @param f_row The row of the first click
	 * @param f_col The column of the first click
	 */
	public Minefield(int rows, int cols, int mines, int f_row, int f_col) {
		// The number of rows in the board must be greater than zero. It is set
		// to one if not.
		m_rows = rows;
		if (m_rows <= 0) {
			m_rows = 1;
		}
		
		// The number of columns in the field must be greater than zero. It is
		// set to one if not.
		m_cols = cols;
		if (m_cols <= 0) {
			m_cols = 1;
		}
		
		// The number of mines in the field must be non-negative, it is set to
		// zero if not.
		int total = m_cols * m_rows;
		m_mines = mines;
		if (m_mines < 0) {
			m_mines = 0;
		}
		// The number of mines must also be no greater than the total number of
		// squares in the field less nine. The nine is due to the fact that the
		// first click necessitates a maximum of nine squares to be non-mines.
		else if (m_mines > total - 9) {
			m_mines = total - 9;
		}
		
		// Initialize the grid for storing the mines.
		m_grid = new boolean[m_rows][m_cols];
		
		// Keep track of which squares have been used so that we can distribute
		// the mines more quickly.
		ArrayList<Integer> unused_squares = new ArrayList<Integer>();
		for (int i = 0; i < m_rows * m_cols; i++) {
			unused_squares.add(i);
		}
		int num_unused_squares = m_rows * m_cols;
		
		// Remove the first square and all of its neighbors from the list of
		// squares which may be used for mines. The order is necessary (right-
		// to-left and bottom-to-top) so that removing one square from the list
		// does not change the positions of the other squares.
		
		// Remove bottom-right if it exists.
		if (removeSquare(f_row + 1, f_col + 1, unused_squares)) { num_unused_squares--; }
		// Remove bottom if it exists.
		if (removeSquare(f_row + 1, f_col, unused_squares))     { num_unused_squares--; }
		// Remove bottom-left if it exists.
		if (removeSquare(f_row + 1, f_col - 1, unused_squares)) { num_unused_squares--; }
		// Remove right if it exists.
		if (removeSquare(f_row, f_col + 1, unused_squares))		{ num_unused_squares--; }
		// Remove the first click.
		if (removeSquare(f_row, f_col, unused_squares))			{ num_unused_squares--; }
		// Remove the left if it exists.
		if (removeSquare(f_row, f_col - 1, unused_squares))		{ num_unused_squares--; }
		// Remove the top-right if it exists.
		if (removeSquare(f_row - 1, f_col + 1, unused_squares)) { num_unused_squares--; }
		// Remove the top if it exists.
		if (removeSquare(f_row - 1, f_col, unused_squares))		{ num_unused_squares--; }
		// Remove the top-left if it exists.
		if (removeSquare(f_row - 1, f_col - 1, unused_squares)) { num_unused_squares--; }
		
		// Randomly generate the mines in the game.
		Random generator = new Random(System.nanoTime());
		int current_mines = 0;
		while (current_mines < m_mines) {
			
			// Find a victim row and column.
			int rand = generator.nextInt(num_unused_squares - 1);
			int rand_row = unused_squares.get(rand) / m_cols;
			int rand_col = unused_squares.get(rand) % m_cols;
			
			// Set the square to be a mine.
			m_grid[rand_row][rand_col] = true;
			current_mines++;
			
			// Remove the square from the usable square list.
			unused_squares.remove(rand);
			num_unused_squares--;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//								ACCESSORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns whether or not the square at (r,c) is a mine. Returns false if
	 * the given coordinates are out-of-bounds.
	 */
	public boolean get(int r, int c) {
		if (r < 0 || c < 0 || r >= m_rows || c>= m_cols) {
			return false;
		}
		
		return m_grid[r][c];
	}
	
	/**
	 * Returns the number of mines around a given square.
	 */
	public int minesSurrounding(int r, int c) {
		int count = 0;
		
		if (get(r-1,c-1)) {count++;}
		if (get(r-1,c)) {count++;}
		if (get(r-1,c+1)) {count++;}
		if (get(r,c-1)) {count++;}
		if (get(r,c+1)) {count++;}
		if (get(r+1,c-1)) {count++;}
		if (get(r+1,c)) {count++;}
		if (get(r+1,c+1)) {count++;}
		
		return count;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//							PRIVATE FUNCTIONS							 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Removes the square at (r, c) from the list of usable squares for mine
	 * locations. This function returns true if the squre was in bounds, or
	 * false if the square was not in bounds.
	 */
	private boolean removeSquare(int r, int c, ArrayList<Integer> l) {
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		l.remove(r * m_cols + c);
		return true;
	}
}
