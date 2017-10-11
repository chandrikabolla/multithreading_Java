package hw2;


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
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        SET auctionServer TO AuctionServer.getIntance()
        // TODO: Add code as needed to debug
        // variables in auctionServer
        //: soldItemsCount
        CALL auctionServer.soldItemsCount() AND PRINT
        //: revenue
        CALL autionServer.revenue() AND PRINT

        //: highestBids CONTAINS (key,value) (listingID and the highest bid price)
        //: we can calculate the the total of bid prices
        CALL auctionServer.totalHighestBids AND PRINT


        //: highestBidders CONTAINS (key,value) (listingID and bidderName
        //: we can calculate the total highestbidders size
        //: and also print highest bidders name for every item
        CALL auctionServer.getHighestBidders().size() and PRINT
        CALL auctionServer.getHighestBiddersNames()

        //:profit
        CALL auctionServer.getTotalProfit() AND PROFIT

        //:static items



        
    }
}