package model;

/**
 * Created by Sharon on 8/26/18.
 * Represents each stock processed.
 * Stocks are equal when hour and stock ticker are the same.
 */
public class Stock{
    String stock;
    double price;
    int hour;
    
    public Stock() {
        stock = "";
        price = 0;
        hour = 0;
    }
    
    public Stock(String [] row) {
        hour = Integer.parseInt(row[0].trim());
        stock = row[1];
        price = Double.parseDouble(row[2]);

    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final Stock other = (Stock) obj;
        return other.stock.equals(this.stock) && other.hour==this.hour;
    }

    @Override
    public String toString() {
        return hour + "|" + stock + "|" + price;
    }
}
