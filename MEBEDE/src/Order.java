public class Order {
	
	 boolean dark;
	 double quantity;
	 int ID;
	 boolean isPicked = false;

	 public Order(int id, double quantity, boolean dark) {
		 this.ID = id;
		 this.quantity = quantity;
		 this.dark = dark;
	 }

	public boolean isDark() {
		return dark;
	}

	public void setDark(boolean dark) {
		this.dark = dark;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public boolean isPicked() {
		return isPicked;
	}

	public void setPicked(boolean isPicked) {
		this.isPicked = isPicked;
	}
	 
	 

}
