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
import java.util.*;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelMaximizer {
	

        static int numThreads = 4; // number of threads for the maximizer
	static int numElements = 1000; // number of integers in the list
	ArrayList<ParallelMaximizerWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);

	public ParallelMaximizer(int numThreads) {
            workers = new ArrayList<ParallelMaximizerWorker>(numThreads);
            
         //   System.out.println("Workers size: "+workers.size());
                
	}


	
	public static void main(String[] args) {
		
		
		
		LinkedList<Integer> list = new LinkedList<Integer>();
		Random rand=new Random();
		// populate the list
		// TODO: change this implementation to test accordingly
		for (int i=0; i<numElements; i++)
                {
                    int randInt=rand.nextInt(10000);
                    list.add(randInt);
                }
		
                
               ParallelMaximizer maximizer = new ParallelMaximizer(numThreads);

		// run the maximizer
		try {
			System.out.println(maximizer.max(list));
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		int max = Integer.MIN_VALUE; // initialize max as lowest value
		
                
                for(int i=1;i<=numThreads;i++)
                {
                workers.add(new ParallelMaximizerWorker(list,"thread"+i));
                }
                
		System.out.println("Workers size:"+workers.size());
                
                
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
		for(ParallelMaximizerWorker worker:workers)
		{
                    System.out.println("The partial max of worker "+ worker.getTName()+" is: "+worker.getPartialMax());
			if(max<worker.getPartialMax())
			{
                            System.out.println("The partial max of worker "+ worker.getTName()+" is greater than Previous Max");
				max=worker.getPartialMax();
			}

		}
                System.out.println("From threads : Final max = "+max);
		return max;
                
	}
	
}
