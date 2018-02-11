package oldChains;

import java.util.ArrayList;
import java.util.List;

public final class Chain {
	public enum ChainType {
		HALFOPEN, CLOSED, NA
	};

	Chain.ChainType type;

	private List<ChainSegment> chain;

	Chain(List<ChainSegment> chain, Chain.ChainType type) {
		this.chain = chain;
		this.type = type;
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param c
	 */
	Chain(Chain c) {
		type = c.type;
		//
		// Chain-Segments only have final attributes, so we can directly copy
		// them.
		//
		chain = new ArrayList<ChainSegment>(c.chain);
	}

	Chain() {
		type = ChainType.NA;
		chain = new ArrayList<ChainSegment>(10);
	}

	ArrayList<ChainSegment> getChain() {
		// TODO: We create a new object here. Can it make trouble, since a new
		// reference is generated?
		return new ArrayList<ChainSegment>(chain);
	}

	public int size() {
		// There may be chains, that have zero elements. Consider these
		return chain.size();
	}

	public ChainSegment get(int index) {
		// try {
		return chain.get(index);
		// }
		// catch(java.lang.IndexOutOfBoundsException e) {
		// System.out.println("Test!");
		// }
		// return null;
	}

	public Chain.ChainType getType() {
		return type;
	}

	public void addAll(int index, Chain c) {
		chain.addAll(index, c.chain);
	}

	public void addAll(Chain c) {
		chain.addAll(c.chain);
	}

	public void removeAll(Chain c) {
		chain.removeAll(c.chain);
	}

	public void removeAll(int index, Chain c) {
		chain.removeAll(c.chain);
	}

	public void addChainSegment(ChainSegment cs) {
		chain.add(cs);
	}

	public boolean contains(ChainSegment cs) {
		return chain.contains(cs);
	}

	public boolean remove(ChainSegment cs) {
		return chain.remove(cs);
	}

	public ChainSegment remove(int index) {
		return chain.remove(index);
	}

	public void add(int index, ChainSegment cs) {
		chain.add(index, cs);
	}
	
	public List<ChainSegment> getChainSegments() {
		return chain;
	}

	public Chain getSubChain(ChainSegment cs, boolean forward) {
		List<ChainSegment> subChain;
		Chain.ChainType ct;

		int index = chain.indexOf(cs);
		//
		// IMPORTANT: The returned sublist is backed by the original list.
		// So future changes are made in the sub-list as well as in the
		// original list
		//
		//
		// If we want the sub-chain in direction of the chain-tail, then we
		// get a new half-open chain. But only, if the original chain was
		// half-open.
		//
		if (forward && index - 1 >= 0) {
			// TODO: had problems with subchain. Make copy now. Maybe change
			// back again, when other problems are solved as well.
			
			// toIndex is exclusive!!!
			subChain = new ArrayList<ChainSegment>(chain.subList(0, index));
			ct = (type == ChainType.HALFOPEN ? ChainType.HALFOPEN
					: ChainType.CLOSED);
			return new Chain(subChain, ct);

		} else if (!forward && index + 1 <= chain.size() - 1) {
			// toIndex is exclusive!!!
			subChain = new ArrayList<ChainSegment>(chain.subList(index + 1,
					chain.size()));
			ct = ChainType.CLOSED;
			return new Chain(subChain, ct);
		}
		return null;
	}

	/**
	 * Only for test-purposes. Use iterator, if you want to run through the list
	 * of chains
	 * 
	 * @return
	 */
	public ChainSegment[] toArray() {
		return chain.toArray(new ChainSegment[chain.size()]);
	}
}