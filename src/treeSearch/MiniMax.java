package treeSearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import dotsAndBoxes.GameState;
import dotsAndBoxes.IAgent;
import dotsAndBoxes.Move;
import dotsAndBoxes.MoveValue;

public class MiniMax implements IAgent {
	private static final Random RND = tools.SRandom.RND;
	private static final boolean useNegamax = true;
	private GameState s;

	/**
	 * Negamax-variant of the Minimax-algorithm. This algorithm only requires
	 * one function in total. It is based on the idea that max(a,b) =
	 * -min(-a,-b).
	 * 
	 * @param depth
	 *            current search depth
	 * @param p
	 *            player to move
	 * @return value returns the game-theoretic value of the current tree-node
	 */
	private double negamax(int depth, int p) {
		// If we have a terminal state, return
		if (s.isTerminal())
			return p * s.getWinner();

		double bestScore = Double.NEGATIVE_INFINITY;

		// Try all moves
		double score;
		// Iterator<Move> i = s.iterator();
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			s.advance(mv);
			if (p == s.getPlayerToMove())
				score = negamax(depth + 1, p);
			else
				score = -negamax(depth + 1, -p);
			s.undo();

			// check, if value is better than old value
			bestScore = Math.max(bestScore, score);
		}
		return bestScore;
	}

	private double max(int depth) {
		if (s.isTerminal())
			return s.getWinner();

		double bestScore = Double.NEGATIVE_INFINITY;

		double score;
		// Iterator<Move> i = s.iterator();
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			s.advance(mv);
			if (s.getPlayerToMove() > 0)
				score = max(depth + 1);
			else
				score = min(depth + 1);
			s.undo();
			bestScore = Math.max(bestScore, score);
		}
		return bestScore;
	}

	private double min(int depth) {
		if (s.isTerminal())
			return s.getWinner();

		double score;
		double bestScore = Double.POSITIVE_INFINITY;
		// Iterator<Move> i = s.iterator();
		// while (i.hasNext()) {
		// Move mv = i.next();
		for (Move mv : s) {
			s.advance(mv);
			if (s.getPlayerToMove() > 0)
				score = max(depth + 1);
			else
				score = min(depth + 1);
			s.undo();
			bestScore = Math.min(bestScore, score);
		}
		return bestScore;
	}

	@Override
	public double getValue(GameState s) {
		this.s = new GameState(s, true);
		if (useNegamax) {
			int p = s.getPlayerToMove();
			return p * negamax(0, p);
		} else {
			return (s.getPlayerToMove() == 1 ? max(0) : min(0));
		}
	}

	@Override
	public Move getBestMove(GameState s) {
		List<Move> bestMoves = getBestMoves(s);
		int select = RND.nextInt(bestMoves.size());
		return bestMoves.get(select);
	}

	@Override
	public List<Move> getBestMoves(GameState s) {
		GameState root = new GameState(s, true);
		int p = root.getPlayerToMove();
		double bestScore = Double.NEGATIVE_INFINITY;
		List<Move> bestMoves = new ArrayList<Move>();

		// Iterator<Move> i = root.iterator();
		// while (i.hasNext()) {
		for (Move mv : root) {
			// Move mv = i.next();
			root.advance(mv);
			double score = p * getValue(root);
			root.undo();
			if (score > bestScore) {
				bestScore = score;
				bestMoves.clear();
				bestMoves.add(mv);
			} else if (score == bestScore)
				bestMoves.add(mv);
		}
		return bestMoves;
	}

	@Override
	public List<MoveValue> getActionValues(GameState s) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		MiniMax minimax = new MiniMax();
		GameState s = new GameState(1, 3);

		System.out.println("value: " + minimax.getValue(s));
		System.out.println(s);

		Iterator<Move> i = s.iterator();
		while (i.hasNext()) {
			Move mv = i.next();
			s.advance(mv);
			System.out.println("value: " + minimax.getValue(s));
			System.out.println(s);
			s.undo();
		}
	}
}
