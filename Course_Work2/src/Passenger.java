public class Passenger {

    private String firstName;
    private String sureName;
    private int seatNumber;
    private int secondsInQueue;
    private boolean isArrived;
    private int generatedDelay;

    public String getFirstName() {
        return firstName;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }

    public int getGeneratedDelay() {
        return generatedDelay;
    }

    public void setGeneratedDelay(int generatedDelay) {
        this.generatedDelay = generatedDelay;
    }

    public Passenger(String firstName, String sureName, int seatNumber){
        this.firstName = firstName;
        this.sureName = sureName;
        this.seatNumber = seatNumber;
    }

    public Passenger(String firstName, String sureName, int seatNumber, boolean isArrived){
        this.firstName = firstName;
        this.sureName = sureName;
        this.seatNumber = seatNumber;
        this.isArrived = isArrived;
    }

    public String getName() {
        return (firstName + " " + sureName);
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setName(String firstName,String sureName) {
        this.firstName = firstName;
        this.sureName = sureName;
    }

    public int getSecondsInQueue() {
        return secondsInQueue;
    }

    public void setSecondsInQueue(int secondsInQueue) {
        this.secondsInQueue = secondsInQueue;
    }

    public boolean getArrived() {
        return isArrived;
    }

    public void setArrived(boolean arrived) {
        this.isArrived = arrived;
    }

    public void display(){
        if (getArrived()) {
            System.out.println("Passenger name is " + getName() +" (" + seatNumber + ") and passenger wait in the queue " + getSecondsInQueue() + " seconds.");
        }else{
            System.out.println("Passenger name is " + getName() + ". Passenger hasn't arrived.");
        }
    }


}
