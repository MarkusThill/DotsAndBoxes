package nTupleTD;

import java.io.Serializable;

/**
 * Extends the {@link UpdateParams} class. The {@link UpdateParams} contains
 * general update-information. This class extends it by parameters that are
 * relevant for a single weight of the system.
 * 
 * @author Markus Thill
 * 
 */
public class WeightUpdateParams extends UpdateParams implements Serializable {
	private static final long serialVersionUID = -5517258907386407872L;

	/**
	 * Subset of LUTs that are addressed (e.g., if we have a set of LUTs for
	 * each player)
	 */
	public int sub;

	/**
	 * Addressed LUT
	 */
	public int lut;

	/**
	 * lut-index of weight i
	 */
	public int i;

	/**
	 * Input for weight i. TODO: x_i is already part of e_i, we have to see if it is
	 * really necessary to keep this attribute.
	 */
	public double x_i;

	/**
	 * Eligibility-trace for weight i.
	 */
	public double e_i;

	/**
	 * The current value of the weight, that is to be updated. Some online
	 * adaptable learning rate algorithms rely on the old value, therefore save
	 * the value in this attribute before an update is performed.
	 */
	public double w_i;

	WeightUpdateParams(UpdateParams u) {
		super(u);
	}
}
