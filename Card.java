
public class Card implements Comparable<Card>{
    private int value;
    private int suit;
    
    public Card(int value, int suit) {
        this.value = value;
        this.suit = suit;
    }
    //A = 13, K = 12 Q = 11 ... 
    public int getValue() {
        return (value);
    }
    // d = 0, s = 1, h = 2, c = 3
    public int getSuit() {
        return suit;
    }
    
    public String toString(){
        return "" + value +" "+ suit;
    }
    @Override
    public int compareTo(Card o) {
        if (o.value < this.value)
            return 1;
        if (o.value > this.value)
            return -1;
        if (o.suit > this.suit)
            return -1;
        return 1;
        
    }
}
