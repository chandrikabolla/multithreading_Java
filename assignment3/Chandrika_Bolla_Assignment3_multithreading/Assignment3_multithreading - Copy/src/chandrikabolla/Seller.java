/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chandrikabolla;

/**
 *
 * @author chand
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Seller implements Client
{
	private static final int MaxItems = 100;
	
	private String name;
	private int cycles;
	private int maxSleepTimeMs;
	
	private List<String> items;
	private Random rand;
	private boolean disqualifyingPrice;
	private AuctionServer server;
	
	public Seller(AuctionServer server, String name, int cycles, int maxSleepTimeMs, long randomSeed)
	{
		this.name = name;
		this.cycles = cycles;
		this.maxSleepTimeMs = maxSleepTimeMs;
		
		// Generate items
		this.rand = new Random(randomSeed);
        int itemCount = MaxItems;
        this.items = new ArrayList<String>();
        this.disqualifyingPrice=false;
        for (int i = 0; i < itemCount; ++i)
        {
            items.add(this.name() + "#" + i);
        }
        
        this.server = server;
	}
	
	@Override
	public String name()
	{
		return this.name;
	}

	@Override
    public void run()
    {			
		for (int i = 0; i < this.cycles && this.items.size() > 0; ++i)
	    {
	    	int index = this.rand.nextInt(this.items.size());
	    	String item = this.items.get(index);
	    	int lowestBiddingPrice=this.rand.nextInt(100);
	    	int listingID = server.submitItem(this.name(), item, lowestBiddingPrice, this.rand.nextInt(100) + 100);  
	    	System.out.println("Seller "+this.name()+" attempted to add item to server at capacity ---------> "+ (80-server.getItemsUpForBidding().size())+" an item with price "+lowestBiddingPrice);
	    	if (listingID != -1)
	    	{
	    		this.items.remove(index);
                        System.out.println("Seller : "+this.name()+" put item : "+item +" on sale");
	    	}

    		try
            {
                Thread.sleep(this.rand.nextInt(this.maxSleepTimeMs));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                return;
            }
	    }
    }
}
