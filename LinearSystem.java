package probcalc;

import java.util.ArrayList;

/**
 * Used to represent a system of linear equations. The functionality of this
 * class is rather limited to what is required of the Minesweeper application,
 * so I would not recommend using this class for your daily linear algebra
 * tasks.
 * 
 * Also... all of the contents are Doubles because the end result of this
 * application involves probabilities between 0 and 1. If you wish to use
 * another data type, I would suggest a more generic class.
 */
public class LinearSystem {
	
	///////////////////////////////////////////////////////////////////////////
	//								MEMBERS									 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Class for containing a single equation (row) in our system (matrix).
	 * 
	 * Contains some wrapper functions for the basic ArrayList functions that
	 * are required of the LinearSystem class. But also allows for some row
	 * operations that come in handy during row-reduction.
	 */
	public class MatRow {
		// The row's contents.
		ArrayList<Double> m_contents; 
		
		/**
		 * Default Constructor: Creates a row of length 0.
		 */
		public MatRow() {
			m_contents = new ArrayList<Double>();
		}
		
		/**
		 * Constructor given size: creates a row filled with that many '0's,
		 * as this is the default behavior for ArrayList.
		 */
		public MatRow(int size) {
			m_contents = new ArrayList<Double>(size);
		}
		
		/**
		 * Constructor given contents: creates a row with the specified
		 * contents.
		 */
		public MatRow(ArrayList<Double> list) {
			m_contents = list;
		}
		
		/**
		 * Matrix row multiplication.
		 * 
		 * Returns the row resulting from multiplying every element of this row
		 * by constant k.
		 */
		public MatRow mult(double k) {
			ArrayList<Double> temp = new ArrayList<Double>();
			for (int i = 0; i < m_contents.size(); i++) {
				temp.add(m_contents.get(i) * k);
			}
			return new MatRow(temp);
		}
		
		/**
		 * Matrix row addition.
		 * 
		 * Returns the row resulting from adding every element in this row to
		 * its counterpart in the 'other' row.
		 */
		public MatRow add(MatRow other) {
			ArrayList<Double> temp = new ArrayList<Double>();
			for(int i = 0; i < m_contents.size(); i++) {
				temp.add(m_contents.get(i) + other.m_contents.get(i));
			}
			return new MatRow(temp);
		}
		
		/**
		 * Shortcut for performing row addition and multiplication at the same
		 * time.
		 * 
		 * Returns the row: <this row> + (k * <'other' row>)
		 */
		public MatRow addmult(double k, MatRow other) {
			return add(other.mult(k));
		}
		
		/**
		 * Returns the contents of the row at index 'n'
		 * 
		 * Throws an array out of bounds exception if the index is invalid.
		 */
		public double get(int n) {
			return m_contents.get(n);
		}
	}

	// The number of equations and unknowns in our system. Note that the number
	// of columns is equal to (m_unknowns + 1) due to the solution column.
	int m_unknowns;
	int m_equations;
	
