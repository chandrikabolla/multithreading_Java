

import static org.junit.Assert.*;
import java.util.*;


import org.junit.Test;

import bolla_A.ParallelMaximizer;

public class PublicTest {
        private  LinkedList<Integer> staticList;
	private int	threadCount = 4; // number of threads to run
	private ParallelMaximizer maximizer;
	
	@Test
	public void compareMax() {
		int size = 10000; // size of list
		LinkedList<Integer> list = new LinkedList<Integer>();
                staticList=new LinkedList<>();
		Random rand = new Random();
		int serialMax = Integer.MIN_VALUE;
		int parallelMax = 0;
		// populate list with random elements
		for (int i=0; i<size; i++) {
			int next = rand.nextInt();
			list.add(next);
                        staticList.add(next);
			serialMax = Math.max(serialMax, next); // compute serialMax
		}
                System.out.println("Serial Max = "+serialMax);
                for(int i=1;i<=10;i++)
                {
		// try to find parallelMax
		try {
                        System.out.println("list size"+staticList.size());
                        if(list.isEmpty())
                        {
                        for(int j=0;j<size;j++)
                        {
                            list.add(staticList.get(j));
                        }
                        }
                        maximizer= new ParallelMaximizer(threadCount);
			parallelMax = maximizer.max(list);
                        
                        
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("The test failed because the max procedure was interrupted unexpectedly.");
		} catch (Exception e) {
			e.printStackTrace();
			fail("The test failed because the max procedure encountered a runtime error: " + e.getMessage());
		}
		
		assertEquals("The serial max doesn't match the parallel max", serialMax, parallelMax);
                }
	}


}
