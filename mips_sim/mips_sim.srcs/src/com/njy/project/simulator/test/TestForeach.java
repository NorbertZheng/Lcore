package com.njy.project.simulator.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestForeach {
	public static void main(String[] args) {
		List<String> s = new ArrayList<String>();
		s.add("A");
		while (true) {
			// Iterator<String> sIterator = s.iterator();
			// while (sIterator.hasNext()) {
			// String string = (String) sIterator.next();
			// System.out.println(string);
			// }
			// System.gc();
			for (String string : s) {
				System.out.println(string);
			}
			// for (int i = 0; i < s.size(); i++) {
			// System.out.println(s.get(i));
			// }
		}
	}
}
