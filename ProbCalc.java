package probcalc;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Main class for the minesweeper probability calculator.
 */
public class ProbCalc extends JFrame {
	
	// The number of rows in the game.
	private final int ROWS = 16;
	// The number of columns in the game.
	private final int COLUMNS = 30;
	// The number of mines in the game.
	private final int MINES = 99;
	
	private JLabel statusbar;
	
	public ProbCalc() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(COLUMNS * Game.CELL_SIZE + Game.CELL_SIZE / 2, (ROWS+1) * Game.CELL_SIZE + Game.CELL_SIZE / 2);
		setLocationRelativeTo(null);
		setTitle("Minesweeper");
		
		statusbar = new JLabel("");
		add(statusbar, BorderLayout.SOUTH);
		add(new Game(ROWS, COLUMNS, MINES, statusbar));
		
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		new ProbCalc();
	}
}
