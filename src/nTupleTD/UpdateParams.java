package nTupleTD;

import java.io.Serializable;

import dotsAndBoxes.GameStateSnapshot;

public class UpdateParams implements Serializable {
	private static final long serialVersionUID = -8020613752257389275L;
	public final GameStateSnapshot s;
	public final double y;
	public final double derivYMSELoss;
	public final double derivYWeightLoss;
	public final double delta;
	public final double globalAlpha;

	public UpdateParams(GameStateSnapshot s, double y, double derivYStdLoss,
			double derivYWeightLoss, double delta, double globalAlpha) {
		this.s = s;
		this.y = y;
		this.derivYMSELoss = derivYStdLoss;
		this.derivYWeightLoss = derivYWeightLoss;
		this.delta = delta;
		this.globalAlpha = globalAlpha;
	}

	public UpdateParams(UpdateParams u) {
		this.s = u.s;
		this.y = u.y;
		this.derivYMSELoss = u.derivYMSELoss;
		this.derivYWeightLoss = u.derivYWeightLoss;
		this.delta = u.delta;
		this.globalAlpha = u.globalAlpha;
	}

}
