public class RepositioningOrderMove {
	
	int[] machineIndex1 = new int[2];
    int machineIndex2;
    double moveTime;

    public RepositioningOrderMove() {

    }

    public void Initialize()
    {
        machineIndex1[0] = -1;
        machineIndex1[1] = -1;
        machineIndex2 = -1;
        moveTime = Double.MAX_VALUE;
    }
    
}
