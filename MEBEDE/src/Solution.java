import java.util.ArrayList;

public class Solution {
	
	ArrayList<ArrayList<Order>> assignedOrders;
	public double time;
		
	public Solution () {	
		assignedOrders = new ArrayList();
		for (int i = 0; i < 5; i++){
			assignedOrders.add(new ArrayList());
		}
	}
	
}