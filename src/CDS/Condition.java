package CDS;

public class Condition {
	private FairLock UrgentLock= new FairLock(1);
	private FairLock ConditionLock= new FairLock(1);
	
	public void await(){
		
	}
	public void signal(){
		
	}
}
