import java.util.ArrayList;


public class PaintProduction {	
	
	/*
	 * Print the orders that exist in each machine
	 */
	public void printOrdersByMachine(ArrayList<Order>[] machine) {
		int i = 1;
		for(ArrayList<Order> ma : machine) {
			System.out.print("Machine " + i + ": ");
			for(Order or : ma) {
				System.out.print(or.getID() + " ");
			}
			System.out.println();
			i++;
		}
	}
		
}



