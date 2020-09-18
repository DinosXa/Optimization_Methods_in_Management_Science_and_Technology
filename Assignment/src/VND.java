import java.util.ArrayList;

public class VND {
	
	
	
	private static Solver solver = new Solver();
	private static Solution sol;
	private static double[][] tt;
	

	public VND(Solution sol, double[][] tt) {
		this.sol = sol;	
		this.tt = tt;
	}

	
	
	public ArrayList<ArrayList<Order>> execute1_1() {
		ArrayList<ArrayList<Order>> initialOrder = sol.assignedOrders;
		ArrayList<ArrayList<Order>> newOrders = sol.assignedOrders;
		ArrayList<ArrayList<Order>> bestNewOrders = sol.assignedOrders;
		Order order1, order2;
		double bestTime = 999999999;
		int v1=0,v2=0,v3=0,v4=0;
		
		for(int i = 0; i < initialOrder.size(); i++) {
			for(int j = 0; j < initialOrder.get(i).size(); j++) {
				
				order1 = initialOrder.get(i).get(j);
				
				for(int k = 0; k < initialOrder.size(); k++) {
					for(int m = 0; m < initialOrder.get(k).size(); m++) {
						
						if(i != k && j != m) {
							order2 = newOrders.get(k).get(m);
							//Change order1 with order2
							newOrders.get(k).set(m, order1);
							newOrders.get(i).set(j, order2);

							double[] timeOfMachine = solver.findOperationTimeOfEachMachine(newOrders, tt);
							double newTime = solver.findMaxOperationTime(timeOfMachine);
														
							if(newTime < bestTime) {						
								/*
								 * Storing values of the must-changed orders
								 * 
								 * v1 and v2 are order1
								 * v3 and v4 are order2
								 */
								v1=i;v2=j;v3=k;v4=m;
								bestTime = newTime;
								bestNewOrders = newOrders;
								
							}
							//Set orders to their initial position, before exchange
							order2 = newOrders.get(i).get(j);
							newOrders.get(k).set(m, order2);
							newOrders.get(i).set(j, order1);
						}
						
						
					}
				}
			}
		}
		
//		System.out.println(v1+" "+v2+" "+v3+" "+v4);
		/*
		 * Exchanging orders, by using the values/indexes which were included, in this best solution.
		 */
		order1 = newOrders.get(v1).get(v2);
		order2 = newOrders.get(v3).get(v4);
		newOrders.get(v3).set(v4, order1);
		newOrders.get(v1).set(v2, order2);
		
		double[] timeOfMachine = solver.findOperationTimeOfEachMachine(bestNewOrders, tt);
		bestTime = solver.findMaxOperationTime(timeOfMachine);
		bestNewOrders = newOrders;

//		System.out.println(bestTime);
//		System.out.println("order1: " + order1.ID + " order 2: " + order2.ID + "\nOrders:");
//		solver.printOrdersByMachine(bestNewOrders);

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
	

	/*
	 * A method to check how many orders are same in the Array.
	 * 
	 * If count=100, then every order is different.
	 * If notsame=9900, then every order is different (10000 - 100 from which 100 are the same)
	 */
	public void areSame(ArrayList<ArrayList<Order>> initialOrder) {
		int count=0,all=0, notsame=0;
		for(int i = 0; i < initialOrder.size(); i++) {
			for(int j = 0; j < initialOrder.get(i).size(); j++) {
				int ord = initialOrder.get(i).get(j).ID;
				
				for(int k = 0; k < initialOrder.size(); k++) {
					for(int m = 0; m < initialOrder.get(k).size(); m++) {
						all++;
						if (ord == initialOrder.get(k).get(m).ID){
							count++;
						}
						
						if(ord != initialOrder.get(k).get(m).ID) {
							notsame++;
						}
					}
				}
				
			}
		}
		System.out.println("SAME ARE: "+ count+ " AND ALL ARE: " + all + " AND NOT SAME: " + notsame);
	}

}
