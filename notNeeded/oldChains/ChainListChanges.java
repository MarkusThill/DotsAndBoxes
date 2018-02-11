package oldChains;

import java.util.ArrayList;

import oldChains.Chain.ChainType;



class ChainListChanges {
	final Chain changedChain;
	final ChainSegment changedSegment;
	final boolean capturing;
	final int capturedIndex;

	//
	// Chains created by this new move
	//
	final ArrayList<Chain> chainsCreated;
	
	//
	// Chains, that are unnecssary after this move
	//
	final ArrayList<Chain> chainsToRemove;

	ChainListChanges(Chain c, ChainSegment changedSegment) {
		this.changedChain = c;
		this.changedSegment = changedSegment;
		capturing = capturingMove();

		//
		// if chain is not a capturing move, then we have to divide the
		// current frame into two new frames and remove the current frame.
		//
		if (!capturing) {
			//
			// get the two subchains
			//
			Chain subChain1 = c.getSubChain(changedSegment, false);
			Chain subChain2 = c.getSubChain(changedSegment, true);
			chainsCreated = new ArrayList<Chain>(2);
			if(subChain1 != null)
			chainsCreated.add(subChain1);
			if(subChain2 != null)
			chainsCreated.add(subChain2);
			capturedIndex = -1;
		} else {
			//
			// We had a capturing move
			//
			// if we have a half-open chain, we have to remove the first
			// chain-segment, which is the last element of the list.
			// In a closed chain, boxes can be captured from both ends of
			// the chain. So we have to check first, from which side we
			// captured a box.
			//
			chainsCreated = null;
			boolean first = c.get(0).equals(changedSegment);
			if (c.getType() == ChainType.HALFOPEN || !first)
				capturedIndex = c.size() - 1;
			else
				capturedIndex = 0;

		}
		//TODO: use this for other cases as well...
		this.chainsToRemove = null;
	}

	public ChainListChanges(Chain changedChain, ArrayList<Chain> newChains) {
		this.changedChain = changedChain;
		changedSegment = null;
		capturing = false;
		capturedIndex = -1;
		chainsCreated = newChains;
		this.chainsToRemove = null;
	}
	
	public ChainListChanges(Chain changedChain, ArrayList<Chain> newChains, ArrayList<Chain> toRemove) {
		this.changedChain = changedChain;
		changedSegment = null;
		capturing = false;
		capturedIndex = -1;
		chainsCreated = newChains;
		this.chainsToRemove = toRemove;
	}

	/**
	 * Check, if the given move is a capturing move. If so, then set
	 * class-variable true, otherwise false.
	 */
	private boolean capturingMove() {
		//
		// When is a move considered a capturing move?
		// 1. If the chain is HALD-OPEN and we close the first segment in
		// the chain.
		// 2. If the chain is CLOSED and we close the first or the last
		// segment of the chain.
		//

		int size = changedChain.size();

		// Check 1.
		// The first segment of the chain, is the last element in the list
		boolean capturing = changedChain.get(size - 1).equals(
				changedSegment);

		// Check 2.
		if (changedChain.getType() == ChainType.CLOSED) {
			// The last segment of the chain, is the first element in the
			// list
			capturing = capturing
					|| changedChain.get(0).equals(changedSegment);
		}
		return capturing;
	}
}