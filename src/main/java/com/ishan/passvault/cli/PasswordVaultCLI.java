package com.ishan.passvault.cli;

import com.ishan.passvault.service.AuthenticationService;
import com.ishan.passvault.service.UserService;
import com.ishan.passvault.model.User;
import com.ishan.passvault.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class PasswordVaultCLI implements CommandLineRunner {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private MenuService menuService;

    private Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    public void run(String... args) throws Exception {
        System.out.println("=================================");
        System.out.println(" ISHAN's SECURE PASSWORD VAULT CLI");
        System.out.println("=================================");

        while (true) {
            if(currentUser == null) {
                showLoginMenu();
            }
            else {
                showMainMenu();
            }
        }

    }

    private void showLoginMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();
        switch(choice) {
            case "1" -> handleLogin();
            case "2" -> handleRegistration();
            case "3" -> {
                System.out.println("See You!");
                System.exit(0);
            }
            default -> System.out.println("Wrong choice bruh, try again!");

        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Master Password: ");
        String password = readPassword();

        try {
            currentUser = authService.authenticate(username, password);
            System.out.println("\n✓ Login successful! Welcome " + currentUser.getUsername());
        } catch (Exception e) {
            System.out.println("\n✗ Login failed: " + e.getMessage());
        }
    }

    private void handleRegistration() {
        System.out.print("Choose username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Choose master password: ");
        String password = readPassword();

        System.out.print("Confirm master password: ");
        String confirmPassword = readPassword();

        if (!password.equals(confirmPassword)) {
            System.out.println("\n✗ Passwords don't match!");
            return;
        }

        try {
            userService.registerUser(username, password);
            System.out.println("\n✓ Registration successful! You can now login.");
        } catch (Exception e) {
            System.out.println("\n✗ Registration failed: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        menuService.showMainMenu(currentUser, scanner, () -> currentUser = null);
    }

    private String readPassword() {
        // In a real CLI, you'd hide password input
        // For demo purposes, using regular input
        return scanner.nextLine().trim();
    }
}
