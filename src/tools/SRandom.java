package tools;

import java.util.Random;

public class SRandom {
	public static final boolean RANDOM_SEED = true;
	public static final int SEED = 1;
	public static final Random RND;

	static {
		if (RANDOM_SEED)
			RND = new Random();
		else {
			RND = new Random(SEED);
			System.out
					.println(SRandom.class + ": Warning: Using a defined seed. This will leed to reproducible random-number sequences! In Class: tools.SRandom");
		}
	}
}
