package solvers;

import dotsAndBoxes.Move;

/**
 * Thrown, when Wilson's solver makes wrong addional moves. "This most likley,
 * is a bug in Wilson's Solver! In some very few cases it can happpen, that
 * Wilsons solver adds some moves that are not capturing. This most likley is a
 * bug in his implementation, since the solver makes an automatic non-capturing
 * move, but does not change the player to move afterwards, and shows that the
 * box is occupied by a player, although only 3 lines are closed. In this case
 * it is the best, to just perform the capturing moves for the given state and
 * leave a hard-hearted handout.
 * 
 * @author Markus Thill
 * 
 */
public class WilsonSolverBugException extends RuntimeException {
	private static final long serialVersionUID = -5801756415589109029L;
	
	/**
	 * Lat move, that caused this exception
	 */
	public Move lastMove;

	public WilsonSolverBugException(String message, Move lastMove) {
		super(message);
		this.lastMove = lastMove;
	}
}
