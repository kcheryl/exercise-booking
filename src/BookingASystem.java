import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class BookingASystem {
  public static final String COMMAND_SETUP = "SETUP";
  public static final String COMMAND_VIEW = "VIEW";
  public static final String COMMAND_AVAILABILITY = "AVAILABILITY";
  public static final String COMMAND_BOOK = "BOOK";
  public static final String COMMAND_CANCEL = "CANCEL";
  public static final String COMMAND_MODE = "MODE";
  public static final String COMMAND_EXIT = "EXIT";

  public static final String ADMIN = "ADMIN";
  public static final String BUYER = "BUYER";

  public static final int MAX_SEATS = 10;
  public static final int MAX_ROWS = 26;

  public static final String ERROR_MSG_INVALID_INPUT = "-- Please try again! Invalid input: ";
  public static final String ERROR_MSG_MODE = "Please switch mode and try again";
  public static final String ERROR_MSG_WRONG_PARAM = "Wrong parameters";
  public static final String ERROR_MSG_INVALID_ROW = "Invalid number of rows";
  public static final String ERROR_MSG_EXCEED_ROW_LIMIT = "Exceeded row limit";
  public static final String ERROR_MSG_INVALID_SEAT = "Invalid number of seats";
  public static final String ERROR_MSG_EXCEED_SEAT_LIMIT = "Exceeded seat limit";
  public static final String ERROR_MSG_INVALID_WINDOW = "Invalid window period";
  public static final String ERROR_MSG_SHOW_NUMBER = "Show Number does not exist";
  public static final String ERROR_MSG_INVALID_SHOW_NUMBER = "Show Number is invalid";
  public static final String ERROR_MSG_DUPLICATE_SHOW =
      "Show Number already exist, please select another number";
  public static final String ERROR_MSG_TICKET_NUMBER = "Ticket Number does not exist";
  public static final String ERROR_MSG_COMMAND = "Command does not exist";
  public static final String ERROR_MSG_SEAT = "Seat is invalid: ";
  public static final String ERROR_MSG_MISMATCH_TICKET_PHONE =
      "Ticket Number with this Phone Number does not exist";
  public static final String ERROR_MSG_INVALID_PHONE_NUMBER =
      "Phone Number is invalid, must be 8 digits and starts with either 6, 8 or 9";
  public static final String ERROR_MSG_DUPLICATE_PHONE =
      "Phone Number has been used for this show, only one booking is allowed per show";

  public static final String MSG_MODE_BUYER =
      "-- Current Mode: Buyer ------------------------------------------------";
  public static final String MSG_MODE_ADMIN =
      "-- Current Mode: Admin ------------------------------------------------";
  public static final String MSG_SETUP = "-- Show added successfully";
  public static final String MSG_BOOK = "-- Ticket booked successfully, #";
  public static final String MSG_CANCEL_SUCCESS = "-- Ticket cancelled successfully, #";
  public static final String MSG_CANCEL_FAILURE =
      "-- Ticket cannot be cancelled, exceeded window period by (mins): ";

  public static void main(String args[]) {
    System.out.println("Welcome to Booking a Show!");
    printCommands();
    System.out.println(MSG_MODE_ADMIN);
    System.out.print(">> ");

    boolean isAdminMode = true;
    Map<Integer, Show> showMap = new HashMap<>();
    Map<Integer, Ticket> ticketMap = new HashMap<>();
    int ticketCount = 1;

    Scanner sc = new Scanner(System.in);
    String input = sc.nextLine();
    while (!input.equalsIgnoreCase(COMMAND_EXIT)) {
      InputParam param;
      try {
        Show show;
        Ticket ticket;
        param = checkInput(input, showMap, ticketMap, isAdminMode);
        switch (param.getCommand()) {
          case COMMAND_MODE:
            if (!param.isAdmin()) {
              isAdminMode = false;
              System.out.println(MSG_MODE_BUYER);
            } else {
              isAdminMode = true;
              System.out.println(MSG_MODE_ADMIN);
            }
            break;

          case COMMAND_SETUP:
            show =
                new Show(
                    param.getShowNum(),
                    param.getTotalRows(),
                    param.getTotalSeats(),
                    param.getCancellationWindow());
            showMap.put(param.getShowNum(), show);
            System.out.println(MSG_SETUP);
            break;

          case COMMAND_VIEW:
            System.out.println(showMap.get(param.getShowNum()));
            break;

          case COMMAND_AVAILABILITY:
            showMap.get(param.getShowNum()).printAvailability();
            break;

          case COMMAND_BOOK:
            show = showMap.get(param.getShowNum());
            for (int i = 0; i < param.getSeats().size(); i++) {
              int row = param.getSeats().get(i).toUpperCase().charAt(0) - 'A';
              int col = Integer.parseInt(param.getSeats().get(i).substring(1)) - 1;
              show.setOccupiedSeats(row, col, true);
            }
            int uniqueTicket = ticketCount++;
            ticket =
                new Ticket(
                    uniqueTicket,
                    param.getPhoneNum(),
                    param.getShowNum(),
                    param.getSeats(),
                    Instant.now());
            show.addTicket(ticket);
            ticketMap.put(uniqueTicket, ticket);
            System.out.println(MSG_BOOK + uniqueTicket);
            break;

          case COMMAND_CANCEL:
            ticket = ticketMap.get(param.getTicketNum());
            show = showMap.get(ticket.getShowNum());
            long durationInMins =
                Duration.between(ticket.getTimestamp(), Instant.now()).toMinutes();
            if (durationInMins <= show.getCancellationWindow()) {
              for (int i = 0; i < ticket.getSeats().size(); i++) {
                int row = ticket.getSeats().get(i).toUpperCase().charAt(0) - 'A';
                int col = Integer.parseInt(ticket.getSeats().get(i).substring(1)) - 1;
                show.setOccupiedSeats(row, col, false);
              }
              show.removeTicket(ticket);
              ticketMap.remove(param.getTicketNum());
              System.out.println(MSG_CANCEL_SUCCESS + param.getTicketNum());
            } else {
              System.out.println(
                  MSG_CANCEL_FAILURE + (int) (durationInMins - show.getCancellationWindow()));
            }
            break;
        }
      } catch (Exception e) {
        System.out.println(ERROR_MSG_INVALID_INPUT + e.getMessage());
        printCommands();
      }

      System.out.print(">> ");
      input = sc.nextLine();
    }
  }

  /**
   * Checks if the String input contains invalid commands/parameters, missing parameters, parameters
   * in wrong format
   *
   * @return the InputParam object with valid inputs
   * @exception NumberFormatException if there is an input which is required to be a number but is
   *     not a parsable integer IllegalArgumentException if there are any missing input parameters
   *     or invalid commands
   */
  public static InputParam checkInput(
      String input, Map<Integer, Show> showMap, Map<Integer, Ticket> ticketMap, boolean isAdminMode)
      throws Exception {
    List<String> cleanInput = new ArrayList<>();
    String[] parts = input.split(" ");
    for (int i = 0; i < parts.length; i++) {
      if (!parts[i].isEmpty() && !parts[i].equals(" ")) {
        cleanInput.add(parts[i]);
      }
    }
    if (cleanInput.size() < 2) {
      throw new IllegalArgumentException();
    }

    InputParam param = new InputParam();
    param.setCommand(cleanInput.get(0).toUpperCase());
    int input1;
    int input2;
    int input3;
    int input4;
    switch (param.getCommand()) {
      case COMMAND_MODE:
        if (cleanInput.size() != 2
            || !(cleanInput.get(1).equalsIgnoreCase(BUYER)
                || cleanInput.get(1).equalsIgnoreCase(ADMIN))) {
          throw new IllegalArgumentException(ERROR_MSG_WRONG_PARAM);
        }
        param.setAdmin(cleanInput.get(1).equalsIgnoreCase(ADMIN));
        break;

      case COMMAND_SETUP:
        if (!isAdminMode) {
          throw new IllegalArgumentException(ERROR_MSG_MODE);
        }
        if (cleanInput.size() != 5) {
          throw new IllegalArgumentException(ERROR_MSG_WRONG_PARAM);
        }
        input1 = Integer.parseInt(cleanInput.get(1));
        if (input1 <= 0) {
          throw new IllegalArgumentException(ERROR_MSG_INVALID_SHOW_NUMBER);
        }
        if (showMap.containsKey(input1)) {
          throw new IllegalArgumentException(ERROR_MSG_DUPLICATE_SHOW);
        }
        input2 = Integer.valueOf(cleanInput.get(2));
        if (input2 <= 0) {
          throw new IllegalArgumentException(ERROR_MSG_INVALID_ROW);
        }
        if (input2 > MAX_ROWS) {
          throw new IllegalArgumentException(ERROR_MSG_EXCEED_ROW_LIMIT);
        }
        input3 = Integer.valueOf(cleanInput.get(3));
        if (input3 <= 0) {
          throw new IllegalArgumentException(ERROR_MSG_INVALID_SEAT);
        }
        if (input3 > MAX_SEATS) {
          throw new IllegalArgumentException(ERROR_MSG_EXCEED_SEAT_LIMIT);
        }
        input4 = Integer.valueOf(cleanInput.get(4));
        if (input4 <= 0) {
          throw new IllegalArgumentException(ERROR_MSG_INVALID_WINDOW);
        }

        param.setShowNum(input1);
        param.setTotalRows(input2);
        param.setTotalSeats(input3);
        param.setCancellationWindow(input4);
        break;

      case COMMAND_VIEW:
        if (!isAdminMode) {
          throw new IllegalArgumentException(ERROR_MSG_MODE);
        }
        if (cleanInput.size() != 2) {
          throw new IllegalArgumentException(ERROR_MSG_WRONG_PARAM);
        }
        input1 = Integer.parseInt(cleanInput.get(1));
        if (!showMap.containsKey(input1)) {
          throw new IllegalArgumentException(ERROR_MSG_SHOW_NUMBER);
        }
        param.setShowNum(input1);
        break;

      case COMMAND_AVAILABILITY:
        if (isAdminMode) {
          throw new IllegalArgumentException(ERROR_MSG_MODE);
        }
        if (cleanInput.size() != 2) {
          throw new IllegalArgumentException(ERROR_MSG_WRONG_PARAM);
        }
        input1 = Integer.parseInt(cleanInput.get(1));
        if (!showMap.containsKey(input1)) {
          throw new IllegalArgumentException(ERROR_MSG_SHOW_NUMBER);
        }
        param.setShowNum(input1);
        break;

      case COMMAND_BOOK:
        if (isAdminMode) {
          throw new IllegalArgumentException(ERROR_MSG_MODE);
        }
        if (cleanInput.size() != 4) {
          throw new IllegalArgumentException(ERROR_MSG_WRONG_PARAM);
        }

        List<String> seatList = new ArrayList<>();
        input1 = Integer.parseInt(cleanInput.get(1));
        Show show;
        if (showMap.containsKey(input1)) {
          show = showMap.get(input1);
          String seats = cleanInput.get(3);
          String[] seatParts = seats.split(",");
          for (int i = 0; i < seatParts.length; i++) {
            if (!seatParts[i].isEmpty() && !seatParts[i].equals(" ") && !seatParts[i].equals(",")) {
              int row = seatParts[i].toUpperCase().charAt(0) - 'A';
              int col = Integer.parseInt(seatParts[i].substring(1)) - 1;
              if (row <= MAX_ROWS && col <= MAX_SEATS && !show.getOccupiedSeats()[row][col]) {
                seatList.add(seatParts[i]);
              } else {
                throw new IllegalArgumentException(ERROR_MSG_SEAT + seatParts[i]);
              }
            }
          }
        } else {
          throw new IllegalArgumentException(ERROR_MSG_SHOW_NUMBER);
        }

        input2 = Integer.valueOf(cleanInput.get(2));
        String phoneNumStr = cleanInput.get(2);
        if (input2 <= 0
            || phoneNumStr.length() != 8
            || !(phoneNumStr.startsWith("6")
                || phoneNumStr.startsWith("8")
                || phoneNumStr.startsWith("9"))) {
          throw new IllegalArgumentException(ERROR_MSG_INVALID_PHONE_NUMBER);
        }

        for (Ticket tik : show.getTickets()) {
          if (tik.getPhoneNum() == input2) {
            throw new IllegalArgumentException(ERROR_MSG_DUPLICATE_PHONE);
          }
        }

        param.setShowNum(input1);
        param.setPhoneNum(input2);
        param.setSeats(seatList);
        break;

      case COMMAND_CANCEL:
        if (isAdminMode) {
          throw new IllegalArgumentException(ERROR_MSG_MODE);
        }
        if (cleanInput.size() != 3) {
          throw new IllegalArgumentException(ERROR_MSG_WRONG_PARAM);
        }
        int ticketNum = Integer.parseInt(cleanInput.get(1));
        if (ticketMap.containsKey(ticketNum)) {
          param.setTicketNum(ticketNum);
        } else {
          throw new IllegalArgumentException(ERROR_MSG_TICKET_NUMBER);
        }
        input2 = Integer.parseInt(cleanInput.get(2));
        if (ticketMap.get(ticketNum).getPhoneNum() == input2) {
          param.setPhoneNum(input2);
        } else {
          throw new IllegalArgumentException(ERROR_MSG_MISMATCH_TICKET_PHONE);
        }
        break;

      default:
        throw new IllegalArgumentException(ERROR_MSG_COMMAND);
    }

    return param;
  }

  /** Prints list of commands to assist user in using the program */
  public static void printCommands() {
    System.out.println();
    System.out.println("List of Commands:");

    System.out.println(COMMAND_MODE + " <Type of User>");
    System.out.println("    Type of User: Admin/Buyer, to switch between these two users");
    System.out.println("    **Default type of user is Admin");
    System.out.println(
        COMMAND_SETUP
            + " <Show Number> <Number of Rows> <Number of seats per row> <Cancellation window in minutes>");
    System.out.println("    To setup the number of seats per show");
    System.out.println(COMMAND_VIEW + " <Show Number>");
    System.out.println(
        "    To display Show Number, Ticket#, Buyer Phone#, Seat Numbers allocated to the buyer");
    System.out.println(COMMAND_AVAILABILITY + " <Show Number>");
    System.out.println("    To list all available seat numbers for a show");
    System.out.println(COMMAND_BOOK + " <Show Number> <Phone#> <Comma separated list of seats>");
    System.out.println("    To book a ticket");
    System.out.println(COMMAND_CANCEL + " <Ticket#> <Phone#>");
    System.out.println("    To cancel a ticket");
    System.out.println(COMMAND_EXIT);
    System.out.println("    To exit the program");
    System.out.println();
  }
}

