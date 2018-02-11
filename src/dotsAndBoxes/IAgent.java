package dotsAndBoxes;

import java.util.List;



public interface IAgent {
	
	public Move getBestMove(GameState s);
	public List<Move> getBestMoves(GameState s);
	public double getValue(GameState s);
	public List<MoveValue> getActionValues(GameState s);
}
