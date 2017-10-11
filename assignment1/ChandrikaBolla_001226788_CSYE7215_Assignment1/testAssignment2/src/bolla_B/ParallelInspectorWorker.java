/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bolla_B;

import java.util.LinkedList;

/**
 *
 * @author chand
 */
public class ParallelInspectorWorker extends Thread{
    
    protected String Tname;
	protected LinkedList<Integer> list;
	protected int partialMax = Integer.MIN_VALUE; // initialize to lowest value
        protected Inspector inspector;
	
	public ParallelInspectorWorker(LinkedList<Integer> list,String name,Inspector inspector) {
		this.list = list;
                this.Tname=name;
                this.inspector=inspector;
	}
	
	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
               
		while (true) {
                    
               
                     int number = 0;
                    
               //      for (int i = 1; i <= 10; i++) {
			
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
                           
				if (list.isEmpty())
					return; // list is empty
                                
                                
                                
				// update partialMax according to new value
				// TODO: IMPLEMENT CODE HERE
				
                         //       System.out.print("At : "+Tname);
                                        
					number = list.remove();
                                        this.inspector.inspect(number);
                                    	
				}
			
			
	//	}
	}


	
        }
        
        public String getTName(){
            return Tname;
        }

    public String getTname() {
        return Tname;
    }

    public void setTname(String Tname) {
        this.Tname = Tname;
    }

    public LinkedList<Integer> getList() {
        return list;
    }

    public void setList(LinkedList<Integer> list) {
        this.list = list;
    }

    public int getPartialMax() {
        return partialMax;
    }

    public void setPartialMax(int partialMax) {
        this.partialMax = partialMax;
    }

    public Inspector getInspector() {
        return inspector;
    }

    public void setInspector(Inspector inspector) {
        this.inspector = inspector;
    }
    
    
    
}
