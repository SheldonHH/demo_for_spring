package com.example.demo.dao;

import com.example.demo.model.*;
import com.example.demo.model.signature.GenerateDigitalSignature;
import com.example.demo.net.i2p.util.NativeBigInteger;
import com.example.demo.p4p.crypto.BitCommitment;
import com.example.demo.p4p.crypto.SquareCommitment;
import com.example.demo.p4p.crypto.ThreeWayCommitment;
import com.example.demo.p4p.server.P4PServer;
import com.example.demo.p4p.sim.P4PSim;
import com.example.demo.p4p.user.JointCommitments;
import com.example.demo.p4p.user.UserVector2;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("postgres0")
public class ServerDataAccessService implements ServerDao{
    private static List<Person> DB = new ArrayList<>();
    private final String url = "jdbc:postgresql://localhost:5432/server1";
    private final String user = "postgres";
    private final String password = "password";
    final static CloseableHttpClient httpClient = HttpClients.createDefault();
    public static Map<String, String> portMap = Stream.of(new String[][] {
            { "f000aa01-0451-4000-b000-000000000000", "6001" },
            { "0c1e1494-aa4a-4afa-b494-d49754b0e244", "6002" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public static UserVector2 uv = new UserVector2(P4PSim.m, P4PSim.F, P4PSim.l, P4PSim.g, P4PSim.h);
    public static P4PServer server = new P4PServer(P4PSim.m, P4PSim.F, P4PSim.l, P4PSim.zkpIterations, P4PSim.g, P4PSim.h);
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public boolean serverVerification(UUID data_id, UiandProof uiandProof){
        List<BitCommitment.BitCommitmentProof> bcList = new ArrayList<>();
        List<ThreeWayCommitment.ThreeWayCommitmentProof> tcList = new ArrayList<>();
        List<SquareCommitment.SquareCommitmentProof> scList = new ArrayList<>();
        String bcjsonstr = uiandProof.getBcpjson_str();
        String tcjsonstr = uiandProof.getTcpjson_str();
        String scjsonstr = uiandProof.getScpjson_str();
        Gson gson = new Gson();    // create Gson instance
        try {
            bcList = Arrays.asList(gson.fromJson(bcjsonstr,
                    BitCommitment.BitCommitmentProof[].class));
            tcList = Arrays.asList(gson.fromJson(tcjsonstr,
                    ThreeWayCommitment.ThreeWayCommitmentProof[].class));
//            scList = Arrays.asList(gson.fromJson(scjsonstr,
//                    SquareCommitment.SquareCommitmentProof[].class));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        UserVector2.L2NormBoundProof2 received_serverProof = uiandProof.getServerProof();
        BitCommitment.BitCommitmentProof[] bcpsArray = new BitCommitment.BitCommitmentProof[bcList.size()];
        ThreeWayCommitment.ThreeWayCommitmentProof[] tcpsArray = new ThreeWayCommitment.ThreeWayCommitmentProof[tcList.size()];
        SquareCommitment.SquareCommitmentProof[] scpsArray = new SquareCommitment.SquareCommitmentProof[scList.size()];
        received_serverProof.setBcProofs(bcList.toArray(bcpsArray));
        received_serverProof.setTcProofs(tcList.toArray(tcpsArray));
        received_serverProof.setScProofs(scList.toArray(scpsArray));
        if(!uv.verify2(received_serverProof)) {
            System.out.println("data_id " + data_id + "'s vector failed the verification.");
            return false;
        }else{
            System.out.println("passed");
            return true;
        }
    }
        @Override
    public int insertUiandProof(UUID data_id, UiandProof uiandProof) {
        portMap.forEach((key, value) -> System.out.println(key + " : " + value));
        String SQL = "INSERT INTO U_PERSON_DATA(data_id,name,u1,u2,verified) "
                    + "VALUES(?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("uiandProof.getUi()"+Arrays.toString(uiandProof.getUi()));
            pstmt.setObject(1, data_id);
            pstmt.setObject(2, uiandProof.getUserid());
            long[] ui_arr =  uiandProof.getUi();
            Long[] aLong = new Long[ui_arr.length];
            Arrays.setAll(aLong, i -> aLong[i]);

            // convert to string array first, then insert as TEXT array
            String[] strArray = Arrays.stream(ui_arr)
                    .mapToObj(String::valueOf)
                    .toArray(String[]::new);

            Array ui_array = conn.createArrayOf("TEXT", strArray);
            pstmt.setArray(3,  ui_array);
            pstmt.setArray(4, ui_array);
//                        if (!peerPassed)
//                            server.disqualifyUser(i);
//                        else
            long [] U_longs = Arrays.stream(strArray).mapToLong(Long::parseLong).toArray();
            uv.setU(U_longs);
            //TODO: user_id int
            uv.setY(uiandProof.getY());
            System.out.println("isForServer: "+uiandProof.getServerProof().isForServer());
//            serverVerification(data_id,uiandProof);
            List<BitCommitment.BitCommitmentProof> bcpList = new ArrayList<>();
            List<ThreeWayCommitment.ThreeWayCommitmentProof> tcpList = new ArrayList<>();
            List<SquareCommitment.SquareCommitmentProof> scpList = new ArrayList<>();

            List<BitCommitment> bcList = new ArrayList<>();
            List<ThreeWayCommitment> tcList = new ArrayList<>();
            List<SquareCommitment> scList = new ArrayList<>();
            String bcpjsonstr = uiandProof.getBcpjson_str();
            String tcpjsonstr = uiandProof.getTcpjson_str();
            String scpjsonstr = uiandProof.getScpjson_str();

            String jcs_json_str = uiandProof.getJcsjson_str();
            int second_occurance = jcs_json_str.indexOf("!", jcs_json_str.indexOf("!") + 1);
            String bcjson_str = jcs_json_str.substring(0,jcs_json_str.indexOf("!"));
            String scjson_str = jcs_json_str.substring(jcs_json_str.indexOf("!")+1,second_occurance);
            String tcjson_str = jcs_json_str.substring(second_occurance+1);

            Gson gson = new Gson();
            JsonArray scsArry = JsonParser.parseString(scjson_str).getAsJsonArray();
            SquareCommitment.SquareCommitmentProof[] scs_array = new SquareCommitment.SquareCommitmentProof[scsArry.size()];
            int counter = 0;
            for(JsonElement pa: scsArry){
                SquareCommitment.SquareCommitmentProof inner_s = null;
                JsonObject scObject = pa.getAsJsonObject();
                JsonArray sg = scObject.get("abgh").getAsJsonArray();
                NativeBigInteger A = new NativeBigInteger(sg.get(0).getAsString());
                NativeBigInteger B = new NativeBigInteger(sg.get(1).getAsString());
                NativeBigInteger g = new NativeBigInteger(sg.get(2).getAsString());
                NativeBigInteger h = new NativeBigInteger(sg.get(3).getAsString());

                BigInteger a = new BigInteger(scObject.get("a").getAsString());
                BigInteger b = new BigInteger(scObject.get("b").getAsString());

                BigInteger sa = new BigInteger(scObject.get("sa").getAsString());
                BigInteger sb = new BigInteger(scObject.get("sb").getAsString());
                SquareCommitment sc = new SquareCommitment(g,h,a,b,A,B,sa, sb);

//                java.lang.UnsupportedOperationException: JsonNull
                if(!scObject.get("value").isJsonNull()) sc.setVal(new BigInteger(scObject.get("value").getAsString()));
                if(!scObject.get("randomness").isJsonNull())  sc.setR(new BigInteger(scObject.get("randomness").getAsString()));
                JsonObject json_scp = scObject.get("proof").getAsJsonObject();
                inner_s = sc.new SquareCommitmentProof();
                SquareCommitment.SquareCommitmentProof au = gson.fromJson(json_scp, SquareCommitment.SquareCommitmentProof.class);
                inner_s.setCommitment(au.getCommitment());
                inner_s.setChallenge(au.getChallenge());
                inner_s.setResponse(au.getResponse());
                System.out.println("inner_s");
                scs_array[counter] = inner_s;
                counter++;
//                SquareCommitment.SquareCommitmentProof  scp_json = json_scp;
            }

            JsonArray tcsArry = JsonParser.parseString(tcjson_str).getAsJsonArray();
            ThreeWayCommitment.ThreeWayCommitmentProof[] tcs_array = new ThreeWayCommitment.ThreeWayCommitmentProof[tcsArry.size()];
            counter = 0;
             for(JsonElement pa: tcsArry){
                ThreeWayCommitment.ThreeWayCommitmentProof inner_t = null;
                JsonObject tcObject = pa.getAsJsonObject();
                NativeBigInteger g = new NativeBigInteger(tcObject.get("ghconst").getAsJsonArray().get(0).getAsString());
                NativeBigInteger h = new NativeBigInteger(tcObject.get("ghconst").getAsJsonArray().get(1).getAsString());
                NativeBigInteger CONST = new NativeBigInteger(tcObject.get("ghconst").getAsJsonArray().get(2).getAsString());

                JsonObject json_tcp = tcObject.get("proof").getAsJsonObject();
                ThreeWayCommitment tc = new ThreeWayCommitment(g,h,CONST);
                 if(!tcObject.get("value").isJsonNull())   tc.setVal(new BigInteger(tcObject.get("value").getAsString()));
                 if(!tcObject.get("randomness").isJsonNull())   tc.setR(new BigInteger(tcObject.get("randomness").getAsString()));
                inner_t = tc.new ThreeWayCommitmentProof();
                ThreeWayCommitment.ThreeWayCommitmentProof au = gson.fromJson(json_tcp, ThreeWayCommitment.ThreeWayCommitmentProof.class);
                inner_t.setCommitment(au.getCommitment());
                inner_t.setChallenge(au.getChallenge());
                inner_t.setResponse(au.getResponse());
                inner_t.setNumeratorProof(gson.fromJson(json_tcp.get("numeratorProof"), BitCommitment.BitCommitmentProof.class));
                inner_t.setDenominatorProof(gson.fromJson(json_tcp.get("denominatorProof"), BitCommitment.BitCommitmentProof.class));
                System.out.println("inner_t");
                tcs_array[counter++] = inner_t;
            }

            JsonArray bjsonArry = JsonParser.parseString(bcjson_str).getAsJsonArray();
            BitCommitment.BitCommitmentProof[] bcs_array = new BitCommitment.BitCommitmentProof[bjsonArry.size()];
            counter = 0;
            for(JsonElement pa: bjsonArry){
                BitCommitment.BitCommitmentProof inner_b = null;
                JsonObject bcObject = pa.getAsJsonObject();
                NativeBigInteger g = new NativeBigInteger(bcObject.get("gh").getAsJsonArray().get(0).getAsString());
                NativeBigInteger h = new NativeBigInteger(bcObject.get("gh").getAsJsonArray().get(1).getAsString());
                JsonObject json_tcp = bcObject.get("proof").getAsJsonObject();
                BitCommitment bbc = new BitCommitment(g,h);
                if(!bcObject.get("value").isJsonNull()) bbc.setVal(new BigInteger(bcObject.get("value").getAsString()));
                if(!bcObject.get("randomness").isJsonNull()) bbc.setR(new BigInteger(bcObject.get("randomness").getAsString()));
                inner_b = bbc.new BitCommitmentProof();
                ThreeWayCommitment.ThreeWayCommitmentProof au = gson.fromJson(json_tcp, ThreeWayCommitment.ThreeWayCommitmentProof.class);
                inner_b.setCommitment(au.getCommitment());
                inner_b.setChallenge(au.getChallenge());
                inner_b.setResponse(au.getResponse());
                System.out.println("inner_b");
                bcs_array[counter++] = inner_b;
            }



//            JsonObject locObj = rootObj.getAsJsonObject("abgh");


//            JointCommitments jcs = uiandProof.getJointCommitments();
//            JointCommitments jcs_from_string = uiandProof.getJointCommitments();
            // create Gson instance
            try {
                bcpList = Arrays.asList(gson.fromJson(bcpjsonstr,
                        BitCommitment.BitCommitmentProof[].class));
                tcpList = Arrays.asList(gson.fromJson(tcpjsonstr,
                        ThreeWayCommitment.ThreeWayCommitmentProof[].class));
                scpList = Arrays.asList(gson.fromJson(scpjsonstr,
                        SquareCommitment.SquareCommitmentProof[].class));
                bcList = Arrays.asList(gson.fromJson(bcjson_str,
                        BitCommitment[].class));
                tcList = Arrays.asList(gson.fromJson(tcjson_str,
                        ThreeWayCommitment[].class));
                scList = Arrays.asList(gson.fromJson(scjson_str,
                        SquareCommitment[].class));
//                jcs_from_string =
//                scList = Arrays.asList(gson.fromJson(jcs_json_str.substring(jcs_json_str.indexOf(":"),jcs_json_str.indexOf("!")),
//                        SquareCommitment[].class));
//                tcList = Arrays.asList(gson.fromJson(jcs_json_str.substring(jcs_json_str.indexOf("!")),
//                        ThreeWayCommitment[].class));

                System.out.println("Here");
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            UserVector2.L2NormBoundProof2 received_serverProof = uiandProof.getServerProof();
            BitCommitment.BitCommitmentProof[] bcpsArray = new BitCommitment.BitCommitmentProof[bcpList.size()];
            ThreeWayCommitment.ThreeWayCommitmentProof[] tcpsArray = new ThreeWayCommitment.ThreeWayCommitmentProof[tcpList.size()];
            SquareCommitment.SquareCommitmentProof[] scpsArray = new SquareCommitment.SquareCommitmentProof[scpList.size()];
            received_serverProof.setBcProofs(bcpList.toArray(bcpsArray));
            received_serverProof.setTcProofs(tcpList.toArray(tcpsArray));
            received_serverProof.setScProofs(scpList.toArray(scpsArray));



            if(!uv.verify2(received_serverProof)) {
                System.out.println("data_id " + data_id + "'s vector failed the verification.");
//                return false;
            }else{
                System.out.println("passed");
//                return true;
            }

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
            pstmt.setBoolean(5, serverPassed);
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

        int index = (int)(Math.random() * availGaussUnits.size());
        System.out.println("Chosed Element is :" + availGaussUnits.get(index));

//        CrunchifyGetPropertyValues properties = new CrunchifyGetPropertyValues();
//        properties.getPropValues();
        System.out.println("boundForGauss.getUser_id()");
        System.out.println(boundForGauss.getUser_id());
        System.out.println("portMap.get(boundForGauss.getUser_id())");
        HashMap<String, String> portHashMap =
                (portMap instanceof HashMap)
                        ? (HashMap) portMap
                        : new HashMap<String, String>(portMap);
        System.out.println(portHashMap.get(boundForGauss.getUser_id().toString()));
        HttpPost request = new HttpPost("http://localhost:"+portHashMap.get(boundForGauss.getUser_id().toString())+"/api/v1/person/sumCountforUnit");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        try {
            StringEntity  json = new StringEntity(mapper.writeValueAsString(availGaussUnits.get(index)), ContentType.APPLICATION_JSON);
            request.setEntity(json);
            CloseableHttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 200){
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
        ArrayList<Long> sumU = sumUi(vSum.getPersonID());

        System.out.print("digital signature is: ");

        if(vSum.getV_sum().size() == 0){
            System.out.println("vSum is empty");
        }else{
            System.out.println(sumUandV(person_ID, sumU, vSum.getV_sum()));
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
    public ArrayList<Long> sumUi(UUID person_id) {
        String SQL = "SELECT u1 FROM U_PERSON_DATA where name=?";
        ArrayList<Long> sum = new ArrayList<>();
        try {
            Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            System.out.println("person_id:"+person_id);
            //TODO: change to UUID tyope
            preparedStatement.setString(1,person_id.toString());
            ResultSet rs = preparedStatement.executeQuery();

            ArrayList<ArrayList<Long>> TwoDResultList = new ArrayList<ArrayList<Long>>();
            ArrayList<String> stringList = new ArrayList<String>();
            while (rs.next()) {
                stringList = new ArrayList<>(Arrays.asList((String[]) rs.getArray("u1").getArray()));
                System.out.println(Arrays.asList((String[]) rs.getArray("u1").getArray()));
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
    public int cancelDS(UUID personID){
        String signature = checkSigWithoutTimer(personID);
        if(signature.equals("")){
            String SQL = "INSERT INTO PERSON_SIGNATURE(person_id,signature) "
                    + "VALUES(?,?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(SQL,
                         Statement.RETURN_GENERATED_KEYS)) {
                byte[] digitSignature = GenerateDigitalSignature.generateDS(personID);
                pstmt.setObject(1, personID);
                pstmt.setString(2, digitSignature.toString());
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
                    + "WHERE person_id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(SQL,
                         Statement.RETURN_GENERATED_KEYS)) {
                byte[] digitSignature = GenerateDigitalSignature.generateDS(personID);
                pstmt.setString(1, "None");
                pstmt.setObject(2, personID);
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

    public String checkSigWithoutTimer(UUID personID){
        String signature = "";
        String selectionSQL = "SELECT * from PERSON_SIGNATURE where person_id = ?";
        try {
            Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(selectionSQL);
            preparedStatement.setObject(1, personID);
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
    public String checkSig(UUID personID){
        String signature = "";
        String selectionSQL = "SELECT * from PERSON_SIGNATURE where person_id = ?";
            try {
                Connection conn = connect();
                PreparedStatement preparedStatement = conn.prepareStatement(selectionSQL);
                preparedStatement.setObject(1, personID);
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

    public byte[] insertToPersonSignature(UUID person_id){
        String SQL = "INSERT INTO PERSON_SIGNATURE(person_id,signature) "
                + "VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            byte[] digitSignature = GenerateDigitalSignature.generateDS(person_id);
            pstmt.setObject(1, person_id);
            // convert to string array first, then insert as TEXT array
            pstmt.setString(2, digitSignature.toString());
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
    public byte[] sumUandV(UUID person_id, ArrayList<Long> uSum, ArrayList<Long> vSum) {
        ArrayList<Long> dSum = new ArrayList<>();
        for(int i = 0; i<uSum.size(); i++) {
            dSum.add(uSum.get(i)+vSum.get(i));
        }
        System.out.println("dSum:"+dSum);
        String signature = "";
        byte[] signature_byte_array = new byte[]{};
        String selectionSQL = "SELECT * from PERSON_SIGNATURE where person_id = ?";
        try {
            Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(selectionSQL);
            preparedStatement.setObject(1, person_id);
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
                signature_byte_array = insertToPersonSignature(person_id);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return signature_byte_array;
    }
}
