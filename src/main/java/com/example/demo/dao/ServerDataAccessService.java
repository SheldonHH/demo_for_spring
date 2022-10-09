package com.example.demo.dao;

import com.example.demo.model.*;
import com.example.demo.model.signature.GenerateDigitalSignature;
import com.example.demo.p4p.server.P4PServer;
import com.example.demo.p4p.sim.P4PSim;
import com.example.demo.p4p.user.UserVector2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("postgres0")
public class ServerDataAccessService implements ServerDao{
    private static List<Person> DB = new ArrayList<>();
    private final String url = "jdbc:postgresql://localhost:5432/server1";
    private final String user = "postgres";
    private final String password = "password";
    final static CloseableHttpClient httpClient = HttpClients.createDefault();
    public static Map<String, String> userPortMap = Stream.of(new String[][] {
            { "f000aa01-0451-4000-b000-000000000000", "6001" },
            { "0c1e1494-aa4a-4afa-b494-d49754b0e244", "6002" },
            { "5ce6d22a-67be-4b64-9fee-e3302c972f6f", "6003" },
            { "7371c17b-f1c4-45f7-84e5-0909d3470a26", "6004" },
            { "b06f2b0a-db55-42f6-a01d-7b4307229896", "6005" },
            { "61dd18f9-0d0a-4dd7-a04e-c8a36c3fc461", "6006" }
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    public static Map<String, String> userNameMap = Stream.of(new String[][] {
            { "f000aa01-0451-4000-b000-000000000000", "client1" },
            { "0c1e1494-aa4a-4afa-b494-d49754b0e244", "client2" },
            { "5ce6d22a-67be-4b64-9fee-e3302c972f6f", "client3" },
            { "7371c17b-f1c4-45f7-84e5-0909d3470a26", "client4" },
            { "b06f2b0a-db55-42f6-a01d-7b4307229896", "client5" },
            { "61dd18f9-0d0a-4dd7-a04e-c8a36c3fc461", "client6" }
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));


    public static Map<String, String> peerNameMap = Stream.of(new String[][] {
            { "a732b18a-17b4-4cfc-bf22-66a64bd2583f", "peer1" },
            { "5ce6d22a-67be-4b64-9fee-e3302c972f6f", "peer2" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    public static Map<String, String> peerPortMap = Stream.of(new String[][] {
            { "a732b18a-17b4-4cfc-bf22-66a64bd2583f", "9001" },
            { "5ce6d22a-67be-4b64-9fee-e3302c972f6f", "9002" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));



    public static UserVector2 uv = new UserVector2(P4PSim.m, P4PSim.F, P4PSim.l, P4PSim.g, P4PSim.h);
    public static P4PServer server = new P4PServer(P4PSim.m, P4PSim.F, P4PSim.l, P4PSim.zkpIterations, P4PSim.g, P4PSim.h);
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

        @Override
    public int insertUiandProof(UUID data_id, UiandProof uiandProof) {
        // userPortMap.forEach((key, value) -> System.out.println(key + " : " + value));
        String SQL = "INSERT INTO U_PERSON_DATA(data_id,client_id,ui,verified,created_at,batch_time) "
                    + "VALUES(?,?,?,?,NOW(),?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

//            System.out.println("uiandProof.getUi()"+Arrays.toString(uiandProof.getUi()));
            pstmt.setObject(1, data_id);
            pstmt.setObject(2, uiandProof.getUserid());
            HashMap<String, String> userNameHashMap =
                    (userNameMap instanceof HashMap)
                            ? (HashMap) userNameMap
                            : new HashMap<String, String>(userNameMap);
            System.out.println(" "+userNameHashMap.get(uiandProof.getUserid().toString())+" is inserting");

            long[] ui_arr =  uiandProof.getUi();
            Long[] aLong = new Long[ui_arr.length];
            Arrays.setAll(aLong, i -> aLong[i]);
            // convert to string array first, then insert as TEXT array
            String[] strArray = Arrays.stream(ui_arr)
                    .mapToObj(String::valueOf)
                    .toArray(String[]::new);

            Array ui_array = conn.createArrayOf("TEXT", strArray);
            pstmt.setArray(3,  ui_array);
//            pstmt.setArray(4, ui_array);
//                        if (!peerPassed)
//                            server.disqualifyUser(i);
//                        else
            long [] U_longs = Arrays.stream(strArray).mapToLong(Long::parseLong).toArray();
            uv.setU(U_longs);
            //TODO: user_id int
            uv.setY(uiandProof.getY());
            System.out.println("isForServer: "+uiandProof.getServerProof().isForServer());
//            ThreeWayCommitment.ThreeWayCommitmentProof[] tcProofs =
//                    uiandProof.getTcProof();
//            System.out.println("tcProof:"+tcProofs);
            boolean serverPassed = false;
//                    serverPassed = uv.verify2(uiandProof.getServerProof());
//            if(!) {
//                System.out.println("User " + 1
//                        + "'s vector failed the verification.");
//                disqualifyUser(user.ID);
//                // TODO: Must let the peer know about disqualified users so he can computes his share
//                // of the sum (the peerSum).
//                disqualified++;
//                continue;
//            }
            pstmt.setBoolean(4, serverPassed);
            pstmt.setString(5, uiandProof.getBatch_timestamp());
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    System.out.println(rs);
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public byte[] addBoundforGauss(BoundforGauss boundForGauss) {
        System.out.println(boundForGauss.getMaxX1());
        System.out.println(boundForGauss.getMaxX2());
        System.out.println(boundForGauss.getMinX1());
        System.out.println(boundForGauss.getMinX2());
        System.out.println(boundForGauss.getGaussian_params());
        System.out.println(boundForGauss.getAvailGaussUnits());
        ArrayList<String> availGaussUnits =  boundForGauss.getAvailGaussUnits();
        String batch_time = boundForGauss.getBatch_time();

        int index = (int)(Math.random() * availGaussUnits.size());
        System.out.println("Chosed Element is :" + availGaussUnits.get(index));

//        CrunchifyGetPropertyValues properties = new CrunchifyGetPropertyValues();
//        properties.getPropValues();
        System.out.println("boundForGauss.getUser_id()");
        System.out.println(boundForGauss.getUser_id());
        System.out.println("userPortMap.get(boundForGauss.getUser_id())");
        HashMap<String, String> portHashMap =
                (userPortMap instanceof HashMap)
                        ? (HashMap) userPortMap
                        : new HashMap<String, String>(userPortMap);
        System.out.println("portHashMap:"+portHashMap.get(boundForGauss.getUser_id().toString()));
        HttpPost request = new HttpPost("http://localhost:"+portHashMap.get(boundForGauss.getUser_id().toString())+"/api/v1/person/sumCountforUnit");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        try {
            StringEntity json = new StringEntity(mapper.writeValueAsString(new RequestUnitRange(""+availGaussUnits.get(index),batch_time)), ContentType.APPLICATION_JSON);
            request.setEntity(json);
            CloseableHttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 200){
                System.out.println("response:"+response);
                System.out.println("Student is not added! "+response.getStatusLine().getStatusCode() );
            }
            response.close();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        synchronized(syncObject) {
//            try {
//                // Calling wait() will block this thread until another thread
//                // calls notify() on the object.
//                syncObject.wait();
//            } catch (InterruptedException e) {
//                // Happens if someone interrupts your thread.
//            }
//        }

        return new byte[2];
    }

    @Override
    public int insertVSum(VSum vSum) {
        UUID person_ID = vSum.getPersonID();
        System.out.println("vSum.getPeerID()"+vSum.getPeerID());
        System.out.println("vSum.getPerson()"+vSum.getPersonID());
        System.out.println("vSum.getV_sum()"+vSum.getV_sum());
        ArrayList<Long> sumU = sumUi(vSum.getPersonID(), vSum.getBatch_time());

        System.out.print("digital signature is: ");

        if(vSum.getV_sum().size() == 0){
            System.out.println("vSum is empty");
        }else{
            System.out.println("call function sumUandV: "+sumUandV(person_ID, sumU, vSum.getV_sum(), vSum.getBatch_time()));
        }
        return 0;
    }


    @Override
    public int distanceofReceiveRquestSumCount(ResponseSumCount responseSumCount) {
        System.out.println("distanceofReceiveRquestSumCount");
        System.out.println("getCount: "+responseSumCount.getCount());
        System.out.println("getDiList"+responseSumCount.getDiList().toString());
        return 0;
    }

    ArrayList<ArrayList<Long>> TwoDResultList = new ArrayList<ArrayList<Long>>();
    @Override
    public ArrayList<Long> sumUi(UUID person_id, String batch_time) {
        String SQL = "SELECT ui FROM U_PERSON_DATA where client_id=? and batch_time=?";
        ArrayList<Long> sum = new ArrayList<>();
        try {
            Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            System.out.println("person_id:"+person_id);
            //TODO: change to UUID tyope
            preparedStatement.setString(1,person_id.toString());
            preparedStatement.setString(2,batch_time);
            ResultSet rs = preparedStatement.executeQuery();

            ArrayList<ArrayList<Long>> TwoDResultList = new ArrayList<ArrayList<Long>>();
            ArrayList<String> stringList = new ArrayList<String>();
            while (rs.next()) {
                stringList = new ArrayList<>(Arrays.asList((String[]) rs.getArray("ui").getArray()));
                System.out.println(Arrays.asList((String[]) rs.getArray("ui").getArray()));
                ArrayList<Long> resultIntList = new ArrayList<Long>();
                for (String stringValue : stringList) {
                    try {
                        //Convert String to long, and store it into long array list.
                        resultIntList.add(Long.parseLong(stringValue));
                    } catch (NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
//                        Log.w("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
                    }
                }
                TwoDResultList.add(resultIntList);
            }
            sum = new ArrayList<Long>(TwoDResultList.get(0).size());
            for (int i = 0; i < TwoDResultList.get(0).size(); i++) {
                sum.add(i, 0L);
            }
            for (ArrayList<Long> ui_array : TwoDResultList) {
                for (int i = 0; i < ui_array.size(); i++) {
                    sum.set(i, sum.get(i) + ui_array.get(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sum;
    }

    @Override
    public int cancelDS(PersonCount personCount){
        String signature = checkSigWithoutTimer(personCount);
        if(signature.equals("")){
            String SQL = "INSERT INTO PERSON_SIGNATURE(person_id,client_name,signature,created_at,batch_time) "
                    + "VALUES(?,?,?,NOW(),?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(SQL,
                         Statement.RETURN_GENERATED_KEYS)) {
                byte[] digitSignature = GenerateDigitalSignature.generateDS(personCount.getPerson_ID());
                pstmt.setObject(1, personCount.getPerson_ID());
                HashMap<String, String> userNameHashMap =
                        (userNameMap instanceof HashMap)
                                ? (HashMap) userNameMap
                                : new HashMap<String, String>(userNameMap);
                pstmt.setString(2, userNameHashMap.get(personCount.getPerson_ID().toString()));
                pstmt.setString(3, digitSignature.toString());
                pstmt.setString(4, personCount.getBatch_time());
                int affectedRows = pstmt.executeUpdate();
                // check the affected rows
                if (affectedRows > 0) {
                    // get the ID back
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        System.out.println(rs);
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }else{
            String SQL = "UPDATE PERSON_SIGNATURE "
                    + "SET signature = ? "
                    + "WHERE person_id = ? and batch_time = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(SQL,
                         Statement.RETURN_GENERATED_KEYS)) {
                byte[] digitSignature = GenerateDigitalSignature.generateDS(personCount.getPerson_ID());
                java.util.Date date = new Date();
                String batch_time =  new Timestamp(date.getTime()).toString();
                pstmt.setString(1, "Canceled DS in :"+batch_time);
                pstmt.setObject(2, personCount.getPerson_ID());
                pstmt.setString(3, personCount.getBatch_time());
                int affectedRows = pstmt.executeUpdate();
                // check the affected rows
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        System.out.println(rs);
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        return 0;
    }

    public String checkSigWithoutTimer(PersonCount personCount){
        String signature = "";
        String selectionSQL = "SELECT * from PERSON_SIGNATURE where person_id = ? and batch_time = ?";
        try {
            Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(selectionSQL);
            preparedStatement.setObject(1, personCount.getPerson_ID());
            preparedStatement.setString(2, personCount.getBatch_time());
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                UUID person_id = (UUID) rs.getObject("person_id");
                signature = rs.getString("signature");
            }
            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return signature;
    }

    @Override
    public String checkSig(PersonCount personCount){
        String signature = "";
        String selectionSQL = "SELECT * from PERSON_SIGNATURE where person_id = ? and batch_time = ?";
            try {
                Connection conn = connect();
                PreparedStatement preparedStatement = conn.prepareStatement(selectionSQL);
                preparedStatement.setObject(1, personCount.getPerson_ID());
                preparedStatement.setString(2, personCount.getBatch_time());
                ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                UUID person_id = (UUID) rs.getObject("person_id");
                signature = rs.getString("signature");
            }
            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        for (int i = 10; i > 0 && !signature.equals(""); i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return signature;
    }

    public byte[] insertToPersonSignature(UUID person_id, String batch_time){
        String SQL = "INSERT INTO PERSON_SIGNATURE(person_id, client_name, signature, created_at, batch_time) "
                + "VALUES(?,?,?, NOW(), ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            byte[] digitSignature = GenerateDigitalSignature.generateDS(person_id);
            pstmt.setObject(1, person_id);
            HashMap<String, String> userNameHashMap =
                    (userNameMap instanceof HashMap)
                            ? (HashMap) userNameMap
                            : new HashMap<String, String>(userNameMap);
            // convert to string array first, then insert as TEXT array
            pstmt.setString(2, userNameHashMap.get(person_id.toString()));
            pstmt.setString(3, digitSignature.toString());
            pstmt.setString(4, batch_time);
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    System.out.println(rs);
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return GenerateDigitalSignature.generateDS(person_id);
    }
    @Override
    public byte[] sumUandV(UUID person_id, ArrayList<Long> uSum, ArrayList<Long> vSum, String batch_time) {
        ArrayList<Long> dSum = new ArrayList<>();
        for(int i = 0; i<uSum.size(); i++) {
            dSum.add(uSum.get(i)+vSum.get(i));
        }
        System.out.println("dSum:"+dSum);
        String signature = "";
        byte[] signature_byte_array = new byte[]{};
        String selectionSQL = "SELECT * from PERSON_SIGNATURE where person_id = ? and batch_time = ?";
        try {
            Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(selectionSQL);
            preparedStatement.setObject(1, person_id);
            preparedStatement.setString(2, batch_time);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
//                UUID person_id = (UUID) rs.getObject("person_id");
                signature = rs.getString("signature");
            }
            if (rs.next()) {
                System.out.println(rs.getString(1));
            }
            if(!signature.equals("None") && signature.equals("")){
                System.out.println("signature:::::"+signature);
                signature_byte_array = insertToPersonSignature(person_id, batch_time);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return signature_byte_array;
    }
}
