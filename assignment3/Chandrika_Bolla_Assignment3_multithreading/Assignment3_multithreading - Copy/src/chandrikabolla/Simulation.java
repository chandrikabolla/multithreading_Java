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
/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */ 
public class Simulation
{
    public static void main(String[] args)
    {                
        int nrSellers = 50;
        int nrBidders = 20;
        
        Thread[] sellerThreads = new Thread[nrSellers];
        Thread[] bidderThreads = new Thread[nrBidders];
        Seller[] sellers = new Seller[nrSellers];
        Bidder[] bidders = new Bidder[nrBidders];
        
        // Start the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            sellers[i] = new Seller(
            		AuctionServer.getInstance(), 
            		"Seller"+i, 
            		100, 50, i
            );
            sellerThreads[i] = new Thread(sellers[i]);
            sellerThreads[i].start();
        }
        
        // Start the buyers
        for (int i=0; i<nrBidders; ++i)
        {
            bidders[i] = new Bidder(
            		AuctionServer.getInstance(), 
            		"Buyer"+i, 
            		1000, 20, 150, i
            );
            bidderThreads[i] = new Thread(bidders[i]);
            bidderThreads[i].start();
        }
        
        // Join on the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            try
            {
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // Join on the bidders
        for (int i=0; i<nrBidders; ++i)
        {
            try
            {
                bidderThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        // TODO: Add code as needed to debug
        //Pseudocode: SET auctionServer TO AuctionServer.getIntance()
        AuctionServer auctionServer=AuctionServer.getInstance();
        
        // from variables in auctionServer
        //: soldItemsCount
        //Pseudocode: CALL auctionServer.soldItemsCount() AND PRINT
        System.out.println("Sold items count: "+auctionServer.soldItemsCount());
        
        //: revenue
        //Pseudocode: CALL autionServer.revenue() AND PRINT
        System.out.println("Total revenue: "+auctionServer.revenue());

        //: highestBids CONTAINS (key,value) (listingID and the highest bid price)
        //: we can calculate the the total of bid prices
        //Pseudocode: CALL auctionServer.totalHighestBids AND PRINT
        System.out.println("HighestBids : "+auctionServer.totalOfHighestBids());


        //: highestBidders CONTAINS (key,value) (listingID and bidderName
        //: we can calculate the total highestbidders size
        //: and also print highest bidders name for every item
        //Pseudocode: CALL auctionServer.getHighestBidders().size() and PRINT
        System.out.println("No of highestBidders: "+ auctionServer.getHighestBidders().size());
        //Pseudocode: CALL auctionServer.getHighestBiddersNames()
//        auctionServer.getHighestBiddersNames();

        
        
        int totalCashSpent=0;
        for(Bidder buyer:bidders)
        {
            totalCashSpent+=buyer.cashSpent();
        }
        System.out.println("Total cash spent by bidders: "+totalCashSpent);
        
        //:profit
        //Pseudocode: CALL auctionServer.getTotalProfit() AND PROFIT
        System.out.println("Total profit: "+ auctionServer.getTotalProfit());
        //:static items
        auctionServer.getStaticItems();
        System.out.println("Total items entered into the auction server : "+auctionServer.getLastListingID());

        
        
    }
}
