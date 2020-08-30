public class SwapOrdersMove {

	    int[] machineIndex1 = new int[2];
	    int[] machineIndex2 = new int[2];
	    double moveTime;
	    boolean flag;
	    
	    public SwapOrdersMove() {
	        
	    }
	    
	    public void Initialize()
	    {
	        machineIndex1[0] = -1;
	        machineIndex1[1] = -1;
	        machineIndex2[0] = -1;
	        machineIndex2[1] = -1;
	        moveTime = Double.MAX_VALUE;
	        flag = false;
	    }
	
}
