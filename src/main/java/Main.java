/*
 * Copyright © 2016  David Williams
 *
 * This file is part of the codeeval-find-min project.
 *
 * codeeval-find-min is free software: you can redistribute it and/or modify it under the terms of the
 * Lesser GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * codeeval-find-min is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public
 * License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with codeeval-find-min.
 * If not, see <a href="http://www.gnu.org/licenses/">www.gnu.org/licenses/</a>.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * <p>The "Find Min" code challenge found at <a href="https://www.codeeval.com/open_challenges/85/">CodeEval</a>.</p>
 *
 * Some thoughts:
 * <ul>
 *     <li>I assumed that the goal is to demonstrate the implementation of the fastest algorithm for finding the result,
 *     not to demonstrate the fastest speed to read and process large files. Due to this, the implementation is
 *     performance constrained by disk IO and string tokenization.</li>
 *     <li>I assumed that user friendly error messages, comments, and test code are a non-goal, though I did provide a
 *     minimal set of comments.</li>
 *     <li>This solution runs in O(log(n^2)) time and O(n) space (really, O(2n) space which simplifies to O(n)).</li>
 * </ul>
 *
 * @author PineForest (see https://github.com/PineForest) 10/20/2015
 */
public class Main {
    // holds the array of n elements
    private static List<Integer> list;
    // holds the k items that proceed the current list index. the key is the integer and the value is how many of these are in the range of size k
    private static TreeMap<Integer, Integer> tree;

    private static void generateDataStructures(String[] values) {
        int n = Integer.valueOf(values[0]);
        int k = Integer.valueOf(values[1]);
        int a = Integer.valueOf(values[2]);
        int b = Integer.valueOf(values[3]);
        int c = Integer.valueOf(values[4]);
        int r = Integer.valueOf(values[5]);
        list = new ArrayList(n);
        tree = new TreeMap();
        list.add(a);
        tree.put(a, 1);
        int previous = a;
        int next;
        for (int i = 1; i < k; previous = next, ++i) {
            next = (b * previous + c) % r;
            treeIncrement(next);
            list.add(next);
        }
        //displayList();
        //displayTree();
    }

    private static int findNthM(String[] values) { // log(n - k) * log(n) === (log n)^2
        int n = Integer.valueOf(values[0]);
        int k = Integer.valueOf(values[1]);
        for (int i = k; i < n; ++i) { // log(n - k)
            Integer previous = -1;
            Integer next = tree.ceilingKey(0); // log n
            int currentM;
            for (; ; previous = next, next = tree.ceilingKey(previous + 1)) { // log k
                if (next == null || next - previous > 1) {
                    currentM = previous + 1;
                    break;
                }
            }
            // maintain the tree such that only the last k elements from the list are in it
            treeIncrement(currentM); // log n
            treeDecrement(list.get(i - k)); // log n
            // add to the end of the list the value k + i
            list.add(currentM); // log 1
        }
        return list.get(n - 1); // log 1
    }

    private static void treeIncrement(Integer key) { // log n
        Integer count = tree.get(key); // log n
        tree.put(key, count == null ? 1 : count + 1); // log n
    }

    // Note: key is assumed to be present in the tree - no error check performed
    private static void treeDecrement(Integer key) { // log n
        Integer count = tree.get(key); // log n
        if (count == 1) {
            tree.remove(key); // log n
        } else {
            tree.put(key, count - 1); // log n
        }
    }

    private static void displayList() {
        System.out.println(list.toString());
    }

    private static void displayTree() {
        System.out.println(tree.toString());
    }

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        BufferedReader buffer = new BufferedReader(new FileReader(file));
        String line;
        while ((line = buffer.readLine()) != null) {
            line = line.trim();
            String[] values = line.split(",");
            generateDataStructures(values);
            System.out.println(findNthM(values));
            //displayList();
            //System.out.println();
        }
    }
}
