package CDS;

import java.util.ArrayDeque;

public final class FairLock {
	private ArrayDeque<Thread> entryQueue=new ArrayDeque<Thread>();
	private ArrayDeque<Thread> urgentQueue=new ArrayDeque<Thread>();
	private boolean available;
	private Thread signaled;
	private String owner;

	public FairLock(){
		super();
		available=true;
		signaled=null;
		owner="no one";
	}
	
	public final class Condition{
		private ArrayDeque<Thread> conditionQueue=new ArrayDeque<Thread>();
		
		public Condition(){
			super();
		}
		
		public final void await() throws InterruptedException{
			synchronized(FairLock.this){
			synchronized(this){
				conditionQueue.addLast(Thread.currentThread());	
				System.out.println("I'm in the await "+Thread.currentThread().getName());
				try{
					wait();
					}
					finally{
						unlock();
					}
				while(Thread.currentThread()!=signaled){					
					System.out.println("i'm not the signaled one I am "+Thread.currentThread().getName());
					try{
						wait();
						}
						finally{
							unlock();
						}
				}
				//System.out.println("exit from await while wait"+Thread.currentThread().getName());			
				conditionQueue.removeFirst();
				signaled=null;			
				//System.out.println("exit from await: "+Thread.currentThread().getName());
			}
			}
		}
		
		public final void signal() throws InterruptedException{
			synchronized(this){
				System.out.println("I'm in the signal "+Thread.currentThread().getName());
				if(!conditionQueue.isEmpty()){
					System.out.println("in the if of signal");
					urgentQueue.addLast(Thread.currentThread());
					signaled=conditionQueue.peek();
					System.out.println("signaled "+signaled);
					notifyAll();				
					try{
					wait();
					}
					finally{
						unlock();
					}
					while(Thread.currentThread()!=signaled){
						try{
							wait();
							}
							finally{
								unlock();
							}						
					}
					urgentQueue.removeFirst();
					signaled=null;
				}
			}
		}
	}
	
	public synchronized void lock() throws InterruptedException{
		System.out.println("I'm in the lock "+Thread.currentThread().getName());
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
		System.out.println("I'm in the unlock "+Thread.currentThread().getName());
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
