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
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.ls.LSInput;



public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
		return this.revenue;
	}



	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 




	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

	private HashMap<String,Integer> disQualifiedSellers=new HashMap<String,Integer>();

	private HashMap<String, Integer> thresholdSellers=new HashMap<String,Integer>();

	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	// private Object instanceLock = new Object(); 

    public int getSoldItemsCount() {
        return soldItemsCount;
    }

    public void setSoldItemsCount(int soldItemsCount) {
        this.soldItemsCount = soldItemsCount;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public List<Item> getItemsUpForBidding() {
        return itemsUpForBidding;
    }

    public void setItemsUpForBidding(List<Item> itemsUpForBidding) {
        this.itemsUpForBidding = itemsUpForBidding;
    }

    public int getLastListingID() {
        return lastListingID;
    }

    public void setLastListingID(int lastListingID) {
        this.lastListingID = lastListingID;
    }

    public HashMap<Integer, Item> getItemsAndIDs() {
        return itemsAndIDs;
    }

    public void setItemsAndIDs(HashMap<Integer, Item> itemsAndIDs) {
        this.itemsAndIDs = itemsAndIDs;
    }

    public HashMap<Integer, Integer> getHighestBids() {
        return highestBids;
    }

    public void setHighestBids(HashMap<Integer, Integer> highestBids) {
        this.highestBids = highestBids;
    }

    public HashMap<Integer, String> getHighestBidders() {
        return highestBidders;
    }

    public void setHighestBidders(HashMap<Integer, String> highestBidders) {
        this.highestBidders = highestBidders;
    }

    public HashMap<String, Integer> getItemsPerSeller() {
        return itemsPerSeller;
    }

    public void setItemsPerSeller(HashMap<String, Integer> itemsPerSeller) {
        this.itemsPerSeller = itemsPerSeller;
    }

    public HashMap<String, Integer> getItemsPerBuyer() {
        return itemsPerBuyer;
    }

    public void setItemsPerBuyer(HashMap<String, Integer> itemsPerBuyer) {
        this.itemsPerBuyer = itemsPerBuyer;
    }

    public HashMap<String, Integer> getDisQualifiedSellers() {
        return disQualifiedSellers;
    }

    public void setDisQualifiedSellers(HashMap<String, Integer> disQualifiedSellers) {
        this.disQualifiedSellers = disQualifiedSellers;
    }

    public HashMap<String, Integer> getThresholdSellers() {
        return thresholdSellers;
    }

    public void setThresholdSellers(HashMap<String, Integer> thresholdSellers) {
        this.thresholdSellers = thresholdSellers;
    }



	
	
	//Invariants: size(itemsUpForBidding) < serverCapacity
	// itemsPerSeller for each seller < maxSellerItems
	// number of bids placed by bidder < maxBidCount



	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */

	//Pre-conditions: sellerName,itemName NOT EQUAL TO null,
	//post-conditions: RETURNS lastListingID of added item to server
	//Exception: In case of violation of pre-conditions or seller exceeding maxSellerItems or disqualified for selling rates >75
	//or at least five items are expired before anyone bids
	//this method is locked by one thread each time to ensure the correctness of lastListingID that is modified after each submitItem call
	public synchronized int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
		if(itemsUpForBidding.size()==serverCapacity)
                {
                    return -1;
                }     
               
                if(lowestBiddingPrice<=0 &&lowestBiddingPrice>99&&biddingDurationMs<=0)
                {
                    return -1;
                }		
		if(!itemsPerSeller.containsKey(sellerName))
                {
                    itemsPerSeller.put(sellerName,1);
                    disQualifiedSellers.put(sellerName,0);
                    thresholdSellers.put(sellerName,0);
                }
                else{
                    int sellerItemsCount=itemsPerSeller.get(sellerName);
                    if(sellerItemsCount==maxSellerItems)
                    {
                        System.out.println("The seller items count exceeded");
                        return -1;
                    }
                    //   Don't forget to increment the number of things the seller has currently listed.
                    itemsPerSeller.put(sellerName,sellerItemsCount++);
                }
                //checking disqualification factors
		//: bidding price is more than $75 for at least three times
                int disqualifyingBids=nearToThreshold(sellerName, lowestBiddingPrice);                

		if(disqualifyingBids >=3){
                    Boolean disqualified=false;
                    for(int i:itemsAndIDs.keySet())
                    {
                        Item currentItem=itemsAndIDs.get(i);
                        if(currentItem.seller().equals(sellerName)&& currentItem.lowestBiddingPrice()<75)
                        {
                            disqualified=true;
                            break;
                        }
                    }
                    if(disqualified==true)
                    {
                    System.out.println(sellerName+" : Seller is disqualified for having "+thresholdSellers.get(sellerName)+" items <$75 ");
                    return -1;
                    }
                }
			
		// : at least 5 items are expired before they are bid
		int expiredItemsCount = expiredItems(sellerName);
                if(expiredItemsCount>=5)
                {
                    System.out.println(sellerName+" : Seller has more than 5 expired items");
                    return -1;
                }

			
                
               
		//only if the seller is  qualified and reach until here
		// the seller can add his item to the server
		//Pseudocode: INCEREMENT lastListingID
                lastListingID++;
		//Pseudocode: INITIALIZE NEW item as ()
                Item item=new Item(sellerName,itemName,lastListingID,lowestBiddingPrice,biddingDurationMs);
		//Pseudocode: ADD item with lastListingID to itemsAndIDs
                itemsAndIDs.put(lastListingID,item);
		//Pseudocode: ADD item to itemsUpForBidding
                itemsUpForBidding.add(item);
		//Pseudocode: ADD (lastListingID ,lowestBiddingPrice) to highestBids
                highestBids.put(lastListingID, lowestBiddingPrice);
                //   If the seller is a new one, add them to the list of sellers.
                System.out.println("Seller : "+sellerName +" added "+item.name()+ " to the server");

		return lastListingID;
	}
	// : at least 5 items are expired before they are bid
	public synchronized int expiredItems(String sellerName)
	{
		int expired=0;
                Item item;
			for(int i:itemsAndIDs.keySet())
                        {
                             item=itemsAndIDs.get(i);
				if(item.biddingOpen()== false)
                                {

					if(item.seller().equals(sellerName) && (item.lowestBiddingPrice()== highestBids.get(item.listingID())))
                                        {
                                            expired++;
                                        }
                                }
                        }
			
			disQualifiedSellers.put(sellerName,expired);
		return expired;
	}
	//checking disqualification factors
	//: bidding price is more than $75 for at least three times
	public synchronized int nearToThreshold(String sellerName,int lowestBiddingPrice)
	{
            int count=thresholdSellers.get(sellerName);
            if(lowestBiddingPrice<75)
            {
                count++;
            }
            if(lowestBiddingPrice>75)
            {

                count=0;
            }

                thresholdSellers.put(sellerName,count);
                return count;

			
	}

	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	//pre-condition: -
	//post-condition: returns list of itemsUpForBidding
	public synchronized List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.
		//Pseudocode: INITIALIZE NEW ARRAYLIST OF ITEM AS items
                List<Item> items=new ArrayList<Item>();
		/*Pseudocode: FOR EACH item in itemsUpForBidding
			IF item.biddingOpen() is true
				ADD item to items
			END IF
		END FOR
                */
                for(Item item:itemsUpForBidding)
                {
                    if(item.biddingOpen())
                    {
                        items.add(item);
                    }
                }

		
		return items;
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	//pre-conditions: bidderName,listingID NOT NULL
	//post-conditions: returns TRUE if bidding possible
	//Exception: If the bidder exceeds maxBidCount or item expires or cannot be bid upon , RETURNS FALSE
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{	// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//  put lock to itemsAndIDs so that present items in list are consistent till bidding success or failure
		synchronized(itemsAndIDs)
                {   if(itemsAndIDs.containsKey(listingID))
                    {
			Item item=itemsAndIDs.get(listingID);
                        if(item!=null)
			{
                            // See if it can be bid upon.
                            // put lock to itemsUpForBidding so that the list cannot be modified till bidding success or failure
                            synchronized(itemsUpForBidding)
                            {
				if(itemsUpForBidding.contains(item)&&item.biddingOpen()==true)
                                {
                                    //   See if this bidder has too many items in their bidding list.
                                    // PUT LOCK TO ITEMSPERBUYER LIST
                                    synchronized(itemsPerBuyer)
                                    {
                                        
                                       
					
					//   Get current bidding info.
                                        if (this.itemsUpForBidding.contains(item) && item.biddingOpen()) {
                                            if(biddingAmount>highestBids.get(listingID))
                                            {
                                                //checking if bidder exists in buyers list
                                                if(!highestBidders.containsKey(listingID)|| !itemsPerBuyer.containsKey(bidderName))
                                                {
                                                    itemsPerBuyer.put(bidderName,1);
                                                }
                                                else if(highestBidders.containsKey(listingID)&&!highestBidders.get(listingID).equals(bidderName))
                                                {
                                                    
                                                    Integer bidderCount=itemsPerBuyer.get(bidderName);
                                                    if(bidderCount!=null&&bidderCount>=maxBidCount)
                                                    {
                                                        return false;
                                                    }
                                                    else if(bidderCount<maxBidCount)
                                                    {
                                                        itemsPerBuyer.put(bidderName,bidderCount+1);
                                                    }
                                                }

                                                //   See if they already hold the highest bid.
                                                String lastBidderName=highestBidders.get(listingID);
                                                if(lastBidderName!=null&&lastBidderName.equals(bidderName))
                                                {
                                                    System.out.println("Same buyer at highest is bidding");
                                                    return false;

                                                }
                                                else{
                                                    if(lastBidderName!=null&&highestBids.get(listingID)<biddingAmount)
                                                    {
                                                        itemsPerBuyer.put(lastBidderName,itemsPerBuyer.get(lastBidderName)-1);
                                                        highestBidders.put(listingID, bidderName);
                                                        highestBids.put(listingID,biddingAmount);
                                                        return true;
                                                    }
                                                    else if(lastBidderName==null)
                                                    {
                                                        if(biddingAmount>item.lowestBiddingPrice())
                                                        {
                                                        highestBidders.put(listingID, bidderName);
                                                        highestBids.put(listingID,biddingAmount);
                                                        return true;
                                                        }
                                                    }

                                                }

                                            }
                                        }
                                        


                                    }
                                }
                            }
                        }
                    }
                }
            return false;
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	//pre-conditions: bidder?Name is not null,listingID>=0
	//post-conditions: results 1,2,3
	//Exceptions: violation of pre-conditions would result -1
	public synchronized int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.		
                Item item=itemsAndIDs.get(listingID);
                if(item!=null&& !item.biddingOpen())
                {
                    if(itemsUpForBidding.contains(item))
                    {
                        //     Remove item from the list of things up for bidding.
                        itemsUpForBidding.remove(item);
                    }
                    // Decrease the count of items being bid on by the winning bidder if there was any...
                    int sellerCount = this.itemsPerSeller.get(item.seller()); 
                    this.itemsPerSeller.put(item.seller(), sellerCount-1);
                    String highestBidder=highestBidders.get(item.listingID());
                    if(highestBidder!=null &&highestBidder.equals(bidderName))
                    {
                        int bidderCount=itemsPerBuyer.get(bidderName);
                        if(bidderCount>0)
                        {
                            itemsPerBuyer.put(bidderName,bidderCount-1);
                        }
                        int bid=highestBids.get(listingID);
                        revenue=revenue+bid;
                        soldItemsCount++;
                        //this bidder has won
                        return 1;
                    }
                
                    else{
                        // this bidder is not the winner.....
                        return 3;
                        
                    }                    
                }
                if(item==null)
                {
                    return 3;
                }
                // the item is  open for auction
              
                return 2;
                
        }

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	//pre-condition: listingID is NOT -1
	//post-condition: returns current highest bid PRICE if listingID is >-1 ELSE -1
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		
                if(listingID<0)
                {
                    return -1;
                }
                else{
                    synchronized(highestBids)
                    {
                        if(highestBids.containsKey(listingID))
                        {
                            return highestBids.get(listingID);
                        }
                    }
                }
			
		
		return -1;
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist,
	 * false otherwise
	 */
	//pre-conditions: listingID is > -1
	//post-conditions: returns true if it is in the list of items and active for bidding but is not bid upon yet
	//else returns true
	public Boolean itemUnbid(int listingID) {
		// TODO: IMPLEMENT CODE HERE
		//put lock to itemsAndIDs to access the current itemsAndIDs and not be modified during the execution of this method

		synchronized(itemsAndIDs)
                {
                    if(itemsAndIDs.containsKey(listingID))
                    {
                            Item item=itemsAndIDs.get(listingID);
                            //put lock to itemsUpForBidding to check current status of item
                            synchronized(itemsUpForBidding)
                            {
                                if(itemsUpForBidding.contains(item))
                                {
                                    //put lock to highestbids so that the current bidding price is not modified by any other thread during this process
                                    if(item.lowestBiddingPrice()==highestBids.get(listingID))
                                    {
                                        return true;
                                    }
                                    else{
                                        return false;
                                    }
                                }
                                else{
                                    return true;
                                }

                            }
                    }
                    else{
                        return true;
                    }
                }
             
		

		
	}

	//HighestBids contains (key,value) (integer,integer) as (item id, highest bid price)
	public int totalOfHighestBids(){
                int totalHighestBids=0;
                Item item;
		for(int itemid:highestBids.keySet())
                {
                 item=itemsAndIDs.get(itemid);
                 if(item.biddingOpen()==false&&highestBids.containsKey(itemid))
                 {
                     if(highestBids.get(itemid)!=item.lowestBiddingPrice())
                     {
                         totalHighestBids+=highestBids.get(itemid);
                     }
                 }
                }
		return totalHighestBids;



	}

	public void getHighestBiddersNames(){

		//the hashMap highestBidders contains (key,value) (Integer,String) as (itemid,biddername)
                Item item;
                for(int itemid:highestBidders.keySet())
                {
                    item=itemsAndIDs.get(itemid);
                    if(item!=null&&item.biddingOpen()==false&&highestBidders.containsKey(itemid))
                    {
                        System.out.println(" "+ itemid+" "+highestBidders.get(itemid));
                    }
                }
		
	}
	//itemsAndIDs and highestBids
	public int  getTotalProfit(){
            int totalProfit=0;
            Item item;
            for(int itemid:highestBids.keySet())
            {
                item=itemsAndIDs.get(itemid);
                if(item!=null)
                {
                    int highestBid=highestBids.get(itemid);
                    int profit=highestBid-(item.lowestBiddingPrice());
                    if(profit>0)
                    {
                        totalProfit+=profit;
                        System.out.println(" "+itemid+" Item named : "+item.name()+" put by "+item.seller()+" at lowestPrice : "+ item.lowestBiddingPrice() +" and sold at "+highestBid +" making a profit : "+profit);
                    }
                }
            }
	return totalProfit;

	}
        
        public void getStaticItems()
        {
            System.out.print("Items static: ");
            Item item;
            int count=0;
            for(int itemid:itemsAndIDs.keySet())
            {
                if(itemUnbid(itemid))
                {
                    count++;
                }
            }
            System.out.println(" "+count);
            System.out.println("Items Expired: ");
            for(int itemid:itemsAndIDs.keySet())
            {
                item=itemsAndIDs.get(itemid);
                if(highestBids.get(itemid)==item.lowestBiddingPrice()&&!item.biddingOpen())
                {
                    System.out.println("item : "+item.name());
                }
            }
        }


}
