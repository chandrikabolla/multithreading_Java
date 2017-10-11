/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bolla_A;

/**
 *
 * @author chand
 */
import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the maximum over a
 * subset of its <code>Integers</code>.
 */
public class ParallelMaximizerWorker extends Thread {

        protected String Tname;
	protected LinkedList<Integer> list;
	protected int partialMax = Integer.MIN_VALUE; // initialize to lowest value
	
	public ParallelMaximizerWorker(LinkedList<Integer> list,String name) {
		this.list = list;
                this.Tname=name;
	}
	
	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
               
		while (true) {
                    
               //     System.out.println("-----------------------At : "+Tname+" --------------------------------");
                     int number = 0;
                     int localMax=0;
                 //   for (int i = 1; i <= 10; i++) {
			
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
                           
				if (list.isEmpty())
					return; // list is empty
                                
                                
                                
				// update partialMax according to new value
				// TODO: IMPLEMENT CODE HERE
				
                             //   System.out.print("At : "+Tname );
                                
					number = list.remove();
                               //         System.out.println("       ||          Checking value: "+number +" against localMax where partialMax : "+partialMax);
					
				}
				if(localMax<number)
                                {
                                    localMax=number;
                                }
                                
                             
                 //           }
                 //           System.out.println(" At "+ this.Tname+"  checking for partialMax : localMax "+ partialMax +" : "+localMax);
                            if (localMax> partialMax) {
					partialMax =localMax;
				}
			
		}
	}


	
	public int getPartialMax() {
		return partialMax;
	}
        
        public String getTName(){
            return Tname;
        }

}

