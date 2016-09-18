package annotationInteraction;

import java.util.ArrayList;
import java.util.List;

public class ClusterIteration {

	private List<Cluster> clusters_in_iteration;
	private double cluster_merge_metric;
	
	public ClusterIteration(List<Cluster> clusters, double merge_metric){
		
		clusters_in_iteration = clusters;
		cluster_merge_metric = merge_metric;
		
	}
	
	public ClusterIteration(ClusterIteration cluster_iteration_to_copy){
		
		cluster_merge_metric = cluster_iteration_to_copy.getClusterMergeMetric();
		
		clusters_in_iteration = new ArrayList<Cluster>();
		List<Cluster> clusters = cluster_iteration_to_copy.getClusters();
		for(int i = 0; i < clusters.size(); i++){
			
			clusters_in_iteration.add(new Cluster(clusters.get(i)));
			
		}
		
	}
	
	public List<Cluster> getClusters(){
		
		return clusters_in_iteration;
		
	}
	
	public double getClusterMergeMetric(){
		
		return cluster_merge_metric;
		
	}
	
}
