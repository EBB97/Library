package com.ebb.library.library;

import com.ebb.library.library.pojos.BooksEntity;
import com.ebb.library.library.pojos.LendingEntity;
import com.ebb.library.library.pojos.ReservationsEntity;
import com.ebb.library.library.pojos.UsersEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The type Library model spring.
 */
public class LibraryModelSpring extends LibraryModel {
    private final String urlHeader;

    private final String urlLending;
    private final String urlReservations;

    private final String urlBooks;

    private final String fieldId;
    private final String fieldLendingDate;
    private final String fieldReturningDate;
    private final String fieldBookISBN;
    private final String fieldBorrowerCode;

    private final String entityBook;
    private final String entityUser;

    private final String errorNull;
    private final String errorDataType;

    /**
     * Instantiates a new model.
     */
    LibraryModelSpring() {
        urlHeader = "http://localhost:8080/api-rest-ebb2223";

        urlLending = "/lending";
        urlReservations = "/reservations";
        urlBooks = "/books";

        fieldId = "id";
        fieldLendingDate = "lendingDate";
        fieldReturningDate = "returningDate";
        fieldBookISBN = "book";
        fieldBorrowerCode = "borrower";

        entityBook = "book entity";
        entityUser = "user entity";

        errorNull = " \nCannot be null.";
        errorDataType = "\nNot a valid data type.";
    }

    // SECTION - BOOKS

