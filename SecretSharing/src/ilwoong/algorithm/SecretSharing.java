/**
 * The MIT License
 *
 * Copyright (c) 2020 Ilwoong Jeong (https://github.com/ilwoong)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ilwoong.algorithm;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SecretSharing {

	private BigInteger prime;
	private int numPieces;
	private int minPieces;
	private ArrayList<BigInteger> poly;

	public SecretSharing() {
		this(13, 3, 5);
	}

	public SecretSharing(int mersennePrimeIdx, int minPieces, int numPieces) {
		this.prime = MersennePrime.get(mersennePrimeIdx);
		this.minPieces = minPieces;
		this.numPieces = numPieces;
		this.poly = buildRandomPolynomial(minPieces - 1);
	}

	public String getName() {
		return "Shamir's secret sharing - (" + minPieces + ", " + numPieces + ") threshold scheme";
	}

	public ArrayList<BigInteger> split(BigInteger secret) {
		var tokens = new ArrayList<BigInteger>();

		for (int i = 1; i <= numPieces; ++i) {
			tokens.add(evaluatePolynomial(poly, i).add(secret).mod(prime));
		}

		return tokens;
	}

	private ArrayList<BigInteger> buildRandomPolynomial(int degree) {
		var rand = new SecureRandom();
		var poly = new ArrayList<BigInteger>();
		var temp = new byte[prime.toByteArray().length];

		for (int i = 0; i < degree; ++i) {
			rand.nextBytes(temp);
			var coef = new BigInteger(temp).mod(prime);
			poly.add(coef);
		}

		return poly;
	}

	private BigInteger evaluatePolynomial(ArrayList<BigInteger> poly, long x) {
		return evaluatePolynomial(poly, BigInteger.valueOf(x));
	}

	private BigInteger evaluatePolynomial(ArrayList<BigInteger> poly, BigInteger x) {
		var eval = BigInteger.ZERO;
		var term = BigInteger.ONE;

		for (int i = 0; i < poly.size(); ++i) {
			var coef = poly.get(i);
			term = term.multiply(x).mod(prime);
			eval = eval.add(coef.multiply(term).mod(prime));
		}

		return eval.mod(prime);
	}

	public BigInteger reconstruct(HashMap<BigInteger, BigInteger> points) {
		if (points.size() < minPieces) {
			throw new IllegalArgumentException("not enough pieces");
		}

		var secret = BigInteger.ZERO;

		var xs = points.keySet();

		for (var xj : xs) {
			var yj = points.get(xj);
			var term = evaluateInnerProduct(xs, xj);
			term = yj.multiply(term);
			secret = secret.add(term);
		}

		return secret.mod(prime);
	}

	private BigInteger evaluateInnerProduct(Set<BigInteger> xs, BigInteger xj) {
		var prod = BigInteger.ONE;

		for (var xm : xs) {
			if (xm.equals(xj)) {
				continue;
			}

			var term = xm.multiply(xm.subtract(xj).modInverse(prime));
			prod = prod.multiply(term).mod(prime);
		}

		return prod;
	}
}
