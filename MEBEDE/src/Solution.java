import java.util.ArrayList;

public class Solution {
	
	ArrayList<ArrayList<Order>> assignedOrders;
	public double time;
	ArrayList<Order> mnb;
		
	public Solution () {	
		assignedOrders = new ArrayList();
		for (int i = 0; i < 5; i++){
			assignedOrders.add(new ArrayList());
		}
	}
	
}