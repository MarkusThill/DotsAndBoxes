package solvers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.TimeLimitExceededException;

import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateFactory;
import dotsAndBoxes.Move;

/**
 * This class provides an interface to play.c of Wilsons Solver. It is possible,
 * using this interface, to get the action-values for a given {@link GameState}
 * s. <br>
 * Note, that the corresponding problem (e.g., the empty 4x4 board) has to be
 * analyzed using analyze.c before it can be used. The Interface should be used
 * in the following or a similar way: <br>
 * <code> &nbsp; WilsonInterface wi = new WilsonInterface();<br>
 * &nbsp; wi.loadProblem("4x4", 4, 4);<br>
 * &nbsp; SolverResponse sr = wi.getRelativeValueList(s);<br>
 * &nbsp; SolverResponse sr2 = wi.getRelativeValueList(s2);<br>
 * &nbsp; &nbsp; .<br>
 *  &nbsp; &nbsp; .<br>
 *  &nbsp; &nbsp; .<br>
 * &nbsp; wi.close();<br>
 * </code> <br>
 * The {@link SolverResponse} <code>sr</code> contains the actions-values for
 * all actions possible from {@link GameState} <code>s</code>. Wilson's Solver
 * often automatically adds extra lines to the board, when chains are found that
 * can be closed. The reason for this is, that moves into a chain are typically
 * optimal (sometimes leaving a hard-hearted handout). The
 * {@link SolverResponse#values} should be then handled carefully, since they do
 * not represent the values for the requested state. It is recommended to
 * perform the moves given in {@link SolverResponse#extraLines} on the
 * {@link GameState} s first, since these moves were considered to be optimal by
 * Wilson's Solver.
 * 
 * <br>
 * There were some changes required in play.c of Wilson's Solver. After output
 * operations to stdout a fflush(stdout) is necessary, otherwise this class will
 * not get any response. <br>
 * Some additional notes: Wilsons solver automatically closes chains (leaving
 * the possibility for a hard-hearted handout). The reason for this is, that
 * these moves are the only optimal ones. A good agent will therefore always
 * select these moves. In reality, an imperfect player could select another
 * move. This requires some additional work.
 * 
 * @author Markus Thill
 * 
 */
public class WilsonInterface {
	/**
	 * (Relative) Path to the executable of Wilson's solver. Note that the
	 * default directory is the directory of the project.
	 */
	private static final String PATH_TO_EXE = "./wilsonSolver/play.exe";

	/**
	 * (Relative) Path to the analyzed problems of Wilson's solver. Make sure
	 * that for each problem a .txt file containing the problem-description and
	 * a folder with the same name (containing the opening data-base) is located
	 * in that path.
	 */
	private static final String PATH_TO_PROBLEMS = "./wilsonSolver";

	/**
	 * Often, the response from Wilson's solver takes some time. In some cases
	 * this may lead to an time-out (defined by
	 * {@link WilsonInterface#RESPONSE_SLEEP}). This variable defines how often
	 * it shall be retried to receive a response from the solver until a final
	 * time-out is thrown.
	 */
	private static final int RESPONSE_RETRY = 50000;

	/**
	 * Sleep for some time, if Wilsons solver did not respond yet after a line
	 * is placed on the board. This value defines, for how long this process
	 * will sleep. After the nap, again it is checked if wilson's solver
	 * responded or not.
	 */
	private static final long RESPONSE_SLEEP = 2;

	/**
	 * Minimum expected length of a response. If the response is too short, then
	 * we probably made an illegal move (placed a line on an already occupied
	 * line.)
	 */
	private static final int MIN_RESPONSE_LENGTH = 70;

	/**
	 * Constant, that defines an empty line in the pre-processing phase of the
	 * solver's response.
	 */
	private static final String EMPTY_LINE = "F";

	/**
	 * Process, that will be started using Wilson's Solver program.
	 */
	private Process process;

	/**
	 * Standard-input stream (stdin), which will be connected to the
	 * output-stream (stdout) of Wilson's solver. Responses of Wilson's solver
	 * will be written into this stream. Note, that also stderr is redirected to
	 * this input-stream. A sample response could look like this:
	 */
	// @formatter:off
	/*
	 * 
	 *   a  b  c  d  e  f  g  h  i
      a  +-----+     +     +     +
                           |      
      b                    |        
                           |      
      c  +     +     +     +-----+
               |           |     |
      d        |           |  B  |  
               |           |     |
      e  +-----+-10  +     +-----+
               |     |     |      
      f        |     |     |        
               |     |     |      
      g  +     +  8  +     +     #
         |     |     |           |
      h  |     |     |           |  
         |     |     |           |
      i  +     +-----+-----+     #

Enter move for player A (two letters); .=stop; <=back; !=reset: 
	 */
	//@formatter:on
	private InputStream stdin;

	/**
	 * Standard-output stream (stdout), which will be connected to the
	 * standard-input stream of Wilson's solver. Commands such as "." (reset the
	 * board), "<" (undo) and coordinates (e.g. "ab" to set the corresponding
	 * line) will be written into this stream by us.
	 */
	private OutputStream stdout;

	/**
	 * Remember, if we are already to connected to Wilson's Solver. This means,
	 * this variable is true if we could succesfully start the process for
	 * wilsons solver and could connect to stdin and stdout.
	 */
	private boolean connected = false;

	/**
	 * Remember, if we already loaded a problem (e.g. 4x4.txt). This information
	 * is needed, e.g. if we want to load another problem. In this case we would
	 * have to reconnect to wilsons solver and load the other problem.
	 */
	private boolean loaded = false;

	/**
	 * Remember the name of the problem that was loaded into the solver.
	 */
	private String problemName = null;

	/**
	 * Number of horizontal boxes of the board of the loaded problem
	 */
	private int m;

	/**
	 * Number of vertical boxes of the board of the loaded problem
	 */
	private int n;

	/**
	 * Initial state of the loaded problem is stored here. The initial state is
	 * typically an empty board, but can also have lines set (e.g., the
	 * icelandic game)
	 */
	private GameState initialState = null;

	/**
	 * Because of a bug in Wilson's solver it is necessary to track the moves
	 * made on the board of the solver, in order to validate the responses.
	 */
	GameState gameState;

