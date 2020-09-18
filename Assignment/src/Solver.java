import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Solver {

	int totOrders = 100;
	double[][] transitionTime = new double[totOrders][totOrders];
	ArrayList<Order> orders;
	Solution sol_simple;
	Solution sol_greedy;
	Solution sol_local;
	Solution sol_local_vnd;

	int bday = 19092000;
	Random ran = new Random(bday);

	/*
	 * Generates Problem's Inputs
	 */
	void generateAssignmentInput() {
		generateTransitionTimeMatrix();
		generateOrders();
	}

	private void generateTransitionTimeMatrix() {
		for (int i = 0; i < totOrders; i++) {
			for (int j = 0; j< totOrders; j++) {
				double randTime = 10 + 20 * ran.nextDouble();
				randTime = Math.round(randTime * 100.0) / 100.0;
				if (i == j) {
					randTime = 0;
				}
				transitionTime[i][j] = randTime;
			}
		}
	}

	private void generateOrders() {
		
		orders = new ArrayList<>();
		
		for (int i = 0 ; i < totOrders; i++){
			Order o = new Order();
			
			o.quantity = 100 + ran.nextInt(401);
			o.dark = false;
			if (ran.nextDouble() < 0.15){
				o.dark = true;
			}
			o.ID = i + 1;
			orders.add(o);
		}
	}

	/*
	 * Simple Algorithm
	 */
	public void simpleAlgorithm() {
		
		sol_simple = new Solution();

		int k = 0;
		for (int i = 0; i < totOrders; i++) {
			sol_simple.assignedOrders.get(k).add(orders.get(i));
			k++;
			if(k == sol_simple.assignedOrders.size()) {
				k = 0;
			}
		}

		double[] timeOfMachine = findOperationTimeOfEachMachine(sol_simple.assignedOrders, transitionTime);
		sol_simple.time = findMaxOperationTime(timeOfMachine);
		System.out.println("Simple Solution: " + sol_simple.time +"\n");
		printOrdersByMachine(sol_simple.assignedOrders);
	}

	/*
	 * Greedy Algorithm
	 */
	public void greedyAlgorithm() {
		
		sol_greedy = new Solution();
		double[] timeOfMachine;
		int minPos;
		Order lastOrder;
		Order nextBestOrder;

		// adding the first order to the first machine
		sol_greedy.assignedOrders.get(0).add(orders.get(0));
		orders.get(0).isPicked = true;

		for (int i = 1; i < totOrders; i++)	{
			timeOfMachine = findOperationTimeOfEachMachine(sol_greedy.assignedOrders, transitionTime);
			minPos = findMinOperationTimePosition(timeOfMachine);
			lastOrder = findLastOrderOfMachine(sol_greedy.assignedOrders.get(minPos));
			nextBestOrder = findNextBestOrder(lastOrder, totOrders, orders, transitionTime);
			// adding the order which will increase the operation time the least after the last order of the machine with the minimum operation time
			sol_greedy.assignedOrders.get(minPos).add(nextBestOrder);
		}

		timeOfMachine = findOperationTimeOfEachMachine(sol_greedy.assignedOrders, transitionTime);
		sol_greedy.time = findMaxOperationTime(timeOfMachine);
		System.out.println("\n\nGreedy Solution: " + sol_greedy.time + "\n");
		printOrdersByMachine(sol_greedy.assignedOrders);
	}

	/*
	 * Local Search
	*/
	void localSearch() {
		sol_local = sol_greedy;
		
        RepositioningOrderMove repositioning = new RepositioningOrderMove();
        // repeating the algorithm 3 times
        for (int i = 0; i < 3; i++) {
            repositioning.Initialize();
        	findBestMove(repositioning, orders, transitionTime);
            // checking if we have reached a local minimum
            if (repositioning.moveTime >= sol_local.time) {
            	System.out.println("\nThe solution can not be improved any further.");
            	break;
            }
            
           	applyMove(repositioning);
            
            double[] timeOfMachine = findOperationTimeOfEachMachine(sol_local.assignedOrders, transitionTime);
            sol_local.time = findMaxOperationTime(timeOfMachine);
            System.out.println("\n\nLocal Search Solution: " + sol_local.time + "\n");
            printOrdersByMachine(sol_local.assignedOrders);
		}
	}
	
	/*
	 * Variable Neighborhood Descent
	 */
	public void vnd() {
		System.out.println("\n\nSTARTING VND\n");
		
		ArrayList<ArrayList<Order>> newAssignedOrders;
		sol_local_vnd = sol_greedy;
		double newTime;
		
		int k = 0;
		while(k < 3) {
			newAssignedOrders = findBestNeighbour(k);
			double[] timeOfMachine = findOperationTimeOfEachMachine(newAssignedOrders, transitionTime);
			newTime = findMaxOperationTime(timeOfMachine);
						
			if (newTime < sol_local_vnd.time) {
				sol_local_vnd.time = newTime;
				sol_local_vnd.assignedOrders = newAssignedOrders;
				
				if(k == 0) {
					System.out.println("1-1 move found new solution: " + newTime);
				} else if(k == 1) {
					System.out.println("1-0 move found new solution: " + newTime);
				} else {
					System.out.println("Improved version of the 1-0 move found new solution: " + newTime);
				}
				
				k = 0;
			} else {
				k++;
			}
		}
		
		System.out.println("\nBest VND Time: " + sol_local_vnd.time + "\n");
		printOrdersByMachine(sol_local_vnd.assignedOrders);
	}
	
	/*
	 * Calculates the time that each machine is operating
	 */
	public double[] findOperationTimeOfEachMachine(ArrayList<ArrayList<Order>> machine, double[][] transitionTime) {
		double[] timeOfMachine = new double[5];
		Arrays.fill(timeOfMachine, 0);

		for (int i = 0; i < machine.size(); i++) {

			if(machine.get(i).size() == 1) {
				timeOfMachine[i] += machine.get(i).get(0).quantity * 6;
			} else {

				for (int j = 0; j < machine.get(i).size() - 1; j++) {
					timeOfMachine[i] += machine.get(i).get(j).quantity * 6
							+ transitionTime[machine.get(i).get(j).ID - 1][machine.get(i).get(j + 1).ID - 1];

					// adding 15 extra minutes if the next color is of a different darkness
					if(machine.get(i).get(j).dark != machine.get(i).get(j + 1).dark) {
						timeOfMachine[i] += 15 * 60;
					}
				}

				if(machine.get(i).size() > 1) {
					//adding the time of the last order of current machine
					timeOfMachine[i] += machine.get(i).get(machine.get(i).size() - 1).quantity * 6;
				}
			}
		}

		return timeOfMachine;
	}

	/*
	 * Finds the max operation time between all machines
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
	 * Finds the position(id/number) of the machine with the minimum operation time between all machines
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
	 * Finds the position(id/number) of the machine with the maximum operation time between all machines
	 */
	public int findMaxOperationTimePosition(double[] timeOfMachine) {
		double maxOpTime = timeOfMachine[0];
		int pos = 0;

		for(int i = 1; i < timeOfMachine.length; i++ ) {
			if(timeOfMachine[i] > maxOpTime) {
				maxOpTime = timeOfMachine[i];
				pos = i;
			}
		}

		return pos;
	}
	
	/*
	 * Finds the position of the largest order within the machine with the maximum operation time
	 */
	public int findMaxOrderPosition(int i, double[][] transitionTime) {
		double maxTimeNeeded = findTimeNeeded(i, 0, transitionTime, sol_local.assignedOrders.get(i).get(0));
		int pos = 0;

		for(int j = 1; j < sol_local.assignedOrders.get(i).size(); j++ ) {
			if(findTimeNeeded(i, j, transitionTime, sol_local.assignedOrders.get(i).get(j)) > maxTimeNeeded) {
				maxTimeNeeded = findTimeNeeded(i, j, transitionTime, sol_local.assignedOrders.get(i).get(j));
				pos = j;
			}
		}

		return pos;
	}

	/*
	 * Finds the last order of a specific machine
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
	 * Finds the order that will add the minimum operation time
	 */
	public Order findNextBestOrder(Order lastOrder, int totOrders, ArrayList<Order> orders, double[][] transitionTime) {
		Order nextBestOrder = null;

		if(lastOrder == null) {
			nextBestOrder = findFirstUnpickedOrder(orders);
		} else {
			int lastOrderID = lastOrder.ID;

			Order nextOrder;
			int nextOrderID;
			double newAddedTime;
			double addedTime = 999999999;

			for (int i = 0; i < totOrders; i++) {
				nextOrder = orders.get(i);
				nextOrderID = nextOrder.ID;
				newAddedTime = findNewAddedTime(lastOrder, nextOrder, transitionTime);

				if(lastOrderID != nextOrderID && addedTime > newAddedTime && !nextOrder.isPicked) {
					nextBestOrder = nextOrder;
					addedTime = newAddedTime;
				}

			}
		}
		nextBestOrder.isPicked = true;
		
		return nextBestOrder;
	}

	/*
	 * Finds the operation time that will be added if the nextOrder is added after the lastOrder.
	 * It does not take into account the time for the production of the nextOrder,
	 * because it does not help the algorithm to come up with a better solution.
	 */
	public double findNewAddedTime(Order lastOrder, Order nextOrder, double[][] transitionTime) {
		double newAddedtime = 0;

		if(lastOrder.dark != nextOrder.dark) {
			newAddedtime += 15 * 60;
		}

		newAddedtime += transitionTime[lastOrder.ID - 1][nextOrder.ID - 1];

		return newAddedtime;
	}

	/*
	 * Finds the first order from the ArrayList orders that is not assigned to a machine
	 */
	public static Order findFirstUnpickedOrder(ArrayList<Order> orders) {
		Order order = null;

		for (int i = 0; i < orders.size(); i++) {
			if (orders.get(i).isPicked == false) {
				order = orders.get(i);
				break;
			}
		}
		return order;
	}

	/*
	 * Prints the orders that exists in each machine
	 */
	public void printOrdersByMachine(ArrayList<ArrayList<Order>> machine) {
		int i = 1;
		for(ArrayList<Order> ma : machine) {
			System.out.print("Machine " + i + ": ");
			for(Order or : ma) {
				System.out.print(or.ID + " ");
			}
			System.out.println();
			i++;
		}
		System.out.print("\n");
	}

	/*
	 * Finds the best move to be made
	 */
	public void findBestMove(RepositioningOrderMove repositioning, ArrayList<Order> orders, double[][] transitionTime) {
		double[] timeOfMachine = findOperationTimeOfEachMachine(sol_local.assignedOrders, transitionTime);
		
		int[] machineIndex1 = new int[2];
	    int machineIndex2;
		int k = findMaxOperationTimePosition(timeOfMachine);
		int l = findMinOperationTimePosition(timeOfMachine);
		machineIndex1[0] = k;
		for (int m = 0; m < sol_local.assignedOrders.get(k).size(); m++) {
			machineIndex1[1] = m;
			// checking if the last order within the machine with the maximum time is of the same darkness as the last order within the machine with the minimum time
			if (sol_local.assignedOrders.get(machineIndex1[0]).get(machineIndex1[1]).dark == 
					(sol_local.assignedOrders.get(l).get(sol_local.assignedOrders.get(l).size()-1)).dark) {
				machineIndex2 = l;
				// finding the machine with the minimum operation time among those that have an order of the same darkness assigned to their last position	
			} else {
				double pickedMachineTime = findMaxOperationTime(timeOfMachine);
				int pos = k;
				for (int i = 0; i < sol_local.assignedOrders.size(); i++) {
					if (i != machineIndex1[0] && i != m) {
						if (sol_local.assignedOrders.get(machineIndex1[0]).get(machineIndex1[1]).dark == 
								(sol_local.assignedOrders.get(i).get(sol_local.assignedOrders.get(i).size()-1)).dark && 
								timeOfMachine[i] < pickedMachineTime) {
							machineIndex2 = k;
							pickedMachineTime = timeOfMachine[i];
							pos = i;
						}
					}
				}
				machineIndex2 = pos;
			}
			
			timeOfMachine[machineIndex1[0]] -=  findTimeNeeded(machineIndex1[0], machineIndex1[1], transitionTime, sol_local.assignedOrders.get(machineIndex1[0]).get(machineIndex1[0]));
    		timeOfMachine[machineIndex2] += findTimeNeeded(machineIndex2, sol_local.assignedOrders.get(machineIndex2).size() - 1, transitionTime, sol_local.assignedOrders.get(machineIndex1[0]).get(machineIndex1[0]));
    		
			double moveTime = findMaxOperationTime(timeOfMachine);
			if (moveTime < repositioning.moveTime) {
				repositioning.moveTime = moveTime;
				repositioning.machineIndex1[0] = machineIndex1[0];
				repositioning.machineIndex1[1] = machineIndex1[1];
				repositioning.machineIndex2 = machineIndex2;
			}
		}
		
	}

	/*
	 * Finds the time an order adds or removes to/from the operation time of the machine it is assigned to if it is added or removed accordingly
	 */
	public double findTimeNeeded(int i, int j, double[][] transitionTime, Order ord ) {
		double neededTime = 0;

		if (j == 0) {
			if(ord.dark != sol_local.assignedOrders.get(i).get(j+1).dark) {
				neededTime += 15 * 60;
			}
			neededTime += transitionTime[ord.ID - 1][sol_local.assignedOrders.get(i).get(j+1).ID - 1];
		} else if (j == sol_local.assignedOrders.get(i).size() - 1) {
			if(ord.dark != sol_local.assignedOrders.get(i).get(j-1).dark) {
				neededTime += 15 * 60;
			}
			neededTime += transitionTime[sol_local.assignedOrders.get(i).get(j-1).ID - 1][ord.ID - 1];
		} else {
			if(ord.dark != sol_local.assignedOrders.get(i).get(j+1).dark) {
				neededTime += 15 * 60;
			}
			if(ord.dark != sol_local.assignedOrders.get(i).get(j-1).dark) {
				neededTime += 15 * 60;
			}
			neededTime += transitionTime[ord.ID - 1][sol_local.assignedOrders.get(i).get(j+1).ID - 1];
			neededTime += transitionTime[sol_local.assignedOrders.get(i).get(j-1).ID - 1][ord.ID - 1];
		}
		neededTime += ord.quantity * 6;
		
		return neededTime;
	}

	/*
	 * Swaps the orders found at FindBestMove
	 */
	private void applyMove(RepositioningOrderMove repositioning) {

		Order s = sol_local.assignedOrders.get(repositioning.machineIndex1[0]).get(repositioning.machineIndex1[1]);
        	
        sol_local.assignedOrders.get(repositioning.machineIndex1[0]).remove(repositioning.machineIndex1[1]);
        sol_local.assignedOrders.get(repositioning.machineIndex2).add(s);

        double[] timeOfMachine = findOperationTimeOfEachMachine(sol_local.assignedOrders, transitionTime);
    	sol_local.time = findMaxOperationTime(timeOfMachine);
    	
    }
	
	/*
	 * Call the right neighbor depending on the "k"
	 */
	public ArrayList<ArrayList<Order>> findBestNeighbour(int k) {
		
		ArrayList<ArrayList<Order>> newOrders;
		
		if(k == 0) {
			System.out.println("Executing 1-1 move");
			newOrders = execute1_1();
		} else if(k == 1) {
			System.out.println("Executing 1-0 move");
			newOrders = execute1_0();
		} else {
			System.out.println("Executing an improved version of the 1-0 move");
			newOrders = execute1_0ImprovedVersion();
		}
		return newOrders;
	}
	
	/*
	 * Creates an ArrayList<ArrayList<Order>> and swaps the two orders that will lead to the maximum decrease of the overall operation time
	 */
	public ArrayList<ArrayList<Order>> execute1_1() {
		ArrayList<ArrayList<Order>> initialOrder = sol_local_vnd.assignedOrders;
		ArrayList<ArrayList<Order>> newOrders = sol_local_vnd.assignedOrders;
		ArrayList<ArrayList<Order>> bestNewOrders = sol_local_vnd.assignedOrders;
		Order order1, order2;
		double bestTime = 999999999;
		int v1=0,v2=0,v3=0,v4=0;
		
		for(int i = 0; i < initialOrder.size(); i++) { //all machines
			for(int j = 0; j < initialOrder.get(i).size(); j++) { //all orders in machine i
				
				order1 = initialOrder.get(i).get(j);
				
				for(int k = 0; k < initialOrder.size(); k++) { //all machines
					for(int m = 0; m < initialOrder.get(k).size(); m++) { //all orders in machine k
						
						if(i != k && j != m) {
							order2 = newOrders.get(k).get(m);
							//Change order1 with order2
							newOrders.get(k).set(m, order1);
							newOrders.get(i).set(j, order2);

							double[] timeOfMachine = findOperationTimeOfEachMachine(newOrders, transitionTime);
							double newTime = findMaxOperationTime(timeOfMachine);
														
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
		
		/*
		 * Exchanging orders, by using the values/indexes which were included, in this best solution.
		 */
		order1 = newOrders.get(v1).get(v2);
		order2 = newOrders.get(v3).get(v4);
		newOrders.get(v3).set(v4, order1);
		newOrders.get(v1).set(v2, order2);
		
		bestNewOrders = newOrders;
		
		return bestNewOrders;
	}
	
	/*
	 * Makes all the possible 1-0 moves and returns the solution (bestNewOrders) with the smallest time
	 */
	public ArrayList<ArrayList<Order>> execute1_0() {
		ArrayList<ArrayList<Order>> initialOrder = sol_local_vnd.assignedOrders;
		ArrayList<ArrayList<Order>> newOrders = sol_local_vnd.assignedOrders;
		ArrayList<ArrayList<Order>> bestNewOrders = sol_local_vnd.assignedOrders;
		Order orderToMove;
		double bestTime = 999999999;
		
		for(int i = 0; i < initialOrder.size(); i++) { //all machines
			for(int j = 0; j < initialOrder.get(i).size(); j++) { //all orders in machine i
				
				orderToMove = newOrders.get(i).get(j); //get the order that will me moved
				newOrders.get(i).remove(j); //remove the order, that will be moved, from its initial position
				
				for(int k = 0; k < newOrders.size(); k++) { //all machines
					for(int m = 0; m < newOrders.get(k).size(); m++) { //all orders in machine k
						
						if(i != k && j != m) {
							
							//Move the orderToMove to machine k in the position m and shift all other orders in this machine.
							newOrders.get(k).add(m, orderToMove);
							
							double[] timeOfMachine = findOperationTimeOfEachMachine(newOrders, transitionTime);
							double newTime = findMaxOperationTime(timeOfMachine);
														
							if(newTime < bestTime) {						
								bestTime = newTime;
								bestNewOrders = newOrders;	
							}
							//Set order to their initial position, before exchange
							newOrders.get(k).remove(m);
						}
					}
				}
				newOrders.get(i).add(j, orderToMove);
			}
		}
		
		bestNewOrders = newOrders;

		return bestNewOrders;
	}
	
	/*
	 * Creates an ArrayList<ArrayList<Order>> and moves the order that will lead to the maximum decrease of the overall operation time to another machine
	 */
	public ArrayList<ArrayList<Order>> execute1_0ImprovedVersion() {
		ArrayList<ArrayList<Order>> bestNewOrders = sol_local_vnd.assignedOrders;
		
        RepositioningOrderMove repositioning = new RepositioningOrderMove();
        
        repositioning.Initialize();
    	checksBestMove(repositioning, orders, transitionTime, bestNewOrders);
    	
    	bestNewOrders = checksMove(repositioning, bestNewOrders);

		return bestNewOrders;
	}

	/*
	 * Checks to find the best move to be made
	 */
	public void checksBestMove(RepositioningOrderMove repositioning, ArrayList<Order> orders, double[][] transitionTime, ArrayList<ArrayList<Order>> bestNewOrders) {
		double[] timeOfMachine = findOperationTimeOfEachMachine(bestNewOrders, transitionTime);
		
		int[] machineIndex1 = new int[2];
	    int machineIndex2;
		int k = findMaxOperationTimePosition(timeOfMachine);
		int l = findMinOperationTimePosition(timeOfMachine);
		machineIndex1[0] = k;
		for (int m = 0; m < bestNewOrders.get(k).size(); m++) {
			machineIndex1[1] = m;
			// checking if the last order within the machine with the maximum time is of the same darkness as the last order within the machine with the minimum time
			if (bestNewOrders.get(machineIndex1[0]).get(machineIndex1[1]).dark == 
					(bestNewOrders.get(l).get(bestNewOrders.get(l).size()-1)).dark) {
				machineIndex2 = l;
				// finding the machine with the minimum operation time among those that have an order of the same darkness assigned to their last position	
			} else {
				double pickedMachineTime = findMaxOperationTime(timeOfMachine);
				int pos = k;
				for (int i = 0; i < bestNewOrders.size(); i++) {
					if (i != machineIndex1[0] && i != m) {
						if (bestNewOrders.get(machineIndex1[0]).get(machineIndex1[1]).dark == 
								(bestNewOrders.get(i).get(bestNewOrders.get(i).size()-1)).dark && 
								timeOfMachine[i] < pickedMachineTime) {
							machineIndex2 = k;
							pickedMachineTime = timeOfMachine[i];
							pos = i;
						}
					}
				}
				machineIndex2 = pos;
			}
			
			timeOfMachine[machineIndex1[0]] -=  findTimeNeeded(machineIndex1[0], machineIndex1[1], transitionTime, bestNewOrders.get(machineIndex1[0]).get(machineIndex1[0]));
    		timeOfMachine[machineIndex2] += findTimeNeeded(machineIndex2, bestNewOrders.get(machineIndex2).size() - 1, transitionTime, bestNewOrders.get(machineIndex1[0]).get(machineIndex1[0]));
    		
			double moveTime = findMaxOperationTime(timeOfMachine);
			if (moveTime < repositioning.moveTime) {
				repositioning.moveTime = moveTime;
				repositioning.machineIndex1[0] = machineIndex1[0];
				repositioning.machineIndex1[1] = machineIndex1[1];
				repositioning.machineIndex2 = machineIndex2;
			}
		}
		
	}
	
	/*
	 * Checks the swap of  the orders found at checksBestMove
	 */
	private ArrayList<ArrayList<Order>> checksMove(RepositioningOrderMove repositioning, ArrayList<ArrayList<Order>> bestNewOrders) {

		Order s = bestNewOrders.get(repositioning.machineIndex1[0]).get(repositioning.machineIndex1[1]);
        	
		bestNewOrders.get(repositioning.machineIndex1[0]).remove(repositioning.machineIndex1[1]);
		bestNewOrders.get(repositioning.machineIndex2).add(s);

    	return bestNewOrders;
    	
    }
}
