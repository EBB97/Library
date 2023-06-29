module com.ebb.library.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.sql;
    requires java.naming;
    requires android.json;


    opens com.ebb.library.library to javafx.fxml;
    exports com.ebb.library.library;
    exports com.ebb.library.library.pojos;
    opens com.ebb.library.library.pojos to javafx.fxml;
}