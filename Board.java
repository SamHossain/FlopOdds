import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Random;

public class Board {
    
    
    public static int checkStraight(Card[] cards) {
        int count = 0; //if count goes over 3 return false
        int high = 0;
        for (int i = 0 ; i < cards.length - 1 ; i++) {
            if ((cards[i].getValue() - 1) == cards[i + 1].getValue()) {
                count++;
                high = Math.max(cards[i].getValue(), high);
            }
            else
                count = 0 ;
            
            if (count > 3) //dont have to keep going since we are already sorted
                break;
        }
        if (count > 3)
            return high;
        return 0;        
    }
    
    

    /**
     * checks for a flush
     * 
     * @param cards
     * @return the highest flush if exists, else null
     */
    public static int checkFlush(Card[] cards) {
        int dCount = 0;
        int sCount = 0;
        int hCount = 0;
        int cCount = 0;
        int flushSuit = -1;
        Card[] flush = new Card[5];
        
        for (int i = 0; i < cards.length; i++) {
            if (cards[i].getSuit() == 0)
                dCount++;
            if (cards[i].getSuit() == 3)
                cCount++;
            if (cards[i].getSuit() == 2)
                hCount++;
            if (cards[i].getSuit() == 1)
                sCount++;
            
        }
        
        if (dCount > 4)
            flushSuit = 0;
        else if (sCount > 4)
            flushSuit = 3;
        else if (hCount > 4)
            flushSuit = 1;
        else if (cCount > 4)
            flushSuit = 2;
        
        for (int i = 0; i < cards.length; i++) {
           if (cards[i].getSuit() == flushSuit)
               return cards[i].getValue();
        }
        return 0;

    }
    
    //motivated from the flush checker, make two maps and track progress of hands
    /**
     * Takes in an ordered set of 7 cards returns a numeric value to the best 5
     * cards-- higher the better
     * 
     * @param cards
     * @return highest numeric representation of highest 5 cards
     */
    
