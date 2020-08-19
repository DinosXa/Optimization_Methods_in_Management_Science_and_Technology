import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random; 
import java.util.Collections;


public class PaintProduction {
	
	public static void main(String args[]) {
			
		ArrayList<Order> orders = new ArrayList<>();
	
		int bday = 19092000;
		Random ran = new Random(bday);
		int totOrders = 100; //change to 100
	
		for (int i = 0 ; i < totOrders; i++){
			int qq = 100 + ran.nextInt(401);
			boolean drk = false; 
		
			if (ran.nextDouble() < 0.15){
				drk = true;
			}
			Order o = new Order(i + 1, qq , drk);
			orders.add(o);
		} 
			
		double[][] transitionTime = new double[totOrders][totOrders];
			
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

			simpleAlgo(transitionTime, totOrders, orders);
			greedyAlgo(transitionTime, totOrders, orders);
	}
	
	
	public static void simpleAlgo(double[][] transitionTime, int totOrders, ArrayList<Order> orders) {
		PaintProduction pp = new PaintProduction();	
		ArrayList<Order>[] machine = new ArrayList[5];
			
		for (int i = 0; i < 5; i++){
			machine[i]= new ArrayList<Order>();
		}
		
		int k = 0;
		for (int i=0; i < totOrders; i++) {
			machine[k].add(orders.get(i));
			k++;
			if(k==5) {
				k = 0;
			}
		}
		
		double[] timeOfMachine = new double[5];
		
		for(int i=0; i<5; i++) {
			timeOfMachine[i] = 0;
		}
		timeOfMachine = pp.findOperationTimeOfEachMachine(machine, transitionTime);
		
		System.out.println("\nSimple:");
		for(int i=0; i < 5; i++) {
			System.out.printf("Time of machine %s: %s\n", i+1, timeOfMachine[i]);
		}

	}
					
	public static double[] findOperationTimeOfEachMachine(ArrayList<Order>[] machine, double[][] transitionTime) {
		double[] timeOfMachine = new double[5];
		
		for(int i=0; i<5; i++) {
			timeOfMachine[i] = 0;
		}
		
		for (int i=0; i < 5; i++) {
			for (int j=0; j < machine[i].size() - 1; j++) {
				timeOfMachine[i] = timeOfMachine[i] + machine[i].get(j).quantity * 6 
						+ transitionTime[machine[i].get(j).ID - 1][machine[i].get(j + 1).ID - 1];

				//adds extra 15 minutes if the next color is different than the current
				if(machine[i].get(j).dark != machine[i].get(j + 1).dark) {
					timeOfMachine[i] += 15 * 60;
				}
			}
			timeOfMachine[i] = timeOfMachine[i] + machine[i].get(machine[i].size() - 1).quantity * 6; //adds the time of the last order of current machine.
		}
		
		return timeOfMachine;
	}
			
	// Problem C
	
	public static void greedyAlgo(double[][] transitionTime, int totOrders, ArrayList<Order> orders) {
		
		//Initialize machines' time
		ArrayList<Order>[] machine = new ArrayList[5];
		for (int i = 0; i < 5; i++){
			machine[i]= new ArrayList<Order>();
		}
		
		double[] timeOfMachine = new double[5];
		for(int i=0; i<5; i++) {timeOfMachine[i] = 0;}
			
		//Sorting orders by quantity
		Collections.sort(orders, (o1, o2) -> Double.compare(o1.quantity, o2.quantity));
		
		int iom = 0;
		double ord1 = 0, ord2 = 0;
		
		for (int i=0; i < totOrders-2; i=i+2) {
			ord1 = orders.get(i).quantity*6 + transitionTime[i][i+1];
			ord2 = orders.get(i+1).quantity*6 + transitionTime[i+1][i];
			
			
			if(ord1 < ord2) {
				machine[iom].add( orders.get(i) );
			}else {
				machine[iom].add( orders.get(i+1) );
			}
			
			iom = findIdOfMinOperationTime(timeOfMachine);
			timeOfMachine = findOT(machine, transitionTime);
			
			
		}
		System.out.println("\nGreedy:");
		for(int i=0; i < 5; i++) {
			System.out.printf("Time of machine %s: %s\n", i+1, timeOfMachine[i]);
		}
		

	
	}
	
	public static double[] findOT(ArrayList<Order>[] machine, double[][] transitionTime) {
		double[] timeOfMachine = new double[5];
		
		for(int i=0; i<5; i++) {
			timeOfMachine[i] = 0;
		}
		
		for (int i=0; i < 5; i++) {
			for (int j=0; j < machine[i].size() - 1; j++) {
				timeOfMachine[i] = timeOfMachine[i] + machine[i].get(j).quantity * 6 
						+ transitionTime[machine[i].get(j).ID - 1][machine[i].get(j + 1).ID - 1];

				//adds extra 15 minutes if the next color is different than the current
				if(machine[i].get(j).dark != machine[i].get(j + 1).dark) {
					timeOfMachine[i] += 15 * 60;
				}
			}
//			timeOfMachine[i] = timeOfMachine[i] + machine[i].get(machine[i].size() - 1).quantity * 6; //adds the time of the last order of current machine.
		}
		
		return timeOfMachine;
	}
	
	public static int findIdOfMinOperationTime(double[] timeOfMachine) {
		double minTime = timeOfMachine[0];
		int mid = 0;
		for(int i = 1; i < timeOfMachine.length; i++ ) {
			if(timeOfMachine[i] < minTime) {
				minTime = timeOfMachine[i];
				mid = i;
			}
		}
		
		return mid;
	}
	
}
	
	
	



