package hw2;

/**
 *  @author Chandrika
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



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
		IF itemsUpForBidding.size() EQUALS serverCapacity
			RETURN -1
		END IF

		//   If the seller is a new one, add them to the list of sellers.
		IF itemsPerSeller NOT CONTAINS sellerName
			PUT (sellerName,1) TO itemsPerSeller
			PUT (sellerName,0) TO disqualfiedSellers
		ELSE
			INITIALIZE sellerItemsCount TO itemsPerSeller.get(sellerName)
			IF 	sellerItemsCount EQUALS maxSellerItems
				RETURN -1
			END IF
		//   If the seller has too many items up for bidding, don't let them add this one.
			PUT (sellerName,sellerItemsCount++) TO itemsPerSeller
		END IF

		IF lowestBiddingPrice <=0 AND lowestBiddingPrice >99 AND biddingDurationMs <=0
			RETURN -1
		END IF

		//checking disqualification factors
		//: bidding price is more than $75 for at least three times
		INITIALIZE disqualifyingBids TO thresholdSellers.get(sellerName)

		IF disqualifyingBids >=3
			RETURN -1
		END IF
		// : at least 5 items are expired before they are bid
		SET ExpiredItemsCount to disqualifiedSellers.get(sellerName)

			IF ExpiredItemsCount >=5
				RETURN -1
		//only if the seller is  qualified and reach until here
		// the seller can add his item to the server
		INCEREMENT lastListingID
		INITIALIZE NEW item as ()
		ADD item with lastListingID to itemsAndIDs
		ADD item to itemsUpForBidding
		ADD (lastListingID ,lowestBiddingPrice) to highestBids

		//   Don't forget to increment the number of things the seller has currently listed.



		return lastListingID;
	}
	// : at least 5 items are expired before they are bid
	public synchronized int expiredItems(String sellerName)
	{
		INITIALIZE expired TO 0
			FOR i=0 to i< itemsAndIDs.size()
				SET item to itemsAndIDs.get(i)
				IF item.isbiddingOn() is false

					IF (item.seller EQUALS sellerName ) AND item.lowestBiddingPrice EQUALS highestbids.get(item.listingId())
						INCREMENT expired
			END FOR
			PUT (sellerName,expired) TO disqualifiedSellers
		RETURN expired
	}
	//checking disqualification factors
	//: bidding price is more than $75 for at least three times
	public synchronized int nearToThreshold(String sellerName)
	{
		INITIALIZE count to 0
			FOR EACH item in itemsPerSeller

				IF item.lowestBiddingPrice >75
					INCREMENT count
				END IF



			END FOR
			PUT (sellerName,count) TO thresholdSellers
		RETURN count

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
		INITIALIZE NEW ARRAYLIST OF ITEM AS items
		FOR EACH item in itemsUpForBidding
			IF item.biddingOpen() is true
				ADD item to items
			END IF
		END FOR

		
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
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//  put lock to itemsAndIDs so that present items in list are consistent till bidding success or failure
		synchronize itemsAndIDs
				IF itemsAndIDs CONTAINS listingID
					INITIALIZE item to itemsAndIDs.get(listingID)
					IF item NOT EQUALS null
					{
						//   See if it can be bid upon.
						// put lock to itemsUpForBidding so that the list cannot be modified till bidding success or failure
						synchronize itemsUpForBidding
						IF itemsUpForBidding CONTAINS item AND item.biddingOpen() IS true
						{
							//   See if this bidder has too many items in their bidding list.
							// PUT LOCK TO ITEMSPERBUYER LIST
							synchronize itemsPerBuyer
							INITIALIZE bidderCount to itemsPerBuyer.get(bidderName)
							IF bidderCount >= maxBidCount
								RETURN false
							END IF
							//   Get current bidding info.
							IF biddingAmount > highestBids.get(listingID)
								//checking if bidder exists in buyers list
								IF highestBidders NOT CONTAINS listingID OR itemsPerBuyer NOT CONTAINS bidderName
									PUT (bidderName,1) to itemsPerBuyer
								ELSE IF highestBidders CONTAINS listingId AND highestBidders.get(listingID) NOT EQUALS bidderName
									INITIALIZE numberOfItems to itemsPerBuyer.get(bidderName)
									IF numberOfItems < maxBidCount
										PUT (bidderName,numberOfItems+1) TO itemsPerBuyer
								    END IF
								END IF
							//   See if they already hold the highest bid.
							IF highestBidders.get(listingID) EQUALS lastbiddername
									PRINT "same buyer at highest is bidding"
									//   See if the new bid isn't better than the existing/opening bid floor.
									IF highestBids.get(listingID) < biddingAmount

									//   Decrement the former winning bidder's count
									PUT (lastbiddername,itemsPerBuyer.get(lastbiddername)-1) TO itemsPerBuyer
									//   Put your bid in place
									PUT (listingID,bidderName) IN highestbidders
									PUTS (listingID,biddingAmount) IN highestbids
									RETURN true

								END IF

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
		INITIALIZE item to FIND listingID in itemsAndIDs
			IF item NOT null AND item.biddingOpen() IS true
			{
				IF itemsUpForBidding CONTAINS item
				{
					//     Remove item from the list of things up for bidding.
					REMOVE item FROM itemsUpForBidding
				}
				//     Decrease the count of items being bid on by the winning bidder if there was any...
				INITIALIZE highestbidder to highestBidders.get(item.listingID)
				IF highestbidder NOT null AND maxbidder EQUALS bidderName
					SET bidderCount TO itemsPerBuyer.get(item)
					IF bidderCount NOT null AND bidderCount >0
					 	PUT (bidderName,bidderCount-1)
					END IF
					SET bid TO highestBids.get(item.listingID)
					ADD bid TO revenue
					INCREMENT soldItemsCount
					//this bidder has won
					RETURN 1

				ELSE
						// this bidder is not the winner.....
						RETURN 3
				END IF






			}
			// the item is not open for auction
			RETURN 2



		

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
		IF listingID < 0
			RETURN -1
		ELSE
				//put a lock to highestbids so that it is not modified during the calculation of correct current price
				synchronized(highestBids)
				{
					IF highestBids CONTAINS listingID
						RETURN highestBids.get(listingID)
					END IF
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

		synchronize itemsAndIDs
		IF itemsAndIDs CONTAINS listingID
		{

			SET item to itemsAndIDs.get(listingID)
			//put lock to itemsUpForBidding to check current status of item
			synchronize itemsUpForBidding
			IF itemsUpForBidding CONTAINS item

			//put lock to highestbids so that the current bidding price is not modified by any other thread during this process
			IF highestBids CONTAINS listingID
			IF item.lowestBiddingPrice() EQUALS highestBids.get(listingID)
			RETURN true
			ELSE
			RETURN false
			END IF
			ELSE
			RETURN false
			END IF


			RETURN false;
		}
		RETURN false
	}

	//HighestBids contains (key,value) (integer,integer) as (item id, highest bid price)
	public int totalOfHighestBids(){

		SET totalHighestBids TO 0
		FOR EACH itemid in highestBids.keySet()
				SET item TO itemsAndIDs.get(i)
				IF item.biddingOpen() IS false AND highestBids CONTAINS itemid
					IF highestBidPrice of itemid NOT EQUALS item.lowestBiddingPrice
						ADD highestBids.get(itemid) TOtotalHighestBids
					END IF
				END IF
		END FOR
		RETURN totalHighestBids



	}

	public void getHighestBiddersNames(){

		//the hashMap highestBidders contains (key,value) (Integer,String) as (itemid,biddername)
		FOR EACH itemid in highestBidders.keySet()
		SET item TO itemsAndIDs.get(i)
		IF item.biddingOpen() IS false AND highestBidders CONTAINS itemid
		PRINT itemid :
		PRINT highestBidders.get(itemid)
		PRINT LINE
		END IF
		END FOR
	}
	//itemsAndIDs and highestBids
	public int  getTotalProfit(){
		SET totalProfit TO 0
		FOR EACH itemid in highestBids.keySet()
				SET item to itemsAndIDs.get(itemid)
				IF item NOT EQUALS null
					SET highestBid to highestBids.get(itemid)
					SET profit TO :
							SUBTRACT item.lowestBiddingPrice FROM highestBid
					IF profit >0
						ADD profit TO totalProfit
						PRINT " itemid : profit "
					END IF
				END IF
		END FOR

		RETRUN totalProfit

	}


}
 