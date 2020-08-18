import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random; 


public class PaintProduction {
	
	public static void main(String args[]) {
		
		PaintProduction pp = new PaintProduction();
			
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
		
		 for (int i = 0; i < orders.size();i++) 
	      { 
			 System.out.print(orders.get(i).ID + " ");
			 System.out.print(orders.get(i).quantity + " ");  
	         System.out.print(orders.get(i).dark + "\n");
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
			
			//start. construction (question A)

			pp.simpleAlgorythm(transitionTime, totOrders, orders);
			

	}
	
	/*
	 * The simple solution
	 */
	public void simpleAlgorythm(double[][] transitionTime, int totOrders, ArrayList<Order> orders) {
		
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
		
		double[] timeOfMachine = pp.findOperationTimeOfEachMachine(machine, transitionTime);
		System.out.println(pp.findMaxOperationTime(timeOfMachine));
				
	}
	
	/*
	 * Pleonektikos algorithmos
	 */
	public void greedyAlgorythm(double[][] transitionTime, int totOrders, ArrayList<Order> orders) {
		
		PaintProduction pp = new PaintProduction();
		ArrayList<Order>[] machine = new ArrayList[5];
		
		for (int i = 0; i < 5; i++){
			machine[i]= new ArrayList<Order>();
		
		}
		
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
			for (int j=0; j < machine[i].size() - 1; j++) {
				timeOfMachine[i] = timeOfMachine[i] + machine[i].get(j).quantity * 6 
						+ transitionTime[machine[i].get(j).ID - 1][machine[i].get(j + 1).ID - 1];

				//adds extra 15 mins if the next color is different than the current
				if(machine[i].get(j).dark != machine[i].get(j + 1).dark) {
					timeOfMachine[i] += 15 * 60;
				}
			}
			timeOfMachine[i] = timeOfMachine[i] + machine[i].get(machine[i].size() - 1).quantity * 6; //adds the time of the last order of curr machine.
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
	
	
	// question C . na katataksoume tis parageleies me vasi to xroma k meta min(tranisistion time).
		
}