    @Override
    protected void deleteBook(String bookISBN) throws DatabaseException {
        try {
            URL url = new URL(urlHeader + urlBooks + "/" + bookISBN);

            sendData(url, "DELETE", bookISBN);
        } catch (Exception exception) {
            throw new SpringException(
                    "Book deletion error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    // SECTION - LENDING

    @Override
    protected void createLending(LendingEntity lending) throws SpringException {
        try {
            String jsonInputString = new JSONObject()
                    .put("id", "")
                    .put("lendingDate", lending.getLendingDate())
                    .put("returningDate", lending.getReturningDate())
                    .put("book", lending.getBookISBN())
                    .put("borrower", lending.getBorrowerCode())
                    .toString();

            URL url = new URL(urlHeader + urlLending);

            sendData(url, "POST", jsonInputString);
        } catch (Exception exception) {
            throw new SpringException("Lending creation error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected LendingEntity getLending(int id) throws SpringException {
        LendingEntity lending;

        try {
            URL url = new URL(urlHeader + urlLending + "/" + id);
            List<String> fieldList = Arrays.asList(fieldId, fieldLendingDate, fieldReturningDate, fieldBookISBN, fieldBorrowerCode);

            List<Object> fieldContent = getJsonAsObjectList(url, fieldList);

            lending = new LendingEntity();

            try {
                if (fieldContent.get(0) == null) {
                    throw new SpringException(fieldId + errorNull);
                }
                lending.setId((Integer) fieldContent.get(0));

                if (fieldContent.get(1) == null) {
                    throw new SpringException(fieldLendingDate + errorNull);
                }
                lending.setLendingDate(Date.valueOf(LocalDate.parse((CharSequence) fieldContent.get(1))));

                if (fieldContent.get(2) != null) {
                    lending.setReturningDate(Date.valueOf(LocalDate.parse((CharSequence) fieldContent.get(2))));
                }

                String bookISBN = (String) fieldContent.get(3);
                BooksEntity book = getBook(bookISBN);
                if (book == null) {
                    throw new SpringException(entityBook + errorNull);
                }
                lending.setBook(book);
                lending.setBookISBN(bookISBN);

                String userCode = (String) fieldContent.get(4);
                UsersEntity user = getUser(userCode);
                if (user == null) {
                    throw new SpringException(entityUser + errorNull);
                }
                lending.setBorrowerCode(userCode);
                lending.setBorrower(user);
            } catch (Exception exception) {
                throw new SpringException(
                        "Lending entity property setting error!\n" + exception.getMessage(),
                        exception.getCause());
            }
        } catch (Exception exception) {
            throw new SpringException(
                    "Lending search by id error!\n" + exception.getMessage(),
                    exception.getCause());
        }

        return lending;
    }

    @Override
    protected int getLatestActiveLendingId(String userCode, String bookISBN) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlLending + "/latest-active-" + bookISBN + "-" + userCode);

            return getIntResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Latest active lending id search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected LocalDate getLendingLendingDate(int id) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlLending + "/" + id + "/lending-date");

            return getDateResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Lending lending date search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected void updateLendingReturningDate(int id, LocalDate returningDate) throws SpringException {
        try {
            LendingEntity lending = getLending(id);

            if (lending == null) {
                throw new SpringException(errorNull);
            }

            String jsonInputString = new JSONObject()
                    .put("id", id)
                    .put("lendingDate", lending.getLendingDate())
                    .put("returningDate", returningDate)
                    .put("book", lending.getBookISBN())
                    .put("borrower", lending.getBorrowerCode())
                    .toString();

            URL url = new URL(urlHeader + urlLending + "/" + id);

            sendData(url, "PUT", jsonInputString);
        } catch (Exception exception) {
            throw new SpringException("Lending update error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected boolean isBookLent(String bookISBN) throws DatabaseException {
        try {
            URL url = new URL(urlHeader + urlLending + "/is-" + bookISBN + "-lent");

            return getBooleanResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Is book lent search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected boolean isUserLendingBook(String userCode, String bookISBN) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlLending + "/active-exists-" + bookISBN + "-" + userCode);

            return getBooleanResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Is user lending book search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    // SECTION - RESERVATIONS

    @Override
    protected void createReservation(ReservationsEntity reservation) throws SpringException {
        try {
            String jsonInputString = new JSONObject()
                    .put("id", "")
                    .put("lendingDate", reservation.getLendingDate())
                    .put("book", reservation.getBookISBN())
                    .put("borrower", reservation.getReserveeCode())
                    .toString();

            URL url = new URL(urlHeader + urlReservations);

            sendData(url, "POST", jsonInputString);
        } catch (Exception exception) {
            throw new SpringException("Reservation creation error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected ReservationsEntity getReservation(int id) throws SpringException {
        ReservationsEntity reservation;

        try {
            URL url = new URL(urlHeader + urlLending + "/" + id);
            List<String> fieldList = Arrays.asList(fieldId, fieldLendingDate, fieldBookISBN, fieldBorrowerCode);

            List<Object> fieldContent = getJsonAsObjectList(url, fieldList);

            reservation = new ReservationsEntity();

            try {
                if (fieldContent.get(0) == null) {
                    throw new SpringException(fieldId + errorNull);
                }
                reservation.setId((Integer) fieldContent.get(0));

                if (fieldContent.get(1) != null) {
                    reservation.setLendingDate(Date.valueOf(LocalDate.parse((CharSequence) fieldContent.get(1))));
                }

                String bookISBN = (String) fieldContent.get(2);
                BooksEntity book = getBook(bookISBN);
                if (book == null) {
                    throw new SpringException(entityBook + errorNull);
                }
                reservation.setBook(book);
                reservation.setBookISBN(bookISBN);

                String userCode = (String) fieldContent.get(3);
                UsersEntity user = getUser(userCode);
                if (user == null) {
                    throw new SpringException(entityUser + errorNull);
                }
                reservation.setReserveeCode(userCode);
                reservation.setReservee(user);
            } catch (Exception exception) {
                throw new SpringException(
                        "Reservation entity property setting error!\n" + exception.getMessage(),
                        exception.getCause());
            }
        } catch (Exception exception) {
            throw new SpringException(
                    "Reservation search by id error!\n" + exception.getMessage(),
                    exception.getCause());
        }

        return reservation;
    }

    @Override
    protected int getLatestActiveReservationId(String userCode, String bookISBN) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlReservations + "/latest-active-" + bookISBN + "-" + userCode);

            return getIntResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Latest active reservation id search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected int getNextActiveReservationId(String bookISBN) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlReservations + "/" + bookISBN + "/active/id");

            return getIntResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Next reservation id search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected String getNextActiveReservationUserName(String bookISBN) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlReservations + "/" + bookISBN + "/active/borrower");

            return getStringResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Next reservation user name search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected void updateReservationLendingDate(int id, LocalDate lendingDate) throws SpringException {
        try {
            ReservationsEntity reservation = getReservation(id);

            if (reservation == null) {
                throw new SpringException(errorNull);
            }

            String jsonInputString = new JSONObject()
                    .put("id", id)
                    .put("lendingDate", lendingDate)
                    .put("book", reservation.getBookISBN())
                    .put("borrower", reservation.getReserveeCode())
                    .toString();

            URL url = new URL(urlHeader + urlReservations + "/" + id);

            sendData(url, "PUT", jsonInputString);
        } catch (Exception exception) {
            throw new SpringException("Reservation update error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected boolean isBookReserved(String bookISBN) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlReservations + "/is-" + bookISBN + "-reserved");

            return getBooleanResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Is book reserved search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    @Override
    protected boolean isUserReservingBook(String userCode, String bookISBN) throws SpringException {
        try {
            URL url = new URL(urlHeader + urlReservations + "/active-exists-" + bookISBN + "-" + userCode);

            return getBooleanResponse(url);
        } catch (Exception exception) {
            throw new SpringException(
                    "Is user reserving book search error!\n" + exception.getMessage(),
                    exception.getCause());
        }
    }

    /**
     * Query a given 'URL' with the expectation of receiving an 'int' as a response.
     *
     * @param url the url to connect to
     * @return the 'int' response
     * @throws SpringException the spring exception
     */
    protected int getIntResponse(URL url) throws SpringException {
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(httpURLConnection.getInputStream())) {
                    String response = scanner.next();

                    if (response == null) {
                        throw new SpringException(errorNull);
                    } else {
                        try {
                            return Integer.parseInt(response);
                        } catch (NumberFormatException numberFormatException) {
                            throw new SpringException(errorDataType);
                        }
                    }
                }
            } else {
                throw new SpringException(getJsonErrorStreamData(httpURLConnection));
            }
        } catch (Exception exception) {
            throw new SpringException(exception.getMessage(), exception.getCause());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    /**
     * Query a given 'URL' with the expectation of receiving a 'String' as a response.
     *
     * @param url the url to connect to
     * @return the 'String' response
     * @throws SpringException the spring exception
     */
    protected String getStringResponse(URL url) throws SpringException {
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(httpURLConnection.getInputStream())) {
                    String response = scanner.next();

                    if (response == null) {
                        throw new SpringException(errorNull);
                    } else {
                        return response;
                    }
                }
            } else {
                throw new SpringException(getJsonErrorStreamData(httpURLConnection));
            }
        } catch (Exception exception) {
            throw new SpringException(exception.getMessage(), exception.getCause());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    /**
     * Query a given 'URL' with the expectation of receiving a 'Boolean' as a response.
     *
     * @param url the url to connect to
     * @return the 'Boolean' response
     * @throws SpringException the spring exception
     */
    protected boolean getBooleanResponse(URL url) throws SpringException {
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(httpURLConnection.getInputStream())) {
                    String response = scanner.next();

                    if (response == null) {
                        throw new SpringException(errorNull);
                    } else {
                        return Boolean.parseBoolean(response);
                    }
                }
            } else {
                throw new SpringException(getJsonErrorStreamData(httpURLConnection));
            }
        } catch (Exception exception) {
            throw new SpringException(exception.getMessage(), exception.getCause());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    /**
     * Query a given 'URL' with the expectation of receiving a 'Date' as a response,
     * which is then converted to 'LocalDate'.
     *
     * @param url the url to connect to
     * @return the 'LocalDate' response
     * @throws SpringException the spring exception
     */
    protected LocalDate getDateResponse(URL url) throws SpringException {
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(httpURLConnection.getInputStream())) {
                    String response = scanner.next();

                    if (response == null) {
                        throw new JSONException(errorNull);
                    } else {
                        try {
                            return LocalDate.parse(response.replace("\"", ""));
                        } catch (DateTimeException dateTimeException) {
                            throw new JSONException(errorDataType);
                        }
                    }
                }
            } else {
                throw new SpringException(getJsonErrorStreamData(httpURLConnection));
            }
        } catch (Exception exception) {
            throw new SpringException(exception.getMessage(), exception.getCause());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    /**
     * Query a given 'URL' with the expectation of receiving a 'JSONObject' as a response.
     * The given "fieldList" determines which JSON keys that must be present, to extract their data.
     *
     * @param url       the 'URL' to connect to
     * @param fieldList the list of expected fields
     * @return the 'List' with the extracted data
     * @throws SpringException the spring exception
     */
    protected List<Object> getJsonAsObjectList(URL url, List<String> fieldList) throws SpringException {
        List<Object> fieldContent = new ArrayList<>();

        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(httpURLConnection.getInputStream())) {
                    String response = scanner.next();

                    JSONObject jsonObject = new JSONObject(response);

                    for (String field : fieldList) {
                        if (!jsonObject.has(field)) {
                            throw new JSONException(field + " is missing from JSON.");
                        }

                        Object fieldObject = jsonObject.get(field);

                        if (fieldObject instanceof Integer) {
                            fieldContent.add(Integer.parseInt(fieldObject.toString()));
                        } else if (fieldObject instanceof String) {
                            fieldContent.add(fieldObject.toString());
                        } else {
                            fieldContent.add(null);
                        }
                    }
                }
            } else {
                throw new SpringException(getJsonErrorStreamData(httpURLConnection));
            }
        } catch (Exception exception) {
            throw new SpringException(exception.getMessage(), exception.getCause());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return fieldContent;
    }

    /**
     * Query a given 'URL' with a given request method, with the intent to write the given "jsonInputString"
     * to it.
     *
     * @param url             the 'URL' to connect to
     * @param requestMethod   the HTTP request method
     * @param jsonInputString the data to send
     * @throws SpringException the spring exception
     */
    protected void sendData(URL url, String requestMethod, String jsonInputString) throws SpringException {
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setRequestProperty("Content-Type", "application/json;utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);

            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            if (httpURLConnection.getResponseCode() != 200) {
                throw new SpringException(getJsonErrorStreamData(httpURLConnection));
            }
        } catch (Exception exception) {
            throw new SpringException(exception.getMessage(), exception.getCause());
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
    }

    // This method parses the error stream of a 'HttpUrlConnection' for status, error and message data.
    private static String getJsonErrorStreamData(HttpURLConnection httpURLConnection) throws JSONException {
        Scanner scanner = new Scanner(httpURLConnection.getErrorStream()).useDelimiter("\\A");
        JSONObject jsonErrorData = new JSONObject(scanner.hasNext() ? scanner.next() : "");

        int status = jsonErrorData.getInt("status");
        String error = jsonErrorData.getString("error");
        String message = jsonErrorData.getString("message");

        return String.format("(%d - %s) %s", status, error, message);
    }

    /**
     * Exception for generic errors related to the Spring API.
     */
    protected static class SpringException extends DatabaseException {

        /**
         * Instantiates a new Spring exception with a custom message and cause.
         *
         * @param message the message
         * @param cause   the cause
         */
        public SpringException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Instantiates a new Spring exception with a custom message.
         *
         * @param message the message
         */
        public SpringException(String message) {
            super(message);
        }
    }
}