	/**
	 * Last response (if any) of the solver is stored in this variable.
	 */
	SolverResponse lastResponse;

	/**
	 * Attempt to connect to Wilsons Solver. THis method will try to start the
	 * process and retrieve the standard input (stdin) and output (stdout) from
	 * the process. If one of both streams cannot be openened, then an exception
	 * is thrown. NOTE: After the solver is not needed anymore
	 * {@link WilsonInterface#close()} must be called, to assure that the
	 * process of the solver is killed. If this is not done, there could remain
	 * some zombie-processes in the system.
	 * 
	 * @return True, if the process could be successfully started, otherwise
	 *         false.
	 */
	private boolean connect() {
		//
		// Start Wilson's solver
		//
		loaded = false;
		problemName = null;
		try {
			ProcessBuilder pb = new ProcessBuilder().command(PATH_TO_EXE)
					.redirectErrorStream(true);
			process = pb.start();

			//
			// Does not work always, but kill child-process (Wilson's solver),
			// if this application crashes or stops for some reason.
			//
			Thread closeChildThread = new Thread() {
				public void run() {
					if (process != null)
						process.destroy();
				}
			};
			Runtime.getRuntime().addShutdownHook(closeChildThread);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error while starting Wilson's solver!");
			stdin = null;
			stdout = null;
			connected = false;
			return connected;
		}

		// Get stdin, stdout. stderr is redirected to stdin.
		stdin = process.getInputStream();
		stdout = process.getOutputStream();

		//
		// Throw exception, if stdin, or stdout is null
		//
		if (stdin == null || stdout == null)
			throw new UnsupportedOperationException(
					"Could not connect to stdin or stdout of Wilson's Solver");
		connected = true;
		return connected;
	}

	/**
	 * Close the connection to Wilson's solver and kill the corresponding
	 * process. NOTE: Make sure, that this method is always called, if the
	 * solver is not needed anymore, otherwise it can happen that the
	 * solver-process still remains active in the system. This can lead to
	 * zombie-processes that require unnecessary resources.
	 * 
	 * @return true, if the connection could be successfully closed, otherwise
	 *         false.
	 */
	public boolean close() {
		try {
			stdin.close();
			stdout.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out
					.println("Something went wrong while closing Wilson's solver!");
			process.destroy();
			return false;
		}

		//
		// Destroy process, if still open!
		//
		process.destroy();
		process = null;
		stdin = null;
		stdout = null;
		return true;

	}

	/**
	 * Reconnect to Wilsons solver. This will close the connection first and
	 * then connect again to the solver.
	 * 
	 * @return true, if the reconnect could be performed successfully, otherwise
	 *         false.
	 */
	private boolean reconnect() {
		//
		// First disconnect all streams and stop process
		//
		boolean dis = close();
		boolean re = false;

		//
		// Try to connect again to Wilson's Solver
		//
		if (dis)
			re = connect();
		//
		// Could be reconnected successfully?
		//
		return dis && re;
	}

