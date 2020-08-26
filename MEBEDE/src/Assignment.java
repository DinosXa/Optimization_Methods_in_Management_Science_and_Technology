public class Assignment {
	
	public static void main(String[] args) {
		
		Solver s = new Solver();
		
		s.generateAssignmentInput();
		
		s.simpleAlgorithm();
		
		s.greedyAlgorithm();
		
		s.localSearch();
	}

}
