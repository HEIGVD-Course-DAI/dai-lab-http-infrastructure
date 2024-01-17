package org.example;

public class Animal {
    public String specie = "";

    public String name = "";
    public int mass;
    public Animal() { }
    public Animal(String specie, String name, int mass) {
        this.specie = specie;
        this.name = name;
        this.mass = mass;
    }
}
