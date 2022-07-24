import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.Arrays;

public class BookingASystemTest {
  private static final String WELCOME_TEXT =
      "Welcome to Booking a Show!\n"
          + "\n"
          + "List of Commands:\n"
          + "MODE <Type of User>\n"
          + "    Type of User: Admin/Buyer, to switch between these two users\n"
          + "    **Default type of user is Admin\n"
          + "SETUP <Show Number> <Number of Rows> <Number of seats per row> <Cancellation window in minutes>\n"
          + "    To setup the number of seats per show\n"
          + "VIEW <Show Number>\n"
          + "    To display Show Number, Ticket#, Buyer Phone#, Seat Numbers allocated to the buyer\n"
          + "AVAILABILITY <Show Number>\n"
          + "    To list all available seat numbers for a show\n"
          + "BOOK <Show Number> <Phone#> <Comma separated list of seats>\n"
          + "    To book a ticket\n"
          + "CANCEL <Ticket#> <Phone#>\n"
          + "    To cancel a ticket\n"
          + "EXIT\n"
          + "    To exit the program\n"
          + "\n"
          + "-- Current Mode: Admin ------------------------------------------------\n";
  private static final String PRINT_TEXT =
      "\n"
          + "List of Commands:\n"
          + "MODE <Type of User>\n"
          + "    Type of User: Admin/Buyer, to switch between these two users\n"
          + "    **Default type of user is Admin\n"
          + "SETUP <Show Number> <Number of Rows> <Number of seats per row> <Cancellation window in minutes>\n"
          + "    To setup the number of seats per show\n"
          + "VIEW <Show Number>\n"
          + "    To display Show Number, Ticket#, Buyer Phone#, Seat Numbers allocated to the buyer\n"
          + "AVAILABILITY <Show Number>\n"
          + "    To list all available seat numbers for a show\n"
          + "BOOK <Show Number> <Phone#> <Comma separated list of seats>\n"
          + "    To book a ticket\n"
          + "CANCEL <Ticket#> <Phone#>\n"
          + "    To cancel a ticket\n"
          + "EXIT\n"
          + "    To exit the program\n"
          + "\n";
  public static final String USER_INPUT_SIGN = ">>";
  public static final String USER_INPUT_SIGN_WAIT = ">> ";
  public static final String NEW_LINE = "\n";

  private final InputStream standardIn = System.in;
  private final PrintStream standardOut = System.out;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void tearDown() {
    System.setIn(standardIn);
    System.setOut(standardOut);
  }

