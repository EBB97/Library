package com.ebb.library.library;

import com.ebb.library.library.LibraryModel.DatabaseException;
import com.ebb.library.library.pojos.BooksEntity;
import com.ebb.library.library.pojos.LendingEntity;
import com.ebb.library.library.pojos.ReservationsEntity;
import com.ebb.library.library.pojos.UsersEntity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LibraryController {
    // SECTION - USER
    @FXML
    private GridPane gridPaneUsers;
    @FXML
    private ImageView imageViewOptionsMainUsers;
    @FXML
    private TextField textFieldUsersCode;
    @FXML
    private TextField textFieldUsersFirstName;
    @FXML
    private TextField textFieldUsersLastName;
    @FXML
    private DatePicker datePickerUsersBirthdate;

    // SECTION - BOOK
    @FXML
    private GridPane gridPaneBooks;
    @FXML
    private ImageView imageViewOptionsMainBooks;
    @FXML
    private TextField textFieldBooksISBN;
    @FXML
    private TextField textFieldBooksTitle;
    @FXML
    private TextField textFieldBooksPublisher;
    @FXML
    private TextArea textAreaBooksCover;
    @FXML
    private TextArea textAreaBooksOutline;
    @FXML
    private TextField textFieldBooksCopies;
    @FXML
    private ComboBox<String> comboBoxBooksBorrowers;

    // SECTION - BORROWING

    @FXML
    private GridPane gridPaneBorrowing;
    @FXML
    private ImageView imageViewOptionsMainBorrowing;
    @FXML
    private TextField textFieldBorrowingUserCode;
    @FXML
    private TextField textFieldBorrowingUserName;
    @FXML
    private ImageView imageViewBorrowingUserBack;
    @FXML
    private TextField textFieldBorrowingBookISBN;
    @FXML
    private TextField textFieldBorrowingBookTitle;
    @FXML
    private ImageView imageViewBorrowingBookBack;

    // SECTION - RETURNING

    @FXML
    private GridPane gridPaneReturning;
    @FXML
    private ImageView imageViewOptionsMainReturning;
    @FXML
    private TextField textFieldReturningUserCode;
    @FXML
    private TextField textFieldReturningUserName;
    @FXML
    private ImageView imageViewReturningUserBack;
    @FXML
    private TextField textFieldReturningBookISBN;
    @FXML
    private TextField textFieldReturningBookTitle;
    @FXML
    private ImageView imageViewReturningBookBack;

    // SECTION - MENUS/OPTIONS

    @FXML
    private GridPane gridPaneOptionsUsersBooksDefault;
    @FXML
    private GridPane gridPaneOptionsUsersBooksConfirmCancel;
    @FXML
    private ImageView imageViewOptionsSecondaryCancel;

    // These variables are used to keep track of the current and/or last menu selected.
    // I chose to categorize them this way because it makes it easier for me to make sure
    // I'm not mixing up menus or writing with typos.
    private byte currentMenu;

    private final byte menuUsers = 1;
    private final byte menuBooks = 2;
    private final byte menuBorrowing = 3;
    private final byte menuReturning = 4;
    // These variables are used to keep track of the current and/or last menu option selected.
    private byte currentOption;

    private final byte optionSearch = 1;
    private final byte optionAdd = 2;
    private final byte optionEdit = 3;

    private final byte optionDelete = 4;

    LibraryModel libraryModel;

    // This method runs at the start.
    @FXML
    public void initialize() {
        libraryModel = new LibraryModelSpring();

        // We set the default menu values.
        currentMenu = menuUsers;
        currentOption = 0;

        gridPaneBooks.setVisible(false);
        gridPaneBorrowing.setVisible(false);
        gridPaneReturning.setVisible(false);

        imageViewOptionsMainUsers.setOpacity(1.0);
        imageViewOptionsMainBooks.setOpacity(0.25);
        imageViewOptionsMainBorrowing.setOpacity(0.25);
        imageViewOptionsMainReturning.setOpacity(0.25);

        imageViewBorrowingUserBack.setVisible(false);
        imageViewBorrowingBookBack.setVisible(false);
        imageViewReturningUserBack.setVisible(false);
        imageViewReturningBookBack.setVisible(false);

        // We configure the calendar found in the "Users" menu.
        // Dates before 04-03-1907 or after the current day will be greyed out.
        LocalDate minDate = LocalDate.of(1907, 3, 4);
        LocalDate maxDate = LocalDate.now();

        datePickerUsersBirthdate.setDayCellFactory(datePicker ->
                new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isAfter(maxDate) || item.isBefore(minDate));
                    }
                });
    }

    // The following four methods trigger upon selecting
    // each of the four menus ("Users", "Books", "Borrowing", "Returning").
    @FXML
    protected void onOptionsUsers() {
        switchMenu(menuUsers);
    }

    @FXML
    protected void onOptionsBooks() {
        switchMenu(menuBooks);
    }

    @FXML
    protected void onOptionsBorrowing() {
        switchMenu(menuBorrowing);
    }

    @FXML
    protected void onOptionsReturning() {
        switchMenu(menuReturning);
    }

    // This method keeps track of the top menu we are currently viewing and the top menu we are switching to.
    // It primarily serves to change the opacity of the menu buttons and the visibility of the relevant
    // fields. It also clears the relevant top menu fields to leave them empty.
    protected void switchMenu(byte newMenu) {
        double noncurrentMenuOpacity = 0.25;
        double currentMenuOpacity = 1.0;

        cleanAllMenuFields();

        // These two conditions are used to check if we're switching from "Users"/"Books" to "Borrowing"/"Returning"
        // or vice-versa. This is because the bottom menu has different functionality depending on which
        // type of top menu we're on.
        if ((currentMenu == menuUsers || currentMenu == menuBooks) && (newMenu == menuBorrowing || newMenu == menuReturning)) {
            imageViewOptionsSecondaryCancel.setVisible(false);
            gridPaneOptionsUsersBooksDefault.setVisible(false);
            gridPaneOptionsUsersBooksConfirmCancel.setVisible(true);
        }

        if ((currentMenu == menuBorrowing || currentMenu == menuReturning) && (newMenu == menuUsers || newMenu == menuBooks)) {
            imageViewOptionsSecondaryCancel.setVisible(true);
            gridPaneOptionsUsersBooksDefault.setVisible(true);
            gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
        }

        // Actions relevant to the current top menu.
        // - The top menu is made invisible.
        // - Its top menu button has its opacity reduced.
        switch (currentMenu) {
            case menuUsers: {
                gridPaneUsers.setVisible(false);
                imageViewOptionsMainUsers.setOpacity(noncurrentMenuOpacity);

                break;
            }
            case menuBooks: {
                gridPaneBooks.setVisible(false);
                imageViewOptionsMainBooks.setOpacity(noncurrentMenuOpacity);

                // Due to its special functionality, we also disable the list of book borrowers here.
                comboBoxBooksBorrowers.setDisable(true);

                break;
            }

            case menuBorrowing: {
                gridPaneBorrowing.setVisible(false);
                imageViewOptionsMainBorrowing.setOpacity(noncurrentMenuOpacity);

                // Due to their differing functionality, we also disable these fields.
                textFieldBorrowingUserCode.setDisable(false);
                textFieldBorrowingBookISBN.setDisable(false);

                break;
            }
            case menuReturning: {
                gridPaneReturning.setVisible(false);
                imageViewOptionsMainReturning.setOpacity(noncurrentMenuOpacity);

                // Due to their differing functionality, we also disable these fields.
                textFieldReturningUserCode.setDisable(false);
                textFieldReturningBookISBN.setDisable(false);

                break;
            }
        }

        // Actions relevant to the new top menu we're switching to.
        // - The top menu is made visible.
        // - The top menu button is brought to full opacity.
        // - The variable for current top menu is updated.
        switch (newMenu) {
            case menuUsers: {
                gridPaneUsers.setVisible(true);
                imageViewOptionsMainUsers.setOpacity(currentMenuOpacity);

                currentMenu = menuUsers;

                break;
            }
            case menuBooks: {
                gridPaneBooks.setVisible(true);
                imageViewOptionsMainBooks.setOpacity(currentMenuOpacity);

                currentMenu = menuBooks;

                break;
            }
            case menuBorrowing: {
                gridPaneBorrowing.setVisible(true);
                imageViewOptionsMainBorrowing.setOpacity(currentMenuOpacity);

                currentMenu = menuBorrowing;

                break;
            }
            case menuReturning: {
                gridPaneReturning.setVisible(true);
                imageViewOptionsMainReturning.setOpacity(currentMenuOpacity);

                currentMenu = menuReturning;

                break;
            }
        }
    }

    // This method triggers on pressing the "Exit" menu option.
    @FXML
    protected void onOptionsExit() {
        Platform.exit();
    }

    // This method triggers on hitting the bottom menu's "Search" button.
    @FXML
    protected void onUsersBooksSearch() {
        // We update the current bottom menu we're on.
        currentOption = optionSearch;

        // We disable all top menu options.
        invertMenuMainAvailability();

        // We clear all relevant fields.
        cleanAllMenuFields();

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(true);
        gridPaneOptionsUsersBooksDefault.setVisible(false);

        // Actions relevant to the top menu we're on.
        // - We enable the code/ISBN field.
        // - We set cursor focus on said field.
        switch (currentMenu) {
            case menuUsers: {
                textFieldUsersCode.setDisable(false);
                textFieldUsersCode.requestFocus();

                break;
            }
            case menuBooks: {
                textFieldBooksISBN.setDisable(false);
                textFieldBooksISBN.requestFocus();

                comboBoxBooksBorrowers.setDisable(true);

                break;
            }
        }
    }

    // This method triggers on hitting the bottom menu's "Add" button.
    @FXML
    protected void onUsersBooksAdd() {
        // We update the current bottom menu we're on.
        currentOption = optionAdd;

        // We disable all top menu options.
        invertMenuMainAvailability();

        // We clear and enable all relevant fields.
        cleanAllMenuFields();
        setAllFieldsAvailability(true);

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(true);
        gridPaneOptionsUsersBooksDefault.setVisible(false);

        // Actions relevant to the top menu we're on.
        // - We set cursor focus on the code/ISBN field.
        switch (currentMenu) {
            case menuUsers: {
                textFieldUsersCode.requestFocus();

                break;
            }
            case menuBooks: {
                textFieldBooksISBN.requestFocus();

                // We disable the list of book borrowers, as it is not currently necessary.
                comboBoxBooksBorrowers.setDisable(true);

                break;
            }
        }
    }

    // This method triggers on hitting the bottom menu's "Edit" button.
    @FXML
    protected void onUsersBooksEdit() {
        // We update the current bottom menu we're on.
        currentOption = optionEdit;

        // We disable all top menu options.
        invertMenuMainAvailability();

        // We enable all relevant fields.
        setAllFieldsAvailability(true);

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(true);
        gridPaneOptionsUsersBooksDefault.setVisible(false);

        // Actions relevant to the top menu we're on.
        // - We disable the code/ISBN keys (they are primary keys and cannot be edited).
        // - We set cursor focus on the name field.
        switch (currentMenu) {
            case menuUsers: {
                textFieldUsersCode.setDisable(true);
                textFieldUsersFirstName.requestFocus();

                break;
            }
            case menuBooks: {
                textFieldBooksISBN.setDisable(true);
                comboBoxBooksBorrowers.setDisable(true);

                textFieldBooksTitle.requestFocus();

                break;
            }
        }
    }

    @FXML
    protected void onUsersBooksDelete() {
        // We update the current bottom menu we're on.
        currentOption = optionDelete;

        // We disable all top menu options.
        invertMenuMainAvailability();

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(true);
        gridPaneOptionsUsersBooksDefault.setVisible(false);
    }

    // This method triggers on hitting the "Borrowing" menu's "User Search" button.
    @FXML
    protected void onBorrowingUserSearch() {
        searchUser(textFieldBorrowingUserCode, imageViewBorrowingUserBack, textFieldBorrowingUserName);
    }

    // This method triggers on hitting the "Borrowing" menu's "User Search" back button.
    @FXML
    protected void onBorrowingUserBack() {
        textFieldBorrowingUserCode.setDisable(false);

        textFieldBorrowingUserCode.setText("");
        textFieldBorrowingUserName.setText("");
    }

    // This method triggers on hitting the "Borrowing" menu's "Book Search" button.
    @FXML
    protected void onBorrowingBookSearch() {
        searchBook(textFieldBorrowingBookISBN, imageViewBorrowingBookBack, textFieldBorrowingBookTitle);
    }

    // This method triggers on hitting the "Borrowing" menu's "Book Search" back button.
    @FXML
    protected void onBorrowingBookBack() {
        textFieldBorrowingBookISBN.setDisable(false);

        textFieldBorrowingBookISBN.setText("");
        textFieldBorrowingBookTitle.setText("");
    }

    // This method triggers on hitting the "Returning" menu's "User Search" button.
    @FXML
    protected void onReturningUserSearch() {
        searchUser(textFieldReturningUserCode, imageViewReturningUserBack, textFieldReturningUserName);
    }

    // This method triggers on hitting the "Returning" menu's "User Search" back button.
    @FXML
    protected void onReturningUserBack() {
        textFieldReturningUserCode.setDisable(false);

        textFieldReturningUserCode.setText("");
        textFieldReturningUserName.setText("");
    }

    // This method triggers on hitting the "Returning" menu's "Book Search" button.
    @FXML
    protected void onReturningBookSearch() {
        searchBook(textFieldReturningBookISBN, imageViewReturningBookBack, textFieldReturningBookTitle);
    }

    // This method triggers on hitting the "Returning" menu's "Book Search" back button.
    @FXML
    protected void onReturningBookBack() {
        textFieldReturningBookISBN.setDisable(false);

        textFieldReturningBookISBN.setText("");
        textFieldReturningBookTitle.setText("");
    }

    // This method triggers on hitting the bottom menu's "Confirm" button.
    @FXML
    protected void onOptionsConfirm() {
        try {
            // Actions relevant to the top menu we're on.
            switch (currentMenu) {
                // Actions relevant to the "Users" top menu.
                case menuUsers: {
                    // Actions relevant to the bottom menu we're on.
                    switch (currentOption) {
                        // Actions relevant to the "Search" bottom menu.
                        case optionSearch: {
                            // If the user code is blank.
                            if (textFieldUsersCode.getText().isBlank()) {
                                throw new IllegalArgumentException("User code cannot be blank!");
                            }

                            // If the user code is not blank, we attempt to search for the user in the database.
                            UsersEntity user = libraryModel.getUser(textFieldUsersCode.getText());

                            // If the user is not found.
                            if (user == null) {
                                throw new IllegalArgumentException("User code not found in database!");
                            }

                            // If the user is found.
                            // We disable the code field.
                            textFieldUsersCode.setDisable(true);

                            // We fill the rest of the fields with the user data.
                            textFieldUsersFirstName.setText(user.getName());
                            textFieldUsersLastName.setText(user.getSurname());
                            if (user.getBirthdate() != null) {
                                datePickerUsersBirthdate.setValue(user.getBirthdate().toLocalDate());
                            }

                            // We enable all top menu options.
                            invertMenuMainAvailability();

                            // We switch out the bottom menu visible.
                            gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
                            gridPaneOptionsUsersBooksDefault.setVisible(true);

                            break;
                        }
                        // Actions relevant to the "Add" bottom menu.
                        case optionAdd: {
                            createOrUpdateUser(true);

                            break;
                        }
                        // Actions relevant to the "Edit" bottom menu.
                        case optionEdit: {
                            // If the user code is blank.
                            // (This should only happen if the user code is somehow deleted between search and edit.
                            // I have given it a different alert message, just in case.)
                            if (textFieldUsersCode.getText().isBlank()) {
                                throw new IllegalArgumentException("User code cannot be blank!");
                            }

                            // If the user code is not blank, we proceed with the editing.
                            createOrUpdateUser(false);

                            break;
                        }
                        case optionDelete: {
                            if (textFieldUsersCode.getText().isBlank()) {
                                throw new IllegalArgumentException("User code cannot be blank!");
                            }

                            String userCode = textFieldUsersCode.getText();

                            if (libraryModel.isUserLending(userCode)) {
                                makeGenericAlert(
                                        Alert.AlertType.WARNING,
                                        "This user cannot be deleted because they have pending lendings.",
                                        "Unable to delete user!");
                            } else if (libraryModel.isUserReserving(userCode)) {
                                ButtonType alertResult = makeGenericAlert(Alert.AlertType.CONFIRMATION,
                                        "This user has pending reservations!",
                                        "Are you sure you want to delete this user?");

                                if (alertResult == ButtonType.OK) {
                                    deleteUser();
                                }
                            } else {
                                ButtonType alertResult = makeGenericAlert(Alert.AlertType.CONFIRMATION,
                                        "Deleting users is irreversible!",
                                        "Are you sure you want to delete this user?");

                                if (alertResult == ButtonType.OK) {
                                    deleteUser();
                                }
                            }

                            cleanAllMenuFields();

                            // We switch out the bottom menu visible.
                            gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
                            gridPaneOptionsUsersBooksDefault.setVisible(true);

                            break;
                        }
                    }

                    break;
                }
                // Actions relevant to the "Books" top menu.
                case menuBooks: {
                    // Actions relevant to the bottom menu we're on.
                    switch (currentOption) {
                        // Actions relevant to the "Search" bottom menu.
                        case optionSearch: {
                            // If the book ISBN is blank.
                            if (textFieldBooksISBN.getText().isBlank()) {
                                throw new IllegalArgumentException("Book ISBN cannot be blank!");
                            }

                            // If the book ISBN is not blank, we attempt to search for the book in the database.
                            BooksEntity book = libraryModel.getBook(textFieldBooksISBN.getText());

                            // If the book is not found.
                            if (book == null) {
                                throw new IllegalArgumentException("Book ISBN not found in database!");
                            }

                            // If the book is found.
                            // We disable the ISBN field.
                            textFieldBooksISBN.setDisable(true);

                            // We fill the rest of the fields with the book data.
                            textFieldBooksTitle.setText(book.getTitle());
                            textFieldBooksPublisher.setText(book.getPublisher());
                            textAreaBooksCover.setText(book.getCover());
                            textAreaBooksOutline.setText(book.getOutline());

                            int availableCopies = book.getCopies();

                            textFieldBooksCopies.setText(String.valueOf(availableCopies));

                            // This part generates and formats a list of borrowers to add
                            // to the 'ComboBox'.
                            ObservableList<String> borrowers = FXCollections.observableArrayList();

                            if (borrowers.size() > 0) {
                                for (LendingEntity lending : book.getBorrowedBy()) {
                                    String code = lending.getBorrowerCode();
                                    String firstName = lending.getBorrower().getName();
                                    String lastName = lending.getBorrower().getSurname();

                                    borrowers.add("[" + code + "] " + firstName + " " + lastName);
                                }

                                comboBoxBooksBorrowers.setItems(borrowers);
                                comboBoxBooksBorrowers.getSelectionModel().select(0);
                                comboBoxBooksBorrowers.setDisable(false);
                            }

                            // We enable all top menu options.
                            invertMenuMainAvailability();

                            // We switch out the bottom menu visible.
                            gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
                            gridPaneOptionsUsersBooksDefault.setVisible(true);

                            break;
                        }
                        // Actions relevant to the "Add" bottom menu.
                        case optionAdd: {
                            createOrUpdateBook(true);

                            break;
                        }
                        // Actions relevant to the "Edit" bottom menu.
                        case optionEdit: {
                            // If the book ISBN is blank.
                            // (This should only happen if the book ISBN is somehow deleted between search and edit.
                            // I have given it a different alert message, just in case.)

                            if (textFieldBooksISBN.getText().isBlank()) {
                                throw new IllegalArgumentException("Book ISBN cannot be blank!");
                            }

                            // If the book ISBN is not blank, we proceed with the editing.
                            createOrUpdateBook(false);

                            break;
                        }
                        case optionDelete: {
                            if (textFieldBooksISBN.getText().isBlank()) {
                                throw new IllegalArgumentException("Book ISBN cannot be blank!");
                            }

                            String bookISBN = textFieldBooksISBN.getText();

                            if (libraryModel.isBookLent(bookISBN)) {
                                makeGenericAlert(
                                        Alert.AlertType.WARNING,
                                        "This book cannot be deleted because it has pending lendings.",
                                        "Unable to delete book!");
                            } else if (libraryModel.isBookReserved(bookISBN)) {
                                ButtonType alertResult = makeGenericAlert(Alert.AlertType.CONFIRMATION,
                                        "This book has pending reservations!",
                                        "Are you sure you want to delete this book?");

                                if (alertResult == ButtonType.OK) {
                                    deleteBook();
                                }
                            } else {
                                ButtonType alertResult = makeGenericAlert(Alert.AlertType.CONFIRMATION,
                                        "Deleting books is irreversible!",
                                        "Are you sure you want to delete this book?");

                                if (alertResult == ButtonType.OK) {
                                    deleteBook();
                                }
                            }

                            cleanAllMenuFields();

                            // We switch out the bottom menu visible.
                            gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
                            gridPaneOptionsUsersBooksDefault.setVisible(true);

                            break;
                        }
                    }

                    break;
                }
                // Actions relevant to the "Borrowing" top menu.
                case menuBorrowing: {
                    // If either field is not disabled, which means nothing has been searched yet.
                    if (!textFieldBorrowingUserCode.isDisabled() || !textFieldBorrowingBookISBN.isDisabled()) {
                        throw new IllegalArgumentException("Must first search for a user and a book!");
                    }

                    // We gather the data from the user code field.
                    String userCode = textFieldBorrowingUserCode.getText();

                    // We gather the 'User' that matches the given user code.
                    UsersEntity user = libraryModel.getUser(userCode);

                    // If the user is still fined.
                    Date fineEndDate = user.getFined();

                    if (fineEndDate != null) {
                        LocalDate fineEndLocalDate = fineEndDate.toLocalDate();

                        if (fineEndLocalDate.compareTo(LocalDate.now()) >= 0) {
                            throw new IllegalArgumentException("Fined user can't borrow books until " + fineEndDate + "!");
                        }
                    }

                    // We set the maximum number of books that a user can concurrently borrow.
                    int userBorrowingLimit = 3;

                    // We get the number of books the user is currently borrowing.
                    int userBooksBorrowed = getActiveLendingCount(user.getLentBooks(), userBorrowingLimit);

                    // If the user is already borrowing the maximum number or higher.
                    if (userBooksBorrowed >= userBorrowingLimit) {
                        throw new IllegalArgumentException("Users can only borrow up to 3 books at a time!");
                    }

                    // We gather the data from the book ISBN field.
                    String bookISBN = textFieldBorrowingBookISBN.getText();

                    // If the user is already borrowing the book.
                    if (libraryModel.isUserLendingBook(userCode, bookISBN)) {
                        throw new IllegalArgumentException("User is already borrowing this book!");
                    }

                    // We gather the 'Book' that matches the given book ISBN.
                    BooksEntity book = libraryModel.getBook(bookISBN);

                    // We get the number of available copies of the book.
                    int bookAvailableCopies = book.getCopies();

                    // We get the number of active lendings for the book.
                    int bookActiveLendings = getActiveLendingCount(book.getBorrowedBy(), bookAvailableCopies);

                    // If there are no copies of the book available to borrow,
                    // we prompt the user on whether they want to make a reservation.
                    if (bookActiveLendings >= bookAvailableCopies) {
                        ButtonType alertResult = makeGenericAlert(Alert.AlertType.CONFIRMATION,
                                "Do you wish to make a reservation?",
                                "No copies available!");

                        if (alertResult == ButtonType.OK) {
                            // The user can only make a reservation if they don't already have one.
                            if (libraryModel.isUserReservingBook(userCode, bookISBN)) {
                                throw new IllegalArgumentException("User already has an active reservation on this book!");
                            }

                            createReservation(userCode, bookISBN);
                        }
                    } else {
                        // If the user is has a reservation on the book.
                        if (libraryModel.isUserReservingBook(userCode, bookISBN)) {
                            int lendingId = libraryModel.getLatestActiveReservationId(userCode, bookISBN);

                            libraryModel.getNextActiveReservationId(bookISBN);

                            // If the user's reservation is not the oldest one for the book.
                            if (lendingId != libraryModel.getNextActiveReservationId(bookISBN)) {
                                throw new IllegalArgumentException("User has an active reservation on this book, but it isn't their turn!");
                            }

                            // If the user has the oldest reservation, the reservation lending date is updated
                            // and the book is lent to the user.
                            LocalDate currentDate = LocalDate.now();

                            libraryModel.updateReservationLendingDate(lendingId, currentDate);

                            createLending(userCode, bookISBN, currentDate);
                        } else if (libraryModel.isBookReserved(bookISBN)) {
                            // If the book is reserved, but not by the user.
                            ButtonType alertResult = makeGenericAlert(Alert.AlertType.CONFIRMATION,
                                    "Do you wish to make a reservation of your own?",
                                    "Book has pending reservations by other users!");

                            if (alertResult == ButtonType.OK) {
                                createReservation(userCode, bookISBN);
                            }
                        } else {
                            // If the book has no reservations, it is lent to the user.
                            createLending(userCode, bookISBN, LocalDate.now());
                        }
                    }

                    // We enable all relevant fields.
                    textFieldBorrowingUserCode.setDisable(false);
                    textFieldBorrowingBookISBN.setDisable(false);

                    // We clear all relevant fields.
                    cleanAllMenuFields();

                    break;
                }
                // Actions relevant to the "Returning" top menu.
                case menuReturning: {
                    // If either field is not disabled, which means nothing has been searched yet.
                    if (!textFieldReturningUserCode.isDisabled() || !textFieldReturningBookISBN.isDisabled()) {
                        throw new IllegalArgumentException("Must first search for a user and a book+!");
                    }

                    // We gather the data from the user input fields.
                    String userCode = textFieldReturningUserCode.getText();
                    String bookISBN = textFieldReturningBookISBN.getText();

                    // If the user is not borrowing the book.
                    if (!libraryModel.isUserLendingBook(userCode, bookISBN)) {
                        throw new IllegalArgumentException("That user is not borrowing that book.");
                    }

                    // We obtain the current date.
                    LocalDate currentDate = LocalDate.now();

                    int lendingId = libraryModel.getLatestActiveLendingId(userCode, bookISBN);

                    LocalDate lendingDate = libraryModel.getLendingLendingDate(lendingId);

                    // If the current date is greater than the book's return due date,
                    // the user is fined.
                    if (lendingDate.plusDays(7).compareTo(currentDate) < 0) {
                        libraryModel.fineUser(userCode);

                        Alert alert = new Alert(Alert.AlertType.WARNING,
                                "User has been fined!",
                                ButtonType.OK);

                        alert.setHeaderText("Book returned past due date!");

                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                        alert.setResizable(true);

                        alert.showAndWait();

                        if (alert.getResult() == ButtonType.OK) {
                            alert.close();
                        }
                    }

                    String nextReservee;
                    try {
                        nextReservee = libraryModel.getNextActiveReservationUserName(bookISBN);
                    } catch (Exception exception) {
                        nextReservee = null;
                    }

                    // If there is another user waiting for the book to be available, we display that name
                    // in the success alert.
                    String nextReserveeMessage = nextReservee != null ? " Next user: " + nextReservee : "";

                    libraryModel.updateLendingReturningDate(lendingId, LocalDate.now());

                    makeSuccessAlert("Book has been returned!" + nextReserveeMessage);

                    // We enable all relevant fields.
                    textFieldReturningUserCode.setDisable(false);
                    textFieldReturningBookISBN.setDisable(false);

                    // We clear all relevant fields.
                    cleanAllMenuFields();

                    break;
                }
            }
        } catch (DatabaseException | IllegalArgumentException exception) {
            makeExceptionAlert(exception);
        }
    }

    // This method discerns whether we are creating or updating a user, and acts accordingly.
    private void createOrUpdateUser(boolean isCreating) throws DatabaseException {
        // This variable changes depending on whether we are creating or updating a user.
        String alertAux;

        // We gather the data from the user input fields.
        String code = textFieldUsersCode.getText();
        String firstName = textFieldUsersFirstName.getText();
        String lastName = textFieldUsersLastName.getText();

        // If any of the fields are empty.
        if (code.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException("You must fill all fields!");
        }

        // If none of the fields are empty, we attempt to validate the input date.
        LocalDate birthdate = validateBirthdate();

        // If the input date is not valid.
        if (birthdate == null) {
            throw new IllegalArgumentException("You must introduce a valid date!");
        }

        // We create a 'User' to store the data into.
        UsersEntity user = new UsersEntity();

        user.setCode(code);
        user.setName(firstName);
        user.setSurname(lastName);
        user.setBirthdate(Date.valueOf(birthdate));

        // We attempt to create/edit the 'User' on the database.
        if (isCreating) {
            libraryModel.createUser(user);
            alertAux = "created";
        } else {
            libraryModel.updateUser(user);
            alertAux = "updated";
        }

        // We clear all relevant fields.
        cleanAllMenuFields();
        // We disable all relevant fields.
        setAllFieldsAvailability(false);
        // We enable all top menu options.
        invertMenuMainAvailability();

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
        gridPaneOptionsUsersBooksDefault.setVisible(true);

        makeSuccessAlert("You have " + alertAux + " a user in the database!");
    }

    private void deleteUser() throws DatabaseException {
        // We gather the data from the user input fields.
        String code = textFieldUsersCode.getText();

        libraryModel.deleteUser(code);

        // We clear all relevant fields.
        cleanAllMenuFields();
        // We disable all relevant fields.
        setAllFieldsAvailability(false);
        // We enable all top menu options.
        invertMenuMainAvailability();

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
        gridPaneOptionsUsersBooksDefault.setVisible(true);

        makeSuccessAlert("You have deleted a user in the database!");
    }

    // This method discerns whether we are creating or updating a book, and acts accordingly.
    private void createOrUpdateBook(boolean isCreating) throws DatabaseException {
        // This variable changes depending on whether we are creating or updating a book.
        String alertAux;

        // We gather the data from the book input fields.
        String isbn = textFieldBooksISBN.getText();
        String title = textFieldBooksTitle.getText();
        String publisher = textFieldBooksPublisher.getText();
        String cover = textAreaBooksCover.getText();
        String outline = textAreaBooksOutline.getText();

        // If any of the fields are empty.
        if (isbn.isBlank() || title.isBlank() || publisher.isBlank() || cover.isBlank() || outline.isBlank()) {
            throw new IllegalArgumentException("You must fill all fields!");
        }

        if (isbn.length() != 13) {
            throw new IllegalArgumentException("Book ISBN must be 13 characters long!");
        }

        // We check that the copies input are a valid number.
        int copies;

        try {
            copies = Integer.parseInt(textFieldBooksCopies.getText());
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("You must input a valid number of copies!");
        }

        // If the number of copies is less than ''0''.
        if (copies <= 0) {
            throw new IllegalArgumentException("You must input a positive number of copies!");
        }

        // We create a 'Book' entity to store the data into.
        BooksEntity book = new BooksEntity();

        book.setIsbn(isbn);
        book.setTitle(title);
        book.setPublisher(publisher);
        book.setCover(cover);
        book.setOutline(outline);
        book.setCopies(copies);

        // We attempt to create/edit the 'Book' on the database.
        if (isCreating) {
            libraryModel.createBook(book);
            alertAux = "created";
        } else {
            libraryModel.updateBook(book);
            alertAux = "updated";
        }

        // We clear all relevant fields.
        cleanAllMenuFields();
        // We disable all relevant fields.
        setAllFieldsAvailability(false);
        // We enable all top menu options.
        invertMenuMainAvailability();

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
        gridPaneOptionsUsersBooksDefault.setVisible(true);

        makeSuccessAlert("You have " + alertAux + " a book in the database!");
    }

    private void deleteBook() throws DatabaseException {
        // We gather the data from the user input fields.
        String isbn = textFieldBooksISBN.getText();

        libraryModel.deleteBook(isbn);

        // We clear all relevant fields.
        cleanAllMenuFields();
        // We disable all relevant fields.
        setAllFieldsAvailability(false);
        // We enable all top menu options.
        invertMenuMainAvailability();

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
        gridPaneOptionsUsersBooksDefault.setVisible(true);

        makeSuccessAlert("You have deleted a book in the database!");
    }

    // This method creates a 'Lending' and adds it to the database.
    private void createLending(String userCode, String bookISBN, LocalDate currentDate) throws DatabaseException {
        // We save the borrowing on the database.
        LendingEntity lending = new LendingEntity();

        lending.setBookISBN(bookISBN);
        lending.setBorrowerCode(userCode);

        if (bookISBN == null || bookISBN.isBlank() || userCode == null || userCode.isBlank()) {
            throw new IllegalArgumentException("Book ISBN and user code must not be null!");
        }

        if (currentDate == null) {
            throw new IllegalArgumentException("Lending date must not be null!");
        }

        lending.setLendingDate(Date.valueOf(currentDate));

        libraryModel.createLending(lending);

        makeSuccessAlert("Book successfully borrowed!");
    }

    // This method creates a 'Reservations' and adds it to the database.
    private void createReservation(String userCode, String bookISBN) throws DatabaseException {
        ReservationsEntity reservation = new ReservationsEntity();

        if (bookISBN == null || bookISBN.isBlank() || userCode == null || userCode.isBlank()) {
            throw new IllegalArgumentException("Book ISBN and user code must not be null!");
        }

        reservation.setBookISBN(bookISBN);
        reservation.setReserveeCode(userCode);

        libraryModel.createReservation(reservation);
    }

    // This method is used in the "Borrowing" and "Returning" menus to search for the full name of a user code.
    // The name is then displayed in the relevant field.
    private void searchUser(TextField textFieldBorrowingUserCode, ImageView imageViewBorrowingUserBack, TextField textFieldBorrowingUserName) {
        try {
            String userCode = textFieldBorrowingUserCode.getText();

            // If the user code is blank.
            if (userCode.isBlank()) {
                throw new IllegalArgumentException("User code cannot be blank!");
            }

            String name = libraryModel.getUserName(userCode);

            // If the user is not found.
            if (name == null) {
                throw new IllegalArgumentException("User code not found in database!");
            }

            textFieldBorrowingUserCode.setDisable(true);

            imageViewBorrowingUserBack.setVisible(true);

            textFieldBorrowingUserName.setText(name);
        } catch (DatabaseException | IllegalArgumentException exception) {
            makeExceptionAlert(exception);
        }
    }

    // This method is used in the "Borrowing" and "Returning" menus to search for the title of a book ISBN.
    // The name is then displayed in the relevant field.
    private void searchBook(TextField textFieldBorrowingBookISBN, ImageView imageViewBorrowingBookBack, TextField textFieldBorrowingBookTitle) {
        try {
            String bookISBN = textFieldBorrowingBookISBN.getText();

            // If the book ISBN is blank.
            if (bookISBN.isBlank()) {
                throw new IllegalArgumentException("Book ISBN cannot be blank!");
            }

            // If the book ISBN is not blank, we attempt to search for the book in the database.
            String title = libraryModel.getBookTitle(bookISBN);

            // If the book is not found.
            if (title == null) {
                throw new IllegalArgumentException("Book ISBN not found in database!");
            }

            textFieldBorrowingBookISBN.setDisable(true);

            imageViewBorrowingBookBack.setVisible(true);

            textFieldBorrowingBookTitle.setText(title);
        } catch (DatabaseException | IllegalArgumentException exception) {
            makeExceptionAlert(exception);
        }
    }

    // This method triggers on hitting the bottom menu's "Cancel" button.
    @FXML
    protected void onOptionsCancel() {
        // We disable all relevant fields.
        setAllFieldsAvailability(false);

        // We clear all relevant fields.
        cleanAllMenuFields();

        // We enable all top menu options.
        invertMenuMainAvailability();

        // We switch out the bottom menu visible.
        gridPaneOptionsUsersBooksConfirmCancel.setVisible(false);
        gridPaneOptionsUsersBooksDefault.setVisible(true);
    }

    // This method inverts the availability of all top menu buttons.
    private void invertMenuMainAvailability() {
        imageViewOptionsMainUsers.setDisable(!imageViewOptionsMainUsers.isDisabled());
        imageViewOptionsMainBooks.setDisable(!imageViewOptionsMainBooks.isDisabled());
        imageViewOptionsMainBorrowing.setDisable(!imageViewOptionsMainBorrowing.isDisabled());
        imageViewOptionsMainReturning.setDisable(!imageViewOptionsMainReturning.isDisabled());
    }

    // This method sets the availability of the current top menu's fields.
    private void setAllFieldsAvailability(boolean toggle) {
        toggle = !toggle;

        switch (currentMenu) {
            case menuUsers: {
                textFieldUsersCode.setDisable(toggle);
                textFieldUsersFirstName.setDisable(toggle);
                textFieldUsersLastName.setDisable(toggle);
                datePickerUsersBirthdate.setDisable(toggle);

                break;
            }
            case menuBooks: {
                textFieldBooksISBN.setDisable(toggle);
                textFieldBooksTitle.setDisable(toggle);
                textFieldBooksPublisher.setDisable(toggle);
                textAreaBooksCover.setDisable(toggle);
                textAreaBooksOutline.setDisable(toggle);
                textFieldBooksCopies.setDisable(toggle);
                comboBoxBooksBorrowers.setDisable(toggle);

                break;
            }
        }
    }

    // This method clears all fields of the current top menu
    // and resets back arrows in the "Borrowing"/"Returning" menus.
    private void cleanAllMenuFields() {
        switch (currentMenu) {
            case menuUsers: {
                textFieldUsersCode.setText("");
                textFieldUsersFirstName.setText("");
                textFieldUsersLastName.setText("");
                datePickerUsersBirthdate.getEditor().setText("");
                datePickerUsersBirthdate.setValue(null);

                break;
            }
            case menuBooks: {
                textFieldBooksISBN.setText("");
                textFieldBooksTitle.setText("");
                textFieldBooksPublisher.setText("");
                textAreaBooksCover.setText("");
                textAreaBooksOutline.setText("");
                textFieldBooksCopies.setText("");
                comboBoxBooksBorrowers.setItems(null);

                break;
            }
            case menuBorrowing: {
                textFieldBorrowingUserCode.setText("");
                textFieldBorrowingUserName.setText("");
                textFieldBorrowingBookISBN.setText("");
                textFieldBorrowingBookTitle.setText("");

                imageViewBorrowingUserBack.setVisible(false);
                imageViewBorrowingBookBack.setVisible(false);

                break;
            }
            case menuReturning: {
                textFieldReturningUserCode.setText("");
                textFieldReturningUserName.setText("");
                textFieldReturningBookISBN.setText("");
                textFieldReturningBookTitle.setText("");

                imageViewReturningUserBack.setVisible(false);
                imageViewReturningBookBack.setVisible(false);

                break;
            }
        }
    }

    // This method is used to validate the input on a 'DatePicker' element.
    // - The date must be between 04-03-1907 and the present day.
    // - If input via the text field, the date must follow 'yyyy-MM-dd' format.
    // - If validation failed, the method returns ''null''.
    private LocalDate validateBirthdate() {
        // We gather the content of the written field.
        String textDate = datePickerUsersBirthdate.getEditor().getText();

        LocalDate localDate;

        // We attempt to parse the written field's date.
        // - If it's not a valid date, we return the value of the 'DatePicker' date.
        // -> If that one is blank, the method returns ''null'', so the ""date"" is invalid.
        // - If the date format is valid, but exceeds the minimum and maximum date, the method returns ''null'' (invalid date).
        // If all checks pass, the 'LocalDate' is returned.
        try {
            localDate = LocalDate.parse(textDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (localDate.isAfter(LocalDate.now()) || localDate.isBefore(LocalDate.of(1907, 3, 4))) {
                return null;
            } else {
                return localDate;
            }
        } catch (DateTimeParseException dateTimeParseException) {
            return datePickerUsersBirthdate.getValue();
        }
    }

    // This method receives a 'List<LendingEntity>' and counts the number of active lendings, up to
    // a given limit.
    // It then returns the number of active lendings.
    private int getActiveLendingCount(List<LendingEntity> lendingEntityList, int limit) {
        int i = 0;

        for (LendingEntity lendingEntity : lendingEntityList) {
            if (lendingEntity.getReturningDate() == null) {
                i++;
            }

            if (i >= limit) {
                break;
            }
        }

        return i;
    }

    // This method is used to configure and show resizable 'Alert' windows with a given 'AlertType', context message
    // and header text.
    // The 'Alert' windows have a simple 'OK' button that closes the window.
    // The result of the 'Alert' is returned.
    private ButtonType makeGenericAlert(Alert.AlertType alertType, String message, String headerText) {
        Alert alert = new Alert(alertType,
                message,
                ButtonType.OK);

        alert.setHeaderText(headerText);

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.setResizable(true);

        alert.showAndWait();

        ButtonType alertResult = alert.getResult();

        if (alertResult == ButtonType.OK) {
            alert.close();
        }

        return alertResult;
    }

    // This method is used to configure and show resizable 'Alert' windows with a given context message.
    // The 'Alert' windows have a simple 'OK' button that closes the window.
    // They are used when a user action is successful.
    private static void makeSuccessAlert(String contextMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, contextMessage, ButtonType.OK);
        alert.setHeaderText("Action successful!");

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.setResizable(true);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    // This method is used to configure and show resizable 'Alert' windows.
    // This method receives an 'Exception' whose message is used for the context message.
    // The header text is selected depending on the type of 'Exception'.
    // - 'IllegalArgumentException' -> used if the user input is not valid.
    // - 'DatabaseException' -> used if there is a database error of some type.
    private static void makeExceptionAlert(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.NONE);

        if (exception instanceof IllegalArgumentException) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setHeaderText("Warning!");
        } else if (exception instanceof DatabaseException) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Database error!");
        }

        alert.setContentText(exception.getMessage());

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.setResizable(true);

        alert.showAndWait();
    }
}