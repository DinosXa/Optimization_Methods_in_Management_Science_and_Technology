public class Assignment {
	
	public static void main(String[] args) {
		
		Solver s = new Solver();
		
		// Generates Problem's Inputs
		s.generateAssignmentInput();
		
		// Simple Algorithm
		s.simpleAlgorithm();
		
		// Greedy Algorithm
		s.greedyAlgorithm();
		
		// Local Search Algorithm
		s.localSearch();
	}

}