    public static int bestFive(Card[] cards) {
        int handRank = 0; //returns the highest hand as a numeric value
        int onePair = 13;
        int twoPair = 13 + 13*13;
        int ttrips = 26 + 13*13;
        int straight = 13 + ttrips;
        int flush = 13 + straight;
        int house = 13*13 + flush;
        int quads = house + 13;
        int straightFlush = quads + 13;
        
        //create map to trach how many of each rank are present (histogram)
        HashMap<Integer, Integer> rankCount = new HashMap<>();
        //track the char
        HashMap<Integer, Integer> suitCount = new HashMap<>();
        
        rankCount.put(13, 0);
        rankCount.put(12, 0);
        rankCount.put(11, 0);
        rankCount.put(10, 0);
        rankCount.put(9, 0);
        rankCount.put(8, 0);
        rankCount.put(7, 0);
        rankCount.put(6, 0);
        rankCount.put(5, 0);
        rankCount.put(4, 0);
        rankCount.put(3, 0);
        rankCount.put(2, 0);
        rankCount.put(1, 0);
        
        suitCount.put(0,0);
        suitCount.put(1,0);
        suitCount.put(2,0);
        suitCount.put(3,0);

        for(int i = 0; i < cards.length; i++) {
            rankCount.replace(cards[i].getValue(), rankCount.get(cards[i].getValue()) + 1); //increments the map based on ith card in cards
            suitCount.replace(cards[i].getSuit(), suitCount.get(cards[i].getSuit()) + 1); 
            
        }
        
        //compute statistics for the value -- interested in counts and min and max
        //we will use these to optimize 
        int maxRank = 0;
        int minRank = 0;
        //iterate through the map and set our max non zero histogram value
        for (int tryRank : rankCount.keySet()) {
            if (rankCount.get(tryRank) == null)
                continue;
            if (rankCount.get(tryRank) > 0) {
                maxRank = tryRank;
                break;
            }
        }
        //analagous min
        for (int tryRank : rankCount.keySet()) {
            if (rankCount.get(tryRank) == null)
                continue;
            if (rankCount.get(tryRank) > 0) {
                minRank = tryRank;
                //no longer need a break since the ordered set is decreasing and we want smallest non zero value
                
            }
        }
        
        if (checkStraight(cards) > 0)
            handRank = Math.max(handRank, ttrips + checkStraight(cards));
        if (checkFlush(cards) > 0)
            handRank = Math.max(handRank, straight + checkFlush(cards));
        
        
        
        //keeps track if we see trips or dubs and records the corresponding card rank
        boolean[] trips = new boolean[3];
        int[] tripRanks = new int[3];
        
        boolean[] dubs = new boolean[3];
        int[] dubRanks = new int[3];
        
        
        for(int tryRank : rankCount.keySet()) {
            int pair = 0;
            int rank = 0;
            if (rankCount.get(tryRank) == null)
                continue;
            if (rankCount.get(tryRank) == 4) {//impissible to have another 4 so update hand rank
                handRank = Math.max(handRank, tryRank + house); //4 of a kind
            }
            
            if (rankCount.get(tryRank) == 3) { //if there are trips, update our pairs and what rank they are in tripRanks
                for (int i = 0; i < trips.length; i++) {
                    if (trips[i]) //already seen one triple
                        continue;
                    else {
                        trips[i] = true;
                        tripRanks[i] = tryRank;
                    }
                }
                    
            }
            
            if (rankCount.get(tryRank) == 2) {
                for (int i = 0; i < dubs.length; i++) {
                    if (dubs[i])
                        continue;
                    else {
                        dubs[i] = true;
                        dubRanks[i] = tryRank;
                    }
                }
            }
        }
        if (trips[0] && dubs[0]) {//indicates that there are Full Houses present (only ranks by the trip!!!)
             int maax = Math.max(tripRanks[1], tripRanks[0]);   
             int miin = Math.min(tripRanks[1], tripRanks[0]);
             handRank = Math.max(handRank, flush + 13*maax + Math.max(miin, dubRanks[0]));    
        }
        else if(trips[0]) //indicates if we have a set
            handRank = twoPair + Math.max(tripRanks[1], tripRanks[0]);
        else if(dubs[0] && dubs[1] )//indicates 2 pair
            handRank = Math.max(handRank, onePair + Math.max(dubRanks[2],Math.max(dubRanks[0], dubRanks[1]))); //only ranks by the top
        else //check if theres a pair
            handRank = Math.max(handRank, dubRanks[0]);
        
    return Math.max(handRank, maxRank); //returns either high card (maxRank or a hand)
    }

    public static Card convert(String s) {
        int rank = 0;
        int suit = 0;
        
        switch (s.charAt(0)) {
            case 'A':
                rank = 13;
                break;
            case 'K':
                rank = 12;
                break;
            case 'Q':
                rank = 11;
                break;
            case 'J':
                rank = 10;
                break;
            case '1':
                rank = 9;
                break;
            case '9':
                rank = 8;
                break;
            case '8':
                rank = 7;
                break;
            case '7':
                rank = 6;
                break;
            case '6':
                rank = 5;
                break;
            case '5':
                rank = 4;
                break;
            case '4':
                rank = 3;
                break;
            case '3':
                rank = 2;
                break;
            case '2':
                rank = 1;
                break;
     
        }
        
        switch (s.charAt(s.length() - 1)) {
            case 'd':
                suit = 0;
                break;
            case 's':
                suit = 1;
                break;
                
            case 'h':
                suit = 2;
                break;
                
            case 'c':
                suit = 3;
                break;
        }
        
        
        
      return new Card(rank, suit);  
        
    }
    