	/**
	 * Read all available information from stdin and return this data in
	 * String-format. In our case, Wilsons solver writes a C-String into our
	 * STDIN-input buffer. This C-String will be converted in a Java String.
	 * This method waits until the whole response of wilsons solver is written
	 * into the buffer. This is the case, when the last line of the response
	 * asks either players for an input.
	 * 
	 * @return Data from stdin in String-format. Each byte from the input-stream
	 *         is converted into a String character.
	 */
	private String readstdin() {
		String response = null;
		int numBytes = 0;
		try {
			//
			// Make sure that the whole response is read in. Wait until this
			// following line is read in...
			//
			String playerToMove = "Enter move for player ";
			int find = 0, retryCounter = 0;
			while (stdin.available() > 0 && numBytes != -1 || find == -1) {
				byte[] array = new byte[500];

				numBytes = stdin.read(array, 0, array.length);

				String s = new String(array);
				s = s.substring(0, numBytes);
				if (response == null)
					response = new String();
				response += s;
				find = response.indexOf(playerToMove);
				if (find == -1) {
					// We did not read in the whole response. Therefore wait a
					// little and repeat. If we do not get this desired
					// response, throw an Exception.
					retryCounter++;
					if (retryCounter >= RESPONSE_RETRY)
						throw new UnsupportedOperationException(
								"Timeout while waiting for the Solver's response");
					try {
						Thread.sleep(RESPONSE_SLEEP);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out
					.println("Error while retreiving response from Wilson's sovler!");
			return null;
		}
		return response;
	}

	/**
	 * Write a line to the standard-input of the solver. The passed string is
	 * terminated by a line-separator.
	 * 
	 * @param line
	 *            The line to be written.
	 */
	private void writeLineStdout(String line) {
		line += System.lineSeparator();
		byte[] b = line.getBytes();
		try {
			stdout.write(b);
			stdout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * NOTE: After the solver is not needed anymore
	 * {@link WilsonInterface#close()} must be called, to assure that the
	 * process of the solver is killed. If this is not done, there could remain
	 * some zombie-processes in the system.
	 * 
	 * @param problemName
	 *            Name of the problem (e.g., 3x3). The name has to be passed
	 *            without the ending ".txt".
	 * @param m
	 *            Number of box in X-direction
	 * @param n
	 *            Number of boxes in Y-direction
	 * @return Response of the solver for the initial board.
	 * @throws FileNotFoundException
	 *             Thrown, if the problem could not be loaded
	 */
	public SolverResponse loadProblem(String problemName, int m, int n)
			throws FileNotFoundException {
		//
		// Check, if problem exists at all.
		// 1. Check, if corresponding .txt exists
		// 2. Check, if folder exists
		//
		String folderPath = PATH_TO_PROBLEMS + "/" + problemName;
		File folder = new File(folderPath);
		if (!folder.exists())
			throw new FileNotFoundException("Did not find the folder: "
					+ folderPath + "!!!");
		String txtPath = PATH_TO_PROBLEMS + "/" + problemName + ".txt";
		File txt = new File(txtPath);
		if (!txt.exists())
			throw new FileNotFoundException("Did not find the folder: "
					+ txtPath);

		//
		// If we have already loaded some other problem, then we have to restart
		// Wilson's solver
		//
		if (loaded) {
			boolean re = reconnect();
			if (!re)
				throw new UnsupportedOperationException(
						"Could not reconnect to the solver");
		}

		//
		// If we are not connected yet, then we have to start Wilson's solver
		//
		if (!connected) {
			boolean con = connect();
			if (!con)
				throw new UnsupportedOperationException(
						"Could not connect to the solver");
		}

		//
		// Now try to load the problem
		//
		this.m = m;
		this.n = n;
		this.problemName = problemName;
		SolverResponse req = null;
		try {
			// has to be folderPath and not problemName, because the default
			// path of play.exe is still in the root-directory of this project.
			// TODO: This could be different on a UNIX-system
			req = requestProblem(folderPath);
		} catch (TimeLimitExceededException e) {
			//
			// if we get a timeout while communicating with the program, then
			// the best thing is to retry.
			//
			reconnect();
			req = loadProblem(problemName, m, n);

		}

		//
		// This is the initial state of the position. Save this initial position
		//
		saveInitialPosition(req);

		//
		// Copy the initial position to the current game-state
		//
		gameState = new GameState(initialState);

		//
		// save last response of the solver.
		//
		lastResponse = req;
		return req;
	}

	/**
	 * From the initial response of the solver create the initial board and save
	 * it. This initial state is needed for several puposes.
	 * 
	 * @param wv
	 *            Response of the solver for the initial state.
	 */
	private void saveInitialPosition(SolverResponse wv) {
		//
		// class variable
		//
		initialState = new GameState(m, n);

		int[][][] lines = wv.boardLines;
		// For the given board-lines, create a GameState
		for (int i = 0; i < lines.length; i++)
			for (int j = 0; j < lines[i].length; j++)
				for (int k = 0; k < lines[i][j].length; k++)
					if (lines[i][j][k] != 0)
						initialState.advance(new int[] { i, j, k });

		//
		// set box-difference to zero, since we do not know, who captured the
		// boxes, if there are any.
		//
		initialState.resetBoxDiff();

		//
		// Set player to move
		//
		initialState.setPlayerToMove(wv.p);
	}

	/**
	 * Attempts to load a problem with Wilson's Solver. Make sure that the
	 * solver is initialized and no problem is loaded yet. Otherwise, this
	 * method will not achieve the desired effect. If another problem has to be
	 * loaded, then the solver requires a restart: This means that the
	 * connection has to be closed and reconnected again (
	 * {@link WilsonInterface#reconnect()}).
	 * 
	 * @param problemName
	 *            Name of the problem, e.g. "4x4"
	 * @return The values of the initial position of the given problem.
	 * @throws TimeLimitExceededException
	 *             Thrown, if problem could not be loaded. Typically, a wrong
	 *             problem name was passed to this method in this case.
	 */
	private SolverResponse requestProblem(String problemName)
			throws TimeLimitExceededException {
		//
		// Everything to lower-case
		//
		String s = problemName.toLowerCase();

		//
		// Send to Wilson's Solver
		//
		writeLineStdout(s);

		//
		// Get and analyze response
		//
		String resp = getResponse();

		//
		// get board from response
		//
		int index = resp.indexOf("   a");
		String board = resp.substring(index);
		SolverResponse values = parseResponse(board, m, n);

		// this.problemName = s;
		loaded = true;

		return values;
	}

/**
	 * After a command was given to the solver (e.g. a coordinate such as "ed"
	 * or undo "<"), it takes a certain amount of time until the response is
	 * available in the input-stream (stdin) of this process.
	 * 
	 * @return The unchanged response of the solver.
	 * 
	 * @throws TimeLimitExceededException Is thrown, if the response cannot be
	 * retrieved in a given time.
	 */
	private String getResponse() throws TimeLimitExceededException {
		int i = 0;
		for (i = 0; i < RESPONSE_RETRY; i++) {
			String response = readstdin();
			if (response != null)
				return response;
			try {
				Thread.sleep(RESPONSE_SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		throw new TimeLimitExceededException(
				"Timeout while interacting with Wilson's Solver");

	}

	/**
	 * See {@link WilsonInterface#advance(int, int, int)} for description.
	 * 
	 * @param coord
	 *            An array containing 3 elements: direction of the line,
	 *            x-coordinate, y-coordinate (in this order).
	 * @return Parsed and analyzed response of the solver, containing the values
	 *         for every possible move from the state we advance to.
	 * @throws TimeLimitExceededException
	 *             Is thrown, if the response cannot be retrieved in a given
	 *             time.
	 */
	protected SolverResponse advance(int[] coord)
			throws TimeLimitExceededException {
		return advance(coord[0], coord[1], coord[2]);
	}

	/**
	 * Advise the solver to make a move on the board. The solver will respond
	 * with a new set of values for every possible action. This response is
	 * parsed and analyzed and the result is returned by this method.
	 * 
	 * @param direction
	 *            Direction of the line to be placed on the board (0=horizontal,
	 *            1=vertical)
	 * @param x
	 *            X-Coordinate of the line that is placed on the board
	 * @param y
	 *            Y-Coordinate of the line that is placed on the board
	 * @return Parsed and analyzed response of the solver, containing the values
	 *         for every possible move from the state we advance to.
	 * @throws TimeLimitExceededException
	 *             Is thrown, if the response cannot be retrieved in a given
	 *             time.
	 */
	protected SolverResponse advance(int direction, int x, int y)
			throws TimeLimitExceededException {
		int bitIndex = GameStateFactory.getLineBitIndex(direction, x, y, m, n);
		return advance(new Move(direction, Byte.valueOf((byte) bitIndex)));
	}

	/**
	 * Performs a move on the board of the solver and returns the response.
	 * Note, that it can occur that the solver automatically adds additional
	 * moves to the board, if a chain is opened. The solver considers capturing
	 * moves to be optimal (leaving a hard hearted handout if necessary) and
	 * therefore automatically captures boxes for the player to m ove.
	 * 
	 * @param mv
	 *            Move to be performed on the board of the solver
	 * @return Response of the solver after the move was performed.
	 * @throws TimeLimitExceededException
	 *             Thrown, if a timeout occurs during the communication process
	 *             with the solver.
	 */
	protected SolverResponse advance(Move mv) throws TimeLimitExceededException {
		//
		// Get coordinates in alphabetic form for Wilson's solver (e.g., "ad")
		//
		int[] c = GameStateFactory.getLineCoord(mv, m, n);
		String coord = getAlphabeticCoordinates(c);
		writeLineStdout(coord);
		String s = getResponse();
		int length = s.length();
		int oldBoxDiff;

		//
		// If the response is too short, then we probably made an illegal move.
		// Return last board in this case.
		//
		SolverResponse temp = null;
		if (length >= MIN_RESPONSE_LENGTH) {
			try {
				temp = parseResponse(s, m, n);
			} catch (WilsonSolverBugException e) {
				throw new WilsonSolverBugException(e.getMessage(), mv);
			}
		}

		oldBoxDiff = gameState.getBoxDifference();
		gameState.advance(mv);

		//
		if (temp != null) {

			//
			// Unfortunately, we also have to track the changes on the real
			// board, because of the bug in Wilson's Solver. Otherwise it is
			// not
			// possible to find the wrong moves made by the solver, which
			// by mistake capture a box. These wrong moves can be
			// identified, if the Solver captures boxes (the solver makes
			// additional moves), but there are no boxes capturable from our
			// real state.
			// This can be checked, by calling findExtraLine, which checks that
			// all moves
			// were capturing, otherwise throws an exception.
			if (lastResponse != null && temp.boxDiff != lastResponse.boxDiff
					&& oldBoxDiff == gameState.getBoxDifference()) {
				try {
					findExtraLines(temp, gameState);
				} catch (WilsonSolverBugException e) {
					throw new WilsonSolverBugException(e.getMessage(), mv);
				}
			}
			lastResponse = temp;
		}

		//
		// Check, if solver closed some extra boxes. This is normally the
		// case, if GameState s contains some chains, that will automatically be
		// closed by the solver.
		//
		if (gameState.countLines() != lastResponse.lineCount) {
			lastResponse.linesAddedBySolver = true;
			try {
				lastResponse.extraLines = findExtraLines(lastResponse,
						gameState);
			} catch (WilsonSolverBugException e) {
				throw new WilsonSolverBugException(e.getMessage(), mv);
			}

		} else
			lastResponse.linesAddedBySolver = false;

		return lastResponse;
	}

	/**
	 * Takes the last move back for Wilson's Solver. The solver will respond
	 * again with the values of the previous board. This response is parsed and
	 * analyzed and returned by this method.
	 * 
	 * @return Parsed and analyzed response of the solver for the last position
	 *         of the solver.
	 * @throws TimeLimitExceededException
	 *             Thrown, if a timeout occurs during the communication process
	 *             with the solver.
	 */
	public SolverResponse undo() throws TimeLimitExceededException {
		//
		// Send "<" to Wilson's program
		//
		writeLineStdout("<");
		String s = getResponse();
		SolverResponse values = parseResponse(s, m, n);

		//
		// Undo one move for the real game-state
		//
		if (values != null)
			gameState.undo();

		//
		// Unfortunately the last response of the solver is needed, because of a
		// bug therein (see advance()).
		//
		lastResponse = values;

		return values;
	}

	/**
	 * Reset the board of wilson's solver. This will load the initial board from
	 * the problem-description file.
	 * 
	 * @return Response of the solver for the initial board.
	 * @throws TimeLimitExceededException
	 *             Thrown, if a timeout occurs during the communication process
	 *             with the solver.
	 */
	public SolverResponse reset() throws TimeLimitExceededException {
		//
		// Send "!" to Wilson's program
		//
		writeLineStdout("!");
		String s = getResponse();
		SolverResponse values = parseResponse(s, m, n);

		//
		// Copy the initial position to the current game-state
		//
		if (values != null)
			gameState = new GameState(initialState);

		//
		// Unfortunately the last response of the solver is needed, because of a
		// bug therein.
		//
		lastResponse = values;
		return values;
	}

	/**
	 * Get the values for a given state for all moves possible from this state.
	 * The lines are placed in the same order in Wilsons solver as already done
	 * in GameState s. Nevertheless, it cannot be guaranteed, that the state of
	 * the board in wilsons solver is exactly the same after placing all lines.
	 * Since Wilson's solver automatically closes chains, it can happen, that
	 * the boxes occupied are different from the passed GameState. This is for
	 * instance the case, if either player does not capture a chain in the real
	 * move sequence, which is however done by default by the solver. The
	 * returned box-difference will not be correct in such cases, as well as the
	 * player to move. Therefore, it is better to use
	 * {@link WilsonInterface#getRelativeValueList(GameState)} instead, which
	 * only returns the box-difference beginning from the current GameState s.
	 * 
	 * @param s
	 *            The game-state, for which the response of the solver is
	 *            requested.
	 * @param actionSequence
	 *            The sequence of moves that the solver has to perform in order
	 *            to reach game-state s. Because of a bug in the solver, it can
	 *            be necessary to try different permutations of the original
	 *            move-sequence in order to reach the state without an error.
	 * @return The response of the solver, which contains the values for all
	 *         moves possible from the given state.
	 * @throws TimeLimitExceededException
	 *             Thrown, if a timeout occurs during the communication process
	 *             with the solver.
	 */

	private SolverResponse getValueList(GameState s, List<Move> actionSequence)
			throws TimeLimitExceededException {
		//
		// Reset the board of Wilsons solver
		//
		reset();

		//
		// Get a list of moves, that were performed
		//
		List<Move> actions;
		if (actionSequence == null)
			actions = s.getActionSequence();
		else
			actions = actionSequence;

		// Now perform these moves
		//Iterator<Move> i = actions.iterator();
		SolverResponse wv = null;

		//while (i.hasNext()) {
			//Move mv = i.next();
		for(Move mv : actions) {
			SolverResponse temp = advance(mv);

			//
			// wv is null, if we make a move that was already automatically done
			// by Wilson's solver (closing a chain)
			//
			if (temp != null) {
				wv = temp;
			}
		}

		//
		// There is one problem, if we have an empty board, in this case the
		// response will be empty. The best solution for us until now is to make
		// a arbritrary move and take the move back again and return the values
		// after this.
		//
		if (actions.isEmpty()) {
			advance(new int[] { 0, 0, 0 });
			wv = undo();
		}
		return wv;
	}

	/**
	 * A wrapper for the method
	 * {@link WilsonInterface#getValueList(GameState, List)}. This wrapper is
	 * needed because of a bug in the solver. In some cases it can happen, that
	 * the solver erroneously captures a box. If this error occurs, an exception
	 * is thrown (@link {@link WilsonSolverBugException}) which has to be
	 * handled. The problem can be solved by trying another move-sequence which
	 * leads to the same state.
	 * 
	 * @param s
	 *            The game-state, for which the response of the solver is
	 *            requested.
	 * @return The response of the solver, which contains the values for all
	 *         moves possible from the given state.
	 * @throws TimeLimitExceededException
	 *             Thrown, if a timeout occurs during the communication process
	 *             with the solver.
	 */
	private SolverResponse getValueListHandleBug(GameState s)
			throws TimeLimitExceededException {
		SolverResponse wv = null;

		ArrayList<Move> actionSequence = null;
		boolean doRun = true;
		while (doRun) {
			try {
				wv = getValueList(s, actionSequence);
				doRun = false;
			} catch (WilsonSolverBugException e) {
				//
				// If the method throws an exception because of the bug in
				// Wilson's
				// solver, the try to handle this problem. Take the last move,
				// that
				// caused the exception and place it in the beginning of the
				// action-sequence, which hopefully should solve the problem.
				// Otherwise, this procedure will be repeated until the problem
				// is solved.
				//
				if (actionSequence == null)
					actionSequence = s.getActionSequence();
				boolean removed = actionSequence.remove(e.lastMove);
				if (!removed) {
					System.out.flush();
					System.err.println("Current Action-Sequence"
							+ actionSequence);
					System.err.println("Real Action-Sequence"
							+ s.getActionSequence());
					throw new UnsupportedOperationException(
							"Should have been in the list!");
				}
				actionSequence.add(0, e.lastMove);

			}
		}
		return wv;
	}

	/**
	 * Retrieves the action-values for a given game-state s. The values returned
	 * are the future box-differences when selecting a certain action.
	 * Therefore, the current box-difference for game-state is not part of the
	 * values.
	 * 
	 * @param s
	 *            The game-state, for which the response of the solver is
	 *            requested.
	 * @return The response of the solver, which contains the values for all
	 *         moves possible from the given state.
	 */
	public SolverResponse getRelativeValueList(GameState s) {

		SolverResponse wv = null;
		try {
			//
			// First get the values as usual
			//
			wv = getValueListHandleBug(s);
		} catch (TimeLimitExceededException e) {
			//
			// if we get a timeout while communicating with the program, then
			// the best thing is to restart the solver.
			//
			close();
			try {
				loadProblem(problemName, m, n);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			return getRelativeValueList(s);
		}

		//
		// If wv is null. Should not happen.
		//
		if (wv == null) {
			System.out.println(s);
			throw new UnsupportedOperationException();
		}

		/*
		 * We want the number of boxes, that are occupied, starting from this
		 * state. The old values are not that interesting, since optimal moves
		 * always maximize the capture of boxes in the future. Additionally,
		 * this simplifies the analysis for Wilson's solver, since the solver
		 * can create from GameState s different box-differences because of the
		 * before mentioned chain-problem (the solver always automatically
		 * closes a chain for the player to move, even if the player in reality
		 * selected another move). We have to subtract all possible future
		 * box-diffs (for every move) from the current box-diff. NOTE: <br>
		 * wv.boxdiff is not necessarily equal to s.getBoxDiff(), as mentioned
		 * before. Therefore, use wv.boxDiff, to get a correct value of the
		 * future number of boxes captured.
		 */
		Double v[][][] = wv.values;
		boolean invert = wv.p != s.getPlayerToMove();
		for (int i = 0; i < v.length; i++)
			// direction: horizontal/vertical
			for (int j = 0; j < v[i].length; j++) {// x-coord
				for (int k = 0; k < v[i][j].length; k++) { // y-coord
					if (!Double.isNaN(v[i][j][k]))
						v[i][j][k] -= wv.boxDiff;

					/*
					 * The values are for the current player to move. They
					 * indicate the number of boxes captured in future by the
					 * player to move. We always assume, that it is
					 * s.playerToMove() turn. In some cases, it can happen that
					 * Wilsons solver mixes this up, because some chains were
					 * automatically closed previously. In such a case, we have
					 * to invert the values, since in reality its the opposite
					 * player's turn.
					 */
					if (invert)
						v[i][j][k] *= -1.0;
				}
			}
		return wv;
	}

	/**
	 * @param response
	 * @return line values in the form [direction][x][y]. If line is set, then
	 *         the value will be NaN.
	 */
	private static SolverResponse parseResponse(String response, int m, int n) {
		Double[][][] values = new Double[2][][];
		int[][][] lines = new int[2][][];

		// create values for horizontal line
		values[0] = new Double[m][n + 1];
		lines[0] = new int[m][n + 1];
		// creates values for vertical lines
		values[1] = new Double[m + 1][n];
		lines[1] = new int[m + 1][n];

		SolverResponse wv = preProcessing(response);

		String board = wv.processedResponse;

		int rowCounter = 0;
		int x = 0, y = 0;
		//
		// Counter for the set lines on the board
		//
		int lineCount = 0;

		//
		// Now move through the board line by line, character by character
		//
		String line;
		int lastLineIndex = 0;
		while (null != (line = extractLine(board, lastLineIndex))) {
			// save the last index of the current line. The length of the
			// line-separtor is system dependent. In windows it is \r\n, in
			// Linux only \n.
			lastLineIndex += line.length() + System.lineSeparator().length();
			// if rowcounter is even, we have horizontal lines
			if (rowCounter % 2 == 0) {
				int i = 0;
				int length = line.length();
				while (i < length - 1) {
					char c = line.charAt(i);
					//
					// first char of the segment has to be a node
					//
					if (c != '#' && c != '+')
						throw new UnsupportedOperationException();
					i++;

					//
					// Second character can be a line, or a value
					//
					c = line.charAt(i);
					if (c == '-') {
						// i++;
						// can be a line, or just a value
						char z = line.charAt(i + 1);
						if (z == '#' || z == '+') {
							//
							// we have a horizontal line
							//
							i++;
							lineCount++;
							lines[0][x][y] = 1;
							values[0][x++][y] = Double.NaN;
							continue;
						}
					}
					//
					// get the value. Since it can have more than one digit,
					// search for the next node-symbol
					//
					int lastIndexValue1 = line.indexOf("#", i);
					if (lastIndexValue1 == -1)
						lastIndexValue1 = Integer.MAX_VALUE;
					int lastIndexValue2 = line.indexOf("+", i);
					if (lastIndexValue2 == -1)
						lastIndexValue2 = Integer.MAX_VALUE;
					int lastIndexValue = Math.min(lastIndexValue1,
							lastIndexValue2);
					String value = line.substring(i, lastIndexValue);
					//
					// In some few cases, Wilson's solver does not assign values
					// to every possible move. In this case, the value will be
					// "."! Store NaN in this case.
					//
					if (!value.equals("."))
						values[0][x++][y] = Double.valueOf(value);
					else
						values[0][x++][y] = Double.NaN;
					i = lastIndexValue;
				}
			} else {
				//
				// Analyze a row of vertical lines
				//
				int i = 0;
				int length = line.length();
				while (i < length) {
					char c = line.charAt(i);
					//
					// First element has to be a line or a value
					//
					if (c == '|') {
						//
						// We have a vertical line
						//
						lineCount++;
						lines[1][x][y] = 1;
						values[1][x][y] = Double.NaN;
						i++;
					} else {
						// get the value. Since it can have more than one digit,
						// search for the next Point. Note, that the last
						// element of the row does not have a point.
						//
						int lastIndexValue = (x < m ? line.indexOf(".", i)
								: line.length());
						if (lastIndexValue == -1)
							throw new UnsupportedOperationException();

						String value = line.substring(i, lastIndexValue);
						//
						// In some few cases, Wilson's solver does not assign
						// values
						// to every possible move. In this case, the value will
						// be
						// an empty String "". Store NaN in this case.
						//
						if (!value.equals(EMPTY_LINE)) {
							try {
								values[1][x][y] = Double.valueOf(value);
							} catch (java.lang.NumberFormatException e) {
								e.printStackTrace();

								System.err.println(response);
								System.err.println(board);
								System.err.println("\nline: ");
								System.err.println(line);

								@SuppressWarnings("unused")
								SolverResponse test = preProcessing(response);
							}

						} else {
							values[1][x][y] = Double.NaN;
						}
						i = lastIndexValue;
					}
					x++;
					//
					// Now we should have a point, but only if we didnt reach
					// the end of the line
					//
					if (x <= m) {
						c = line.charAt(i);
						if (c != '.')
							throw new UnsupportedOperationException();
					}
					i++;
				}
			}

			//
			// check x:
			// For horizontal lines, x must be m
			//
			if (rowCounter % 2 == 0 && x != m)
				throw new UnsupportedOperationException();
			else if (rowCounter % 2 != 0 && x != m + 1) {
				System.err.println(response);
				System.err.println(board);
				System.err.println("\nline: ");
				System.err.println(line);
				System.err.println("\n x: " + x);
				@SuppressWarnings("unused")
				SolverResponse test = preProcessing(response);

				// For vertical lines, x must be m+1
				throw new UnsupportedOperationException();
			}

			//
			// Reset x again:
			//
			x = 0;
			if (rowCounter % 2 != 0)
				// We analyzed a row of horizontal lines and vertical lines in
				// the string. Now it is time to move to the next row of the
				// board.
				y++;

			rowCounter++;
		}

		if (rowCounter != 2 * n + 1)
			throw new UnsupportedOperationException();

		wv.boardLines = lines;
		wv.values = values;
		wv.lineCount = lineCount;
		return wv;
	}

	/**
	 * Pre-processing of the response of the solver. This simplifies the later
	 * parsing of the response.
	 * 
	 * @param s
	 *            Unchanged response of the solver.
	 * @return A pre-processed version of the response.
	 */
	private static SolverResponse preProcessing(String s) {
		SolverResponse wv = new SolverResponse();

		//
		// Determine player to move
		//
		String playerToMove = "Enter move for player ";
		int indexPlayer = s.indexOf(playerToMove) + playerToMove.length();
		char p = s.charAt(indexPlayer);
		if (p == 'A')
			wv.p = 1;
		else if (p == 'B')
			wv.p = -1;
		else {
			// Should not happen! The response should contain the player to
			// move. Most likley, the response was not read completly.
			System.out.flush();
			System.err.println("Response: ");
			System.err.println(s);
			throw new UnsupportedOperationException("Wrong player!");
		}

		//
		// Save response
		//
		wv.response = new String(s);

		//
		// First node of the board starts with a + or a #
		// Leave the two spaces infront for the moment, since the whole board is
		// shifted by two spaces.
		//
		int start1 = s.indexOf("  +");
		if (start1 == -1)
			start1 = Integer.MAX_VALUE;
		int start2 = s.indexOf("  #");
		if (start2 == -1)
			start2 = Integer.MAX_VALUE;
		int start = Math.min(start1, start2);

		//
		// Last node is again a + or a #
		//
		int end1 = s.lastIndexOf("+");
		int end2 = s.lastIndexOf("#");
		int end = Math.max(end1, end2);

		//
		// Extract all characters from first node of the board to the last node
		//
		String b = s.substring(start, end + 1);

		//
		// Remove the row and column labels (a,b,c,...)
		//
		b = b.replaceAll("[a-z]", "");

		//
		// Now first count the box-difference
		//
		int numBoxA = b.split("A", -1).length - 1;
		int numBoxB = b.split("B", -1).length - 1;
		wv.boxDiff = numBoxA - numBoxB;

		// replace A and B was here before

		//
		// Remove every second line, starting from the second one
		//
		StringBuffer text = new StringBuffer(b);
		int first = -1, second;
		do {
			first = text.indexOf(System.lineSeparator(), first) + 1;
			second = text.indexOf(System.lineSeparator(), first);
			if (first != 0)
				text = text.replace(first, second + 1, "");
		} while (first != 0);
		b = text.toString();

		// For every captured Box check, if all sides are closed. This has to be
		// done because of a bug in Wilsons solver, that causes boxes to be
		// captured without all 4 sides of the box beeing closed before.
		int lineLength = b.indexOf(System.lineSeparator())
				+ System.lineSeparator().length();
		int index = 0;
		String players[] = new String[] { "A", "B" };
		boolean correct = true;
		for (String ltr : players) {
			do {
				index = b.indexOf(ltr, index + 1);
				if (index != -1) {
					// The line above has to contain a vertical line "--"
					if (b.charAt(index - lineLength) != '-'
							|| b.charAt(index - lineLength + 1) != '-')
						correct = false;
					if (b.charAt(index + lineLength + 2) != '-'
							|| b.charAt(index + lineLength + 3) != '-')
						correct = false;
					if (b.charAt(index + 3) != '|')
						correct = false;
					if (b.charAt(index - 3) != '|')
						correct = false;
				}
			} while (index != -1);
		}
		if (!correct) {
			//System.out.flush();
			//System.err.print(s);
			//System.err.flush();
			throw new WilsonSolverBugException(
					"This most likley, is a bug in Wilson's Solver!"
							+ "In some very few cases it can happpen, that Wilsons solver"
							+ "adds some moves that are not capturing. This most likley is a"
							+ "bug in his implementation, since the solver makes an"
							+ " automatic non-capturing move, but does not change the player"
							+ "to move afterwards, and shows that the box is occupied by a"
							+ " player, although only 3 lines are closed. In this case it is"
							+ " the best, to just perform the capturing moves for the given"
							+ " state and leave a hard-hearted handout.", null);
		}

		//
		// Remove spaces at the beginning of every line
		//
		b = b.replaceAll(System.lineSeparator() + "  ", System.lineSeparator()
				+ "");

		//
		// remove remaining letters.
		//
		b = b.replaceAll("[A-Z]", " ");

		//
		// In some special cases, the solver does not print the values for many
		// moves. This is especially bad for vertical moves. In this case remove
		// the space, at which the move-value should have been and replace by an
		// Symbol
		//
		b = b.replaceAll(System.lineSeparator() + " ", System.lineSeparator()
				+ "");
		b = b.replaceAll("      ", "     " + EMPTY_LINE);

		//
		// Replace spaces between vertical lines (vertical line values) with
		// points
		//
		b = b.replaceAll("    ", ".");

		//
		// Sometimes the space is only 3 (vertical line values such as -10)
		//
		b = b.replaceAll("   ", ".");

		//
		// Now remove all spaces left
		//
		b = b.replaceAll(" ", "");

		//
		// Replace every sequence of minus (horizontal lines) with a single
		// minus
		//
		b = b.replaceAll("[-]+", "-");

		// Special Case: Two points next to each other are not allowed. This can
		// happen, if the solver does not assign values for each possible move,
		// but leaves it empty.
		// String bOld;
		// do {
		// bOld = b;
		// b = b.replaceAll("[.]{2}", "." + EMPTY_LINE + ".");
		// } while (!b.equals(bOld));
		String cr = System.lineSeparator() + "\\.";
		b = b.replaceAll(cr, System.lineSeparator() + EMPTY_LINE + ".");

		//
		// F.\r\n is not allowed, the point has to be removed
		//
		cr = EMPTY_LINE + "\\." + System.lineSeparator();
		b = b.replaceAll(cr, EMPTY_LINE + System.lineSeparator());

		//
		// .\r\n is not allowed. Append Error-Symbol "F" in this case
		//
		cr = "\\." + System.lineSeparator();
		b = b.replaceAll(cr, "." + EMPTY_LINE + System.lineSeparator());

		wv.processedResponse = new String(b);
		return wv;
	}

	/**
	 * Extract a single line of a string, starting at a certain index of the
	 * string. A line is considered to be terminated with a line-separator
	 * symbol.
	 * 
	 * @param s
	 *            String, from which a line should be extracted.
	 * @param startWith
	 *            Starting-index in the string.
	 * @return Line of the String, starting from startWidth until the first
	 *         line-separator-symbol is detected.
	 */
	private static String extractLine(String s, int startWith) {
		String x = System.lineSeparator();
		int firstLineBreak = s.indexOf(x, startWith);
		//
		// Return line without line-break
		//
		if (firstLineBreak > 0)
			return s.substring(startWith, firstLineBreak);
		else if (startWith >= s.length())
			return null;
		return s.substring(startWith);
	}

	/**
	 * Compare the lines of the board from Wilson's Solvers response and the
	 * current GameState s. Wilson's solver in many cases adds lines to the
	 * board, because it automatically adds lines to chains. These extra lines
	 * will be returned as an array.
	 * 
	 * @param wv
	 *            Response of Wilson's solver for the GameState s. It is
	 *            possible, that Wilson's solver added additional lines.
	 * @param s
	 *            Current GameState
	 * @return An array of extra lines, which were added by Wilsons solver.
	 */
	private static List<int[]> findExtraLines(SolverResponse wv, GameState s) {
		//
		// Compare wv.values and GameState s. If a value is NaN, then the line
		// is set.
		// Run through the values-list
		//
		int numLinesGameState = s.countLines();
		int numLinesResponse = wv.lineCount;
		//
		// GameState s should not have more lines than the solvers respsonse.
		//
		if (numLinesGameState > numLinesResponse) {
			throw new UnsupportedOperationException(
					"This should normally not be possible");
		}

		ArrayList<int[]> extraLines = new ArrayList<int[]>();
		int[][][] v = wv.boardLines;
		for (int i = 0; i < v.length; i++)
			for (int j = 0; j < v[i].length; j++)
				for (int k = 0; k < v[i][j].length; k++) {
					// careful: == Double.NaN is not correct
					if (v[i][j][k] != 0) {
						if (!s.isSet(i, j, k)) {
							extraLines.add(new int[] { i, j, k });
						}
					}
				}
		if (extraLines.size() == 0)
			return null;

		//
		// make a copy of the current game-state.
		//
		GameState ss = new GameState(s);
		int[] dim = ss.getBoardDimensions();
		// int boxDiffBegin = ss.getBoxDifference();

		boolean foundCapture;

		// Now sort the lines in the correct order closing the chain(s) box by
		// box.
		ArrayList<int[]> extraLinesSorted = new ArrayList<int[]>(
				extraLines.size());
		while (!extraLines.isEmpty()) {
			Iterator<int[]> i = extraLines.iterator();
			while (i.hasNext()) {
				int[] mv = i.next();
				byte bitIndex = (byte) GameStateFactory.getLineBitIndex(mv,
						dim[0], dim[1]);
				int oldBoxDiff = ss.getBoxDifference();
				ss.advance(mv[0], bitIndex);
				// check, if we captured a box
				if (ss.getBoxDifference() != oldBoxDiff) {
					extraLinesSorted.add(mv);
					boolean successRem = extraLines.remove(mv);
					if (!successRem) {
						throw new UnsupportedOperationException(
								"Should not have happend. mv has to be in the list");
					}
					break; // we have to break, because we changed the
							// underlying structure of the iterator.
				} else
					ss.undo();

				foundCapture = i.hasNext();
				if (!foundCapture) {

					// remove this later
					// System.out.flush();
					// System.err
					// .println("In class WilsonInterface#findExtraLines."
					// + "Remove Later. Just for Test-puposes.");
					// System.err.println("Response");
					// System.err.println(wv.response);
					// System.err.println("Real Board:");
					// System.err.println(s);
					// System.err.flush();

					// extraLinesSorted.addAll(extraLines);
					// extraLines.clear();
					throw new WilsonSolverBugException(
							"This most likley, is a bug in Wilson's Solver!"
									+ "In some very few cases it can happpen, that Wilsons solver"
									+ "adds some moves that are not capturing. This most likley is a"
									+ "bug in his implementation, since the solver makes an"
									+ " automatic non-capturing move, but does not change the player"
									+ "to move afterwards, and shows that the box is occupied by a"
									+ " player, although only 3 lines are closed. In this case it is"
									+ " the best, to just perform the capturing moves for the given"
									+ " state and leave a hard-hearted handout.",
							null);
					// give null as parameter for the move, since the last move
					// before findExtraLines is called, causes this exception.

				}
			}

		}

		if (extraLinesSorted.size() != numLinesResponse - numLinesGameState) {
			System.out.flush();

			//
			// Error: Should not have happend!
			//
			System.err.println("Real State: ");
			System.err.print(s);
			System.err.println("Response of the solver: ");
			System.err.println(wv.response);
			System.err.println("Extra lines: ");
			System.err.println("Values: ");
			System.err.println(Arrays.deepToString(wv.values));
			int[][] array = extraLines.toArray(new int[extraLines.size()][]);
			System.out.println(Arrays.deepToString(array));
			throw new AssertionError(
					"The number of extra lines is somehow not correct!");
		}

		return extraLinesSorted;
	}

	/**
	 * Converts a line-position on the board into a format, that Wilson's solver
	 * understands. For instance the horizontal line in the uppermost top-left
	 * corner (0,0,0) is converted into "ab".
	 * 
	 * @param point
	 *            Position on the board in the format {direction, x, y}
	 * @return Position-encoding for the move, which is understandable by
	 *         Wilson's solver.
	 */
	private static String getAlphabeticCoordinates(int[] point) {
		String xx, yy;
		char c;
		int direction = point[0];
		int x = point[1];
		int y = point[2];
		if (direction == 0) {
			// x: b, d, f, ...
			// y: a, c, e, ...
			c = (char) ('b' + (char) 2 * x);
			xx = new String(c + "");
			c = (char) ('a' + (char) 2 * y);
			yy = new String(c + "");
		} else {
			// x: b, d, f, ...
			// y: a, c, e, ...
			c = (char) ('a' + (char) 2 * x);
			xx = new String(c + "");
			c = (char) ('b' + (char) 2 * y);
			yy = new String(c + "");
		}
		return yy + xx;
	}

	// @SuppressWarnings("unused")
	// public static void main(String args[]) throws InterruptedException,
	// IOException, TimeLimitExceededException {
	// WilsonInterface iw = new WilsonInterface();
	// iw.loadProblem("3x3", 3, 3);
	// iw.close();
	// }
}

/**
 * SolverResponse contains all important information, that Wilson's solver
 * returns after an action (e.g., placing a line on the board). The unformatted,
 * unchanged response is stored as well as the processed and analyzed response.
 * 
 * @author Markus Thill
 * 
 */
class SolverResponse {
	/**
	 * Unchanged Response of Wilson's Solver.
	 */
	String response;

	/**
	 * Basically only needed for Debug-Purposes. The response of Wilson's Solver
	 * is pre-processed in order to make it possible to parse the response. The
	 * result of the pre-processing is stored here.
	 */
	String processedResponse;

	/**
	 * Board, containing the lines. Array has the form board[direction][x][y].
	 * An edge is considered to be set if boardLines[direction][x][y] is not
	 * equal to zero (0).
	 */
	int boardLines[][][];

	/**
	 * Values for all possible moves for the position represented by this
	 * response. An action that is not possible will get the value Double.NaN.
	 * The array has the form values[direction][x][y], where direction=0
	 * described all horizontal lines, direction=1 describes all vertical lines
	 * and, x and y describe the position of the edge on the board (e.g., x=0,
	 * y=0 describes the top-left edge).
	 */
	Double values[][][];

	/**
	 * Wilson's Solver often automatically adds extra lines to the board, when
	 * chains are found that can be closed. The reason for this is, that the
	 * optimal moves are always into a chain (sometimes leaving a hard-hearted
	 * handout). If this is the case, this variable will be set to true. The
	 * {@link SolverResponse#values} should be then handled carefully, since
	 * they do not represent the values for the requested state. It is
	 * recommended to perform the moves given in
	 * {@link SolverResponse#extraLines} on the {@link GameState} s first, since
	 * these moves were considered to be optimal by Wilson's Solver.
	 */
	boolean linesAddedBySolver;

	/**
	 * Extra lines, added by Wilson's Solver to the board. Wilson's Solver often
	 * automatically adds extra lines to the board, when chains are found that
	 * can be closed. The reason for this is, that the optimal moves are always
	 * into a chain (sometimes leaving a hard-hearted handout.)
	 */
	List<int[]> extraLines;

	/**
	 * Future Box-Difference. This means, only boxes captured in the future are
	 * considered. Already captured boxes will not be considered. To get the
	 * final box-difference, the current box-difference of the state has to be
	 * added to this value.
	 */
	int boxDiff;

	/**
	 * Number of lines set in the response of Wilsons Solver. This can be more
	 * than expected, because the solver automatically closes chains.
	 */
	int lineCount;

	/**
	 * Player to move from the corresponding position.
	 */
	protected int p;
}