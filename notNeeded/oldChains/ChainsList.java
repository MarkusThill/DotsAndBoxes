package oldChains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import oldChains.Chain.ChainType;

import dotsAndBoxes.GameState;
import dotsAndBoxes.GameStateFactory;
import dotsAndBoxes.Move;

import tools.BitFiddling;

//TODO: easier algorithm: For each state we again look for all chains. We start from the first box and check, if three lines are checked. If so, then we look where the open part of the box is and jump to the neighbored box. Then, check for the neighbored box, if it continues the chain. 

public class ChainsList {

	private ArrayList<Chain> chainsList = null;
	private Stack<ChainListChanges> undoStack = new Stack<ChainListChanges>();
	private GameState s;

	public ChainsList(GameState s) {
		chainsList = new ArrayList<Chain>();
		this.s = s;
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param l
	 */
	public ChainsList(ChainsList l, GameState s) {
		// The GameState is not changed
		this.s = s;

	}

	/**
	 * For a move, check if it extends an already existing chain. Check this, by
	 * iterating through all chains.
	 * 
	 * @param cs
	 * @return
	 */
	private ChainListChanges findExtendedChange(ChainSegment cs) {
		Iterator<Chain> i = chainsList.iterator();
		while (i.hasNext()) {
			Chain c = i.next();
			// if chain is empty, then continue
			if (c.size() == 0)
				continue;
			//
			// get the last segment of the chain, which is the first in the list
			//
			ChainSegment cs1 = c.get(0);
			// Temporarily set the last chain-segment
			s.setLine(cs1.direction, cs1.bitIndex);
			//
			// Now check, if this creates a new chain. In this case, two chains
			// are returned. If only one chain is returned, then we did not
			// create an additional chain.
			ArrayList<Chain> newChains = findChain(cs);
			s.removeLine(cs1.direction, cs1.bitIndex);

			assert (newChains.size() >= 1);

			//
			// We most likely extended the corresponding chain, if we get one or
			// two
			// new chains. If we have one new chain, then we have a special
			// case, where a half-open chain of length one is extended
			//
			if (newChains != null) {
				//
				// Now we have to figure out, which chain is the new extending
				// one. If one of them is half-open, then we know that this one
				// is the extension.
				// If both are closed, then we connected two half-open chains
				// and we get one large closed chain.
				//
				Chain c1 = newChains.get(0);
				Chain c2 = null;
				if (newChains.size() == 2)
					c2 = newChains.get(1);
				if (c2 == null && c1.type == ChainType.HALFOPEN || c2 != null
						&& c1.type != c2.type) {
					//
					// one is half-open, one is closed.
					//
					if (c1.type == ChainType.HALFOPEN) {
						//
						// c1 is our extending chain
						//
						if (newChains.size() == 2)
							newChains.remove(1);
						return new ChainListChanges(c, newChains);
					} else {
						//
						// c2 is our extending chain
						//
						if (newChains.size() == 2)
							newChains.remove(0);
						return new ChainListChanges(c, newChains);
					}
				}
				//
				// Both chains are most likley closed chains.
				//
				else if (c2 == null || c1.type == ChainType.CLOSED) {
					//
					// This means, that we connected two half-open chains.
					// Therefore, we get one large chain, that is CLOSED.
					//
					int listSize = chainsList.size();
					assert (listSize >= 2);

					//
					// One of both chains should contain the original chain c.
					// Then extendingC will contain the extending segments for
					// c.
					//
					Chain extendingC = null;
					// Should be sufficient to check only chain-segment, since
					// every segment can only be part of one chain.
					// Take the last segment.
					if (c1.contains(cs1) && c2 != null) {
						extendingC = c2;
					} else if (c2 != null && c2.contains(cs1)) {
						extendingC = c1;
					} else
						extendingC = c1;

					//
					// We somehow have to merge these to half-open chains to
					// only one closed chain. This means, we have to remove one
					// chain from the list
					//
					Chain remove = null;
					for (int j = listSize - 1; j >= 0; j--) {
						remove = chainsList.get(j);
						if (remove.contains(extendingC.get(0)))
							break;
					}

					//
					// In total, we have to add the elements of extendingC to
					// chain c and we have to remove the additional chain
					// remove.
					// Use a dirty trick here. We return an ArrayList containing
					// first the chain to be removed and then the chain that
					// extends c.
					newChains.clear();
					newChains.add(remove);
					newChains.add(extendingC);
					return new ChainListChanges(c, newChains);

				}
				//
				// it is impossible that both are half-open: ERROR
				//
				else
					assert (c1.type != ChainType.HALFOPEN);

			}

		}
		return null;
	}

	private ChainListChanges findChainListChanges(ChainSegment cs) {
		// iterate through all chains and check, if a chain-segment is part of a
		// chain.
		// TODO: For now assume that a chain-segment can only be part of one
		// chain. Is this really so?
		for (Chain i : chainsList) {
			if (i.contains(cs)) {
				return new ChainListChanges(i, cs);
			}
		}

		//
		// Move is not part of a chain. Therefore, check if we created a new
		// chain and add the new chain to the list.
		//
		ArrayList<Chain> newChains = findChain(cs);
		if (newChains != null) {
			// TODO: In very few cases it can happen, that a move creates a new
			// chain, which extends an existing chain.
			/*@formatter:off*/
			/**
			 * Here the move was set in (1,9), which creates a new chain, 
			 * that extends the 1-segment chain at (0,2). Therefore we also 
			 * have to check, if the old chain is part of the new chain and 
			 * then remove the old chain.
			 *  +---+---+---+
				| B | B |   |  
				+---+---+   +
				|       |   | 
				+   +---+   +
				|           |
				+---+---+---+
			 */
			/*@formatter:on*/
			
			

			return new ChainListChanges(null, newChains);
		}

		//
		// If we did not create a new chain, it is possible, that we extended an
		// existing chain. This is the case, if the last chain-segment of a
		// chain would open a new chain. In fact, we would get two new chains:
		// One chain contains the already known chain-segments. The other chain
		// would contain new elements.
		// It is not possible, that a move extends two chains, with exception:
		// The move connects two half-open chains.
		// Also, it is possible, that an extension converts a half-open chain
		// into a closed chain.
		return findExtendedChange(cs);

	}

	// TODO: Simplify this whole procedure. It is far too complicated...
	private void adjustChainsList(ChainListChanges cc) {
		//
		// First check, if this move is a capturing move, hence, it reduces a
		// chain by one. If so, remove this chain-element from the corresponding
		// chain.
		//
		// Otherwise, it is possible that we divide a chain into two new chains.
		// In this case we have to create two new chains.
		if (cc.capturing) {

			Chain c = cc.changedChain;
			ChainSegment cs;
			cs = c.remove(cc.capturedIndex);
			assert (cs.equals(cc.changedSegment));
		}

		//
		// If we have divided a chain into two new chains, then:
		// 1. First Remove old chain. This is the case if capturing == false and
		// changedSegment != null
		// 2. Then add the two new ones, that were created by this move
		//
		// TODO: We do not need all of them. Which ones can be removed to get a
		// minimal if-clause?
		// Enough should be cc.capturing == false && cc.changedSegment != null
		else if (cc.changedSegment != null) {
			//
			// TODO: We now have removing possibility in ChainListChanges
			// We have a dividing move. Remove old chain
			//
			chainsList.remove(cc.changedChain);
		}

		else if (cc.chainsCreated != null && cc.changedChain != null) {
			//
			// We have extended a chain, or connected two chains.
			//
			// If we extended our chain, only one new chain is contained in cc
			if (cc.chainsCreated.size() == 1) {
				//
				// We extended an existing half-open chain. Add all segments of
				// chainsCreated to the corresponding chain. We have to insert
				// at index 0, since the last element of the chain is considered
				// as the first element in the list.
				//
				cc.changedChain.addAll(0, cc.chainsCreated.get(0));

			} else {
				//
				// We connected two half open chains to one closed chain.
				//
				// The first chain has to be removed.
				//
				// TODO: We now have removing possibility in ChainListChanges
				chainsList.remove(cc.chainsCreated.get(0));

				// The elements of the second chain have to be added to the
				// current frame. We have to insert at index 0, since the last
				// element of the chain is considered as the first element in
				// the list.
				cc.changedChain.addAll(0, cc.chainsCreated.get(1));

				//
				// Also the type of this chain has to be changed to CLOSED
				//
				cc.changedChain.type = ChainType.CLOSED;

			}
		}

		//
		// Now add two new chains to the list.
		// Note: The returned list are backed up by the old list, when we have a
		// chain-dividing move. Keep this in mind when changing the list.
		//
		if (cc.chainsCreated != null
				&& (!cc.capturing && cc.changedSegment != null || cc.changedChain == null))
			chainsList.addAll(cc.chainsCreated);

	}

	public void addMove(Move mv) {
		// When a move is performed a new chain can be generated (or even two in
		// certain cases). Find these chains first.
		//
		// The problem is, that a move can change an already existing chain: It
		// can be either placed completely in the middle of a chain, so that two
		// new chains are created. Or, it can make an existing chain shorter.
		//
		ChainSegment cs = new ChainSegment(mv);
		ChainListChanges cc = findChainListChanges(cs);

		//
		// add to chain-list, only if a chain was found. In some cases, one
		// move creates two chains, so that two chains have to be added to
		// the list.
		//
		if (cc != null)
			adjustChainsList(cc);

		//
		// add to the undo-stack, even if we did not find or change a chain.
		// When undoing a move, nothing will happen in this case.
		//
		undoStack.push(cc);

	}

	public void undo() {
		// Undoing a move can:
		// remove an existing chain...
		// make an existing chain larger again...
		// melt two chains to one again...

		//
		// We have to do the exact opposite of
		// adjustChainsList(ChainListChanges)
		//

		//
		// Only do something, if any chains were added or adjusted
		//
		ChainListChanges cc = undoStack.pop();
		if (cc != null) {
			//
			// First remove chains that were previously added.
			//
			if (cc.chainsCreated != null
					&& (!cc.capturing && cc.changedSegment != null || cc.changedChain == null))
				chainsList.removeAll(cc.chainsCreated);

			//
			// Then, if we had a capturing move, add the segment again to the
			// corresponding chain.
			//
			if (cc.capturing) {
				Chain c = cc.changedChain;
				// TODO: This was total bullcrap?? ChainSegment cs =
				// c.get(cc.capturedIndex);
				ChainSegment cs = cc.changedSegment;
				c.add(cc.capturedIndex, cs);
			}
			//
			// If we have a move, dividing a chain into two chains, we have to
			// restore the original chain. The two subchains should be deleted
			// at this point.
			//
			else if (cc.changedSegment != null) {
				chainsList.add(cc.changedChain);
			}

			//
			// If we have extended a chain, or connected two half-open chains
			//
			else if (cc.chainsCreated != null && cc.changedChain != null) {
				//
				// We have extended a chain, or connected two chains.
				//
				// If we extended our chain, only one new chain is contained in
				// cc
				if (cc.chainsCreated.size() == 1) {
					//
					// We extended an existing half-open chain. Now again remove
					// all segments of
					// chainsCreated from the corresponding chain.
					//
					cc.changedChain.removeAll(cc.chainsCreated.get(0));

				} else {
					//
					// We connected two half open chains to one closed chain.
					//
					// The first chain has to be added again.
					//
					chainsList.add(cc.chainsCreated.get(0));

					// The elements of the second chain have to be removed again
					// from the current chain
					cc.changedChain.removeAll(cc.chainsCreated.get(1));

					//
					// Also the type of this chain has to be changed back to
					// HALFOPEN
					//
					cc.changedChain.type = ChainType.HALFOPEN;

				}
			}

		}
	}

	/**
	 * 
	 * @param cs
	 * @return Returns one or two chains, that were created by the given move.
	 *         If no chain was found, null is returned. NOTE: The returend
	 *         chains are in inverse order. This means: The first chain-segment
	 *         in the list is actually the last segment in the chain.
	 */
	public ArrayList<Chain> findChain(ChainSegment cs) {
		Chain cs1 = findSingleChain(cs, 1);
		Chain cs2 = findSingleChain(cs, 2);

		ArrayList<Chain> r = null;
		if (cs1.size() != 0 || cs2.size() != 0)
			r = new ArrayList<Chain>(2);

		//
		// if we have a loop, we only have one chain and not two. Check this.
		// This is the case when the first chain-segment of the first chain is
		// equal to the last chain-segment of the second chain.
		//
		if (cs1.size() != 0 && cs2.size() != 0) {
			if (cs1.size() == cs2.size())
				if (cs1.get(0).equals(cs2.get(cs2.size() - 1)))
					// delete one chain, if we had a loop
					cs2 = null;
		}

		if (cs1.size() != 0)
			r.add(cs1);
		if (cs2 != null && cs2.size() != 0)
			r.add(cs2);

		return r;
	}

	private Chain findSingleChain(ChainSegment cs, int whichBox) {
		// TODO: Maybe extract whole class...
		// Vertical move can open a chain starting from the left or right box
		// from that move.
		// Horizontal move can open a chain starting from the top or bottom box

		// First check, if we created a capturable box at all?
		// TODO: use getter-method....
		long masks[] = GameStateFactory.ALL_BOX_MASKS[s.m - 1][s.n - 1][cs.direction][cs.bitIndex];

		Chain chain2 = null;
		Chain chain1 = null;

		long horBox = 0;
		int countHor = 0;
		long vertBox = 0;
		int countVert = 0;

		int count1 = 0;
		if (whichBox != 2) {
			// Long.bitCount uses the fast assembler instruction popcnt
			horBox = s.lines[0] & masks[0];
			countHor = Long.bitCount(horBox);
			vertBox = s.lines[1] & masks[1];
			countVert = Long.bitCount(vertBox);
			count1 = countHor + countVert;

			// Check, if three edges of first box are closed.
			if (count1 == 3) {
				ChainSegment missing;
				// either a horizontal, or vertical line is missing to complete
				// box
				if (countHor == 1)
					missing = new ChainSegment(0, BitFiddling.ld(~horBox
							& masks[0]));
				else
					missing = new ChainSegment(1, BitFiddling.ld(~vertBox
							& masks[1]));

				//
				// Set this line and see, if the chain is longer
				// recursive method call
				//
				if (!s.isSet(missing.direction, missing.bitIndex)) {
					s.setLine(missing.direction, missing.bitIndex);
					chain1 = findSingleChain(missing, 3);
					s.removeLine(missing.direction, missing.bitIndex);
					chain1.addChainSegment(missing);
				}
			}
		}

		//
		// Check, if three edges of second box are closed.
		//
		int count2 = 0;
		if (whichBox != 1) {
			if (masks.length > 2) {
				horBox = s.lines[0] & masks[2];
				countHor = Long.bitCount(horBox);
				vertBox = s.lines[1] & masks[3];
				countVert = Long.bitCount(vertBox);
				count2 = countHor + countVert;
			}
			if (count2 == 3) {
				ChainSegment missing;
				// either a horizontal, or vertical line is missing to complete
				// box
				if (countHor == 1)
					missing = new ChainSegment(0, BitFiddling.ld(~horBox
							& masks[2]));
				else
					missing = new ChainSegment(1, BitFiddling.ld(~vertBox
							& masks[3]));

				//
				// Set this line and see, if the chain is longer
				// recursive method call
				//
				if (!s.isSet(missing.direction, missing.bitIndex)) {
					s.setLine(missing.direction, missing.bitIndex);
					chain2 = findSingleChain(missing, 3);
					s.removeLine(missing.direction, missing.bitIndex);
					chain2.addChainSegment(missing);
				}
			}
		}

		//
		// Test this. After one or two chains are opened, it is not possible,
		// that another chain-segment opens a new chain
		//
		if (chain1 != null && chain2 != null)
			throw new UnsupportedOperationException("Should not have happend!");

		if (chain1 != null)
			return chain1;
		if (chain2 != null)
			return chain2;

		Chain c = new Chain();
		if (count1 == 4 && count2 == 4)
			c.type = ChainType.CLOSED;
		else
			c.type = ChainType.HALFOPEN;
		return c;
	}

	public int chainCount() {
		int count = 0;
		// some chains may be empty. Do not count them
		for (Chain i : chainsList)
			if (i.size() > 0)
				count++;
		return count;
	}

	public int chainUndoStackCount() {
		return undoStack.size();
	}

	public Chain get(int index) {
		return chainsList.get(index);
	}

	// Optimal strategy
	// Half-open chain:
	// 1. Capture every possible box and then make another move or
	// 2. Capture every box, except two and close the end of the chain
	// (Hard-hearted handout)

	// What about chains of length 1?
	// Probably optimal to capture it always, since we anyways would have to
	// place a edge somewhere else

	// Double-crosses
	// Are closed chains of length 2. So one move in this chain closes two
	// boxes. Typically, it is bad, if we have a half-open chain of length 2
	// to occupy both boxes, since often a new chain has to be openend
	// afterwards. It is better to just close the end of the chain
	// If we have a half-open chain of length two, there are also only two
	// possible optimal strategies:
	// 1. Occupy both boxes and do another move (normally not so good)
	// 2. Close the end of the chain
	// 3. Instead of 1 and 2 we could also directly make another arbritrary
	// move. Since we have to do that as well if we follow 1, we only need to
	// consider 1 & 2

	// Closed chains
	// Possible strategies:
	// 1. Occupy all possible boxes and make another move
	// 2. Leave 2 hard-hearted handouts (if possible).
	// 3. Make any arbritray other move. Since we have to do this for 1. as
	// well, we can forget about 3.

	// General rules for the strategy with chains.
	// 1. Capture all chains and make an additional move.
	// 2. Capture all moves, and leave a hard-hearted handout, which requires a
	// minimum length of a chain:
	// 2.1. Min. Length of 2 for a half-open chain.
	// 2.2 Min. Length of 4 for a half-open chain.
	// 3. If 2. is not available, just do 1

	public List<ChainSegment> findStrategy(boolean hardHearted) {
		// If we want to find a strategey for a hard-hearted handout, we have to
		// find a chain of sufficient length first.
		Chain hard = null;
		if (hardHearted) {
			hard = hardHearted();
			if (hard == null)
				return null;
		}

		// Occupy all boxes, except the hard-hearted handout (only if selected)
		ArrayList<ChainSegment> b = new ArrayList<ChainSegment>();
		for (Chain i : chainsList) {
			if (i != hard && i.size() > 0) {
				//
				// Iterate through chain and add all segments
				//
				List<ChainSegment> chain = i.getChainSegments();
				for (int j = i.size() - 1; j >= 0; j--)
					b.add(chain.get(j));
			}
		}

		// Check, if we have a hard-hearted handout
		if (hard != null) {
			//
			// The handout depends on the chain-type
			//
			if (hard.type == ChainType.HALFOPEN) {
				//
				// Add all, but two segments to the list.
				//
				for (int i = hard.size() - 1; i >= 2; i--)
					b.add(hard.get(i));
				//
				// Now close the chain on the other end
				//
				b.add(hard.get(0));
			} else {
				//
				// Add all, but 3 segments to the list
				//
				for (int i = hard.size() - 1; i >= 3; i--)
					b.add(hard.get(i));
				//
				// Now create two double-crosses with one move
				//
				b.add(hard.get(0));
			}
		}
		if (b.size() > 0)
			return b;
		return null;
	}

	/**
	 * Looks for the first chain, that has sufficient length for a hard-hearted
	 * handout. Prefer half-open chains.
	 * 
	 * @return
	 */
	public Chain hardHearted() {
		Chain tmp = null;
		for (Chain i : chainsList) {
			//
			// For a half-open chain, a length of 2 is sufficient for a
			// half-hearted handout.
			//
			if (i.type == ChainType.HALFOPEN && i.size() >= 2) {
				return i;
			}
			//
			// For a closed chain, a length of 4 is necessary (so 3
			// segments) to create a hard-hearted handout. But we prefer
			// half-open chains.
			//
			if (i.type == ChainType.CLOSED && i.size() >= 3) {
				tmp = i;
			}
		}
		return tmp;
	}

}
