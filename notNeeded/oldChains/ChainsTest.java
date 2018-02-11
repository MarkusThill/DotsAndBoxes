package oldChains;

import static org.junit.Assert.*;

import java.util.List;


import org.junit.Test;

import dotsAndBoxes.GameState;

public class ChainsTest {
	
	@Test
	public void testError1() {
		// This setup leads to an error. y?
		GameState s = new GameState(3,3);
		s.advance(0,(byte)25);
		s.advance(0,(byte)9);
		s.advance(0,(byte)17);
		s.advance(1,(byte)24);
		s.advance(0,(byte)24);
		s.advance(0,(byte)3);
		s.advance(0,(byte)8);
		s.advance(1,(byte)0);
		s.advance(0,(byte)19);
		s.advance(0,(byte)16);
		s.advance(0,(byte)0);
		s.advance(1,(byte)8);
		s.advance(1,(byte)19);
		s.advance(1,(byte)16);
		// no chains until here
		assertEquals(0, s.chainCount());
		assertEquals(14, s.chainUndoStackSize());
		
		s.advance(1,(byte)2);
		// now we have two chains
		assertEquals(2, s.chainCount());
		assertEquals(15, s.chainUndoStackSize());
		
		// First chain
		Chain c = s.chainGet(0);
		ChainSegment cs = c.get(0);
		assertEquals(1, c.size());
		assertEquals(1, cs.direction);
		assertEquals(3, cs.bitIndex);
		
		// Second chain
		c = s.chainGet(1);
		cs = c.get(0);
		assertEquals(2, c.size());
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		
		
		s.advance(1,(byte)1);
		assertEquals(2, s.chainCount());
		s.advance(1,(byte)3);
		assertEquals(1, s.chainCount());
		s.advance(1,(byte)9);
		assertEquals(2, s.chainCount());
		s.advance(0,(byte)2);
		assertEquals(2, s.chainCount());
		s.advance(1,(byte)25);
		assertEquals(2, s.chainCount());
		s.advance(0,(byte)18);
		assertEquals(2, s.chainCount());
	}
	
	@Test
	public void testFindStrategy() {
		GameState s = new GameState(3,3);
		
		List<ChainSegment> strat = s.findStrategy(true);
		assertTrue(strat == null);
		strat = s.findStrategy(false);
		assertTrue(strat == null);
		//
		// create a half-open chain of length one
		//
		s.advance(0,(byte)25);
		s.advance(0,(byte)9);
		s.advance(1, (byte)9);
		
		strat = s.findStrategy(true);
		assertTrue(strat == null);
		
		strat = s.findStrategy(false);
		assertEquals(strat.size(), 1);
		ChainSegment cs = strat.get(0);
		assertEquals(cs.direction, 1);
		assertEquals(cs.bitIndex, 25);
		
		//extend the chain
		s.advance(0,(byte)17);
		s.advance(0,(byte)18);
		
		strat = s.findStrategy(true);
		assertEquals(strat.size(), 1);
		cs = strat.get(0);
		assertEquals(cs.direction, 1);
		assertEquals(cs.bitIndex, 24);
		
		strat = s.findStrategy(false);
		assertEquals(strat.size(), 2);
		cs = strat.get(0);
		assertEquals(cs.direction, 1);
		assertEquals(cs.bitIndex, 25);
		
		cs = strat.get(1);
		assertEquals(cs.direction, 1);
		assertEquals(cs.bitIndex, 24);
		
		//Make a new chain
		s.advance(1,(byte)18);
		s.advance(1,(byte)19);
		s.advance(0,(byte)0);
		s.advance(1,(byte)8);
		s.advance(1,(byte)0);
		s.advance(1,(byte)1);
		
		
		strat = s.findStrategy(true);
		assertEquals(strat.size(), 4);
		cs = strat.get(0);
		assertEquals(0, cs.direction);
		assertEquals(1,cs.bitIndex);
		cs = strat.get(1);
		assertEquals(0, cs.direction);
		assertEquals(2,cs.bitIndex);
		cs = strat.get(2);
		assertEquals(0, cs.direction);
		assertEquals(3,cs.bitIndex);
		cs = strat.get(3);
		assertEquals(1, cs.direction);
		assertEquals(24,cs.bitIndex);
	}

