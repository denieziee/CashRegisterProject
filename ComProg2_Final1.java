import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class ComProg2_Final1 {
    private static final ArrayList<User> users = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    private static String currentUsername;

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //----------- STARTS HERE -----------
    @SuppressWarnings("UnnecessaryContinue")
    public static void main(String[] args) {
        clearScreen();
        while (true) {
            System.out.println("----------------------------------------------------");
            System.out.println("              W   E   L   C   O   M   E");
            System.out.println("----------------------------------------------------");
            System.out.println("  1 - Sign Up");
            System.out.println("  2 - Login");
            System.out.println("  3 - Exit\n");
            System.out.println("----------------------------------------------------");
            System.out.print("Select an option: ");
            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println(" Invalid input. Please enter a number.");
                sc.nextLine();
                continue;
            }
            switch (choice) {
                case 1 -> {
                    if (!signup()) {
                        continue; // User chose to go back during signup
                    }
                }
                case 2 -> {
                    if (login()) {
                        accessCashRegister();
                    }
                }
                case 3 -> {
                    System.out.println("\nExiting...");
                    return;
                }
                default -> System.out.println("\n         Invalid choice. Please try again.");
            }
        }
    }

    //----------- SIGN UP -----------
    private static boolean signup() {
        clearScreen();
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("""
                                                  G U I D E L I N E S

                When choosing your username, please keep these points in mind:
                 - It should be between 6 and 15 characters long.
                 - Please use only letters.
                 - Your username must start with an uppercase letter (A-Z).
                 - No special characters, numbers or spaces are allowed.

                For your password, here's what you need to know:
                 - It should be between 8 and 12 characters long.
                 - No special characters are allowed.
                 - Make sure to include at least one uppercase letter (A-Z) and at least one number (0-9).
                """ //
        );
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("*** SIGN UP ***");
        System.out.println("\n Note: type 'back' to return.\n");
        String username, password;
        while (true) {
            System.out.print(" Username: ");
            username = sc.nextLine().trim();
            if (username.equalsIgnoreCase("back")) {
                return false; // User chose to go back
            }
            if (!isValidUsername(username)) {
                System.out.println(" Invalid username. Please try again!\n");
                continue;
            }
            System.out.print(" Password: ");
            password = sc.nextLine().trim();
            if (!isValidPassword(password)) {
                System.out.println(" Invalid password. Please try again!\n");
                continue;
            }
            users.add(new User(username, password));
            clearScreen();
            System.out.println("              --- YOU'RE ALL SET! ---");
            return true; // Successful signup
        }
    }

    private static boolean isValidUsername(String username) {
        return Pattern.matches("^[A-Z][a-zA-Z]{6,12}$", username);
    }

    private static boolean isValidPassword(String password) {
        return Pattern.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$", password);
    }

    //----------- LOGIN -----------
    private static boolean login() {
        String username, password;
        boolean firstAttempt = true;

        while (true) {
            clearScreen();
            System.out.println("--------------------------------------------------------------------");
            System.out.println("*** LOGIN ***\n");
            System.out.println(" Note: type 'back' to return.\n");

            if (!firstAttempt) {
                System.out.println(" Invalid credentials. Please try again.\n");
            }

            System.out.print(" Username: ");
            username = sc.nextLine().trim();
            if (username.equalsIgnoreCase("back")) {
                return false;
            }
            System.out.print(" Password: ");
            password = sc.nextLine();
            if (password.equalsIgnoreCase("back")) {
                return false;
            }
            if (isValidLogin(username, password)) {
                currentUsername = username;
                return true;
            }
            firstAttempt = false;
        }
    }

    private static boolean isValidLogin(String username, String password) {
        return users.stream().anyMatch(user -> user.username.equals(username) && user.password.equals(password));
    }

    private static String getCurrentUsername() {
        return currentUsername;
    }

    //----------- CASH REGISTER -----------
    private static void accessCashRegister() {
        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<Double> itemPrices = new ArrayList<>();
        ArrayList<Integer> itemQuantities = new ArrayList<>();

        while (true) {
            clearScreen();
            double total = 0.0;

            for (int i = 0; i < itemNames.size(); i++) {
                total += itemPrices.get(i) * itemQuantities.get(i);
            }
            printReceipt(itemNames, itemPrices, itemQuantities, total);
            System.out.println("\n 1 - Add to Cart");
            System.out.println(" 2 - Update Item Quantity");
            System.out.println(" 3 - Delete Item");
            System.out.println(" 4 - Check out");
            System.out.println(" 5 - New Transaction");
            System.out.println(" 6 - Transaction History");
            System.out.println(" 7 - Exit");
            System.out.print("\n Select an option: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> addToCart(itemNames, itemPrices, itemQuantities);
                case 2 -> updateItemQuantity(itemNames, itemPrices, itemQuantities, total);
                case 3 -> deleteItem(itemNames, itemPrices, itemQuantities, total);
                case 4 -> checkOut(itemNames, itemPrices, itemQuantities, total);
                case 5 -> newTransaction(itemNames, itemPrices, itemQuantities);
                case 6 -> showTransactionHistory();
                case 7 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> {
                    System.out.println("\n --- INVALID OPTION ---\n");
                    System.out.println("Do you want to exit the program? (yes/no): ");
                    String response = sc.nextLine().trim();
                    if (response.equals("yes")) {
                        System.out.println("\n Exiting the program...");
                        System.exit(0);
                    }
                }
            }
            if (!continueTransaction()) {
                break; // Exit 
            }
        }
    }

    //----------- 1. ADD TO CART -----------
    private static void addToCart(ArrayList<String> itemNames, ArrayList<Double> itemPrices, ArrayList<Integer> itemQuantities) {
        clearScreen();
        System.out.println("\n *** Add to Cart ***");
        System.out.print("\n  - Item name: ");
        String itemName = sc.nextLine();
        double itemPrice = getItemPrice();
        int itemQuantity = getItemQuantity();
        System.out.println("\n  --- ADDED TO CART ---");
        itemNames.add(itemName);
        itemPrices.add(itemPrice);
        itemQuantities.add(itemQuantity);
        sortItems(itemNames, itemPrices, itemQuantities);
    }

    private static double getItemPrice() {
        double itemPrice = 0.00;
        while (true) {
            try {
                System.out.print("  - Price: Php ");
                itemPrice = Double.parseDouble(sc.nextLine());
                if (itemPrice < 0) {
                    System.out.println(" Price can't be negative. Try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid number.");
            }
        }
        return itemPrice;
    }

    private static int getItemQuantity() {
        int itemQuantity = 0;
        while (true) {
            try {
                System.out.print("  - Quantity: ");
                itemQuantity = Integer.parseInt(sc.nextLine());
                if (itemQuantity <= 0) {
                    System.out.println(" Quantity must be greater than zero. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println(" Invalid input for quantity. Please try again");
            }
        }
        return itemQuantity;
    }

    //----------- 2. UPDATE QUANTITY -----------
    private static void updateItemQuantity(ArrayList<String> itemNames, ArrayList<Double> itemPrices, ArrayList<Integer> itemQuantities, double total) {
        clearScreen();
        printReceipt(itemNames, itemPrices, itemQuantities, total);
        System.out.println("\n *** Update Item Quantity ***");
        System.out.print("\n - Item name: ");
        String nameToUpdate = sc.nextLine();
        int indexToUpdate = findItemIndex(itemNames, nameToUpdate);
        if (indexToUpdate != -1) {
            int newQuantity = getItemQuantity();
            itemQuantities.set(indexToUpdate, newQuantity);
            System.out.println("\n --- SUCCESSFUL ---");
        } else {
            System.out.println(" --- ITEM NOT FOUND ---");
        }
    }

    //----------- 3. DELETE ITEM -----------
    private static void deleteItem(ArrayList<String> itemNames, ArrayList<Double> itemPrices, ArrayList<Integer> itemQuantities, double total) {
        clearScreen();
        printReceipt(itemNames, itemPrices, itemQuantities, total);
        System.out.println("\n *** Delete an Item ***");
        System.out.println("\n 1 - Continue");
        System.out.println(" 2 - Cancel");
        System.out.print("\n Select an option: ");
        int removeChoice;
        try {
            removeChoice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(" Invalid input. Please enter 1 or 2.");
            return;
        }

        if (removeChoice == 1) {
            System.out.print("\n Item to remove: ");
            String nameToRemove = sc.nextLine();
            int indexToRemove = findItemIndex(itemNames, nameToRemove);
            if (indexToRemove != -1) {
                System.out.print(" Are you sure? (yes/no): ");
                String confirmation = sc.nextLine().trim().toLowerCase();
                if (confirmation.equals("yes")) {
                    itemNames.remove(indexToRemove);
                    itemPrices.remove(indexToRemove);
                    itemQuantities.remove(indexToRemove);
                    System.out.println("\n  --- ITEM REMOVED ---");
                } else {
                    System.out.println("\n --- CANCELED ---");
                }
            } else {
                System.out.println("\n --- ITEM NOT FOUND ---");
            }
        } else {
            System.out.println("\n --- CANCELED ---");
        }
    }

    //----------- 4. CHECKOUT -----------
    private static void checkOut(ArrayList<String> itemNames, ArrayList<Double> itemPrices, ArrayList<Integer> itemQuantities, double total) {
        clearScreen();
        System.out.println("\n *** Check Out ***");
        System.out.printf("\n   Total Amount: Php %.2f%n", total);
        double payment = getPaymentAmount();
        if (payment >= total) {
            double change = payment - total;
            System.out.printf("\n   --- PAYMENT ACCEPTED --- \n   Change: Php %.2f%n", change);
            logTransaction(getCurrentUsername(), itemNames, itemQuantities, itemPrices, total);
            itemNames.clear();
            itemPrices.clear();
            itemQuantities.clear();
            System.out.printf("\n   --- PAYMENT SUCCESSFUL ---");
        } else {
            System.out.println("   Insufficient Money.");
        }
    }

    //----------- FOR RECEIVING MONEY -----------
    private static double getPaymentAmount() {
        double payment = 0.0;
        while (true) {
            try {
                System.out.print("\n   Payment Amount: Php ");
                payment = Double.parseDouble(sc.nextLine());
                if (payment < 0) {
                    System.out.println("   Payment cannot be negative. Try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("   Invalid input. Enter a valid number.");
            }
        }
        return payment;
    }

    //----------- 5. NEW TRANSACTION -----------
    private static void newTransaction(ArrayList<String> itemNames, ArrayList<Double> itemPrices, ArrayList<Integer> itemQuantities) {
        clearScreen();
        System.out.println("\n *** New Transaction ***");
        System.out.println("\n   Clearing cart...");
        itemNames.clear();
        itemPrices.clear();
        itemQuantities.clear();
        System.out.println("\n Cart cleared. Starting new transaction...");
    }

    //----------- 6. TRANSACTION HISTORY -----------
    private static void showTransactionHistory() {
        clearScreen();
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("                     T R A N S A C T I O N   H I S T O R Y       ");
        System.out.println("----------------------------------------------------------------------------");

        try (BufferedReader reader = new BufferedReader(new FileReader("transactionss.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No transaction history found.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the transaction history: " + e.getMessage());
        }
        System.out.println("\nPress Enter to return to the Cash Register...");
        sc.nextLine();
    }

    //----------- PRINT RECEIPT -----------
    private static void printReceipt(ArrayList<String> itemNames, ArrayList<Double> itemPrices, ArrayList<Integer> itemQuantities, double total) {
        System.out.println(" ---------------------------------------------------------------------------");
        System.out.println("                          R   E   C   E   I   P   T");
        System.out.println(" ---------------------------------------------------------------------------");
        System.out.printf(" %-30s %-10s %-20s %-10s%n", " Item Name", "Quantity", "Price", "Subtotal");
        System.out.println(" ---------------------------------------------------------------------------");

        for (int i = 0; i < itemNames.size(); i++) {
            double subtotal = itemPrices.get(i) * itemQuantities.get(i);
            System.out.printf("   %-30s %-10s %-20s %-10s%n",
                    itemNames.get(i), itemQuantities.get(i), itemPrices.get(i), subtotal);
        }
        System.out.printf("\n   Total Amount :                                             Php %.2f%n", total);
        System.out.println(" ---------------------------------------------------------------------------");
        System.out.println("                              - Hello " + currentUsername + " -");
    }

    //----------- SORTING ITEMS -----------
    private static void sortItems(ArrayList<String> names, ArrayList<Double> prices, ArrayList<Integer> quantities) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            list.add(i);
        }

        Collections.sort(list, Comparator.comparing(names::get, String.CASE_INSENSITIVE_ORDER));
        ArrayList<String> sortedNames = new ArrayList<>();
        ArrayList<Double> sortedPrices = new ArrayList<>();
        ArrayList<Integer> sortedQuantities = new ArrayList<>();

        for (int index : list) {
            sortedNames.add(names.get(index));
            sortedPrices.add(prices.get(index));
            sortedQuantities.add(quantities.get(index));
        }

        //----------- FOR NEW TRANSACTION -----------
        names.clear();
        prices.clear();
        quantities.clear();
        names.addAll(sortedNames);
        prices.addAll(sortedPrices);
        quantities.addAll(sortedQuantities);
    }

    //----------- FOR TRANSACTION HISTORY -----------
    private static void logTransaction(String username, ArrayList<String> itemNames, ArrayList<Integer> itemQuantities, ArrayList<Double> itemPrices, double total) {
        String filePath = "c:\\Users\\Zeane Denel G. Capuy\\Documents\\NOTES\\2ND SEM\\ComProg 2\\codes\\transactionss.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("Date & Time: " + dateTime);
            writer.newLine();
            writer.write("Cashier: " + username);
            writer.newLine();
            writer.write("\nItems Purchased:");
            writer.newLine();
            for (int i = 0; i < itemNames.size(); i++) {
                writer.write(itemNames.get(i) + " - Quantity: " + itemQuantities.get(i) + ", Price: Php " + itemPrices.get(i));
                writer.newLine();
            }
            writer.write("\nTotal Amount: Php " + String.format("%.2f", total));
            writer.newLine();
            writer.write("-----------------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred while logging the transaction: " + e.getMessage());
        }
    }

    //----------- FOR CONTINUING TRANSACTION -----------
    private static boolean continueTransaction() {
        String anotherTransaction;
        while (true) {
            System.out.print("\n   Continue transaction? (yes/no): ");
            anotherTransaction = sc.nextLine().trim().toLowerCase();
            switch (anotherTransaction) {
                case "yes" -> {
                    clearScreen();
                    return true;
                }
                case "no" -> {
                    return false;
                }
                default -> System.out.println(" Invalid selection.");
            }
        }
    }

    //----------- FOR FINDING AN ITEM -----------
    private static int findItemIndex(ArrayList<String> itemNames, String nameToFind) {
        for (int i = 0; i < itemNames.size(); i++) {
            if (itemNames.get(i).equalsIgnoreCase(nameToFind)) {
                return i;
            }
        }
        return -1;
    }

    //----------- FOR CURRENT USERNAME -----------
    private static class User {
        String username;
        String password;

        User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
