/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.util;

import java.util.List;

public class Regression {

	private double R2;
	private double slope;
	private double offset;
	private double slope_stderr;
	private double offset_stderr;
	private double mean_y;
	
	public double getR2() {
		return R2;
	}
	
	public double getSlope() {
		return slope;
	}
	
	public double getOffset() {
		return offset;
	}
	
	public double getSlopeStdErr() {
		return slope_stderr;
	}
	
	public double getOffsetStdErr() {
		return offset_stderr;
	}
	
	public double getMeanY() {
		return mean_y;
	}
	
	public static Regression fromList(List<Float> list, float xFactor) {
		double[] x = new double[list.size()];
		double[] y = new double[list.size()];
		int i = 0;
		for (Float f : list) {
			x[i] = i * xFactor;
			y[i] = f;
			i++;
		}
		return new Regression(x, y, list.size());
	}
	
	public static Regression fromArray(float[] array, float xFactor) {
		double[] x = new double[array.length];
		double[] y = new double[array.length];
		int i = 0;
		for (Float f : array) {
			x[i] = i * xFactor;
			y[i] = f;
			i++;
		}
		return new Regression(x, y, array.length);
	}
	
	public Regression(double[] x, double[] y, int n) {
		//if (x.length != y.length || x.length == 0) {
		//	throw new IllegalArgumentException("the length of x and y must be equal and greater than 0");
		//}

		// first pass: read in data, compute xbar and ybar
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;

		for (int i = 0; i < n; i++) {
			sumx  += x[i];
			sumx2 += x[i] * x[i];
			sumy  += y[i];
		}
		
		mean_y = sumy / n;

		double xbar = sumx / n;
		double ybar = sumy / n;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		slope = xybar / xxbar;
		offset = ybar - slope * xbar;

		// analyze results
		int df = n - 2;
		double rss = 0.0;      // residual sum of squares
		double ssr = 0.0;      // regression sum of squares
		for (int i = 0; i < n; i++) {
			double fit = slope*x[i] + offset;
			rss += (fit - y[i]) * (fit - y[i]);
			ssr += (fit - ybar) * (fit - ybar);
		}
		R2    = ssr / yybar;
		double svar  = rss / df;
		double slope_svar = svar / xxbar;
		double offset_svar = svar/n + xbar*xbar*slope_svar;
		
		slope_stderr = Math.sqrt(slope_svar);
		
		//System.out.println("std error of slope = " + slope_stderr);
		//System.out.println("std error of offset = " + offset_stderr);
		
		offset_svar = svar * sumx2 / (n * xxbar);
		offset_stderr = Math.sqrt(offset_svar);
		//System.out.println("std error of beta_0 = " + offset_stderr);

		//System.out.println("SSTO = " + yybar);
		//System.out.println("SSE  = " + rss);
		//System.out.println("SSR  = " + ssr);
	}
	
	public static void main(String[] args) {
		float[] fa = new float[] {
		-0.03438535f,
		-0.001072304f,
		-0.025765238f,
		-0.06889491f,
		-0.12807816f,
		-0.08385677f,
		-0.06945282f,
		-0.05720186f,
		-0.0765444f,
		-0.07541519f,
		-0.06647564f,
		-0.06854767f,
		-0.064474806f,
		-0.075060524f,
		-0.097160205f,
		-0.06379209f,
		-0.051146973f,
		-0.07026017f,
		-0.15329772f,
		-0.5233211f};
		
		System.out.println(Regression.fromArray(fa, 0.1f).getSlope());
	}

}
