public class SwapOrdersMove {

	    int[] machineIndex1 = new int[2];
	    int[] machineIndex2 = new int[2];
	    double moveTime;
	    // Checks if there is a swap to be made
	    boolean flag;

	    public SwapOrdersMove() {

	    }

	    public void Initialize(double time)
	    {
	        machineIndex1[0] = -1;
	        machineIndex1[1] = -1;
	        machineIndex2[0] = -1;
	        machineIndex2[1] = -1;
	        moveTime = time;
	        flag = false;
	    }

}