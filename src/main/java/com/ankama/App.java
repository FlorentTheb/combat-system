package com.ankama;

/**
 * Hello world!
 *
 */
public class App {
    private String name;
    private String surname;
    private int age;

    public App(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age * 2;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
