package probcalc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class runs an actual minesweeper game. It is the barrier between the
 * player's board and the actual minefield (so you know the computer doesn't
 * cheat at all...)
 *
 */
public class Game extends JPanel {
	
	///////////////////////////////////////////////////////////////////////////
	//								MEMBERS									 //
	///////////////////////////////////////////////////////////////////////////
	
	// The number of pixels on each edge of a single cell in the game.
	public final static int CELL_SIZE = 36;
	
	// The number of rows in the game board.
	private int m_rows;
	// The number of columns in the game board.
	private int m_cols;
	// The number of mines in the game board.
	private int m_mines;
	// The number of flags currently up in the game board.
	private int m_flags;
	
	// True if and only if the player has won the game.
	private boolean m_wongame;
	// True if and only if the player has lost the game.
	private boolean m_lostgame;
	// True only during the period before a player's first click.
	private boolean m_started;
	
	// The board the player can see.
	private Board m_board;
	// The field containing the mines.
	private Minefield m_field;
	// The probability calculator.
	private Calculator m_calc;
	
	// The bar displaying the number of mines left and any notifications.
	private JLabel m_statusbar;
	
	///////////////////////////////////////////////////////////////////////////
	//								CONSTRUCTORS							 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Standard constructor. Sets up a minesweeper game of the given dimensions
	 * with the desired amount of mines.
	 */
	public Game(int rows, int cols, int mines, JLabel status) {
		// The number of rows must be greater than zero, it is set to one if
		// otherwise.
		m_rows = rows;
		if (m_rows < 1) {
			m_rows = 1;
		}
		
		// The number of columns must be greater than zero, it is set to one if
		// otherwise.
		m_cols = cols;
		if (m_cols < 1) {
			m_cols = 1;
		}
		
		// The number of mines must be non-negative, it is set to zero if
		// otherwise.
		m_mines = mines;
		if (m_mines < 0) {
			m_mines = 0;
		}
		// The number of mines must not be greater than the total number of
		// squares on the board less nine (to allow room for the first click).
		else if (m_mines > m_rows * m_cols - 9) {
			m_mines = m_rows * m_cols - 9;
		}
		
		m_statusbar = status;
		setDoubleBuffered(true);
		addMouseListener(new MinesAdapter());
		newGame();
	}
	