	// The contents of our matrix.
	ArrayList<MatRow> m_mat;
	
	
	///////////////////////////////////////////////////////////////////////////
	//								CONSTRUCTORS							 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Standard constructor.
	 * 
	 * Creates an initially empty system with the given number of equations and
	 * unknowns.
	 * 
	 * @param equations The number of equations (must be greater than 0, set
	 * 					to 1 if not).
	 * @param unknowns The number of unknowns (must be greater than 0, set to
	 * 				   1 if not).
	 */
	public LinearSystem(int unknowns, int equations) {
		
		m_unknowns = unknowns;
		if (m_unknowns < 1) {
			m_unknowns = 1;
		}
		
		m_equations = equations;
		if (m_equations < 1) {
			m_equations = 1;
		}
		
		m_mat = new ArrayList<MatRow>();
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//								ACCESSORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the coefficient in front of the 'c'th unknown of the 'r'th
	 * equation.
	 */
	public double get(int r, int c) {
		return m_mat.get(r).get(c);
	}
	
	/**
	 * Returns the number of rows (equations) in our matrix (system).
	 */
	public int rows() {
		return m_equations;
	}
	
	/**
	 * Returns the number of columns (unknowns + 1) in our matrix (system).
	 */
	public int cols() {
		return m_unknowns+1;
	}
	
	/**
	 * Solves the system of equations for x_unknown. This function should be
	 * called with all of the required variable values to solve for x_unknown.
	 * 
	 * @param unknown, The unknown we are solving for in the system.
	 * @param variables, The variables required to solve for this unknown.
	 * @param values, The values for these variables.
	 */
	public double solveFor(int unknown, ArrayList<Integer> variables, ArrayList<Integer> values) {
		
		// Generate a list of variable values. This list contains a -1 if the
		// unknown was not supplied in 'variables.' Otherwise, it contains the
		// corresponding 0 or 1 from 'values.'
		ArrayList<Integer> allvars = new ArrayList<Integer>();
		for(int i = 0; i < m_unknowns; i++) {
			if (variables.contains(i)) {
				int index = variables.indexOf(i);
				allvars.add(values.get(index));
			}
			else {
				allvars.add(-1);
			}
		}
		
		// What will eventually contain our answer.
		double answer = 0;
		
		// Either the variable is a pivot in its row (and all of the variables
		// required to solve for it have been supplied), or it is supplied
		// in the variables array.
		boolean is_pivot = (allvars.get(unknown) == -1);
		
		// If this variable is a pivot, then all of the necessary variables
		// for determining its solution have been supplied, and we simply
		// solve the equation for it.
		if (is_pivot) {
			
			// Find the row to use
			int unknown_row = 0;
			for (int r = 0; r < m_equations; r++) {
				if (get(r, unknown) != 0) {
					unknown_row = r;
					break;
				}
			}
			
			// Start with the last value (the constant in the equation), and
			// subtract the necessary variables multiplied by their proper
			// coefficients from the 'allvars' array.
			answer = m_mat.get(unknown_row).get(m_unknowns);
			boolean first_found = false;
			for (int c = 0; c < m_unknowns; c++) {
				if (get(unknown_row,c) != 0) {
					if (first_found == false) {
						first_found = true;
					}
					else {
						// This function assumes that the proper setup has been
						// done to ensure that allvars.get(c) is -1 only if
						// get(unknown_row, c) is 0, and so these values do not
						// contribute to the answer.
						answer -= get(unknown_row,c) * allvars.get(c);
					}
				}
			}
		}
		// If the unknown is not a pivot variable, then it has been supplied in
		// the unknown values array, and we can simply look it up in 'allvars'.
		else {
			answer = allvars.get(unknown);
		}
		
		return answer;
	}
	
	/**
	 * Returns true if plugging in the variables with their respective values
	 * results in all variables in the system being either 0 or 1. X_0 (the
	 * first row/column) is ignored because it can take on other values.
	 * 
	 * @param variables, The variables which we are plugging in values for.
	 * @param values, Their respective values.
	 */
	public boolean canSolve(ArrayList<Integer> variables, ArrayList<Integer> values) {
		
		for (int i = 1; i < m_unknowns; i++) {
			double result = solveFor(i, variables, values);
			if (result != 0 && result != 1) {
				return false;
			}
		}
		return true;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//								MUTATORS								 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Add an equation to the system. The equation must contain the same number
	 * of unknowns as the system. This function returns false if it does not,
	 * and true otherwise.
	 */
	public boolean add_equation(ArrayList<Double> vals) {
		
		if (vals.size() != m_unknowns + 1) {
			return false;
		}
		
		m_mat.add(new MatRow(vals));
		return true;
	}
	
	/**
	 * Converts the matrix of the system to reduced row-echelon form (RREF).
	 * 
	 * Namely, a matrix is in RREF if:
	 *     1) All nonzero rows (rows with at least one nonzero element) are
	 *        above any rows containing only zeros.
	 *     2) The "leading coefficient" or "pivot" (first nonzero number from
	 *        the left) of a nonzero row is always strictly to the right of the
	 *        leading coefficient of the row above it.
	 *     3) Every leading coefficient has value 1, and is the only nonzero
	 *        entry in its column.
	 *        
	 * See:
	 * http://en.wikipedia.org/wiki/Row_echelon_form#Reduced_row_echelon_form
	 */
	public void rref() {

		int pivot_row = 0;
		
		for (int c = 0; c <= m_unknowns && pivot_row < m_equations; c++) {
			
			// Skip columns of all zeros.
			if (columnAllZeros(c, pivot_row)) {
				continue;
			}
			
			// Find pivot, and place in next row.
			for (int r = pivot_row; r < m_equations; r++) {
				if (m_mat.get(r).get(c) != 0) {
					swap_rows(r, pivot_row);
					break;
				}
			}
			
			// Set pivot equal to 1.
			double pivot_value = m_mat.get(pivot_row).get(c);
			m_mat.set(pivot_row, m_mat.get(pivot_row).mult(1.0 / pivot_value));
			
			// Make all other values in the column 0.
			for (int r = 0; r < m_equations; r++) {
				// Skip pivot row.
				if (r == pivot_row) {
					continue;
				}
				
				// Get value currently in that column.
				double current_value = m_mat.get(r).get(c);
				// Set to 0.
				m_mat.set(r, m_mat.get(r).addmult(current_value * -1.0, m_mat.get(pivot_row)));
			}
			
			pivot_row++;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//							PRIVATE FUNCTIONS							 //
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Swap two equations (rows) in the system (matrix).
	 * 
	 * Under normal conditions, rows 'a' and 'b' are swapped. If either row is
	 * out of the bounds of the system, or if 'a' and 'b' are the same row,
	 * this function does nothing.
	 */
	private void swap_rows(int a, int b) {
		if (a == b || a < 0 || b < 0 || a >= m_equations || b >= m_equations) {
			return;
		}
		
		MatRow a_row = m_mat.get(a);
		MatRow b_row = m_mat.get(b);
		
		m_mat.set(a, b_row);
		m_mat.set(b, a_row);
		
	}
	
	/**
	 * Returns true if column 'c' contains only zeros below row 'r' (including)
	 * this row. Otherwise, returns false.
	 */
	private boolean columnAllZeros(int c, int r) {
		if (c < 0 || r < 0 || c > m_unknowns || r >= m_equations) {
			return false;
		}
		
		for (int i = r; i < m_equations; i++) {
			if (m_mat.get(i).get(c) != 0) {
				return false;
			}
		}
		
		return true;
	}
}
