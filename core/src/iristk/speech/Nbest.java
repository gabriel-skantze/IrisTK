package iristk.speech;

import iristk.util.Record;

import java.util.*;

public class Nbest {

	private String selector;
	private Set<String> excludes = new HashSet<>();
	private List<Hyp> hypotheses = new ArrayList<>();

	public Nbest(List<?> nbest, String selector) {
		this.selector = selector;
		merge(nbest);
	}

	private void merge(List<?> nbest) {
		int score = 11;
		if (nbest != null) {
			NBEST:
			for (Object obj : nbest) {
				if (obj instanceof Record) {
					Record rhyp = (Record)obj;
					String shyp = rhyp.getString(selector);
					if (shyp != null) {
						score--;
						for (Hyp hyp : hypotheses) {
							if (hyp.text.equals(shyp)) {
								hyp.score += score;
								continue NBEST;
							}
						}
						hypotheses.add(new Hyp(shyp, score));
					}
				}
			}
		}
		Collections.sort(hypotheses);
		System.out.println(hypotheses);
	}
	
	public String getBest() {
		for (Hyp hyp : hypotheses) {
			if (!excludes.contains(hyp.text))
				return hyp.text;
		}
		if (hypotheses.size() > 0)
			return hypotheses.get(0).text;
		else
			return null;
	}

	public Nbest merge(List<?> nbest, String exclude) {
		excludes.add(exclude);
		merge(nbest);
		return this;
	}

	private static class Hyp implements Comparable {
		
		public Hyp(String text, int score) {
			this.text = text;
			this.score = score;
		}
		
		public String text;
		
		public Integer score;
		
		@Override
		public int compareTo(Object o) {
			return ((Hyp)o).score - score; 
		}
		
		@Override
		public String toString() {
			return text + "(" + score + ")";
		}
		
	}
	
}
