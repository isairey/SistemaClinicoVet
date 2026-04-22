module com.veterinaria.veterinaria {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    
    // Módulos para base de datos
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.postgresql.jdbc;
    requires org.slf4j;
    
    // BCrypt para encriptación
    requires bcrypt;

    // Módulo para manejo de imágenes (BufferedImage, ImageIO)
    requires java.desktop;

    // Exportar y abrir paquetes para JavaFX
    exports co.edu.upb.veterinaria.app;
    exports co.edu.upb.veterinaria.config;
    exports co.edu.upb.veterinaria.controllers.login;
    exports co.edu.upb.veterinaria.controllers.ControllerEmailRecoverPassword to javafx.fxml;

    opens co.edu.upb.veterinaria.app to javafx.fxml;
    opens co.edu.upb.veterinaria.config to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.login to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerRecoverPasswordPrueba to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerMainMenu to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerInventary to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerRegisterProduct to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerGenerateReport to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerPersonalData to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerAddClient to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerAddPets to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerAddSurgicalProcedure to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerClientVisualizeRegister to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerCreateUser to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerSailsVisualizeRegister to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllersAddSuppliers to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerSectionSales to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerSeeNotifications to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerSeeSurgicalProcedure to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllersSectionSuppliers to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerUserSection to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerVisualizeRegister to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerEmailRecoverPassword to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerverifyCodeDialog to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerInventaryMedicament to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerInventaryAliment to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerInventaryMaterialQ to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerInventaryJugueteAccs to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerAddServices to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerSectionServices to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerAddMarca to javafx.fxml;
    opens co.edu.upb.veterinaria.controllers.ControllerSectionBrands to javafx.fxml;
}