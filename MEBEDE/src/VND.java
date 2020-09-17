import java.util.ArrayList;

public class VND {
	
	
	
	private static Solver solver = new Solver();
	private static Solution sol;
	
	public VND(Solution sol) {
		this.sol = sol;	
	}
	
	public void executeVND() {
		System.out.println("\n\n");
		ArrayList<ArrayList<Order>> newAssignedOrders;
		double newTime;
		int k = 0;
		
		while(k < 3) {
			newAssignedOrders = findBestNeighbour(k);
			double[] timeOfMachine = solver.findOperationTimeOfEachMachine(newAssignedOrders, solver.transitionTime);
			newTime = solver.findMaxOperationTime(timeOfMachine);
			
			System.out.println("new " + newTime);
			System.out.println("sol " + sol.time);
			System.out.println("sol " + solver.transitionTime[2][32]); //ta vgazei ola miden. lathos
			
			if (newTime < sol.time) {
				sol.time = newTime;
				sol.assignedOrders = newAssignedOrders;
				k = 0;
			} else {
				k++;
			}
		}
		
		System.out.println("VND: " + sol.time);
	}
	
	public static ArrayList<ArrayList<Order>> findBestNeighbour(int k) {
		ArrayList<ArrayList<Order>> newOrders;
		
		if(k==0) {
			newOrders = execute1_1();
		} else if(k==1) {
			newOrders = execute1_0();
		} else {
			newOrders = executeOurs();
		}
		
		return newOrders;
	}
	
	public static ArrayList<ArrayList<Order>> execute1_1() {
		
		ArrayList<ArrayList<Order>> newOrders = sol.assignedOrders;
		ArrayList<ArrayList<Order>> bestNewOrders = sol.assignedOrders;
		Order order1, order2;
		double bestTime = 999999999;
		
		for(int i = 0; i < newOrders.size() - 1; i++) {
			for(int j = 0; j < newOrders.get(i).size() - 1; j++) {
				
				order1 = newOrders.get(i).get(j);
				
				for(int k = 0; k < newOrders.size() - 1; k++) {
					for(int m = 0; m < newOrders.get(k).size() - 1; m++) {
						
						if(i != k && j != m) {
							
							order2 = newOrders.get(k).get(m);
							//newOrders upadte
							newOrders.get(k).set(m, order1);
							newOrders.get(i).set(j, order2);
							
							double[] timeOfMachine = solver.findOperationTimeOfEachMachine(newOrders, solver.transitionTime);
							double newTime = solver.findMaxOperationTime(timeOfMachine);
							
							
							if(newTime < bestTime) {
								System.out.println("best1 " + bestTime);
								bestTime = newTime;
								bestNewOrders = newOrders;
								System.out.println("best2 " + bestTime);
							}
							//newOrders = sol.assignedOrders;
						}
						
						
					}
				}
			}
		}
		
		return bestNewOrders;
	}
	
	public static ArrayList<ArrayList<Order>> execute1_0() {
		ArrayList<ArrayList<Order>> newOrders = sol.assignedOrders;
		return newOrders;
	}
	
	public static ArrayList<ArrayList<Order>> executeOurs() {
		ArrayList<ArrayList<Order>> newOrders = sol.assignedOrders;
		return newOrders;
	}
}
