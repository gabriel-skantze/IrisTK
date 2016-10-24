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
package iristk.app.chess;

import java.util.*;

public class RandomX extends Random {

	private static final long[] x = { 1410651636l, 3012776752l, 3497475623l,
			2892145026l, 1571949714l, 3253082284l, 3489895018l, 387949491l,
			2597396737l, 1981903553l, 3160251843l, 129444464l, 1851443344l,
			4156445905l, 224604922l, 1455067070l, 3953493484l, 1460937157l,
			2528362617l, 317430674l, 3229354360l, 117491133l, 832845075l,
			1961600170l, 1321557429l, 747750121l, 545747446l, 810476036l,
			503334515l, 4088144633l, 2824216555l, 3738252341l, 3493754131l,
			3672533954l, 29494241l, 1180928407l, 4213624418l, 33062851l,
			3221315737l, 1145213552l, 2957984897l, 4078668503l, 2262661702l,
			65478801l, 2527208841l, 1960622036l, 315685891l, 1196037864l,
			804614524l, 1421733266l, 2017105031l, 3882325900l, 810735053l,
			384606609l, 2393861397l };
	private long[] y = new long[55];
	private int j, k;

	// synchronized protected int next(int bits)
	// {
	// seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1); return (int)(seed
	// >>> (48 - bits));
	// }

	public RandomX() {
		int i;

		for (i = 0; i < 55; i++)
			y[i] = x[i];
		j = 24 - 1;
		k = 55 - 1;
	}

	/**
	 * A 32 bit random number generator. An implementation in C of the algorithm
	 * given by Knuth, the art of computer programming, vol. 2, pp. 26-27. We
	 * use e=32, so we have to evaluate y(n) = y(n - 24) + y(n - 55) mod 2^32,
	 * which is implicitly done by unsigned arithmetic.
	 */
	@Override
	synchronized protected int next(int bits) {
		long ul;

		ul = (y[k] += y[j]);
		if (--j < 0)
			j = 55 - 1;
		if (--k < 0)
			k = 55 - 1;
		return (int) (ul >>> (48 - bits));
	}

}