class Show {
  private int showNum;
  private int totalRows;
  private int totalSeats;
  private int cancellationWindow;

  private boolean[][] occupiedSeats;
  private List<Ticket> tickets;

  public Show(int showNum, int totalRows, int totalSeats, int cancellationWindow) {
    this.showNum = showNum;
    this.totalRows = totalRows;
    this.totalSeats = totalSeats;
    this.cancellationWindow = cancellationWindow;

    occupiedSeats = new boolean[totalRows][totalSeats];
    tickets = new ArrayList<>();
  }

  public int getCancellationWindow() {
    return cancellationWindow;
  }

  public boolean[][] getOccupiedSeats() {
    return occupiedSeats;
  }

  public void setOccupiedSeats(int row, int col, boolean flag) {
    occupiedSeats[row][col] = flag;
  }

  public List<Ticket> getTickets() {
    return tickets;
  }

  public void addTicket(Ticket ticket) {
    tickets.add(ticket);
  }

  public void removeTicket(Ticket ticket) {
    tickets.remove(ticket);
  }

  public void printAvailability() {
    System.out.println("-- Available Seats for Show Number " + showNum + ":");
    for (int r = 0; r < totalRows; r++) {
      for (int c = 0; c < totalSeats; c++) {
        if (!occupiedSeats[r][c]) {
          StringBuilder builder = new StringBuilder();
          builder.append((char) ('A' + r));
          builder.append(c + 1);
          System.out.print(builder.toString() + " ");
        } else {
          System.out.print("XX ");
        }
      }
      System.out.println();
    }
    System.out.println("** Occupied seats are indicated with XX");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Show show = (Show) o;
    return showNum == show.showNum;
  }

