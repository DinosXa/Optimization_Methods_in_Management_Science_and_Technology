import java.util.ArrayList;

public class Machine {
	
	public ArrayList<Order> batch = new ArrayList<Order>();
	public int machID;
    public int capacity;
    public int load;
    public boolean closed;

    public Machine(int id, int cap)
    {
        this.machID = id;
        this.capacity = cap;
        this.load = 0;
        this.closed = false;
        this.batch.clear();
    }

    public void addOrder(Order order) //Add Order to Machine
    {
       batch.add(order);
       //this.curLocation = Order.ID;
    }

    public boolean checkIfFits(int quantity) //Check if we have Quantity Violation
    {
        return ((load + quantity <= capacity));
    }

}
