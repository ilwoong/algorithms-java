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

package ilwoong.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import ilwoong.algorithm.SecretSharing;

public class TestSecretSharing {
    public static void main(String args[]) {
        testSucceed();
        testFailed();
    }

    public static void testSucceed() {
        var ss = new SecretSharing();
        var secret = new BigInteger("3141592", 10);
        var pieces = ss.split(secret);

        System.out.println(ss.getName());
        System.out.println();

        printPieces(pieces);
        tryReconstruct(ss, pieces, 3, 2, 1);
    }

    public static void testFailed() {
        var ss = new SecretSharing();
        var secret = new BigInteger("3141592", 10);
        var pieces = ss.split(secret);

        System.out.println(ss.getName());
        System.out.println();

        printPieces(pieces);
        tryReconstruct(ss, pieces, 3, 1);
    }

    private static void tryReconstruct(SecretSharing ss, ArrayList<BigInteger> pieces, int... indices) {

        if (ss == null || pieces == null || indices == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        var points = choose(pieces, indices);

        System.out.println("Trying to reconstruct using the folling " + indices.length + " pieces");
        printPoints(points);

        try {
            var reconstructed = ss.reconstruct(points);

            System.out.println("Reconstructed secret: " + reconstructed);
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("Not enough pieces to reconstruct secret: " + indices.length);
        }
    }

    public static HashMap<BigInteger, BigInteger> choose(ArrayList<BigInteger> pieces, int... variables) {
        var points = new HashMap<BigInteger, BigInteger>();
        for (int i : variables) {
            var x = BigInteger.valueOf(i);
            var y = pieces.get(i - 1);
            points.put(x, y);
        }

        return points;
    }

    private static void printPieces(ArrayList<BigInteger> pieces) {
        System.out.println("Shared secret pieces");
        int x = 1;
        for (var piece : pieces) {
            System.out.println(x + ", " + piece.toString(16));
            x += 1;
        }
        System.out.println();
    }

    private static void printPoints(HashMap<BigInteger, BigInteger> points) {
        System.out.println("Points");
        var xs = points.keySet();
        for (var x : xs) {
            System.out.println(x + ", " + points.get(x).toString(16));
        }
        System.out.println();
    }
}
