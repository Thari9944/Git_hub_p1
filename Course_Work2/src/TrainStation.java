import com.mongodb.client.FindIterable;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class TrainStation extends Application {

    //create global variables
    private static final int SEATINGCAPACITY = 42;
    //create global arrays for store data
    private static String[] IMPORTEDDATA_1 = new String[SEATINGCAPACITY]; //store data which load from database
    private static String[] IMPORTEDDATA_2 = new String[SEATINGCAPACITY];
    public static Passenger[] PASSENGERS = new Passenger[SEATINGCAPACITY]; // to get information create a passener array
    private static Passenger[] WAITINGROOM = new Passenger[SEATINGCAPACITY];
    //create passengerQueue object as a global object
    private static PassengerQueue passengerQueue = new PassengerQueue();

    private static int delayTime = 0;

    public static void main(String[] args) throws InterruptedException {
        /*LoadData();
        AddPassengersToPassengerClass();
        AddPassengersToWaitingRoom();
        AddToQueue();
        passengerQueue.display(0);
        if (!WAITINGROOM[0].isInWaitingRoom()){
            System.out.println("Null");
        }else {
            System.out.println("Value");
        }*/
        launch();
    }

    private static int SetDate(){
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("\nEnter the date : (Valid until One Month.) Type : yyyy-mm-dd\n\t");
                LocalDate date = LocalDate.parse(sc.next());
                if (date.isAfter(LocalDate.now()) && date.isBefore(LocalDate.now().plusMonths(1))) {
                    return (date.getDayOfMonth()%31-1);
                } else {
                    System.out.println("Date is not in Range.");
                }
            } catch (Exception e) {
                System.out.println("Sorry. Please check your input.");
            }
        }
    }

    private static int SetDestination(){
        Scanner sc = new Scanner(System.in);
        while (true){
            try {
                System.out.print("\n1 for Colombo to Badulla \t2 for Badulla to Colombo \nEnter your Choice : ");
                int destinationNo = Integer.parseInt(sc.next())-1;
                switch (destinationNo) {
                    case 0:
                    case 1:
                        return destinationNo;
                    default:
                        System.out.println("Invalid Input. Please Check");
                }
            }catch (Exception e){
                System.out.println("Integer Required.");
            }
        }
    }

    private static void LoadDataToCw2(int dateAsIndex, int destinationAsIndex){

        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("TrainBookingDetails");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("PassengerDetails");
        FindIterable<Document> documentInDb = mongoCollection.find();

        for (Document docs : documentInDb){
            if (docs.getInteger("DateNo") == dateAsIndex){
                if (docs.getInteger("DestinationNo") == destinationAsIndex) {
                    int seat = docs.getInteger("Seat No");
                    IMPORTEDDATA_1[seat - 1] = docs.getString("FirstName");
                    IMPORTEDDATA_2[seat - 1] = docs.getString("SureName");
                }
            }
        }
        System.out.println("Imported all data from database.");
    }

    private static void AddPassengersToPassengerClass(){
        for (int i = 0; i < IMPORTEDDATA_1.length; i++){
            if(IMPORTEDDATA_1[i] != null){
                PASSENGERS[i] = new Passenger(IMPORTEDDATA_1[i], IMPORTEDDATA_2[i],(i+1));
            }else {
                PASSENGERS[i] = new Passenger("No booking", "No booking",(i+1));
            }
        }
    }

    private static void AddPassengersToWaitingRoom(){
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Enter the arrived passenger Seat Number : (-1 for Exit) ");
                int seatNo = Integer.parseInt(sc.next());
                if(seatNo > 0 && seatNo <= 42){
                    if(IMPORTEDDATA_1[seatNo-1] != null){
                        WAITINGROOM[seatNo-1] = new Passenger(IMPORTEDDATA_1[seatNo-1], IMPORTEDDATA_2[seatNo-1], (seatNo), true);
                        System.out.println("Passenger added to the waiting room. \n");
                        PASSENGERS[seatNo-1].setArrived(true);
                        IMPORTEDDATA_1[seatNo-1] = null;
                    }else {
                        System.out.println("Please check the seat number is correct.\n");
                    }
                }else if (seatNo == -1){
                    break;
                }else{
                    System.out.println("Invalid input.\n");
                }
            }catch (Exception e){
                System.out.println("Integer required. Between 1 and 42.\n");
            }
        }
    }

    private static void AddPassengersToWaitingRoom(int seatNo){
        WAITINGROOM[seatNo-1] = new Passenger(IMPORTEDDATA_1[seatNo-1], IMPORTEDDATA_2[seatNo-1], seatNo, true);
        IMPORTEDDATA_1[seatNo-1] = null;
    }

    private static void AddToQueue(int dateNo, int destinationNo) {

        AnchorPane anchorPane = new AnchorPane();
        Stage stage = new Stage();
        stage.setTitle("Train Booking System");
        Label headinglable = new Label("TRAIN BOOKING SYSTEM");
        headinglable.setLayoutX(250);
        headinglable.setLayoutY(10);
        headinglable.setFont(Font.font("Verdana", 24));
        anchorPane.getChildren().add(headinglable);
        Label detailslable = new Label();
        if (destinationNo == 0){
            detailslable.setText("Train Name : DENUWARA MENIKE      Date : " + LocalDate.now().plusDays((dateNo+1)-LocalDate.now().getDayOfMonth())   +"   Type : A/C      Destination : BADULLA");
        }
        else {
            detailslable.setText("Train Name : DENUWARA MENIKE      Date : "+  LocalDate.now().plusDays((dateNo+1)-LocalDate.now().getDayOfMonth())   +"   Type : A/C      Destination : COLOMBO");
        }
        detailslable.setLayoutX(40);
        detailslable.setLayoutY(60);
        detailslable.setFont(Font.font("Verdana", 16));
        anchorPane.getChildren().add(detailslable);

        FlowPane flowPaneForQueue = new FlowPane(Orientation.VERTICAL);
        flowPaneForQueue.setAlignment(Pos.CENTER);
        flowPaneForQueue.setLayoutY(100);
        flowPaneForQueue.setMinSize(450,300);
        flowPaneForQueue.setStyle("-fx-background-color:green;");

        FlowPane flowPaneForWaitingRoom = new FlowPane();
        flowPaneForWaitingRoom.setHgap(5);
        flowPaneForWaitingRoom.setVgap(5);
        flowPaneForWaitingRoom.setAlignment(Pos.CENTER);
        flowPaneForWaitingRoom.setLayoutX(451);
        flowPaneForWaitingRoom.setLayoutY(100);
        flowPaneForWaitingRoom.setMinSize(450,300);
        flowPaneForWaitingRoom.setStyle("-fx-background-color:blue;");

        FlowPane flowPaneForHasToArrive = new FlowPane();
        flowPaneForHasToArrive.setHgap(10);
        flowPaneForHasToArrive.setVgap(10);
        flowPaneForHasToArrive.setAlignment(Pos.CENTER);
        flowPaneForHasToArrive.setLayoutY(401);
        flowPaneForHasToArrive.setMinSize(900,200);
        flowPaneForHasToArrive.setStyle("-fx-background-color:red;");

        Label labelForQueue = new Label("Passenger Queue");
        labelForQueue.setFont(Font.font("Verdana", 16));
        labelForQueue.setLayoutX(20);
        labelForQueue.setLayoutY(120);

        Label labelForWaiting = new Label("Passenger Waiting Room");
        labelForWaiting.setFont(Font.font("Verdana", 16));
        labelForWaiting.setLayoutX(470);
        labelForWaiting.setLayoutY(120);

        Label labelForHasToArrive = new Label("Passengers Who haven't arrived Yet - \"Click the Button to add Passenger to Waiting Room\"");
        labelForHasToArrive.setFont(Font.font("Verdana", 16));
        flowPaneForHasToArrive.getChildren().add(labelForHasToArrive);
        flowPaneForHasToArrive.setMargin(labelForHasToArrive, new Insets(5, 150, 0, 20));

        Label [] labelsForWaitingRoomForLoad = new Label[SEATINGCAPACITY];

        for (int k = 0; k < labelsForWaitingRoomForLoad.length; k++){
           if (WAITINGROOM[k] != null){
               Label label = new Label();
               labelsForWaitingRoomForLoad[k] = label;
               labelsForWaitingRoomForLoad[k].setText(WAITINGROOM[k].getSeatNumber() + "-" + WAITINGROOM[k].getName());
               labelsForWaitingRoomForLoad[k].setMaxWidth(70);
               labelsForWaitingRoomForLoad[k].setMinWidth(70);
               flowPaneForWaitingRoom.getChildren().add(labelsForWaitingRoomForLoad[k]);
           }
        }

        Label [] labelsForWaitingRoom = new Label[SEATINGCAPACITY];

        for (int i = 0; i < IMPORTEDDATA_1.length; i++){
            if (IMPORTEDDATA_1[i] != null) {
                Button button = new Button((i + 1) + " - " + IMPORTEDDATA_1[i] + " " + IMPORTEDDATA_2[i]);
                button.setMaxWidth(90);
                button.setMinWidth(90);
                flowPaneForHasToArrive.getChildren().add(button);
                int seatNumber = i+1;
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        AddPassengersToWaitingRoom(seatNumber);
                        flowPaneForHasToArrive.getChildren().remove(button);
                        for (int j = 0; j < labelsForWaitingRoom.length; j++){
                            if (labelsForWaitingRoom[j] != null){
                                flowPaneForWaitingRoom.getChildren().remove(labelsForWaitingRoom[j]);
                            }
                            if (labelsForWaitingRoomForLoad[j] != null){
                                flowPaneForWaitingRoom.getChildren().remove(labelsForWaitingRoomForLoad[j]);
                                labelsForWaitingRoom[j] = labelsForWaitingRoomForLoad[j];
                                labelsForWaitingRoomForLoad[j] = null;
                            }
                        }
                        Label label = new Label();
                        labelsForWaitingRoom[seatNumber-1] = label;
                        labelsForWaitingRoom[seatNumber-1].setText(WAITINGROOM[seatNumber-1].getSeatNumber() + "-" + WAITINGROOM[seatNumber-1].getName());
                        labelsForWaitingRoom[seatNumber-1].setMaxWidth(70);
                        labelsForWaitingRoom[seatNumber-1].setMinWidth(70);

                        for (int k = 0; k < labelsForWaitingRoom.length; k++){
                            if (WAITINGROOM[k] != null){
                                if (labelsForWaitingRoom[k] != null){
                                    flowPaneForWaitingRoom.getChildren().add(labelsForWaitingRoom[k]);
                                }
                            }
                        }
                    }
                });
            }
        }

        Label [] labelsForPassengerQueue = new Label[SEATINGCAPACITY];

        for (int k = 0; k < labelsForPassengerQueue.length; k++){
            if (passengerQueue.getQueuePassenger(k) != null) {
                labelsForPassengerQueue[k] = new Label((k+1) + ". " + passengerQueue.getQueuePassenger(k).getName());
                flowPaneForQueue.getChildren().add(labelsForPassengerQueue[k]);
            }
        }

        Button btnAddToQueue = new Button("Add To Queue");
        btnAddToQueue.setLayoutX(800);
        btnAddToQueue.setLayoutY(350);
        btnAddToQueue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int rangeValue = 6;
                int items = 0;
                for (Passenger passenger : WAITINGROOM) {
                    if (passenger != null) {
                        items++;
                    }
                }
                if (items < 6){
                    rangeValue = items;
                }
                int numberOfPassengersToQueue = (int) (Math.random() * rangeValue +1);
                if (rangeValue > 0) {
                    for (int l = 0; l < numberOfPassengersToQueue; l++) {
                        for (int m = 0; m < SEATINGCAPACITY; m++) {
                            if (WAITINGROOM[m] != null) {
                                int waitingTimeOfQueueMembers = 0;
                                int secondsOfGenerated = 0 ;
                                for (int n = 0; n < 42; n++){
                                    if (passengerQueue.getQueuePassenger(n) != null){
                                        waitingTimeOfQueueMembers += passengerQueue.getQueuePassenger(n).getGeneratedDelay();
                                    }
                                }
                                for (int o = 0; o < 3; o++){
                                    int time = (int) (Math.random() * 6 +1);
                                    secondsOfGenerated += time;
                                }
                                WAITINGROOM[m].setGeneratedDelay(secondsOfGenerated);
                                WAITINGROOM[m].setSecondsInQueue(waitingTimeOfQueueMembers + secondsOfGenerated);
                                passengerQueue.addPassenger(WAITINGROOM[m]);
                                flowPaneForWaitingRoom.getChildren().remove(labelsForWaitingRoom[m]);
                                flowPaneForWaitingRoom.getChildren().remove(labelsForWaitingRoomForLoad[m]);
                                labelsForPassengerQueue[passengerQueue.getLength()-1] = new Label(passengerQueue.getLength() + ". " + passengerQueue.getQueuePassenger(passengerQueue.getLength()-1).getName());
                                flowPaneForQueue.getChildren().add(labelsForPassengerQueue[passengerQueue.getLength()-1]);
                                WAITINGROOM[m] = null;
                                break;
                            }
                        }
                    }

                }else{
                    System.out.println("Waiting Room Is Empty.");
                }
            }
        });

        Button btnToTrain = new Button("Button To Add Train");
        btnToTrain.setLayoutX(400);
        btnToTrain.setLayoutY(350);
        btnToTrain.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (int x = 0; x < labelsForPassengerQueue.length; x++) {
                    flowPaneForQueue.getChildren().remove(labelsForPassengerQueue[x]);
                }
                int passengersInQueue = passengerQueue.getLength();
                System.out.println(passengersInQueue);
                for (int y = 0; y < passengersInQueue; y++) {
                    passengerQueue.removePassenger();
                }
            }
        });




        anchorPane.getChildren().addAll(flowPaneForQueue, flowPaneForWaitingRoom, flowPaneForHasToArrive, btnAddToQueue, btnToTrain, labelForQueue, labelForWaiting);
        anchorPane.setStyle("-fx-background-color:#ebe8eb;");
        stage.setScene(new Scene(anchorPane, 900, 600));
        stage.showAndWait();

    }

    private static void ViewPassengerQueue(int dateNo, int destinationNo) {

        AnchorPane anchorPane = new AnchorPane();
        Stage stage = new Stage();
        stage.setTitle("Train Booking System");
        Label headinglable = new Label("TRAIN BOOKING SYSTEM");
        headinglable.setLayoutX(250);
        headinglable.setLayoutY(10);
        headinglable.setFont(Font.font("Verdana", 24));
        anchorPane.getChildren().add(headinglable);
        Label detailslable = new Label();
        if (destinationNo == 0) {
            detailslable.setText("Train Name : DENUWARA MENIKE      Date : " + LocalDate.now().plusDays((dateNo + 1) - LocalDate.now().getDayOfMonth()) + "   Type : A/C      Destination : BADULLA");
        } else {
            detailslable.setText("Train Name : DENUWARA MENIKE      Date : " + LocalDate.now().plusDays((dateNo + 1) - LocalDate.now().getDayOfMonth()) + "   Type : A/C      Destination : COLOMBO");
        }
        detailslable.setLayoutX(40);
        detailslable.setLayoutY(60);
        detailslable.setFont(Font.font("Verdana", 16));
        anchorPane.getChildren().add(detailslable);

        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setLayoutY(100);
        flowPane.setMinSize(900,500);
        flowPane.setStyle("-fx-background-color:green;");
        for (int i = 0; i < passengerQueue.getLength(); i++){
            if (passengerQueue.getQueuePassenger(i) != null) {
                Label label = new Label((i+1) + " Name : " + passengerQueue.getQueuePassenger(i).getName() + "  Seat : " + passengerQueue.getQueuePassenger(i).getSeatNumber());
                flowPane.getChildren().add(label);
            }
        }

        Button btnToTrain = new Button("Go to the train");
        btnToTrain.setLayoutX(750);
        btnToTrain.setLayoutY(550);
        btnToTrain.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                Stage stageTrain = new Stage();
                stageTrain.setTitle("Train Seats View");
                AnchorPane anchorPaneForTrain = new AnchorPane();

                Label headinglable = new Label("TRAIN BOOKING SYSTEM");
                headinglable.setLayoutX(250);
                headinglable.setLayoutY(10);
                headinglable.setFont(Font.font("Verdana", 24));
                anchorPaneForTrain.getChildren().add(headinglable);
                Label detailslable = new Label();
                if (destinationNo == 0) {
                    detailslable.setText("Train Name : DENUWARA MENIKE      Date : " + LocalDate.now().plusDays((dateNo + 1) - LocalDate.now().getDayOfMonth()) + "   Type : A/C      Destination : BADULLA");
                } else {
                    detailslable.setText("Train Name : DENUWARA MENIKE      Date : " + LocalDate.now().plusDays((dateNo + 1) - LocalDate.now().getDayOfMonth()) + "   Type : A/C      Destination : COLOMBO");
                }
                detailslable.setLayoutX(40);
                detailslable.setLayoutY(60);
                detailslable.setFont(Font.font("Verdana", 16));
                anchorPaneForTrain.getChildren().add(detailslable);

                FlowPane flowPaneForTrain = new FlowPane();
                flowPaneForTrain.setAlignment(Pos.CENTER);
                flowPaneForTrain.setHgap(25);
                flowPaneForTrain.setLayoutY(100);
                flowPaneForTrain.setMinSize(900,500);
                flowPaneForTrain.setStyle("-fx-background-color:green;");
                for (int i = 0; i < PASSENGERS.length; i++){
                    if (PASSENGERS[i] != null){
                        Label label = new Label("Seat Number : " + (i+1) + "\nName : " + PASSENGERS[i].getName() + "\nSeconds in queue : " + PASSENGERS[i].getSecondsInQueue());
                        flowPaneForTrain.getChildren().add(label);
                    }else {
                        Label label = new Label("Seat Number : " + (i+1) + "\nName : Not Arrived Yet"  + "\nSeconds in queue : 0" );
                        flowPaneForTrain.getChildren().add(label);
                    }
                }


                Button btnBack = new Button("Exit");
                btnBack.setLayoutX(750);
                btnBack.setLayoutY(550);
                btnBack.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        stageTrain.close();
                    }
                });
                flowPaneForTrain.getChildren().add(btnBack);

                anchorPaneForTrain.getChildren().addAll(flowPaneForTrain);
                stageTrain.setScene(new Scene(anchorPaneForTrain, 900, 600));
                stageTrain.showAndWait();
            }
        });


        anchorPane.getChildren().addAll(flowPane, btnToTrain);
        anchorPane.setStyle("-fx-background-color:#ebe8eb;");
        stage.setScene(new Scene(anchorPane, 900, 600));
        stage.showAndWait();
    }

    private static void DeletePassengerFromQueue(){
        Scanner sc = new Scanner(System.in);
        while(true){
            try {
                System.out.print("Enter the Passenger Seat Number : (0 for exit)");
                int deleteSeat = Integer.parseInt(sc.next());
                if ( deleteSeat == 0){
                    break;
                }else if (deleteSeat > 0 && deleteSeat <= 42){
                    boolean isInQueue = false;
                    int deleteIndex = 0;
                    for (int i = 0; i < passengerQueue.getLength(); i++) {
                        if (passengerQueue.getQueuePassenger(i).getSeatNumber() == deleteSeat) {
                            isInQueue = true;
                            deleteIndex = i;
                            break;
                        }
                    }
                    if (isInQueue) {
                        System.out.println("Passenger Details : Name " + passengerQueue.getQueuePassenger(deleteIndex).getName() + "Seat Number " + passengerQueue.getQueuePassenger(deleteIndex).getSeatNumber());
                        System.out.print("Are you sure : (y\\n)");
                        String user = sc.next();
                        if (user.equals("y")) {
                            PASSENGERS[deleteSeat-1] = passengerQueue.getQueuePassenger(deleteIndex);
                            passengerQueue.removePassenger(deleteIndex);
                            System.out.println("Delete was sucessfull.");
                            break;
                        }
                    } else {
                        System.out.println("That passenger isn't in the queue.");
                    }
                }
                else{
                    System.out.println("Not in range. Enter number between 1 and 42.");
                }
            }catch (Exception e){
                System.out.println("Please, Enter an integer.");
            }
        }
    }

    public static void StoreDate() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("TrainBookingDetails2");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("PassengerDetails2");
        for (int i = 0; i < 42; i++) {
            if (passengerQueue.getQueuePassenger(i) != null) {
                Document document = new Document();
                document.append("FirstName", passengerQueue.getQueuePassenger(i).getFirstName());
                document.append("SecondName", passengerQueue.getQueuePassenger(i).getSureName());
                document.append("SeatNumber", passengerQueue.getQueuePassenger(i).getSeatNumber());
                document.append("SecondsInQueue", passengerQueue.getQueuePassenger(i).getSecondsInQueue());
                document.append("IsArrived", passengerQueue.getQueuePassenger(i).getArrived());
                document.append("SecondsGenerated", passengerQueue.getQueuePassenger(i).getGeneratedDelay());
                mongoCollection.insertOne(document);
                System.out.println("Data has been saved in database.");
            }
        }
    }
            //load data from database
    public static  void LoadData(){
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("TrainBookingDetails2");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("PassengerDetails2");
        FindIterable<Document> documentInDb = mongoCollection.find();
        int i = 0;
        for (Document  docs : documentInDb){
            passengerQueue.getQueuePassenger(i).setFirstName(docs.getString("FirstName"));
            passengerQueue.getQueuePassenger(i).setSureName(docs.getString("SureName"));
            passengerQueue.getQueuePassenger(i).setSeatNumber(docs.getInteger("SeatNumber"));
            passengerQueue.getQueuePassenger(i).setSecondsInQueue(docs.getInteger("SecondsInQueue"));
            passengerQueue.getQueuePassenger(i).setGeneratedDelay(docs.getInteger("SecondsGenerated"));
            passengerQueue.getQueuePassenger(i).setArrived(docs.getBoolean("IsArrived"));
            i++;

        }
        System.out.println("Imported all data from database.");
    }

    private static void GiveAReport( int dateNo, int destinationNo) throws IOException {
        int maximumLength = passengerQueue.getMaxStay();
        int minimumWaitingTime = 1000;
        int maximumWaitingTime = 0;
        int numberOfPassengersInTrain = 0;
        int totalOfWaitingTime = 0;

        for (int i = 0; i < PASSENGERS.length; i++){
            if (PASSENGERS[i] != null){
                numberOfPassengersInTrain +=1;
                if (PASSENGERS[i].getSecondsInQueue() > maximumWaitingTime){
                    maximumWaitingTime = PASSENGERS[i].getSecondsInQueue();
                }
                if (PASSENGERS[i].getSecondsInQueue() < minimumWaitingTime){
                    minimumWaitingTime = PASSENGERS[i].getSecondsInQueue();
                }
                totalOfWaitingTime += PASSENGERS[i].getSecondsInQueue();
            }
        }

        AnchorPane anchorPane = new AnchorPane();
        Stage stage = new Stage();
        stage.setTitle("Train Booking System");
        Label headinglable = new Label("TRAIN BOOKING SYSTEM");
        headinglable.setLayoutX(250);
        headinglable.setLayoutY(10);
        headinglable.setFont(Font.font("Verdana", 24));
        anchorPane.getChildren().add(headinglable);
        Label detailslable = new Label();
        if (destinationNo == 0) {
            detailslable.setText("Train Name : DENUWARA MENIKE      Date : " + LocalDate.now().plusDays((dateNo + 1) - LocalDate.now().getDayOfMonth()) + "   Type : A/C      Destination : BADULLA");
        } else {
            detailslable.setText("Train Name : DENUWARA MENIKE      Date : " + LocalDate.now().plusDays((dateNo + 1) - LocalDate.now().getDayOfMonth()) + "   Type : A/C      Destination : COLOMBO");
        }
        detailslable.setLayoutX(40);
        detailslable.setLayoutY(60);
        detailslable.setFont(Font.font("Verdana", 16));
        anchorPane.getChildren().add(detailslable);

        Label labelForMinimumTime = new Label("Minimum Waiting Time :\t" + minimumWaitingTime);
        labelForMinimumTime.setLayoutX(250);
        labelForMinimumTime.setLayoutY(100);
        labelForMinimumTime.setFont(Font.font("Verdana", 16));

        Label labelForMaximumTime = new Label("Maximum Waiting Time :\t" + maximumWaitingTime);
        labelForMaximumTime.setLayoutX(250);
        labelForMaximumTime.setLayoutY(120);
        labelForMaximumTime.setFont(Font.font("Verdana", 16));

        Label labelForNumberOfPassengersInTrain = new Label("Number Of Passengers In Train :\t" + numberOfPassengersInTrain);
        labelForNumberOfPassengersInTrain.setLayoutX(250);
        labelForNumberOfPassengersInTrain.setLayoutY(140);
        labelForNumberOfPassengersInTrain.setFont(Font.font("Verdana", 16));

        Label labelForAverageTime = new Label("Average Waiting Time :\t" + (totalOfWaitingTime/ numberOfPassengersInTrain));
        labelForAverageTime.setLayoutX(250);
        labelForAverageTime.setLayoutY(160);
        labelForAverageTime.setFont(Font.font("Verdana", 16));

        Label labelForMaxStay = new Label("Maximum length of the Queue :\t" + maximumLength);
        labelForMaxStay.setLayoutX(250);
        labelForMaxStay.setLayoutY(180);
        labelForMaxStay.setFont(Font.font("Verdana", 16));

        Button btnToMainMenu = new Button("Back To Menu");
        btnToMainMenu.setLayoutX(250);
        btnToMainMenu.setLayoutY(200);
        btnToMainMenu.setFont(Font.font("Verdana", 16));

        anchorPane.getChildren().addAll(labelForMinimumTime, labelForMaximumTime, labelForNumberOfPassengersInTrain, labelForAverageTime, btnToMainMenu);
        anchorPane.setStyle("-fx-background-color:#ebe8eb;");
        stage.setScene(new Scene(anchorPane, 900, 600));
        stage.showAndWait();

        File file = new File("passenger_queue_details.txt");
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        printWriter.println("Train Booking System\n" + LocalDate.now().plusDays((dateNo + 1) - LocalDate.now().getDayOfMonth()) + "\n");
        printWriter.println("Minimum Waiting Time :\\t" + minimumWaitingTime +
                        "\nMaximum Waiting Time :\t" + maximumWaitingTime +
                        "\nNumber Of Passengers In Train :\t" + numberOfPassengersInTrain +
                        "\nAverage Waiting Time :\t" + (totalOfWaitingTime/ numberOfPassengersInTrain) +
                        "\nMaximum length of the Queue :\t" + maximumLength);
        printWriter.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("\t=== TRAIN BOOKING SYSTEM PART 2 ===");
        System.out.println("Train : Denuwara Menike\nDestination : Badulla & Colombo\nNumber Of Seats : 42\nType : A/C");
        System.out.println("******------------------------------------------------------------------------------******");
        int dateValue = SetDate();
        int destinationValue = SetDestination();
        LoadDataToCw2(dateValue, destinationValue);
        while (true) {
            //display instructions to user
            System.out.println("\n|W    Add Passengers to Waiting Room   |           " +
                    "\t\t|A    Add Passengers To Queue|                              " +
                    "\t\t|V    View Passenger Queue|                                 " +
                    "\n|D    Delete Passenger from Passenger Queue|                  " +
                    "\t|S    Store Data|                                             " +
                    "\t\tL    Load Data|                                             " +
                    "\n|R    Get Report GUI |                                        " +
                    "\t\t|Q     For Exit the Programme|  ");
            System.out.print("What do you want : ");
            String userResponse = sc.next().toLowerCase();
            switch(userResponse) {
                case "w":
                    AddPassengersToWaitingRoom();
                    break;
                case "a":
                    AddToQueue(dateValue, destinationValue);
                    break;
                case "v":
                    ViewPassengerQueue(dateValue, destinationValue);
                    break;
                case "d":
                    DeletePassengerFromQueue();
                    break;
                case "s":
                    StoreDate();
                    break;
                case "l":
                    LoadData();
                    break;
                case "r":
                    GiveAReport(dateValue, destinationValue);
                    break;
                case "q":
                    System.out.println("Thank You For using our service.");
                    System.exit(0);
                default:
                    System.out.println("Invalid Input. Please check your Input.");
                    break;
            }
        }

    }
}