  @Test
  public void setup_success() {
    String input = "SETUP 100 10 10 2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT + USER_INPUT_SIGN_WAIT + BookingASystem.MSG_SETUP + NEW_LINE + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_failure_show_num_negative() {
    String input = "SETUP -100 10 10 2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_INVALID_SHOW_NUMBER
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_failure_duplicate_show_num() {
    String input = "SETUP 100 6 5 2\nSETUP 100 10 10 2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_DUPLICATE_SHOW
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_failure_num_rows_exceed() {
    String input = "SETUP 100 27 10 2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_EXCEED_ROW_LIMIT
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_failure_num_rows_negative() {
    String input = "SETUP 100 -2 10 2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_INVALID_ROW
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_failure_num_seats_exceed() {
    String input = "SETUP 100 20 11 2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_EXCEED_SEAT_LIMIT
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_failure_num_seats_negative() {
    String input = "SETUP 100 20 -10 2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_INVALID_SEAT
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_failure_window_period_negative() {
    String input = "SETUP 100 20 10 -2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_INVALID_WINDOW
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_view_success() {
    String input = "SETUP 100 10 10 2\nVIEW 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);
    Show show = new Show(100, 10, 10, 2);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + show.toString()
            + NEW_LINE
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void view_failure() {
    String input = "VIEW 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_SHOW_NUMBER
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_availability_success() {
    String input = "SETUP 100 10 10 2\nMODE BUYER\nAVAILABILITY 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + ">> -- Available Seats for Show Number 100:\n"
            + "A1 A2 A3 A4 A5 A6 A7 A8 A9 A10 \n"
            + "B1 B2 B3 B4 B5 B6 B7 B8 B9 B10 \n"
            + "C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 \n"
            + "D1 D2 D3 D4 D5 D6 D7 D8 D9 D10 \n"
            + "E1 E2 E3 E4 E5 E6 E7 E8 E9 E10 \n"
            + "F1 F2 F3 F4 F5 F6 F7 F8 F9 F10 \n"
            + "G1 G2 G3 G4 G5 G6 G7 G8 G9 G10 \n"
            + "H1 H2 H3 H4 H5 H6 H7 H8 H9 H10 \n"
            + "I1 I2 I3 I4 I5 I6 I7 I8 I9 I10 \n"
            + "J1 J2 J3 J4 J5 J6 J7 J8 J9 J10 \n"
            + "** Occupied seats are indicated with XX\n"
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void availability_failure_mode() {
    String input = "AVAILABILITY 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_MODE
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void availability_failure_show_num() {
    String input = "MODE BUYER\nAVAILABILITY 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_SHOW_NUMBER
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_availability_success() {
    String input =
        "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 61234567 D3,D4,D5\nAVAILABILITY 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_BOOK
            + "1"
            + NEW_LINE
            + ">> -- Available Seats for Show Number 100:\n"
            + "A1 A2 A3 A4 A5 A6 A7 A8 A9 A10 \n"
            + "B1 B2 B3 B4 B5 B6 B7 B8 B9 B10 \n"
            + "C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 \n"
            + "D1 D2 XX XX XX D6 D7 D8 D9 D10 \n"
            + "E1 E2 E3 E4 E5 E6 E7 E8 E9 E10 \n"
            + "F1 F2 F3 F4 F5 F6 F7 F8 F9 F10 \n"
            + "G1 G2 G3 G4 G5 G6 G7 G8 G9 G10 \n"
            + "H1 H2 H3 H4 H5 H6 H7 H8 H9 H10 \n"
            + "I1 I2 I3 I4 I5 I6 I7 I8 I9 I10 \n"
            + "J1 J2 J3 J4 J5 J6 J7 J8 J9 J10 \n"
            + "** Occupied seats are indicated with XX\n"
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_view_success() {
    String input =
        "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 61234567 D3,D4,D5\nMODE ADMIN\nVIEW 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);
    Show show = new Show(100, 10, 10, 2);
    Ticket ticket = new Ticket(1, 61234567, 100, Arrays.asList("D3", "D4", "D5"), Instant.now());
    show.addTicket(ticket);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_BOOK
            + "1"
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_ADMIN
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + show.toString()
            + NEW_LINE
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_failure_show_num() {
    String input = "SETUP 100 10 10 2\nMODE BUYER\nBOOK 200 61234567 D3,D4,D5\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_SHOW_NUMBER
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_failure_phone_num_1() {
    String input = "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 123 D3,D4,D5\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_INVALID_PHONE_NUMBER
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_failure_phone_num_2() {
    String input = "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 -6123456 D3,D4,D5\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_INVALID_PHONE_NUMBER
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_failure_seat_num() {
    String input = "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 91234567 D12,A2\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_SEAT
            + "D12"
            + NEW_LINE
            + PRINT_TEXT
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_cancel_availability_success() {
    String input =
        "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 61234567 D3,D4,D5\nCANCEL 1 61234567\nAVAILABILITY 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_BOOK
            + "1"
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_CANCEL_SUCCESS
            + "1"
            + NEW_LINE
            + ">> -- Available Seats for Show Number 100:\n"
            + "A1 A2 A3 A4 A5 A6 A7 A8 A9 A10 \n"
            + "B1 B2 B3 B4 B5 B6 B7 B8 B9 B10 \n"
            + "C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 \n"
            + "D1 D2 D3 D4 D5 D6 D7 D8 D9 D10 \n"
            + "E1 E2 E3 E4 E5 E6 E7 E8 E9 E10 \n"
            + "F1 F2 F3 F4 F5 F6 F7 F8 F9 F10 \n"
            + "G1 G2 G3 G4 G5 G6 G7 G8 G9 G10 \n"
            + "H1 H2 H3 H4 H5 H6 H7 H8 H9 H10 \n"
            + "I1 I2 I3 I4 I5 I6 I7 I8 I9 I10 \n"
            + "J1 J2 J3 J4 J5 J6 J7 J8 J9 J10 \n"
            + "** Occupied seats are indicated with XX\n"
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_cancel_availability_failure_ticket_num() {
    String input =
        "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 61234567 D3,D4,D5\nCANCEL 2 61234567\nAVAILABILITY 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_BOOK
            + "1"
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_TICKET_NUMBER
            + NEW_LINE
            + PRINT_TEXT
            + ">> -- Available Seats for Show Number 100:\n"
            + "A1 A2 A3 A4 A5 A6 A7 A8 A9 A10 \n"
            + "B1 B2 B3 B4 B5 B6 B7 B8 B9 B10 \n"
            + "C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 \n"
            + "D1 D2 XX XX XX D6 D7 D8 D9 D10 \n"
            + "E1 E2 E3 E4 E5 E6 E7 E8 E9 E10 \n"
            + "F1 F2 F3 F4 F5 F6 F7 F8 F9 F10 \n"
            + "G1 G2 G3 G4 G5 G6 G7 G8 G9 G10 \n"
            + "H1 H2 H3 H4 H5 H6 H7 H8 H9 H10 \n"
            + "I1 I2 I3 I4 I5 I6 I7 I8 I9 I10 \n"
            + "J1 J2 J3 J4 J5 J6 J7 J8 J9 J10 \n"
            + "** Occupied seats are indicated with XX\n"
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }

  @Test
  public void setup_book_cancel_availability_failure_phone_num() {
    String input =
        "SETUP 100 10 10 2\nMODE BUYER\nBOOK 100 61234567 D3,D4,D5\nCANCEL 1 21234567\nAVAILABILITY 100\nEXIT\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    BookingASystem.main(new String[0]);

    Assert.assertEquals(
        WELCOME_TEXT
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_SETUP
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_MODE_BUYER
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.MSG_BOOK
            + "1"
            + NEW_LINE
            + USER_INPUT_SIGN_WAIT
            + BookingASystem.ERROR_MSG_INVALID_INPUT
            + BookingASystem.ERROR_MSG_MISMATCH_TICKET_PHONE
            + NEW_LINE
            + PRINT_TEXT
            + ">> -- Available Seats for Show Number 100:\n"
            + "A1 A2 A3 A4 A5 A6 A7 A8 A9 A10 \n"
            + "B1 B2 B3 B4 B5 B6 B7 B8 B9 B10 \n"
            + "C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 \n"
            + "D1 D2 XX XX XX D6 D7 D8 D9 D10 \n"
            + "E1 E2 E3 E4 E5 E6 E7 E8 E9 E10 \n"
            + "F1 F2 F3 F4 F5 F6 F7 F8 F9 F10 \n"
            + "G1 G2 G3 G4 G5 G6 G7 G8 G9 G10 \n"
            + "H1 H2 H3 H4 H5 H6 H7 H8 H9 H10 \n"
            + "I1 I2 I3 I4 I5 I6 I7 I8 I9 I10 \n"
            + "J1 J2 J3 J4 J5 J6 J7 J8 J9 J10 \n"
            + "** Occupied seats are indicated with XX\n"
            + USER_INPUT_SIGN,
        outputStreamCaptor.toString().trim());
  }
}
