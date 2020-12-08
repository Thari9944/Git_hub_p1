import java.util.LinkedList;

public class PassengerQueue {

    private Passenger[] queueArray = new Passenger[42];
    private int first = -1;
    private int last = -1;
    private int maxStayInQueue = 0;
    private int maxLength = 42;

    public Passenger getQueuePassenger(int index){
        return this.queueArray[index];
    }

    public void addPassenger(Passenger passenger){
        if (!isFull()){
            if (first == last && first == -1){
                first +=1;
            }
            last = (last+1) % maxLength;
            this.queueArray[last] = passenger;
            TrainStation.PASSENGERS[passenger.getSeatNumber() -1] = passenger;
            if(maxStayInQueue < (last+1)){
                this.maxStayInQueue = (last+1);
            }
        }else{
            System.out.println("Passenger queue is full");
        }
    }

    public void removePassenger(){
        if (!isEmpty()){
            this.queueArray[first] = null;
            System.out.println("done");
            last -=1;
            for (int i =0 ; i < last; i++){
                if (this.queueArray[i] != null){
                    this.queueArray[i-1] = this.queueArray[i];
                    this.queueArray[i] = null;
                }
            }
            if (last == -1){
                this.first = -1;
            }
        }else {
            System.out.println("Passenger queue is empty.");
        }
    }

    public void removePassenger(int index) {
        if (isEmpty()) {
            this.queueArray[index] = null;
            for (int i = index; i < last; i++) {
                this.queueArray[i] = this.queueArray[i + 1];
                this.queueArray[i + 1] = null;
            }
            last -= 1;
        }else{
            System.out.println("Passenger queue is empty.");
        }
    }

    public boolean isEmpty(){
        if (first == last && first == -1){
            return true;
        }else {
            return false;
        }
    }

    public boolean isFull(){
        if ((last+1) % maxLength == first){
            return true;
        }else {
            return false;
        }
    }

    public void display(int index){
       // System.out.println(queueList.get(index));
    }

    public int getLength(){
        return (last+1);
    }

    public int getMaxStay(){
        return this.maxStayInQueue;
    }
}
