import java.util.ArrayList;
import java.util.Collections; 
import java.util.Arrays;
import java.util.Random; 


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
			
			for (int i = 0; i < transitionTime.length; i++) {
				for (int j = 0; j < transitionTime[i].length; j++) {
					//System.out.print(transitionTime[i][j] + "  ");
				}
				//System.out.println();
			}
			
			//start. construction (question A)
			
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
			
			
			ArrayList<Order> darkOrders = createSortedDarkOrders(orders);
			ArrayList<Order> lightOrders = createSortedLightOrders(orders);
			
			FirstFitIncreasingAlgorithm(darkOrders,lightOrders);	
			
			calculateOpTime(transitionTime,FirstFitIncreasingAlgorithm(darkOrders,lightOrders));
			
		}
	
	
	
	
	
		// dimiourgo 2 taksinomimenous ( kata aujoysa seira) pinakes  me vasi to xroma kai quantity.
		
		public static ArrayList <Order> createSortedDarkOrders(ArrayList <Order> orders) {
			
			ArrayList<Order> darkOrders = new ArrayList<>();
			
			for ( Order element: orders) {
				if (element.dark == true) {
					darkOrders.add(element);
				}				
			}
			Collections.sort(darkOrders,(q1, q2) -> Double.compare(q1.quantity,q2.quantity));
			
			return darkOrders;
		}
		
		
		public static ArrayList<Order> createSortedLightOrders(ArrayList <Order> orders) {
			
			ArrayList<Order> lightOrders = new ArrayList<>();
			
			for ( Order element: orders) {
				if (element.dark == false) {
					lightOrders.add(element);
					
				}				
			}
			Collections.sort(lightOrders,(q1, q2) -> Double.compare(q1.quantity,q2.quantity));	
			
			return lightOrders;
		}
		
		
		// topotheto kathe order sthn amesos epomenh mixani. dld 1 order me 1 mixani, 2 order sth deuterh mixani klp. 		
		// FisrtFitIncreasingAlgorithm (
					
		public static ArrayList<Order>[] FirstFitIncreasingAlgorithm(ArrayList<Order> darkOrders, ArrayList<Order> lightOrders) {
			
			ArrayList<Order>[] machine = new ArrayList[5];	
			for (int i = 0; i < 5; i++){
				machine[i]= new ArrayList<Order>();		
			}
				int k = 0;
				for ( int i = 0 ; i < darkOrders.size(); i++) {							
					machine[k].add(darkOrders.get(i));
					k++;				
					k = k==5 ? 0 : k;										
				}
				for ( int i = 0 ; i < lightOrders.size(); i++) {						
					machine[k].add(lightOrders.get(i));
					k++;				
					k = k==5 ? 0 : k;							
				}
				return machine;
		}
	
		
		public static void calculateOpTime(double[][] transitionTime, ArrayList<Order>[] machine) {
			
			double[] timeOfMachine = new double[5];
			
			for(int i=0; i<5; i++) {
				timeOfMachine[i] = 0;
			}
			
			//calculates the time that each machine is working (Question B)
			for (int i=0; i < 5; i++) {
				for (int j=0; j < machine[i].size() - 1; j++) {
					timeOfMachine[i] = timeOfMachine[i] + machine[i].get(j).quantity * 6 
							+ transitionTime[machine[i].get(j).ID-1][machine[i].get(j + 1).ID-1];

					//adds extra 15 mins if the next color is different than the current
					if(machine[i].get(j).dark != machine[i].get(j + 1).dark) {
						timeOfMachine[i] += 15 * 60;
					}
				}
				timeOfMachine[i] = timeOfMachine[i] + machine[i].get(machine[i].size() - 1).quantity * 6; //adds the time of the last order of curr machine.
				
			}
			for (int i=0;i<5;i++) {
				System.out.println(timeOfMachine[i]);
			}
		}
		
		
		
		
		/*public static void getMinTime(double[][] transitionTime,ArrayList <Order> orders) {
			double Inverse = 0;
			int Row = 0;
			int Column = 0;
			double Min = transitionTime[1][0];
			
			for (int i = 0; i < transitionTime.length; i++) {
				
				for (int j = 0; j < transitionTime[i].length; j++) {			
					
					if (transitionTime[i][j] < Min && transitionTime[i][j] != 0.0) { //find min time excluding diagonal
						
						Min = transitionTime[i][j];
						Inverse = transitionTime[j][i];
						Row = i;
						Column = j;
						  				
					}
				}
				
			}*/
			
			
			/*System.out.println();
			System.out.println("Shortest:"+ Min);
			System.out.println("Row:"+ Row);
			System.out.println("Column:"+ Column);
			
			System.out.println("id"+orders.get(Row-1).ID);
			System.out.println("dark"+orders.get(Row-1).dark);	  
			System.out.println("id"+orders.get(Column-1).ID);
			System.out.println("dark"+orders.get(Column-1).dark);	
			
			System.out.println();
			System.out.println("Shortest:"+ Inverse);
			System.out.println("Row:"+ Column);
			System.out.println("Column:"+ Row);*/
				
			
	
	//}
		
	
	
	
	
	
	
		
}



