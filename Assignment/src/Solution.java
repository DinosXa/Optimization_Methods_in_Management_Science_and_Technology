import java.util.ArrayList;

public class Solution {
	
	ArrayList<ArrayList<Order>> assignedOrders;
	public double time;
		
	public Solution () {	
		assignedOrders = new ArrayList<ArrayList<Order>>();
		for (int m = 0; m < 5; m++){
			assignedOrders.add(new ArrayList<Order>());
		}
	}
	
}
