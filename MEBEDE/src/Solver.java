import java.util.ArrayList;
import java.util.Random;

public class Solver {
	
	int totOrders = 100;
	double[][] transitionTime = new double[totOrders][totOrders];
	ArrayList<Order> orders;
	Solution sol;
	Solution bestSolution;
	
	int bday = 19092000;
	Random ran = new Random(bday);

	void generateAssignmentInput() {
		
		generateTransitionTimeMatrix();
		generateOrders();
		
	}
	
	private void generateTransitionTimeMatrix() {

		for (int i = 0; i < totOrders; i++) {
			for (int j = 0; j< totOrders; j++) {
				double randTime = 10 + 20 * ran.nextDouble();
				randTime = Math.round(randTime * 100.0)/100.0;
				if (i==j) {
					randTime = 0;
				}
				transitionTime[i][j] = randTime;
			}
	
		}
	}
	
	private void generateOrders() {
		orders = new ArrayList<>();
		
		for (int i = 0 ; i < totOrders; i++){
			int qq = 100 + ran.nextInt(401);
			boolean drk = false; 
		
			if (ran.nextDouble() < 0.15){
				drk = true;
			}
			Order o = new Order(i + 1, qq , drk);
			orders.add(o);
		} 
	}
	
	/*
	 * Simple Algorithm
	 */
	public void simpleAlgorithm() {
		
		ArrayList<Order>[] machine = new ArrayList[5];
		
		for (int i = 0; i < 5; i++){
			machine[i] = new ArrayList<Order>();
		
		}
		
		int k = 0;
		for (int i=0; i < totOrders; i++) {
			machine[k].add(orders.get(i));
			k++;
			if(k==5) {
				k = 0;
			}
		}
		
		double[] timeOfMachine = findOperationTimeOfEachMachine(machine, transitionTime);
		System.out.println("Simple Solution: " + findMaxOperationTime(timeOfMachine) +"\n");				
	}
	
	/*
	 * Greedy Algorithm
	 */
	public void greedyAlgorithm() {
		
		ArrayList<Order>[] machine = new ArrayList[5];
		ArrayList<Order>[] machineDemo = new ArrayList[5];
		double[] timeOfMachine;
		int minPos;
		Order lastOrder;
		Order nextBestOrder;
		
		for (int i = 0; i < 5; i++){
			machine[i] = new ArrayList<Order>();
			machineDemo[i] = new ArrayList<Order>();
		}
		
		//add the first order to the first machine
		machine[0].add(orders.get(0));
		machineDemo[0].add(orders.get(0));
		orders.get(0).setPicked(true);
		
		for (int i = 1; i < totOrders; i++)	{
			
			timeOfMachine = findOperationTimeOfEachMachine(machine, transitionTime);
			minPos = findMinOperationTimePosition(timeOfMachine);
			lastOrder = findLastOrderOfMachine(machine[minPos]);
			nextBestOrder = findNextBestOrder(lastOrder, totOrders, orders, transitionTime);
			machine[minPos].add(nextBestOrder);
			
		}
		
		timeOfMachine = pp.findOperationTimeOfEachMachine(machine, transitionTime);
		System.out.println("Greedy Solution: " + pp.findMaxOperationTime(timeOfMachine) + "\n");
		pp.printOrdersByMachine(machine);
		System.out.println("\nMachine 1: " + timeOfMachine[0]);
		System.out.println("Machine 2: " + timeOfMachine[1]);
		System.out.println("Machine 3: " + timeOfMachine[2]);
		System.out.println("Machine 4: " + timeOfMachine[3]);
		System.out.println("Machine 5: " + timeOfMachine[4]);
	}
	
	
	
	
	