    public static Card[] convertHole(String s) {
        Card[] holes = new Card[2];
        
        if (s.charAt(1) == 'd' || s.charAt(1) == 's' || s.charAt(1) == 'h' || s.charAt(1) == 'c') {
            holes[0] = convert(s.substring(0, 2));
            holes[1] = convert(s.substring(2));
        }
        else if (s.charAt(1) == '0' && (s.charAt(2) == 'd' || s.charAt(2) == 's' || s.charAt(2) == 'h' || s.charAt(2) == 'c')) { //consider the case of 10
            holes[0] = convert(s.substring(0, 3));
            holes[1] = convert(s.substring(3));
        }
        
        else if (s.charAt(1) == '0') {
            String c1 = "10" + s.charAt(s.length() - 1);
            holes[0] = convert(c1);
            holes[1] = convert(s.substring(2));
        }
        else {
            String c1 = "" + s.charAt(0) + s.charAt(s.length() - 1);
            holes[0] = convert(c1);
            holes[1] = convert(s.substring(1));
        }
        //deal with 10s
        return holes;
    }
    
    
    
    public static void main(String[] args) {
        Random rand = new Random();
        ArrayList<Card> deck = new ArrayList<>();
        ArrayList<Card> myHand = new ArrayList<>(7);
        ArrayList<Card> vHand = new ArrayList<>(7);
        int count = 0;
        int total = 0;
        Scanner sc = new Scanner(System.in);
        String hole = "";
        Card[] holeCards = new Card[2];
        Card[] vHoleCards = new Card[2];
        //must feed in ordered cards
        for (int i = 13; i > 0; i--)
            for (int j = 0; j < 4; j ++)
                deck.add(new Card(i , j));
        
        //take my two inserted cards out
        System.out.println("[p]reflop or p[o]stflop");
        String stage = sc.next();
        if (stage.equals("p")) {
            System.out.println("Hole cards [AKc or 7c6s]") ;
                hole = sc.next();
                holeCards = convertHole(hole); 
            System.out.println("Villian? [AKc or 7c6s]") ;
                hole = sc.next();
                vHoleCards = convertHole(hole);    
           
        }
        
        for (Card c : holeCards) {
            deck.remove(c);
        }
        
        for (Card c: vHoleCards) {
          deck.remove(c); //we know these cards wont be in deck either
        }
        
        
        
        //we need a way to systematically get the cards in sorted order
        for (int i = 0; i < 47; i++)
            for (int j = i + 1;j < 47; j++)
                for (int k = j + 1;k < 47; k++)
                    for (int l = k + 1;l < 47; l++)
                        for (int m = l + 1;m < 47; m++) {
                            myHand.add(deck.get(i));
                            myHand.add(deck.get(j));
                            myHand.add(deck.get(k));
                            myHand.add(deck.get(l));
                            myHand.add(deck.get(m)); 
                            for (int n = 0; i < myHand.size(); i++) {
                                if (holeCards[0].compareTo(myHand.get(n)) > 0) {
                                    myHand.add(n, holeCards[0]);
                                    break;
                                }
                            }
                            
                            for (int p = 0; i < myHand.size(); i++) {
                                if (holeCards[0].compareTo(myHand.get(p)) > 0) {
                                    myHand.add(p, holeCards[0]);
                                    break;
                                }
                            }
                            
                            //analagous for villian
                            vHand.add(deck.get(i));
                            vHand.add(deck.get(j));
                            vHand.add(deck.get(k));
                            vHand.add(deck.get(l));
                            vHand.add(deck.get(m));
                            
                            for (int n = 0; i < vHand.size(); i++) {
                                if (vHoleCards[0].compareTo(vHand.get(n)) > 0) {
                                    vHand.add(n, vHoleCards[0]);
                                    break;
                                }
                            }
                            
                            for (int p = 0; i < vHand.size(); i++) {
                                if (vHoleCards[0].compareTo(vHand.get(p)) > 0) {
                                    vHand.add(p, vHoleCards[0]);
                                    break;
                                }
                            }
                                             
                            Card[] aMyHand = myHand.toArray(new Card[0]);
                            Card[] aVHand = vHand.toArray(new Card[0]);
                            
                            if (bestFive(aMyHand) >= bestFive(aVHand))
                                count++;
                            total++;
                        }
         System.out.print((double)count/total);
        
    }
}
