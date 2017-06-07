package CDS;

import java.util.ArrayDeque;

public class FairLock {
	private ArrayDeque<Thread> queue=new ArrayDeque<Thread>();
	private int n;

	public FairLock(int n){
		this.n=n;
	}
	
	public synchronized void lock() throws InterruptedException{
		if(n<=0 || !queue.isEmpty()){
			queue.addLast(Thread.currentThread());
			wait();
		}
		//Retrieves, but does not remove, the head of the queue represented by this deque, or returns null if this deque is empty.
		while(Thread.currentThread()!=queue.peek())
			wait();
		n--;
		queue.removeFirst();
	}
	public synchronized void unlock(){
		n++;
		notifyAll();
	}
	public void newCondition(){
		
	}
}
