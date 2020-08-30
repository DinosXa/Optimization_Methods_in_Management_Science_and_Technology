import java.util.ArrayList;
import java.util.Random;

public class Solver {

	int totOrders = 100;
	double[][] transitionTime = new double[totOrders][totOrders];
	ArrayList<Order> orders;
	Solution sol_simple;
	Solution sol_greedy;
	Solution sol_local;

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
		for (int i=0; i < totOrders; i++) {
			sol_simple.assignedOrders.get(k).add(orders.get(i));
			k++;
			if(k==5) {
				k = 0;
			}
		}

		double[] timeOfMachine = findOperationTimeOfEachMachine(sol_simple.assignedOrders, transitionTime);
		sol_simple.time = findMaxOperationTime(timeOfMachine);
		System.out.println("Simple Solution: " + sol_simple.time +"\n");

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

		//add the first order to the first machine
		sol_greedy.assignedOrders.get(0).add(orders.get(0));
		orders.get(0).isPicked = true;

		for (int i = 1; i < totOrders; i++)	{

			timeOfMachine = findOperationTimeOfEachMachine(sol_greedy.assignedOrders, transitionTime);
			minPos = findMinOperationTimePosition(timeOfMachine);
			lastOrder = findLastOrderOfMachine(sol_greedy.assignedOrders.get(minPos));
			nextBestOrder = findNextBestOrder(lastOrder, totOrders, orders, transitionTime);
			sol_greedy.assignedOrders.get(minPos).add(nextBestOrder);

		}

