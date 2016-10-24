package iristk.util;

import java.util.Arrays;

public class IIRFilter {

	private double[] itaps;
	private double[] otaps;
	private double[] idata;
	private double[] odata;
	private int ipos;
	private int opos;
	
	public static IIRFilter newLowPassFilter() {
		return new IIRFilter(new double[] { 0.0021, 0.0042, 0.0021 }, new double[] { 1.0000, -1.8669, 0.8752 });
	}

	public IIRFilter(double[] B, double[] A) {
		this.itaps = B;
		this.otaps = A;
		this.idata = new double[this.itaps.length];
		this.odata = new double[this.otaps.length];
		Arrays.fill(idata, 0);
		Arrays.fill(odata, 0);

		this.ipos = this.itaps.length - 1;
		this.opos = this.otaps.length - 1;
	}

	public double flow(double x) {

		int j, k;
		double y;

		this.idata[this.ipos] = x;

		y = 0.0;

		if (this.itaps.length > 0) {

			k = this.ipos;

			for (j = 0; j < this.itaps.length; j++) {

				y += this.itaps[j] * this.idata[k];
				k = (k + 1) % (this.itaps.length);
			}

			this.ipos = (this.ipos == 0 ? this.itaps.length - 1 : this.ipos - 1);
		}

		if (this.otaps.length > 0) {

			k = this.opos;

			for (j = 1; j < this.otaps.length; j++) {

				y -= this.otaps[j] * this.odata[k];
				k = (k + 1) % (this.otaps.length);
			}

			this.opos = (this.opos == 0 ? this.otaps.length - 1 : this.opos - 1);
			y /= this.otaps[0];
			this.odata[this.opos] = y;
		}

		return y;
	}
}
