package com.kelidon;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Repo {
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "pass";

    Connection connection;

    public Repo() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public ResultSet findHorseRiders(String name) throws Exception {
        PreparedStatement st = connection.prepareStatement("select id from horses where name=?");
        st.setString(1, name);
        ResultSet rs = st.executeQuery();
        int horseId = -1;
        while (rs.next()) {
            horseId = rs.getInt(1);
        }
        if(horseId == -1) throw new Exception("Nothing found");
        st = connection.prepareStatement("select rider_id from horse_rider where horse_id=? limit 1");
        st.setInt(1, horseId);
        rs = st.executeQuery();
        int riderId = -1;
        while (rs.next()) {
            riderId = rs.getInt(1);
        }
        if(riderId == -1) throw new Exception("No riders found");
        st = connection.prepareStatement("select * from riders where id=?");
        st.setInt(1, riderId);
        return st.executeQuery();
    }

    public ResultSet findOwnersWithAmount(int horsesNumber) throws Exception {
        PreparedStatement st = connection
                .prepareStatement("select owner_id from horse_owner where owner_id in (select owner_id from horse_owner group by owner_id having count(*) >= ?)");
        st.setInt(1, horsesNumber);
        ResultSet rs = st.executeQuery();
        Set<Integer> owners = new HashSet<>();
        while (rs.next()) {
            owners.add(rs.getInt(1));
        }
        if(owners.isEmpty()) throw new Exception("No results");

        StringBuilder builder = new StringBuilder();

        for (Integer owner : owners) {
            builder.append(owner);
            builder.append(",");
        }

        String placeHolders =  builder.deleteCharAt( builder.length() -1 ).toString();
        String statement = "select * from owners where id in ("+placeHolders+")";
        st = connection
                .prepareStatement(statement);
        return st.executeQuery();
    }

    public ResultSet findWinnersByYear(int year) throws Exception {
        PreparedStatement st = connection
                .prepareStatement("select id from competitions where (select extract (year from date))=?");
        st.setInt(1, year);
        ResultSet rs = st.executeQuery();
        Set<Integer> competitionsIds = new HashSet<>();
        while (rs.next()) {
            competitionsIds.add(rs.getInt(1));
        }
        if(competitionsIds.isEmpty()) throw new Exception("No competitions that year");

        StringBuilder builder = new StringBuilder();

        for (Integer owner : competitionsIds) {
            builder.append(owner);
            builder.append(",");
        }

        String placeHolders =  builder.deleteCharAt( builder.length() -1 ).toString();
        String statement = "select horse_id from results where competition_id in ("+placeHolders+") and horse_result=1";
        st = connection
                .prepareStatement(statement);
        rs = st.executeQuery();


        Set<Integer> horsesIds = new HashSet<>();
        while (rs.next()) {
            horsesIds.add(rs.getInt(1));
        }
        if(horsesIds.isEmpty()) throw new Exception("No results for that year competitions");

        builder = new StringBuilder();

        for (Integer horseId : horsesIds) {
            builder.append(horseId);
            builder.append(",");
        }

        placeHolders =  builder.deleteCharAt( builder.length() -1 ).toString();
        statement = "select * from horses where id in ("+placeHolders+")";
        st = connection
                .prepareStatement(statement);
        return st.executeQuery();
    }
}
