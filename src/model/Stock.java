package model;

/**
 * Created by Sharon on 8/26/18.
 */
public class Stock extends Row{
    String stock;
    double price;
    int hour;

//    enum type {
//        ACTUAL, PREDICTED;
//    }

    public Stock(String [] row) {
        hour = Integer.parseInt(row[0]);
        stock = row[1];
        price = Double.parseDouble(row[2]);

    }

    public Stock(String stock, Double price, int hour) {
        this.stock = stock;
        this.price = price;
        this.hour = hour;
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

    //todo override equals.
    //todo equal if hour & stock are the same
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final Stock other = (Stock) obj;
        return other.stock.equals(this.stock) && other.hour==this.hour;
    }
}
