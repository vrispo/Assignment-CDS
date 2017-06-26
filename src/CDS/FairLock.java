package CDS;

import java.util.ArrayDeque;

public final class FairLock {
	private ArrayDeque<Thread> entryQueue=new ArrayDeque<Thread>();
	private ArrayDeque<Thread> urgentQueue=new ArrayDeque<Thread>();
	private boolean available;
	private Thread signaled;

	public FairLock(){
		super();
		available=true;
		signaled=null;
	}
	
	public final class Condition{
		private ArrayDeque<Thread> conditionQueue=new ArrayDeque<Thread>();
		
		public Condition(){
			super();
		}
		
		public final synchronized void await() throws InterruptedException{
			conditionQueue.addLast(Thread.currentThread());
			unlock();
			wait();
			while(Thread.currentThread()!=signaled)
				wait();
			//System.out.println("exit from await while wait"+Thread.currentThread().getName());
			conditionQueue.removeFirst();
			signaled=null;
			//System.out.println("exit from await: "+Thread.currentThread().getName());
		}
		
		public final synchronized void signal() throws InterruptedException{
			synchronized(FairLock.this){
				System.out.println("in the signal");
				if(!conditionQueue.isEmpty()){
					System.out.println("in the if of signal");
					urgentQueue.addLast(Thread.currentThread());
					signaled=conditionQueue.peek();
					System.out.println("signaled "+signaled);
					notifyAll();
					wait();
					while(Thread.currentThread()!=signaled){
						wait();
					}
					urgentQueue.removeFirst();
					signaled=null;
				}
			}
		}
	}
	
	public synchronized void lock() throws InterruptedException{
		if(!available || !entryQueue.isEmpty() || !urgentQueue.isEmpty()){
			entryQueue.addLast(Thread.currentThread());
			wait();
		
			//Method peek: retrieves, but does not remove, the head of the queue represented by this deque, or returns null if this deque is empty.
			while(signaled!=Thread.currentThread())
				wait();
			entryQueue.removeFirst();
			signaled=null;
		}
		else
			available=false;
	}
	
	public synchronized void unlock(){
		if((signaled==null)&&(!urgentQueue.isEmpty())){
			signaled=urgentQueue.peek();
			notifyAll();
		}
		else if(((signaled==null))&&(!entryQueue.isEmpty())){
			signaled=entryQueue.peek();
			notifyAll();
		}
		else if(signaled==null){
			available=true;
		}
	}
	
	public Condition newCondition(){
		return new Condition();
	}
}
