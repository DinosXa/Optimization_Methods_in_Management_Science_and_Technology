//import java.util.ArrayList;

public class Machine {
	
	//public ArrayList<Order> batch = new ArrayList<Order>();
	public int machID;
    public double SumofTime;
   
    public Machine(int id, double sumtime)
    {
        this.machID = id;
        this.SumofTime = sumtime;
        //this.batch.clear();
    }
}
