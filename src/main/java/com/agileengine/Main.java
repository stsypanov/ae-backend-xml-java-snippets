package com.agileengine;

public class Main {
    public static void main(String[] args) {
        final String pathToOriginFile = args[0];
        final String pathToOtherFile = args[1];
        final String pathToButton = new Application().getPathToButton(pathToOriginFile,pathToOtherFile);
        System.out.println(pathToButton);
    }
}
