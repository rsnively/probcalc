package probcalc;

import java.util.ArrayList;

/**
 * Given a minesweeper board, this class calculates the probabilities of each
 * unknown square on the board being a mine.
 * 
 * This class does not behave well if the player does not follow its
 * instructions. For example, flagging a square which may or may not be a mine
 * could (will...) lead to incorrect probabilities being calculated. Un-
 * flagging that same square will return everything to normal, however.
 */
public class Calculator {

	// The board for which we will be calculating probabilities.
	private Board m_field;
	
	// The probabilities we report for the given minefield.
	private double[][] m_prob;
	
	///////////////////////////////////////////////////////////////////////////
	//								CONSTRUCTORS							 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Standard Constructor: Takes the given board, and fills in the grid of
	 * probabilities accordingly.
	 */
	public Calculator(Board m) {
		
		// Set up some local variables.
		m_field = m;
		int rows = m.rows();
		int cols = m.cols();

		// The number of variables and equations we will be using to solve for
		// the probabilities. We start from one, because there is always the
		// x_0 variable which we denote as the probability of any square not
		// adjacent to a known square being a mine.
		int numvars = 1;
		int numequations = 1;
		
		// Create the matrix that will eventually hold the probabilities of any
		// square being a mine.
		m_prob = new double[rows][cols];
		
		// Holds the variables representing the probability of each square.
		//     -1 : The square is known or flagged already.
		//	    0 : The square is not adjacent to a known square, and so has
		//          the same probability as any square with a 0 (So I guess
		//          technically this is the same as n...).
		//      n : This square has probability x_n
		int[][] vars = new int[rows][cols];
		
		// Determine which squares need calculating, and how many equations we
		// will have to deal with.
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				// If the square is unknown and next to a known square, then it
				// is a variable we will have to calculate.
				if (m_field.at(r, c) == Square.UNKNOWN &&
					m_field.known_adjacent(r, c)) {
					vars[r][c] = numvars;
					numvars++;
				}
				
				// If a square is unknown, but not next to a known square, then
				// it has the same probability of being a mine as all other
				// such squares (which we will denote with x_0).
				else if (m_field.at(r,c) == Square.UNKNOWN) {
					vars[r][c] = 0;
				}
				
				// If a square is known...
				else {
					// This is a known square, so we don't calculate it.
					vars[r][c] = -1;
					
					// If this square is adjacent to an unknown square, we can
					// generate an equation from it.
					if (m_field.at(r,c) != Square.FLAG &&
						m_field.unknown_adjacent(r, c)) {
						numequations++;
					}
				}
			}
		}
		
		// Create the system of equations we will be using to calculate our
		// probabilities.
		LinearSystem s = new LinearSystem(numvars, numequations);
		
		// Create the equation for x_0.
		ArrayList<Double> zeroequation = new ArrayList<Double>();
		// The coefficient for x_0 is the number of unknown squares which are
		// not already variables (we add 1 because the x_0 variable itself does
		// not denote a square).
		zeroequation.add((double) (m_field.unknown() - numvars + 1));
		for (int v = 1; v < numvars; v++) {
			// The coefficient for every variable which is not x_0 is a 1.
			zeroequation.add(1.0);
		}
		// These values should all add up to the number of mines which have not
		// already been flagged.
		zeroequation.add((double) m_field.mines_left());
		s.add_equation(zeroequation);
		
		// Generate all of the other equations we can.
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				// If the square is known and has some unknowns next to it,
				// then we can make an equation out of it.
				if (m_field.known(r,c) &&
				    m_field.unknown_adjacent(r,c)) {
					
					// Create blank equation.
					ArrayList<Double> equation = new ArrayList<Double>();
					for(int i = 0; i <= numvars; i++) {
						equation.add((double) 0);
					}
					
					// The number of flags surrounding the square.
					int num_flags = 0;
					
					// Determine which unknowns participate in the equation.
					
					// Check top-left.
					if (m_field.unknown(r-1, c-1)) {
						int v = vars[r-1][c-1];
						if (v > 0) {
							equation.set(v,  1.0);
						}
					}
					else if (m_field.at(r-1,c-1) == Square.FLAG) {
						num_flags++;
					}
					// Check top.
					if (m_field.unknown(r-1, c)) {
						int v = vars[r-1][c];
						if (v > 0) {
							equation.set(v, 1.0);
						}
					}
					else if (m_field.at(r-1,c) == Square.FLAG) {
						num_flags++;
					}
					// Check top-right.
					if (m_field.unknown(r-1, c+1)) {
						int v = vars[r-1][c+1];
						if (v > 0) {
							equation.set(v, 1.0);
						}
					}
					else if (m_field.at(r-1,c+1) == Square.FLAG) {
						num_flags++;
					}
					// Check left.
					if (m_field.unknown(r, c-1)) {
						int v = vars[r][c-1];
						if (v > 0) {
							equation.set(v, 1.0);
						}
					}
					else if (m_field.at(r,c-1) == Square.FLAG) {
						num_flags++;
					}
					// Check right.
					if (m_field.unknown(r, c+1)) {
						int v = vars[r][c+1];
						if (v > 0) {
							equation.set(v, 1.0);
						}
					}
					else if (m_field.at(r,c+1) == Square.FLAG) {
						num_flags++;
					}
					// Check bottom-left.
					if (m_field.unknown(r+1, c-1)) {
						int v = vars[r+1][c-1];
						if (v > 0) {
							equation.set(v, 1.0);
						}
					}
					else if (m_field.at(r+1,c-1) == Square.FLAG) {
						num_flags++;
					}
					// Check bottom.
					if (m_field.unknown(r+1, c)) {
						int v = vars[r+1][c];
						if (v > 0) {
							equation.set(v, 1.0);
						}
					}
					else if (m_field.at(r+1,c) == Square.FLAG) {
						num_flags++;
					}
					// Check bottom-right.
					if (m_field.unknown(r+1, c+1)) {
						int v = vars[r+1][c+1];
						if (v > 0) {
							equation.set(v, 1.0);
						}
					}
					else if (m_field.at(r+1,c+1) == Square.FLAG) {
						num_flags++;
					}
					// Add the solution (the contents of this square minus
					// the number of flags already surrounding it) to the end
					// of the equation, and add this equation to the system.
					equation.set(numvars, (double) Square.toInt(m_field.at(r,c)) - num_flags);
					s.add_equation(equation);
				}
			}
		}

		// Put the system into reduced row-echelon form.
		s.rref();
		
		// Determine which variables belong to the minimal set of dependencies
		// for solving our set of equations (we call these 'u-variables').
		// These variables will be the ones which are not pivots in a row.
		ArrayList<Integer> uvars = new ArrayList<Integer>();
		int pivot_row = 0;
		for (int c = 0; c < numvars; c++) {
			for (int r = 0; r < numequations; r++) {
				if (s.get(r,c) != 0 && r != pivot_row) {
					pivot_row--;
					if (!uvars.contains(c)) {
						uvars.add(c);
					}
					break;
				}
			}
			pivot_row++;
		}
		
		// Generate the probability array for our variables.
		ArrayList<Double> probabilities = new ArrayList<Double>();
		for (int v = 0; v < numvars; v++) {
			probabilities.add((double) 0);
		}
		
		// This is the number of possible mine combinations for our variables,
		// without taking into account any limiting factors of the game of
		// minesweeper. Not all variables are considered, only those in the
		// so called u-vector.
		int possibilities = (int) Math.pow(2, uvars.size());
		int total_valid = 0;
		
		// For each possible mine orientation for our u-vectors.
		for (int i = 0; i < possibilities; i++) {
			ArrayList<Integer> uvals = getPermutation(i, possibilities);
			// If the vales for our u-variables generate a plausible minefield.
			if (s.canSolve(uvars, uvals)) {
				// Add the results for each variable to our probability array.
				// Other than x_0, this should be a 0 or a 1.
				for (int v = 0; v < numvars; v++) {
					probabilities.set(v, probabilities.get(v) + s.solveFor(v, uvars, uvals));
				}

				// Increment the number of valid solutions we have found.
				total_valid++;
			}
		}
		
		// Divide each probability by the number of valid solutions found to
		// get the actual probability of each square being a mine.
		for (int v = 0; v < numvars; v++) {
			probabilities.set(v, probabilities.get(v) / total_valid);
		}
		
		// Populate the probabilities matrix.
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				int v = vars[r][c];
				
				// This square is already known.
				if (v == -1) {
					// If it is a flag, it has a 100% chance of being a mine,
					// unless the player has been flagging willy-nilly.
					if (m_field.at(r,c) == Square.FLAG) {
						m_prob[r][c] = 1;
					}
					// Otherwise, the square is uncovered, and so it has a 0%
					// chance of being a mine.
					else {
						m_prob[r][c] = 0;
					}
				}
				// The square has a variable associated with it.
				else {
					m_prob[r][c] = probabilities.get(v);
				}
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//								ACCESSORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the probability of a given square being a mine, or -1 if an
	 * out-of-bounds square is given.
	 */
	public double prob(int r, int c) {
		if (r < 0 || c < 0 || r >= m_field.rows() || c >= m_field.cols()) {
			return -1;
		}
		return m_prob[r][c];
	}
	
	/**
	 * Returns a string representing the probability of a square being a mine.
	 * The probability is a string consisting of a single digit, a decimal
	 * place, and three digits following the decimal place.
	 */
	public String formattedProb(int r, int c) {
		
		// Return the empty string if the square is out-of-bounds.
		if (r < 0 || c < 0 || r >= m_field.rows() || c >= m_field.cols()) {
			return "";
		}
		
		// Get the probability.
		String s = Double.toString(m_prob[r][c]);
		int leftover = 6 - s.length();
		// Pad with zeros if too short.
		while (leftover > 0) {
			s += "0";
			leftover--;
		}
		// Remove excess if too long.
		s = s.substring(0,5);
		return s;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//							PRIVATE FUNCTIONS							 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the 'current'th permutation of 'total' such permutations in
	 * binary.
	 * 
	 * For example: getPermutation(3,4) returns (1,1) - the third permutation
	 * of the set {0,1} when there are four total permutations.
	 * 
	 * @param current, The n in 'n'th permutation.
	 * @param total, The total number of permutations (it better be a power of
	 * 				 two...)
	 */
	private ArrayList<Integer> getPermutation(int current, int total) {
		// If we have no variables, return empty list
		if (total == 0 || total == 1) {
			return new ArrayList<Integer>(0);
		}
		
		ArrayList<Integer> p = getPermutation(current/2, total/2);
		p.add(current % 2);
		return p;
	}
	
}
