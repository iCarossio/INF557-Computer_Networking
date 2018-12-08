/**************************************
 * INF557 - 2018-2019 - Final Project *
 *            Flavien Solt            *
 *          Antoine Carossio          *
 **************************************/


import test.*;

import java.net.SocketException;

public class Main {

    public static void testExo3() {
        new TestExo3().runTest();
    }

    public static void testExo5() {
        try {
            new TestExo5().runTest();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void testExo7() {
        try {
            new TestExo7().runTest();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void testFinal() {
        try {
            new TestFinal().runTest();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void testDatabase() {
        try {
            new TestDatabase().runTest();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]) {
        testFinal();
    }
}
