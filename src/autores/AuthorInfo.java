package autores;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class used to store information about an author
 * 
 */

@SuppressWarnings("serial")
public class AuthorInfo implements Serializable {
	private String name;
	private int soloPublications;
	private int jointPublications;
	private TreeMap<String, Integer> coauthorsInfo; 

	public AuthorInfo(String name) {
		this.name = name;
		this.soloPublications = 0;
		this.jointPublications = 0;
		this.coauthorsInfo = new TreeMap<>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getTotalPublications() {
		return this.soloPublications + this.jointPublications;
	}
	
	public int getSoloPublications() {
		return this.soloPublications;
	}

	public int getJointPublications() {
		return this.jointPublications;
	}
	
	/**
	 * adds a publication to the author
	 */
	public void addPublication(Collection<String> coauthors) {
		Integer coauthorTotal;
		
		for (String coauthor : coauthors) {
			if (!coauthor.equals(this.name)) {
				coauthorTotal = this.coauthorsInfo.get(coauthor);
				if (coauthorTotal == null) this.coauthorsInfo.put(coauthor, 1);
				else this.coauthorsInfo.put(coauthor, coauthorTotal + 1);
			}
		}
		
		if(coauthors.size() == 1) this.soloPublications++;
		else this.jointPublications++;
	}
	

	/**
	 * Returns the coauthors who published the most with the author
	 * @param numberOfCoauthors
	 * @return coauthors who published the most with the author
	 */
	public Set<Tuple<String, Integer>> topCoauthors(int numberOfCoauthors) {
		TreeSet<Tuple<String, Integer>> ret = new TreeSet<Tuple<String, Integer>>(new AuthorPubsTupleComparator());
		Tuple<String, Integer> t;
		
		for (Map.Entry<String, Integer> entry : this.coauthorsInfo.entrySet()) {
			t = new Tuple<String, Integer>(entry.getKey(), entry.getValue());
			
			if (ret.size() < numberOfCoauthors) ret.add(t);
			else if (t.getSecond() > ret.first().getSecond()) {
				ret.pollFirst();
				ret.add(t);
			}
		}
		
		return ret;
	}
	
	/**
	 * Returns true if the author only published alone
	 * @return true if the author only published alone
	 */
	public boolean onlySolo() {
		return this.jointPublications == 0;
	}
	
	/**
	 * Returns true if the author never published alone
	 * @return true if the author never published alone
	 */
	public boolean neverSolo() {
		return this.soloPublications == 0;
	}
	
	/**
	 * Returns information about coauthors as a map, mapping the name to the number of publications with the coauthor
	 * @return information about coauthors as a map, mapping the name to the number of publications with the coauthor
	 */
	public Map<String, Integer> getCoauthorsInfo() {
		return this.coauthorsInfo;
	}
	
	/**
	 * Returns a set with all the coauthors
	 * @return set with all the coauthors
	 */
	public Set<String> getCoauthors() {
		return this.coauthorsInfo.keySet();
	}
	
	/** 
	 * Returns information about coauthors as a map, mapping a tuple with the both the author's and coauthor's names to the number of publications
	 * @return information about coauthors as a map, mapping a tuple with the both the author's and coauthor's names to the number of publications
	 */
	public Map<Tuple<String, String>, Integer> getAuthorPairs() {
		TreeMap<Tuple<String, String>, Integer> pairs = new TreeMap<>( new AuthorTupleComparator() );
		for( Map.Entry<String, Integer> coauthor : this.coauthorsInfo.entrySet() ) {
			int val = coauthor.getValue();
			String coauthorName = coauthor.getKey();
			if( this.name.compareTo(coauthorName) < 0 )
				pairs.put(new Tuple<String, String>(this.name, coauthorName), val);
			else
				pairs.put(new Tuple<String, String>(coauthorName, this.name), val);
		}
		
		return pairs;
	}
	
	/**
	 * Returns total number of coauthors
	 * @return total number of coauthors
	 */
	public int totalCoauthors() {
		return this.coauthorsInfo.size();
	}
	
	/**
	 * Returns a Tuple with a Set containing the coauthors with whom the author published and the total publications with them
	 * @return Tuple with a Set containing the coauthors with whom the author published and the total publications with them
	 */
	public Tuple<Set<String>, Integer> partnershipInfo() {
		Tuple<Set<String>, Integer> t = new Tuple<Set<String>, Integer>(new HashSet<String>(), 0); 
		for (Map.Entry<String, Integer> entry : this.coauthorsInfo.entrySet()) {
			t.getFirst().add(entry.getKey());
			t.setSecond(t.getSecond() + entry.getValue());
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ");
		sb.append(name);
		sb.append("\nSolo publications: ");
		sb.append(this.soloPublications);
		sb.append("\nJoint publications: ");
		sb.append(this.jointPublications);
		for(Map.Entry<String, Integer> coauthor : this.coauthorsInfo.entrySet()) {
			sb.append("\n\t: ");
			sb.append(coauthor.getKey());
			sb.append("- ");
			sb.append(coauthor.getValue());
		}
		
		return sb.toString();
	}
	
	
}