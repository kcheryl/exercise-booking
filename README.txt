Solution is implemented as Java 8 standalone application with JUnit test

On top of existing commands (Setup/View/Availability/Book/Cancel), two more commands are added:
- Mode (To switch between two types of users (Admin/Buyer) which are restricted to their functionalities i.e. only Admin can use Setup command
- Exit (To quit the application)

BookingASystem:
- To run compiled version: Inside src, run >> java BookingASystem
- To compile: Inside src, run >> javac BookingASystem.java

BookingASystemTest:
- To run compiled version: Inside src, run >> javac -cp ../lib/junit-platform-console-standalone-1.8.2.jar:. BookingASystemTest.java
- To compile: Inside src, run >> java -jar ../lib/junit-platform-console-standalone-1.8.2.jar --class-path . --select-class BookingASystemTest

Assumptions:
- Commands are case insensitive
- Show number is unique
- Phone number has a fixed format: 8 digits and starts with either 6, 8 or 9
- All number parameters (excluding phone number) has to be a valid integer