	@Test
	public void testChains() {
		// Start with a small board
		GameState s = new GameState(3, 3);

		//
		// Make one move
		//
		s.advance(1, (byte) 3);

		//
		// The number of chains should be zero
		//
		assertEquals(0, s.chainCount());

		//
		// The number of elements on the stack should be 1
		//
		assertEquals(1, s.chainUndoStackSize());

		s.advance(0, (byte) 16);
		assertEquals(0, s.chainCount());
		assertEquals(2, s.chainUndoStackSize());

		//
		// Now we create a trivial chain. This leads to a chain of length 1
		//
		s.advance(0, (byte) 17);
		assertEquals(1, s.chainCount());
		assertEquals(3, s.chainUndoStackSize());
		Chain c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());
		ChainSegment cs = new ChainSegment(1, 2);
		assertTrue(c.contains(cs));

		//
		// Now we extend the chain
		//
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 25);
		assertEquals(1, s.chainCount());
		assertEquals(5, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		// undo last extension of chain
		//
		s.undo();
		s.undo();
		assertEquals(1, s.chainCount());
		assertEquals(3, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = new ChainSegment(1, 2);
		assertTrue(c.contains(cs));

		//
		// extend by two boxes
		//
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 2);
		s.advance(0, (byte) 24);
		c = s.chainGet(0);
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		s.advance(0, (byte) 25);
		assertEquals(1, s.chainCount());
		assertEquals(7, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(0, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		// Close last segment of the chain in order to create a closed chain.
		//
		s.advance(1, (byte) 0);
		assertEquals(1, s.chainCount());
		assertEquals(8, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(ChainType.CLOSED, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		// now test exact opposite: close the other side
		s.undo();
		assertEquals(1, s.chainCount());
		assertEquals(7, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(0, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		s.undo();
		s.undo();
		s.undo();
		s.undo();
		s.advance(0, (byte) 17);
		assertEquals(1, s.chainCount());
		assertEquals(3, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = new ChainSegment(1, 2);
		assertTrue(c.contains(cs));

		s.undo();
		s.undo();
		s.undo();
		assertEquals(0, s.chainCount());
		assertEquals(0, s.chainUndoStackSize());

		//
		// Now create a chain from the opposite direction
		// Create a "pipe" first
		//
		s.advance(0, (byte) 2);
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 25);
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 17);
		assertEquals(0, s.chainCount());
		assertEquals(6, s.chainUndoStackSize());
		// close end of pipe
		s.advance(1, (byte) 0);
		assertEquals(1, s.chainCount());
		assertEquals(7, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(3, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);
		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		// now close other end of chain as well
		s.advance(1, (byte) 3);
		assertEquals(1, s.chainCount());
		assertEquals(8, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(2, c.size());
		assertEquals(ChainType.CLOSED, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		//
		// undo everything again to empty board.
		//
		s.undo();
		s.undo();
		s.undo();
		s.undo();
		s.undo();
		s.undo();
		s.undo();
		s.undo();

		//
		// Now create two half-open chains and connect them
		//
		s.advance(1, (byte) 3);
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 17);
		assertEquals(1, s.chainCount());
		assertEquals(3, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);
		// second half-chain
		s.advance(1, (byte) 0);
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 2);
		assertEquals(2, s.chainCount());
		assertEquals(6, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		c = s.chainGet(1);
		assertEquals(1, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		// now connect both half-chains...
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 25);
		assertEquals(1, s.chainCount());
		assertEquals(8, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(2, c.size());
		assertEquals(ChainType.CLOSED, c.getType());
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);
		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		// undo all again
		for (int i = 0; i < 8; i++)
			s.undo();

	}

	@Test
	public void testLongChain() {
		GameState s = new GameState(3, 3);
		//
		// Make a long chain
		//
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 17);
		s.advance(0, (byte) 25);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 8);
		s.advance(0, (byte) 1);
		s.advance(0, (byte) 9);
		s.advance(1, (byte) 24);
		s.advance(0, (byte) 0);
		s.advance(0, (byte) 8);
		s.advance(0, (byte) 19);
		s.advance(1, (byte) 16);

		// no chain yet
		assertEquals(0, s.chainCount());
		assertEquals(14, s.chainUndoStackSize());

		// now close one side of the pipe
		s.advance(1, (byte) 19);
		assertEquals(1, s.chainCount());
		assertEquals(15, s.chainUndoStackSize());

		Chain c = s.chainGet(0);
		assertEquals(9, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		ChainSegment cs = c.get(8);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(7);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(6);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(5);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(3, cs.bitIndex);

		// Try connecting two half-open pipes
		s = new GameState(3, 3);
		//
		// Make a long chain
		//
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 17);
		s.advance(0, (byte) 25);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 8);
		s.advance(0, (byte) 1);
		s.advance(0, (byte) 9);
		// s.advance(1, (byte)24);
		s.advance(0, (byte) 0);
		s.advance(0, (byte) 8);
		s.advance(0, (byte) 19);
		s.advance(1, (byte) 16);

		// no chain yet
		assertEquals(0, s.chainCount());
		assertEquals(13, s.chainUndoStackSize());

		// close first chain
		s.advance(1, (byte) 3);
		assertEquals(1, s.chainCount());
		assertEquals(14, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(5, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(2);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		// now close second pipe
		//
		s.advance(1, (byte) 19);
		assertEquals(2, s.chainCount());
		assertEquals(15, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(5, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(2);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		c = s.chainGet(1);
		assertEquals(3, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		//
		// now connect both half-open-chains
		//
		s.advance(1, (byte) 24);
		assertEquals(1, s.chainCount());
		assertEquals(16, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(8, c.size());
		assertEquals(ChainType.CLOSED, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(2);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(5);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(6);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(7);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		// if we undo this again, we get the old results
		//
		s.undo();
		assertEquals(2, s.chainCount());
		assertEquals(15, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(5, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(2);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		c = s.chainGet(1);
		assertEquals(3, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		//
		// close again
		//
		s.advance(1, (byte) 24);
		//
		// Divide into two chains
		//
		s.advance(1, (byte) 9);
		assertEquals(2, s.chainCount());
		assertEquals(17, s.chainUndoStackSize());

		//
		c = s.chainGet(0);
		assertEquals(3, c.size());
		assertEquals(ChainType.CLOSED, c.getType());

		cs = c.get(0);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		c = s.chainGet(1);
		assertEquals(4, c.size());
		assertEquals(ChainType.CLOSED, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(2);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		//
		// Undo again
		//
		s.undo();
		assertEquals(1, s.chainCount());
		assertEquals(16, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(8, c.size());
		assertEquals(ChainType.CLOSED, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(2);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(5);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(6);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(7);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		// undo again
		//
		s.undo();
		s.advance(1, (byte) 19);
		assertEquals(2, s.chainCount());
		assertEquals(15, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(5, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(2);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		c = s.chainGet(1);
		assertEquals(3, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		//
		// Restart
		//
		s = new GameState(3, 3);
		//
		// Make a long chain
		//
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 17);
		s.advance(0, (byte) 25);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 8);
		s.advance(0, (byte) 1);
		s.advance(0, (byte) 9);
		s.advance(1, (byte) 24);
		s.advance(0, (byte) 0);
		s.advance(0, (byte) 8);
		s.advance(0, (byte) 19);
		s.advance(1, (byte) 16);
		s.advance(1, (byte) 3);

		// chain opposite
		assertEquals(1, s.chainCount());
		assertEquals(15, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(9, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(5);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(6);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(7);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(8);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		// now divide into two chains
		//
		s.advance(1, (byte) 25);
		assertEquals(2, s.chainCount());
		assertEquals(16, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(4, c.size());
		assertEquals(ChainType.CLOSED, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(1);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(3);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		//
		c = s.chainGet(1);
		assertEquals(4, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		// -----------------------------------------------------
		//
		// Restart
		//
		s = new GameState(3, 3);
		//
		// Make a long chain
		//
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 17);
		s.advance(0, (byte) 25);
		s.advance(1, (byte) 0);
		s.advance(1, (byte) 8);
		s.advance(0, (byte) 1);
		s.advance(0, (byte) 9);
		s.advance(1, (byte) 24);
		s.advance(0, (byte) 0);
		s.advance(0, (byte) 8);
		s.advance(0, (byte) 19);
		s.advance(1, (byte) 16);
		s.advance(1, (byte) 3);

		// capture 1
		s.advance(1, (byte) 2);
		assertEquals(1, s.chainCount());
		assertEquals(16, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(8, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(5);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(6);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		cs = c.get(7);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		// capture 2
		s.advance(1, (byte) 1);
		assertEquals(1, s.chainCount());
		assertEquals(17, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(7, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(5);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		cs = c.get(6);
		assertEquals(0, cs.direction);
		assertEquals(2, cs.bitIndex);

		// capture 3
		s.advance(0, (byte) 2);
		assertEquals(1, s.chainCount());
		assertEquals(18, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(6, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		cs = c.get(5);
		assertEquals(1, cs.direction);
		assertEquals(9, cs.bitIndex);

		// capture 4
		s.advance(1, (byte) 9);
		assertEquals(1, s.chainCount());
		assertEquals(19, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(5, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(4);
		assertEquals(1, cs.direction);
		assertEquals(25, cs.bitIndex);

		// capture 5
		s.advance(1, (byte) 25);
		assertEquals(1, s.chainCount());
		assertEquals(20, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(4, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		cs = c.get(3);
		assertEquals(0, cs.direction);
		assertEquals(18, cs.bitIndex);

		// capture 6
		s.advance(0, (byte) 18);
		assertEquals(1, s.chainCount());
		assertEquals(21, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(3, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		cs = c.get(2);
		assertEquals(1, cs.direction);
		assertEquals(17, cs.bitIndex);

		// capture 7
		s.advance(1, (byte) 17);
		assertEquals(1, s.chainCount());
		assertEquals(22, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(2, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(18, cs.bitIndex);

		// capture 8
		s.advance(1, (byte) 18);
		assertEquals(1, s.chainCount());
		assertEquals(23, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(19, cs.bitIndex);

		// capture 9
		s.advance(1, (byte) 19);
		assertEquals(0, s.chainCount());
		assertEquals(24, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(0, c.size());
		assertEquals(ChainType.HALFOPEN, c.getType());
	}

	@Test
	public void testShortChain() {
		GameState s = new GameState(3, 3);
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 3);
		s.advance(0, (byte) 17);
		s.advance(0, (byte) 25);
		s.advance(0, (byte) 2);
		s.advance(1, (byte) 3);
		s.advance(1, (byte) 0);
		// we should have one closed chain
		assertEquals(1, s.chainCount());
		assertEquals(8, s.chainUndoStackSize());

		Chain c = s.chainGet(0);
		assertEquals(2, c.size());
		assertEquals(ChainType.CLOSED, c.getType());

		ChainSegment cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(1, cs.bitIndex);

		cs = c.get(1);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		// capture 1
		s.advance(1, (byte) 1);
		assertEquals(1, s.chainCount());
		assertEquals(9, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.CLOSED, c.getType());

		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);

		// capture 2
		s.advance(1, (byte) 2);
		assertEquals(0, s.chainCount());
		assertEquals(10, s.chainUndoStackSize());

		c = s.chainGet(0);
		assertEquals(0, c.size());
		assertEquals(ChainType.CLOSED, c.getType());
		
		// some random move
		s.advance(1, (byte) 25);
		
		// now undo again
		s.undo();
		s.undo();
		s.undo();
		
		// ------------------------------------------------
		// make a small closed chain
		// ------------------------------------------------
		s = new GameState(3,3);
		s.advance(0, (byte) 16);
		s.advance(0, (byte) 24);
		s.advance(0, (byte) 17);
		s.advance(0, (byte) 25);
		s.advance(1, (byte) 3);
		s.advance(1, (byte) 1);
		assertEquals(1, s.chainCount());
		assertEquals(6, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.CLOSED, c.getType());
		
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);
		
		// capture box
		s.advance(1, (byte) 2);
		assertEquals(0, s.chainCount());
		assertEquals(7, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(0, c.size());
		assertEquals(ChainType.CLOSED, c.getType());
		
		// undo again
		s.undo();
		assertEquals(1, s.chainCount());
		assertEquals(6, s.chainUndoStackSize());
		c = s.chainGet(0);
		assertEquals(1, c.size());
		assertEquals(ChainType.CLOSED, c.getType());
		
		cs = c.get(0);
		assertEquals(1, cs.direction);
		assertEquals(2, cs.bitIndex);
	}

	@Test
	public void testAddMove() {
		
	}

	@Test
	public void testUndo() {
		
	}

	@Test
	public void testFindChain() {
		
	}

	// Tests
	// create chain and then capture one by one of the elements...
	// Make a pipe and then close one end
	// Extend a chain by one first, then extend by a larger pipe
	// Connect two half open chains with each other: This should lead to only
	// one closed chain...
	// Close a half-open chain at the end to create a closed chain.
}
