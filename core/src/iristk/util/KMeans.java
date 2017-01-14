package iristk.util;

/* 
 * KMeans.java ; Cluster.java ; Point.java
 *
 * Solution implemented by DataOnFocus
 * www.dataonfocus.com
 * 2015
 *
*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KMeans {

    private List<Point> points;
    private List<Cluster> clusters;
	private int dim;
    
    public KMeans(int dim) {
    	this.dim = dim;
    	this.points = new ArrayList<>();
    	this.clusters = new ArrayList<>();    	
    }

    public void addPoint(Point p) {
    	if (p.getCoords().length != dim)
    		throw new IllegalArgumentException("Wrong dimensionality of point");
    	points.add(p);
    }

	public List<Point> getPoints() {
		return points;
	}
    
	//The process to calculate the K Means, with iterating method.
    public List<Point> calculate(int numClusters) {
    	clusters.clear();
    	double[] min = new double[dim];
    	Arrays.fill(min, Double.MAX_VALUE);
    	double[] max = new double[dim];
    	Arrays.fill(max, Double.MIN_VALUE);
    	for (Point p : points) {
    		for (int i = 0; i < dim; i++) {
    			min[i] = Math.min(min[i], p.getCoords()[i]);
    			max[i] = Math.max(max[i], p.getCoords()[i]);
    		}
    	}
    	for (int i = 0; i < numClusters; i++) {
    		Cluster cluster = new Cluster(Point.createRandomPoint(dim, min, max));
    		clusters.add(cluster);
    	}
    	
        boolean finish = false;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
        	//Clear cluster state
        	clearClusters();
        	
        	List<Point> lastCentroids = getCentroids();
        	
        	//Assign points to the closer cluster
        	assignCluster();
            
            //Calculate new centroids.
        	calculateCentroids();
        	
        	List<Point> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += Point.distance(lastCentroids.get(i),currentCentroids.get(i));
        	}
        	        	
        	if(distance == 0) {
        		finish = true;
        	}
        }
        return getCentroids();
    }
    
    private void clearClusters() {
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    private List<Point> getCentroids() {
    	List<Point> centroids = new ArrayList<>(clusters.size());
    	for(Cluster cluster : clusters) {
    		Point aux = cluster.getCentroid();
    		Point point = new Point(Arrays.copyOf(aux.getCoords(), aux.getCoords().length));
    		centroids.add(point);
    	}
    	return centroids;
    }
    
    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max; 
        int cluster = 0;                 
        double distance = 0.0; 
        
        for(Point point : points) {
        	min = max;
            for(int i = 0; i < clusters.size(); i++) {
            	Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }
    
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            List<Point> list = cluster.getPoints();
            int n_points = list.size();
            
            Point centroid = cluster.getCentroid();
            if(n_points > 0) {
            	for (int i = 0; i < centroid.getCoords().length; i++) {
            		double sum = 0;
            		 for(Point point : list) {
            			 sum += point.getCoords()[i];
                     }
                     centroid.getCoords()[i] = sum / n_points;
            	}
            }
        }
    }
    
    private static class Cluster {
    	
    	public List<Point> points;
    	public Point centroid;
    	
    	//Creates a new Cluster
    	public Cluster(Point centroid) {
    		this.points = new ArrayList<>();
    		this.centroid = centroid;
    	}

    	public List<Point> getPoints() {
    		return points;
    	}
    	
    	public void addPoint(Point point) {
    		points.add(point);
    	}

    	public Point getCentroid() {
    		return centroid;
    	}
    	
    	public void clear() {
    		points.clear();
    	}

    }
    
    public static class Point {

        private double[] coords;
        private int cluster_number = 0;

        public Point(double... coords) {
        	this.coords = coords;
        }
        
        public void setCluster(int n) {
            this.cluster_number = n;
        }
        
        public int getCluster() {
            return this.cluster_number;
        }
        
        public double[] getCoords() {
        	return coords;
        }
        
        //Calculates the distance between two points.
        protected static double distance(Point p, Point centroid) {
        	double sum = 0;
        	for (int i = 0; i < p.getCoords().length; i++) {
        		sum += Math.pow(centroid.getCoords()[i] - p.getCoords()[i], 2);
        	}
        	return Math.sqrt(sum);
        }
        
        //Creates random point
        protected static Point createRandomPoint(int dim, double[] min, double[] max) {
        	Random r = new Random();
        	double[] c = new double[dim];
        	for (int i = 0; i < dim; i++) {
        		c[i] =  min[i] + (max[i] - min[i]) * r.nextDouble();
        	}
        	return new Point(c);
        }
        
        @Override
		public String toString() {
        	StringBuilder sb = new StringBuilder();
        	for (int i = 0; i < coords.length; i++) {
        		if (sb.length() > 0)
        			sb.append(",");
        		sb.append(coords[i]);
        	}
        	return "(" + sb.toString() + ")";
        }

    }

    
}
