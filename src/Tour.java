import java.io.*;

// tour class
public class Tour implements Serializable {

    private static final long serialVersionUID = 1L;

    // some tour staff
    String tourName;
    String clientName;
    double pricePerDay;
    int days;
    double fare;
    double costOfTravel;
    transient boolean isZipped;

    // constructor
    Tour(String name, String fullName, double pricePerDay, int days, double fare, double costOfTravel) {
        this.tourName = name;
        this.clientName = fullName;
        this.pricePerDay = pricePerDay;
        this.days = days;
        this.fare = fare;
        this.costOfTravel = costOfTravel;
    }

    // setting zipped
    public void setIsZipped(boolean isZipped) {
        this.isZipped = isZipped;
    }
}