	/*
	 * Calculates the time that each machine is working
	 */
	public double[] findOperationTimeOfEachMachine(ArrayList<Order>[] machine, double[][] transitionTime) {
		double[] timeOfMachine = new double[5];
		
		for(int i=0; i<5; i++) {
			timeOfMachine[i] = 0;
		}
		
		for (int i=0; i < 5; i++) {
			
			if(machine[i].size() == 1) {
				timeOfMachine[i] = timeOfMachine[i] + machine[i].get(0).quantity * 6;
			} else {
			
				for (int j=0; j < machine[i].size() - 1; j++) {
					timeOfMachine[i] = timeOfMachine[i] + machine[i].get(j).quantity * 6 
							+ transitionTime[machine[i].get(j).ID - 1][machine[i].get(j + 1).ID - 1];
	
					//adds extra 15 minutes if the next color is different than the current
					if(machine[i].get(j).dark != machine[i].get(j + 1).dark) {
						timeOfMachine[i] += 15 * 60;
					}
				}
				
				if(machine[i].size() > 1) {
					timeOfMachine[i] = timeOfMachine[i] + machine[i].get(machine[i].size() - 1).quantity * 6; //adds the time of the last order of current machine.
				}
			}
		}
		
		return timeOfMachine;
	}
	
	/*
	 * Finds the max operation time of the machines
	 */
	public double findMaxOperationTime(double[] timeOfMachine) {
		double maxOpTime = timeOfMachine[0];
		
		for(int i = 1; i < timeOfMachine.length; i++ ) {
			if(timeOfMachine[i] > maxOpTime) {
				maxOpTime = timeOfMachine[i];
			}
		}
		
		return maxOpTime;
	}
	
	/*
	 * Finds the position(id/number) of the minimum operation time of the machines
	 */
	public int findMinOperationTimePosition(double[] timeOfMachine) {
		double minOpTime = timeOfMachine[0];
		int pos = 0;
		
		for(int i = 1; i < timeOfMachine.length; i++ ) {
			if(timeOfMachine[i] < minOpTime) {
				minOpTime = timeOfMachine[i];
				pos = i;
			}
		}
		
		return pos;
	}
		
	/*
	 * Find the last order of a specific machine
	 */
	public Order findLastOrderOfMachine(ArrayList<Order> minTimeMachine) {
		Order lastOrder;
		if(minTimeMachine.size() == 0) {
			lastOrder = null;
		} else {
			lastOrder = minTimeMachine.get(minTimeMachine.size() - 1);
		}
		return lastOrder;
	}

	/*
	 * Find the order with that will add the minimum operation time.
	 */
	public Order findNextBestOrder(Order lastOrder, int totOrders, ArrayList<Order> orders, double[][] transitionTime) {
		Order nextBestOrder = null;
		
		if(lastOrder == null) {
			nextBestOrder = findFirstUnpickedOrder(orders);
			nextBestOrder.setPicked(true);
			return nextBestOrder;
		}
		
		int lastOrderID = lastOrder.getID();
		
		Order nextOrder;
		int nextOrderID;
		double newAddedTime;
		double addedTime = 999999999;
		
		for (int i = 0; i < totOrders; i++) {
			nextOrder = orders.get(i);
			nextOrderID = nextOrder.getID();
			newAddedTime = findNewAddedTime(lastOrder, nextOrder, transitionTime);
			
			if(lastOrderID != nextOrderID && addedTime > newAddedTime && !nextOrder.isPicked()) {
				nextBestOrder = nextOrder;
				addedTime = newAddedTime;
			}
			
		}
		nextBestOrder.setPicked(true);
		
		return nextBestOrder;
	}
	
	/*
	 * Find the operation time that will be added if the nextOrder is added after the lastOrder.
	 * It does not take into account the time for the production of the nextOrder,
	 * because it does not help the algorithm to come up with a better solution.
	 */
	public double findNewAddedTime(Order lastOrder, Order nextOrder, double[][] transitionTime) {
		double newAddedtime = 0;
		
		if(lastOrder.isDark() != nextOrder.isDark()) {
			newAddedtime += 15 * 60;
		}
		
		newAddedtime += transitionTime[lastOrder.getID() - 1][nextOrder.getID() - 1];

		return newAddedtime;
	}
	
	/*
	 * Find the first order from the ArrayList orders that is not put into production
	 */
	public static Order findFirstUnpickedOrder(ArrayList<Order> orders) {
		Order order = null;
		
		for (int i = 0; i < orders.size(); i++) {

			if (orders.get(i).isPicked() == false) {
				order = orders.get(i);
				break;
			}
		}
		return order;
	}
	
	
	
	

}
