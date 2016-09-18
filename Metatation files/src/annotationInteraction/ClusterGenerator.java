package annotationInteraction;

import java.util.ArrayList;
import java.util.List;

public class ClusterGenerator {
	
	private static Cluster.spaceMetric space_metric_type = Cluster.spaceMetric.NoDiff;

	private List<ClusterIteration> cluster_iterations;
	private int stop_at_iteration;
	
	public ClusterGenerator(List<PenStroke> pen_strokes_to_cluster, int viewer_resize_factor){
		
		cluster_iterations = new ArrayList<ClusterIteration>();
		List<Cluster> initial_clusters_preserved = initialize(pen_strokes_to_cluster, viewer_resize_factor);
		
		List<Cluster> initial_clusters = initialize(pen_strokes_to_cluster, viewer_resize_factor);
		List<Cluster> clusters_to_merge = new ArrayList<Cluster>();

		for(int i = 0 ; i < initial_clusters.size(); i++){
			
			clusters_to_merge.add(new Cluster(initial_clusters.get(i)));
			
		}
		
		//System.out.println("***************");
		//System.out.println("pen strokes at start: " + initial_clusters.size());
		
		// keep on merging the clusters till only one cluster is left in the end
		while(clusters_to_merge.size() > 1){

			/*System.out.println("before: ");
			
			for(int i = 0; i < clusters_to_merge.size(); i++){
				
				System.out.println("cluster: " + i + " pen strokes: " + clusters_to_merge.get(i).getPenStrokes().size());
				
			}*/
			
			ClusterIteration current_iteration = update_clusters(clusters_to_merge, Cluster.clusterMetric.Min, viewer_resize_factor);
			cluster_iterations.add(new ClusterIteration(current_iteration));
			
			//System.out.println("after: ");
			
			clusters_to_merge = new ArrayList<Cluster>();
			List<Cluster> updated_clusters = current_iteration.getClusters();
			
			for(int i = 0 ; i < updated_clusters.size(); i++){
				
				clusters_to_merge.add(new Cluster(updated_clusters.get(i)));
				//System.out.println("cluster: " + i + "pen strokes: " + updated_clusters.get(i).getPenStrokes().size());
				
			}
			
		}
		
		/*System.out.println("initial clusters: ");
		for(int i = 0; i < initial_clusters_preserved.size(); i++){
			
			System.out.println("cluster: " + i + " pen strokes: " + initial_clusters_preserved.get(i).getPenStrokes().size());
			
		}*/
	
		double max_consecutive_diff_ratio = Double.NEGATIVE_INFINITY;
		
		if(cluster_iterations.isEmpty()){
			
			cluster_iterations.add(new ClusterIteration(initial_clusters_preserved, Double.NaN));
			stop_at_iteration = 0;
			
		}
		else{
			
			if(cluster_iterations.size() < 3){
				
				cluster_iterations.add(0, new ClusterIteration(initial_clusters_preserved, Double.NaN));
				stop_at_iteration = 0;
				
			}
			else{
				
				for(int i = 1; i < cluster_iterations.size() - 1; i++){

					double diff_in_metric_from_i_plus_one_to_i = cluster_iterations.get(i + 1).getClusterMergeMetric() - cluster_iterations.get(i).getClusterMergeMetric();
					double diff_in_metric_from_i_to_i_minus_one = cluster_iterations.get(i).getClusterMergeMetric() - cluster_iterations.get(i - 1).getClusterMergeMetric();
					
					double consecutive_diff_ratio = (diff_in_metric_from_i_plus_one_to_i / diff_in_metric_from_i_to_i_minus_one) * diff_in_metric_from_i_plus_one_to_i;
						
					if(consecutive_diff_ratio > max_consecutive_diff_ratio){
							
						max_consecutive_diff_ratio = consecutive_diff_ratio;
						stop_at_iteration = i;
							
					}

				}
				
			}
			
		}
		
		//System.out.println(stop_at_iteration);
		
		/*System.out.println("******** \nTo worksheet: ");
		for(int i = 0; i < cluster_iterations.size(); i++){
			
			System.out.println("iteration: " + i);
			
			List<Cluster> clusters = cluster_iterations.get(i).getClusters();
			for(int j = 0; j < clusters.size(); j++){
				
				System.out.println("cluster: " + j + " pen strokes: " + clusters.get(j).getPenStrokes().size());
				
			}
			
		}*/

	}
	
	private List<Cluster> initialize(List<PenStroke> pen_strokes_to_cluster, int viewer_resize_factor){
	
		List<Cluster> initial_clusters = new ArrayList<Cluster>();
		
		for(int i = 0; i < pen_strokes_to_cluster.size(); i++){
			
			Cluster initial_cluster = new Cluster(pen_strokes_to_cluster.get(i));
			initial_cluster.setClusterBounds(viewer_resize_factor);
			
			initial_clusters.add(initial_cluster);
			
		}
		
		return initial_clusters;
		
	}
	
	private ClusterIteration update_clusters(List<Cluster> clusters_to_merge, Cluster.clusterMetric metric_type, int viewer_resize_factor){
		
		double metric = Double.POSITIVE_INFINITY;
		int[] indices_of_clusters_to_merge = new int[2];
		
		for(int i = 0; i < clusters_to_merge.size() - 1; i++){
			
			Cluster cluster_1 = clusters_to_merge.get(i);
			
			for(int j = i + 1; j < clusters_to_merge.size(); j++){
				
				Cluster cluster_2 = clusters_to_merge.get(j);
				
				double cluster_metric = cluster_1.getMetric(cluster_2, metric_type, space_metric_type);
				
				if(cluster_metric < metric){
					
					metric = cluster_metric;
					indices_of_clusters_to_merge[0] = i;
					indices_of_clusters_to_merge[1] = j;
					
				}
				
			}
			
		}
		
		List<Cluster> updated_clusters = new ArrayList<Cluster>();
		
		for(int i = 0; i < clusters_to_merge.size(); i++){
			
			if(i != indices_of_clusters_to_merge[1]){
				
				Cluster merged_cluster = new Cluster(clusters_to_merge.get(i));
				
				if(i == indices_of_clusters_to_merge[0]){
					
					merged_cluster.addPenStrokes(clusters_to_merge.get(indices_of_clusters_to_merge[1]).getPenStrokes());
					merged_cluster.setClusterBounds(viewer_resize_factor);
					
				}
				
				updated_clusters.add(merged_cluster);
				
			}
			
		}
		
		return new ClusterIteration(updated_clusters, metric);
		
	}
	
	public List<ClusterIteration> getClusterIterationsForPenStroke(){
		
		return cluster_iterations;
		
	}
	
	public int getStopIterationIndex(){
		
		return stop_at_iteration;
		
	}
	
	public ClusterIteration getClusterIterationAtStopIterationIndex(){
		
		return cluster_iterations.get(stop_at_iteration);
		
	}
	
}