  @Override
  public int hashCode() {
    return Objects.hash(showNum);
  }

  @Override
  public String toString() {
    return "-- List of Shows: \nShow{"
        + "showNum="
        + showNum
        + ",\n"
        + "\ttickets="
        + tickets
        + '}';
  }
}

class Ticket {
  private int ticketNum;
  private int phoneNum;
  private int showNum;
  private List<String> seats;
  private Instant timestamp;

  public Ticket(int ticketNum, int phoneNum, int showNum, List<String> seats, Instant timestamp) {
    this.ticketNum = ticketNum;
    this.phoneNum = phoneNum;
    this.showNum = showNum;
    this.seats = seats;
    this.timestamp = timestamp;
  }

  public int getPhoneNum() {
    return phoneNum;
  }

  public int getShowNum() {
    return showNum;
  }

  public List<String> getSeats() {
    return seats;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Ticket ticket = (Ticket) o;
    return ticketNum == ticket.ticketNum && phoneNum == ticket.phoneNum;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticketNum, phoneNum);
  }

  @Override
  public String toString() {
    return "\n\t\tTicket{"
        + "ticketNum="
        + ticketNum
        + ", "
        + "phoneNum="
        + phoneNum
        + ", "
        + "seats="
        + seats
        + '}';
  }
}

