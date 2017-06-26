import CDS.FairLock;
import CDS.FairLock.*;

public class FairlockDEMO {
	public static void main(String[] args){
		Manager m=new Manager();
		ThreadTypeA clienta1=new ThreadTypeA(m,"clienta1");
		ThreadTypeA clienta2=new ThreadTypeA(m,"clienta2");
		ThreadTypeB clientb=new ThreadTypeB(m,"clientb");
		
		clienta1.setName("a1");
		clienta2.setName("a2");
		clientb.setName("b");
		
		clienta1.start();
		clienta2.start();
		clientb.start();
	}
}

class Manager {
	private String shared_resource;
	private FairLock lock;
	private Condition lock_condA;
	private int waitingonA;
	private Condition lock_condB;
	private int waitingonB;
	private boolean isLocked=false;
	
	Manager(){
		shared_resource="resource created";
		lock=new FairLock();
		lock_condA=lock.newCondition();
		lock_condB=lock.newCondition();
		waitingonA=0;
		waitingonB=0;
	}
	
	public void requestfromA(String ThreadName) throws InterruptedException{
		lock.lock();
		try{
			System.out.println("Thread type A "+ThreadName+" request resource");
			if(isLocked==true){
				System.out.println("Blocked "+ThreadName+":resource owned by"+this.shared_resource);
				waitingonA++;
				lock_condA.await();
				System.out.println("Awakened "+ThreadName);
			}
			shared_resource=ThreadName;
			isLocked=true;
			System.out.println("Resouce owned by "+ThreadName);
		}finally{
			lock.unlock();
		}
	}
	
	public void requestfromB(String ThreadName) throws InterruptedException{
		lock.lock();
		try{
			System.out.println("Thread type B "+ThreadName+" request resource");
			if(isLocked==true){
				System.out.println("Blocked "+ThreadName+":resource owned by"+this.shared_resource);
				waitingonB++;
				lock_condB.await();
				System.out.println("Awakened "+ThreadName);
			}
			shared_resource=ThreadName;
			isLocked=true;
			System.out.println("Resouce owned by "+ThreadName);
		}finally{
			lock.unlock();
		}
	}
	
	public void releasefromA(String ThreadName) throws InterruptedException{
		lock.lock();
		try{
			System.out.println("Thread A "+this.shared_resource+" is releasing the resource");
			this.isLocked=false;
			if(waitingonB>0){
				waitingonB--;
				lock_condB.signal();
			}
			else if(waitingonA>0){
				waitingonA--;
				lock_condA.signal();
			}
			System.out.println("Thread A "+this.shared_resource+" has released the resource");
			this.shared_resource="no one";
		}finally{
			lock.unlock();
		}
	}
	
	public void releasefromB(String ThreadName) throws InterruptedException{
		lock.lock();
		try{
			System.out.println("Thread B "+this.shared_resource+" is releasing the resource");
			this.isLocked=false;
			if(waitingonA>0){
				waitingonA--;
				lock_condA.signal();
			}
			System.out.println("Thread B "+this.shared_resource+" has released the resource");
			this.shared_resource="no one";
		}finally{
			lock.unlock();
		}
	}
}

class ThreadTypeA extends Thread{
	private Manager m;
	private String name;
	ThreadTypeA(Manager m, String name){
		this.m=m;
		this.name=name;
	}
	@Override
	public void run(){
		try {
			sleep((long) (1000*Math.random()));
			m.requestfromA(name);
			sleep((long) (1000*Math.random()));
			m.releasefromA(name);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ThreadTypeB extends Thread{
	private Manager m;
	private String name;
	ThreadTypeB(Manager m, String name){
		this.m=m;
		this.name=name;
	}
	@Override
	public void run(){
		try {
			sleep((long) (1000*Math.random()));
			m.requestfromB(name);
			sleep((long) (1000*Math.random()));
			m.releasefromB(name);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}