	///////////////////////////////////////////////////////////////////////////
	//								ACCESSORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Checks to see whether or not the game is over. This happens when all of
	 * the squares which are not mines are revealed. Note that flagging all of
	 * the mined squares is not good enough.
	 * 
	 * @return If the game has been won or not
	 */
	public boolean wonGame() {
		for (int r = 0; r < m_rows; r++) {
			for (int c = 0; c < m_cols; c++) {
				if (m_field.get(r, c) == false && (!m_board.known(r,c))) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//								MUTATORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Sets up a new game with the desired dimensions. Note that the minefield
	 * is not generated, as this cannot be done until the first click occurs.
	 */
	public void newGame() {
		m_board = new Board(m_rows, m_cols, m_mines);
		m_calc = new Calculator(m_board);
		m_wongame = false;
		m_lostgame = false;
		m_started = false;
		m_flags = 0;
	}
	
	/**
	 * Starts a game at the given row and column. Generates a minefield which
	 * conforms to the first click.
	 */
	public void startGame(int r, int c) {
		m_field = new Minefield(m_rows, m_cols, m_mines, r, c);
		m_started = true;
	}
	
	/**
	 * Paints the game board into the application window.
	 */
	public void paint(Graphics g) {
		
		// Re-calculate probabilities
		m_calc = new Calculator(m_board);
		
		// Display the number of mines left.
		m_statusbar.setText("Mines Remaining: " + Integer.toString(m_mines-m_flags));
		
		// If the player has lost the game...
		if (m_lostgame) {
			// Reveal all of the squares.
			for (int r = 0; r < m_rows; r++) {
				for (int c = 0; c < m_cols; c++) {
					// Unknown mined squares become mines.
					if (m_field.get(r,c) == true && m_board.at(r,c) == Square.UNKNOWN) {
						m_board.set(r,c,Square.MINE);
					}
					// Properly flagged squares become mines.
					else if (m_field.get(r,c) == true && m_board.at(r,c) == Square.FLAG) {
						m_board.set(r,c,Square.MINE);
					}
					// Improperly flagged squares become x-ed out flags.
					else if (m_field.get(r,c) == false && m_board.at(r, c) == Square.FLAG) {
						m_board.set(r,c,Square.WRONGFLAG);
					}
					// Unknown squares become the proper number
					else if (m_board.at(r,c) == Square.UNKNOWN) {
						int m = m_field.minesSurrounding(r,c);
						m_board.set(r,c,Square.toSquare(m));
					}
				}
			}		
			// Set the losing message.
			m_statusbar.setText("You lose. Sorry bro.");
		}
		
		// If the player has won the game...
		if (m_wongame) {
			m_statusbar.setText("VICTORY!");
		}
		
		// Draw all of the squares.
		for (int r = 0; r < m_rows; r++) {
			for (int c = 0; c < m_cols; c++) {
				// Drawing an empty square.
				if (m_board.at(r, c) == Square.UNKNOWN) { 
					g.setColor(Color.BLUE);
					g.fillRect(c*CELL_SIZE, r*CELL_SIZE, CELL_SIZE, CELL_SIZE);
					
					if (m_calc.prob(r,c) < 0.001 || m_started == false) {
						g.setColor(Color.WHITE);
					}
					else if (m_calc.prob(r, c) < 0.25) {
						g.setColor(Color.GREEN);
					}
					else if (m_calc.prob(r, c) < 0.5) {
						g.setColor(Color.ORANGE);
					}
					else {
						g.setColor(Color.RED);
					}
					Font f = new Font("Arial", Font.BOLD, 10);
					g.setFont(f);
					
					// If the game hasn't started yet, every square has a zero
					// probability of being a mine, as the first click cannot
					// make you lose the game.
					if (m_started == false) {
						g.drawString("0.000", c * CELL_SIZE + 4, r * CELL_SIZE + CELL_SIZE / 2 + 6);
					}
					else {
						g.drawString(m_calc.formattedProb(r,c), c * CELL_SIZE + 4, r * CELL_SIZE + CELL_SIZE / 2 + 6);
					}
				}
				// Drawing a flag.
				else if (m_board.at(r, c) == Square.FLAG) {
					drawFlag(r, c, g);
				}
				// Drawing a mine (once game has been lost).
				else if (m_board.at(r, c) == Square.MINE) {
					drawMine(r, c, g);
				}
				// Drawing a flag in the wrong place (once game has been lost).
				else if (m_board.at(r,c) == Square.WRONGFLAG) {
					drawWrongFlag(r, c, g);
				}
				// Drawing a known square.
				else {
					g.setColor(Color.GRAY);
					g.fillRect(c*CELL_SIZE, r*CELL_SIZE, CELL_SIZE, CELL_SIZE);
					
					Font f = new Font("Arial", Font.BOLD, 20);
					g.setFont(f);
					
					switch (m_board.at(r,c)) {
						case EMPTY: break;
						case ONE:   g.setColor(Color.BLUE);
								    drawNumber(r, c, "1", g);
								    break;
						case TWO:   g.setColor(Color.GREEN);
									drawNumber(r, c, "2", g);
								    break;
						case THREE: g.setColor(Color.RED);
									drawNumber(r, c, "3", g);
						            break;
						case FOUR:  g.setColor(Color.MAGENTA);
									drawNumber(r, c, "4", g);
								    break;
						case FIVE:  g.setColor(Color.ORANGE);
									drawNumber(r, c, "5", g);
				                    break;
						case SIX:   g.setColor(Color.CYAN);
									drawNumber(r, c, "6", g);
					    			break;
						case SEVEN: g.setColor(Color.YELLOW);
									drawNumber(r, c, "7", g);
					    			break;
						case EIGHT: g.setColor(Color.LIGHT_GRAY);
									drawNumber(r, c, "8", g);
					    			break;
						default:  	g.setColor(Color.BLACK);
									drawNumber(r, c, "?", g);
					}
				}
				
				// Draw a border around each square.
				g.setColor(Color.BLACK);
				g.drawRect(c*CELL_SIZE, r*CELL_SIZE, CELL_SIZE, CELL_SIZE);
			}
		}
	}
	
	/**
	 * Write out the given string in the center of the given row and
	 * column.
	 */
	public void drawNumber(int r, int c, String s, Graphics g) {
		g.drawString(s, c*CELL_SIZE + CELL_SIZE / 2 - 6, r*CELL_SIZE + CELL_SIZE / 2 + 6);
	}
	
	/**
	 * Draw a flag in the center of the given row and column.
	 */
	public void drawFlag(int r, int c, Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(c*CELL_SIZE, r*CELL_SIZE, CELL_SIZE, CELL_SIZE);
		g.setColor(Color.RED);
		g.fillRect(c*CELL_SIZE + 10, r*CELL_SIZE + 10, 15, 10);
		g.setColor(Color.BLACK);
		g.fillRect(c*CELL_SIZE + 25, r*CELL_SIZE + 10, 3, 20);
		g.fillRect(c*CELL_SIZE + 21, r*CELL_SIZE + 30, 11, 3);
	}
	
	/**
	 * Draw a mine in the center of a given row and column (this should only
	 * happen when the game is over).
	 */
	public void drawMine(int r, int c, Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(c*CELL_SIZE, r*CELL_SIZE, CELL_SIZE, CELL_SIZE);
		g.setColor(Color.RED);
		g.drawLine(c*CELL_SIZE + 10, r*CELL_SIZE + 10, (c+1) * CELL_SIZE - 10, (r+1) * CELL_SIZE - 10);
		g.drawLine((c+1) * CELL_SIZE - 10, r*CELL_SIZE + 10, c * CELL_SIZE + 10, (r+1) * CELL_SIZE - 10);
		g.drawLine(c*CELL_SIZE + 8, r *CELL_SIZE + CELL_SIZE / 2, (c+1) * CELL_SIZE - 8, r * CELL_SIZE + CELL_SIZE / 2);
		g.drawLine(c*CELL_SIZE + CELL_SIZE / 2, r*CELL_SIZE + 8, c*CELL_SIZE + CELL_SIZE / 2, (r+1) * CELL_SIZE - 8);
	}
	
	/**
	 * Draw an X through any flags that were improperly placed when the game
	 * is over. We can't re-use the drawFlag function because the background
	 * colors differ.
	 */
	public void drawWrongFlag(int r, int c, Graphics g) {
		// Draw flag.
		g.setColor(Color.GRAY);
		g.fillRect(c*CELL_SIZE, r*CELL_SIZE, CELL_SIZE, CELL_SIZE);
		g.setColor(Color.RED);
		g.fillRect(c*CELL_SIZE + 10, r*CELL_SIZE + 10, 15, 10);
		g.setColor(Color.BLACK);
		g.fillRect(c*CELL_SIZE + 25, r*CELL_SIZE + 10, 3, 20);
		g.fillRect(c*CELL_SIZE + 21, r*CELL_SIZE + 30, 11, 3);
		
		// Draw an X through it.
		g.setColor(Color.YELLOW);
		g.drawLine(c*CELL_SIZE, r*CELL_SIZE, (c+1)*CELL_SIZE, (r+1)*CELL_SIZE);
		g.drawLine(c*CELL_SIZE, (r+1)*CELL_SIZE, (c+1)*CELL_SIZE, r*CELL_SIZE);
	}
	
	/**
	 * Performs the actions necessary when a square is clicked on. This
	 * includes potentially winning or losing the game, revealing a square,
	 * or nothing at all.
	 * 
	 * @param r The row of the square we click
	 * @param c The column of the square we click
	 * @return Whether or not we need to repaint the grid
	 */
	public boolean clickSquare(int r, int c) {
		
		// If the square is out-of-bounds, do nothing.
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		// If we're waiting to start a new game, start the game with this click
		// as the first one.
		if (m_started == false) {
			startGame(r, c);
		}
		
		// If we click on a known square, do nothing.
		if (m_board.known(r, c) || m_board.at(r, c) == Square.FLAG) {
			return false;
		}
		// If we click on a mine, game over.
		if (m_field.get(r, c)) {
			m_lostgame = true;
			return true;
		}
			
		// Otherwise, reveal the square.
		int m = m_field.minesSurrounding(r, c);
		m_board.set(r, c, Square.toSquare(m));
		
		// If the square is empty, also click surrounding squares.
		if (m == 0) {
			clickSquare(r-1,c-1);
			clickSquare(r-1,c);
			clickSquare(r-1,c+1);
			clickSquare(r,c-1);
			clickSquare(r,c+1);
			clickSquare(r+1,c-1);
			clickSquare(r+1,c);
			clickSquare(r+1,c+1);
		}
		
		// See if we won the game.
		m_wongame = wonGame();
		
		//Repaint the board.
		return true;
	}
	
	/**
	 * Performs the actions necessary when right clicking on a square.
	 * Flagging a square, unflagging a square, or nothing.
	 * 
	 * @param r The row of the clicked square
	 * @param c The column of the clicked square
	 * @return Whether or not to repaint the board
	 */
	public boolean rightClickSquare(int r, int c) {
		// If the square is out-of-bounds, do nothing.
		if (r < 0 || c < 0 || r >= m_rows || c >= m_cols) {
			return false;
		}
		
		// If we click on an unknown square, flag it.
		if (m_board.at(r, c) == Square.UNKNOWN) {
			m_board.set(r, c, Square.FLAG);
			m_flags++;
			return true;
		}
		// If we click on a flagged square, un-flag it.
		else if (m_board.at(r, c) == Square.FLAG) {
			m_board.set(r, c, Square.UNKNOWN);
			m_flags--;
			return true;
		}
		// If we click on a known square, do nothing.
		return false;
	}
	
	/**
	 * Listener class for mouse events.
	 */
	class MinesAdapter extends MouseAdapter {
		
		/**
		 * Tracks when a mouse button is pressed (clicked and released).
		 */
		public void mousePressed(MouseEvent e) {
			
			// If the game is over, then clicking anywhere on the grid should
			// start a new game.
			if (m_lostgame || m_wongame) {
				newGame();
				repaint();
				return;
			}
			
			// Get the location of the click, and the corresponding row and
			// column.
			int x = e.getX();
			int y = e.getY();
			int c_col = x / CELL_SIZE;
			int c_row = y / CELL_SIZE;
			
			boolean rep  = false;
			
			if (x < m_cols * CELL_SIZE && y < m_rows * CELL_SIZE) {
				// When a right click occurs.
				if (e.getButton() == MouseEvent.BUTTON3) {
					rep = rightClickSquare(c_row, c_col);
				}
				// When a left click occurs.
				else {
					rep = clickSquare(c_row, c_col);
				}
				
				// Repaint the grid if anything changed.
				if (rep) {
					repaint();
				}
			}
		}
		
		/**
		 * Tracks when a mouse is clicked (not necessarily released). This is
		 * used to track double-clicks.
		 */
		public void mouseClicked(MouseEvent e) {
			// If the mouse is clicked twice.
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				// Get the click location, and the corresponding row and
				// column.
				int x = e.getX();
				int y = e.getY();
				int c_col = x / CELL_SIZE;
				int c_row = y / CELL_SIZE;
				
				// If the square is unknown or flagged, do nothing.
				if (m_board.at(c_row,c_col) == Square.UNKNOWN ||
					m_board.at(c_row,c_col) == Square.FLAG) {
					return;
				}
				
				// Count number of flags
				int flag_count = 0;
				if (m_board.at(c_row-1,c_col-1) == Square.FLAG) {flag_count++;}
				if (m_board.at(c_row-1,c_col) == Square.FLAG) {flag_count++;}
				if (m_board.at(c_row-1,c_col+1) == Square.FLAG) {flag_count++;}
				if (m_board.at(c_row,c_col-1) == Square.FLAG) {flag_count++;}
				if (m_board.at(c_row,c_col+1) == Square.FLAG) {flag_count++;}
				if (m_board.at(c_row+1,c_col-1) == Square.FLAG) {flag_count++;}
				if (m_board.at(c_row+1,c_col) == Square.FLAG) {flag_count++;}
				if (m_board.at(c_row+1,c_col+1) == Square.FLAG) {flag_count++;}
				
				// Only reveal the surrounding squares if the number of flags
				// surrounding the click location add up to the number of mines
				// surrounding the click location.
				if (flag_count != Square.toInt(m_board.at(c_row,c_col))) {
					return;
				}
				
				boolean rep = false;
				
				// Reveal all of the surrounding squares.
				if (x < m_cols * CELL_SIZE && y < m_rows * CELL_SIZE) {
					boolean rep1 = clickSquare(c_row-1,c_col-1);
					boolean rep2 = clickSquare(c_row-1,c_col);
					boolean rep3 = clickSquare(c_row-1,c_col+1);
					boolean rep4 = clickSquare(c_row,c_col-1);
					boolean rep5 = clickSquare(c_row,c_col+1);
					boolean rep6 = clickSquare(c_row+1,c_col-1);
					boolean rep7 = clickSquare(c_row+1,c_col);
					boolean rep8 = clickSquare(c_row+1,c_col+1);
					
					rep = (rep1 || rep2 || rep3 || rep4 || rep5 || rep6 || rep7 || rep8);
				}
				
				// Repaint the grid if anything changed.
				if (rep) {
					repaint();
				}
			}
		}
	}
}
