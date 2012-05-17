import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * @author Joost Rijneveld
 */

public class KMeans {
	
	public static final String infile = "dataforkmeans.csv";
	public static final String outfile = "dataforkoutput.csv";
	
	public int K = 3;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FileReader ifile = new FileReader(infile);
		Scanner scanner = new Scanner(ifile);
		ArrayList<Record> dataset = new ArrayList<Record>();
		while (scanner.hasNext()) {
			ArrayList<Double> attributes = new ArrayList<Double>();
			String[] recarray = scanner.next().split(",");
			for (String a : recarray) {
				attributes.add(Double.parseDouble(a));
			}
			dataset.add(new Record(attributes));
		}
		
		(new KMeans()).cluster(dataset);
		
		FileWriter ofile = new FileWriter(outfile);
		for (int i = 0; i < dataset.size(); i++) {
			StringBuilder str = new StringBuilder();
			for (int j = 0; j < dataset.get(i).attributes.size(); j++) {
				str.append(dataset.get(i).attributes.get(j) + ",");
			}
			ofile.write(str + "" + dataset.get(i).cluster + "\n");
		}
		ofile.close();
		System.out.println("Finished clustering, outputted to "+outfile);
	}
	
	public void cluster(ArrayList<Record> data) {
		if (data.size() < K) {
			K = data.size();
		}
		ArrayList<Record> centroids = new ArrayList<Record>(data); //shallow copy; no space overhead
		ArrayList<Record> oldcentroids = new ArrayList<Record>(centroids);
		Collections.shuffle(centroids);
		centroids.subList(K, centroids.size()).clear();
		for (int i = 0; i < centroids.size(); i++) {
			centroids.get(i).cluster = i;
		}
		do {
			oldcentroids = centroids;
			for (Record r : data) {
				double mindistSq = Double.MAX_VALUE;
				for (Record c : centroids) {
					double dist = c.distanceSq(r);
					if (dist < mindistSq) {
						mindistSq = dist;
						r.cluster = c.cluster;
					}
				}
			}
			centroids = new ArrayList<Record>();
			for (int i = 0; i < oldcentroids.size(); i++) {
				ArrayList<Record> cluster = new ArrayList<Record>();
				for (Record r : data) {
					if (r.cluster == i) {
						cluster.add(r);
					}
				}
				Record centroid = attributeAvg(cluster);
				if (centroid != null)
					centroids.add(centroid);
			}
		} while (!equalsets(centroids,oldcentroids));
	}
	
	private Record attributeAvg(ArrayList<Record> cluster) {
		if (cluster.size() == 0)
			return null;
		ArrayList<Double> attributes = new ArrayList<Double>();
		for (int i = 0 ; i < cluster.get(0).attributes.size(); i++) {
			Double sum = .0;
			for (Record r : cluster) {
				sum += r.attributes.get(i);
			}
			attributes.add(sum / cluster.size());
		}
		return new Record(attributes, cluster.get(0).cluster);
	}

	private <T> boolean equalsets(ArrayList<T> centroids, ArrayList<T> oldcentroids) {
		if (centroids.size() != oldcentroids.size())
			return false;
		for (T t : centroids) {
			if (!oldcentroids.contains(t))
				return false;
		}
		return true;
	}

	public static class Record {
		private ArrayList<Double> attributes = new ArrayList<Double>();
		private Integer cluster; //Integer instead of int, so null is possible
		
		public Record(ArrayList<Double> attributes) {
			this(attributes, null);
		}
		
		public double distanceSq(Record r) {
			double dist = 0;
			for (int i = 0; i < attributes.size(); i++) {
				dist += Math.pow(attributes.get(i) - r.attributes.get(i),2);
			}
			return dist;
		}

		public Record(ArrayList<Double> attributes, Integer cluster) {
			this.attributes = attributes;
			this.cluster = cluster;
		}
		
		@Override
		public boolean equals(Object r) {
			if (r instanceof Record) {
				Record rec = (Record) r;
				return rec.cluster.equals(cluster) && rec.attributes.equals(attributes);
			}
			return false;			
		}
	}

}