		timeOfMachine = findOperationTimeOfEachMachine(sol_greedy.assignedOrders, transitionTime);
		sol_greedy.time = findMaxOperationTime(timeOfMachine);
		System.out.println("Greedy Solution: " + sol_greedy.time + "\n");
		printOrdersByMachine(sol_greedy.assignedOrders);
		System.out.println("\nMachine 1: " + timeOfMachine[0]);
		System.out.println("Machine 2: " + timeOfMachine[1]);
		System.out.println("Machine 3: " + timeOfMachine[2]);
		System.out.println("Machine 4: " + timeOfMachine[3]);
		System.out.println("Machine 5: " + timeOfMachine[4]);
	}

	/*
	 * Local Search
	*/
	void localSearch() {
		sol_local = new Solution();
        sol_local = sol_greedy;
        SwapOrdersMove swap = new SwapOrdersMove();
        for (int i = 0; i < 1; i++) {
            FindBestSwapOrdersMove(swap, orders, transitionTime);
            if (swap.moveTime >= 0) {
                break;
            }

            applyMove(swap);

            double[] timeOfMachine = findOperationTimeOfEachMachine(sol_local.assignedOrders, transitionTime);
    		sol_local.time = findMaxOperationTime(timeOfMachine);
    		System.out.println("\nLocal Search Solution: " + sol_local.time + "\n");
    		printOrdersByMachine(sol_local.assignedOrders);
    		System.out.println("\nMachine 1: " + timeOfMachine[0]);
    		System.out.println("Machine 2: " + timeOfMachine[1]);
    		System.out.println("Machine 3: " + timeOfMachine[2]);
    		System.out.println("Machine 4: " + timeOfMachine[3]);
    		System.out.println("Machine 5: " + timeOfMachine[4]);
        }

	}

	/*
	 * Calculates the time that each machine is operating
	 */
	public double[] findOperationTimeOfEachMachine(ArrayList<ArrayList<Order>> machine, double[][] transitionTime) {
		double[] timeOfMachine = new double[5];

		for(int i=0; i<5; i++) {
			timeOfMachine[i] = 0;
		}

		for (int i=0; i < 5; i++) {

			if(machine.get(i).size() == 1) {
				timeOfMachine[i] = timeOfMachine[i] + machine.get(i).get(0).quantity * 6;
			} else {

				for (int j=0; j < machine.get(i).size() - 1; j++) {
					timeOfMachine[i] = timeOfMachine[i] + machine.get(i).get(j).quantity * 6
							+ transitionTime[machine.get(i).get(j).ID - 1][machine.get(i).get(j + 1).ID - 1];

					//adds 15 extra minutes if the next color is of a different darkness
					if(machine.get(i).get(j).dark != machine.get(i).get(j + 1).dark) {
						timeOfMachine[i] += 15 * 60;
					}
				}

				if(machine.get(i).size() > 1) {
					timeOfMachine[i] += machine.get(i).get(machine.get(i).size() - 1).quantity * 6; //adds the time of the last order of current machine
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
	 * Finds the position(id/number) of the minimum operation time between all machines
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
	 * Finds the order that will add the minimum operation time.
	 */
	public Order findNextBestOrder(Order lastOrder, int totOrders, ArrayList<Order> orders, double[][] transitionTime) {
		Order nextBestOrder = null;

		if(lastOrder == null) {
			nextBestOrder = findFirstUnpickedOrder(orders);
			nextBestOrder.isPicked = true;
			return nextBestOrder;
		}

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
	 * Finds the first order from the ArrayList orders that is not put into production
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
	}

	private void FindBestSwapOrdersMove(SwapOrdersMove swap, ArrayList<Order> orders, double[][] transitionTime) {
	    swap.Initialize();
	    swap.moveTime = sol_local.time;
	    int k;
	    int l;
	    boolean flag1 = false;
	    boolean flag2 = false;
	    for (int n = 0; n < sol_local.assignedOrders.size(); n++) {
	    	for (int j = 0; j < sol_local.assignedOrders.get(n).size(); j++) {
	    		if (j == sol_local.assignedOrders.get(n).size() - 1) {
	        		k = 0;
	        		l = n + 1;
	        	} else {
	        		k = j + 1;
	        		l = n;
	        	}
	        	while (!flag1 && !flag2 && !(n == sol_local.assignedOrders.size() - 1 && j == sol_local.assignedOrders.get(n).size() - 1)) {
	        		
	        		double[] timeOfMachine = findOperationTimeOfEachMachine(sol_local.assignedOrders, transitionTime);
	        		timeOfMachine[n] += findTimeNeeded(n, j, transitionTime, sol_local.assignedOrders.get(l).get(k)) - findTimeNeeded(n, j, transitionTime, sol_local.assignedOrders.get(n).get(j));
	        		timeOfMachine[l] += findTimeNeeded(l, k, transitionTime, sol_local.assignedOrders.get(n).get(j)) - findTimeNeeded(l, k, transitionTime, sol_local.assignedOrders.get(l).get(k));

	        		double moveTime = findMaxOperationTime(timeOfMachine);

    				if (moveTime < swap.moveTime) {
                    	swap.moveTime = moveTime;
                    	swap.flag = true;
                    	swap.machineIndex1[0] = n;
                    	swap.machineIndex1[1] = j;
                    	swap.machineIndex2[0] = l;
                    	swap.machineIndex2[1] = k;

                    }

	        		if (k == sol_local.assignedOrders.get(n).size() - 1 && l != sol_local.assignedOrders.size() - 1) {
	        			k = 0;
	        			l = l + 1;
	        		} else {
	        			k = k + 1;
	        		}

	        		// Checks if we have compared our order with all the following ones
	        		if (l == sol_local.assignedOrders.size() - 1 && k == sol_local.assignedOrders.get(l).size()) {
	        			flag1 = true;
	        		}
	        		// Checks if we have reached the pen-ultimate order
	        		if (n == sol_local.assignedOrders.size() - 1 && j == sol_local.assignedOrders.get(l).size() - 2) {
	        			flag2 = true;
	        		}
	        	}
	    	}
	    }
	}

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

    private void applyMove(SwapOrdersMove swap) {
        if (swap.flag)
        {
            Order s1 = sol_local.assignedOrders.get(swap.machineIndex1[0]).get(swap.machineIndex1[1]);
            Order s2 = sol_local.assignedOrders.get(swap.machineIndex2[0]).get(swap.machineIndex2[1]);

            sol_local.assignedOrders.get(swap.machineIndex1[0]).set(swap.machineIndex1[1], s2);
            sol_local.assignedOrders.get(swap.machineIndex2[0]).set(swap.machineIndex2[1], s1);

            double[] timeOfMachine = findOperationTimeOfEachMachine(sol_simple.assignedOrders, transitionTime);
    		sol_local.time = findMaxOperationTime(timeOfMachine);

        }
    }

}
