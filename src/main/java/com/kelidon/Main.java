package com.kelidon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            Repo repo = new Repo();
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            while(!command.equals("end")){
                String[] params = command.split(" ");
                if(params.length!=2){
                    System.out.println("wrong input");
                } else {
                    try{
                    switch (params[0]){
                        case "riders":{
                            String horseName = params[1];
                            showResult(repo.findHorseRiders(horseName));
                            break;
                        }
                        case "owners":{
                            int horsesNumber = Integer.parseInt(params[1]);
                            showResult(repo.findOwnersWithAmount(horsesNumber));
                            break;
                        }
                        case "winners":{
                            int year = Integer.parseInt(params[1]);
                            showResult(repo.findWinnersByYear(year));
                            break;
                        }
                        default:
                            System.out.println("Wrong command");
                    }} catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                command = scanner.nextLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static void showResult(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        System.out.println("\n_______________________");
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if(!rsmd.getColumnName(i).equals("id")) {
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue + " (" + rsmd.getColumnName(i) + ")");
                    if (i < columnsNumber) System.out.print(",  ");
                }
            }
            System.out.println();
        }
        System.out.println("_______________________");
    }
}
