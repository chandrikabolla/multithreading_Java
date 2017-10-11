/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bolla_B;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author chand
 */
public class ParallelInspector {
        static LinkedList<Integer> staticList;
        static int numThreads = 4; // number of threads for the maximizer
	static int numElements = 1000; // number of integers in the list
	ArrayList<ParallelInspectorWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);
        static int failures=0;
	public ParallelInspector(int numThreads,LinkedList<Integer> list) {
            workers = new ArrayList<ParallelInspectorWorker>(numThreads);
            
         //   System.out.println("Workers size: "+workers.size());
                
	}


	
	public static void main(String[] args) {
		
		
		staticList=new LinkedList<Integer>();
		LinkedList<Integer> list = new LinkedList<Integer>();
		Random rand=new Random();
		// populate the list
		// TODO: change this implementation to test accordingly
		for (int i=0; i<numElements; i++)
                {
                    int randInt=rand.nextInt(100000);
                    list.add(randInt);
                    staticList.add(randInt);
                }
		
                for(int i=1;i<=10;i++)
                {
                    if(list.isEmpty())
                    {
                 for (int j=0; j<numElements; j++)
                {
                    
                    list.add(staticList.get(j));
                }
                    }
               ParallelInspector inspector = new ParallelInspector(numThreads,list);

		// run the maximizer
		try {
			System.out.println("------------->> Failure rate: "+inspector.max(list));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
                }
	}
	
	/**
	 * Finds the maximum by using <code>numThreads</code> instances of
	 * <code>ParallelMaximizerWorker</code> to find partial maximums and then
	 * combining the results.
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public int max(LinkedList<Integer> list) throws InterruptedException {
		//int failures = 0; // initialize failures to 0
		EvenInspector eInspector=new EvenInspector("even");
                OddInspector oInspector=new OddInspector("odd");
                OrderInspector orInspector=new OrderInspector("order");
                JackInspector jInspector=new JackInspector("Jack");
                workers.add(new ParallelInspectorWorker(list,"thread1",eInspector));
                workers.add(new ParallelInspectorWorker(list,"thread2",oInspector));
                workers.add(new ParallelInspectorWorker(list,"thread3",orInspector));
                workers.add(new ParallelInspectorWorker(list,"thread4",jInspector));
                
		//System.out.println(workers.size());
		// run numThreads instances of ParallelMaximizerWorker
		for (int i=0; i < workers.size(); i++) {
			//workers.set(i, new ParallelMaximizerWorker(list));
			workers.get(i).start();
		}
               
		// wait for threads to finish               
		for (int i=0; i<workers.size(); i++)
                    try{
			workers.get(i).join();
                    }
                    catch(InterruptedException ie)
                    {
                        ie.printStackTrace();
                        
                    }
                
		
		// take the highest of the partial maximums
		// TODO: IMPLEMENT CODE HERE
		for(ParallelInspectorWorker worker:workers)
		{
                    Inspector inspector=worker.getInspector();
                    if(worker.getInspector().getName().equalsIgnoreCase("Even"))
                    {
                        System.out.println("--------------> The results form Even Inspector  <---------------");
                        EvenInspector evenInspector=(EvenInspector)inspector;
                        LinkedList<Integer> tempList=evenInspector.getList();
                        if(tempList.isEmpty())
                        {
                            failures++;
                            System.out.println("Failure: Even Inspector failed");
                        }
                        else{
                        for(int i:tempList)
                        {
                         System.out.println(" "+i);   
                        }
                        }
                        
                    }
                    else if(worker.getInspector().getName().equalsIgnoreCase("Odd"))
                    {
                         System.out.println("--------------> The results form Odd Inspector  <---------------");
                        OddInspector oddInspector=(OddInspector)inspector;
                        LinkedList<Integer> tempList=oddInspector.getList();
                        if(tempList.isEmpty())
                        {
                            failures++;
                            System.out.println("Failure: Odd Inspector failed");
                        }
                        else{
                        for(int i:tempList)
                        {
                         System.out.println(" "+i);   
                        }
                        }
                        
                    }
                    else if(worker.getInspector().getName().equalsIgnoreCase("Order"))
                    {
                         System.out.println("--------------> The results form Order Inspector  <---------------");
                        OrderInspector orderInspector=(OrderInspector)inspector;
                        LinkedList<Integer> tempList=orderInspector.getList();
                        if(tempList.isEmpty())
                        {
                            failures++;
                            System.out.println("Failure: Order Inspector failed");
                        }else{
                        for(int i:tempList)
                        {
                         System.out.println(" "+i);   
                        }
                        }
                        
                    }
                    else if(worker.getInspector().getName().equalsIgnoreCase("Jack"))
                    {
                         System.out.println("--------------> The results form Jack Inspector  <---------------");
                        JackInspector jackInspector=(JackInspector)inspector;
                        LinkedList<Integer> tempList=jackInspector.getList();
                        if(tempList.isEmpty())
                        {
                            failures++;
                            System.out.println("Failure: Jack Inspector failed");
                        }else{
                        for(int i:tempList)
                        {
                         System.out.println(" "+i);   
                        }
                        }
                        
                    }
                

		}
		
		return failures;
	}
}
