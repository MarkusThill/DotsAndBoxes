package oldChains;

import dotsAndBoxes.Move;


public final class ChainSegment {
	public final int direction;
	public final int bitIndex;

	public ChainSegment(int direction, int bitIndex) {
		this.direction = direction;
		this.bitIndex = bitIndex;
	}

	public ChainSegment(Move mv) {
		this.direction = mv.direction;
		this.bitIndex = mv.bitIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ChainSegment) {
			ChainSegment cs = (ChainSegment) o;
			return cs.direction == direction && cs.bitIndex == bitIndex;
		}
		return false;
	}
}