class InputParam {
  private String command;
  private int showNum;
  // mode
  private boolean isAdmin;
  // setup
  private int totalRows;
  private int totalSeats;
  private int cancellationWindow;
  // book
  private int phoneNum;
  private List<String> seats;
  // cancel
  private int ticketNum;

  InputParam() {
    seats = new ArrayList<>();
    isAdmin = true;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public int getShowNum() {
    return showNum;
  }

  public void setShowNum(int showNum) {
    this.showNum = showNum;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  public int getTotalSeats() {
    return totalSeats;
  }

  public void setTotalSeats(int totalSeats) {
    this.totalSeats = totalSeats;
  }

  public int getCancellationWindow() {
    return cancellationWindow;
  }

  public void setCancellationWindow(int cancellationWindow) {
    this.cancellationWindow = cancellationWindow;
  }

  public int getPhoneNum() {
    return phoneNum;
  }

  public void setPhoneNum(int phoneNum) {
    this.phoneNum = phoneNum;
  }

  public List<String> getSeats() {
    return seats;
  }

  public void setSeats(List<String> seats) {
    this.seats = seats;
  }

  public int getTicketNum() {
    return ticketNum;
  }

  public void setTicketNum(int ticketNum) {
    this.ticketNum = ticketNum;
  }
}
