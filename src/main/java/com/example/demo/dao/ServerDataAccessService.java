package com.example.demo.dao;

import com.example.demo.model.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository("postgres0")
public class ServerDataAccessService implements ServerDao{
    private static List<Person> DB = new ArrayList<>();
    private final String url = "jdbc:postgresql://localhost:5432/server1";
    private final String user = "postgres";
    private final String password = "password";
    final static CloseableHttpClient httpClient = HttpClients.createDefault();
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

        @Override
    public int insertUiandProof(UUID data_id, UiandProof uiandProof) {
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

            Array sg = conn.createArrayOf("TEXT", strArray);
            pstmt.setArray(3,  sg);
            pstmt.setArray(4, sg);
            pstmt.setBoolean(5, false);
            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    System.out.println(rs);
//                    if (rs.next()) {
////                        id = rs.getLong(1);
//                    }
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
    public int addBoundforGauss(BoundforGauss boundForGauss) {
        System.out.println(boundForGauss.getMaxX1());
        System.out.println(boundForGauss.getMaxX2());
        System.out.println(boundForGauss.getMinX1());
        System.out.println(boundForGauss.getMinX2());
        System.out.println(boundForGauss.getGaussian_params());
        System.out.println(boundForGauss.getAvailGaussUnits());
        ArrayList<String> availGaussUnits =  boundForGauss.getAvailGaussUnits();

        int index = (int)(Math.random() * availGaussUnits.size());
        System.out.println("Chosed Element is :" + availGaussUnits.get(index));

        HttpPost request = new HttpPost("http://localhost:6001/api/v1/person/sumCountforUnit");
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



        return 0;
    }

    @Override
    public int insertVSum(VSum vSum) {
        System.out.println("vSum.getPeerID()"+vSum.getPeerID());
        System.out.println("vSum.getV_sum()"+vSum.getV_sum());
        return 0;
    }


    @Override
    public int distanceofReceiveRquestSumCount(ResponseSumCount responseSumCount) {
        System.out.println("distanceofReceiveRquestSumCount");
        System.out.println("getCount: "+responseSumCount.getCount());
        System.out.println("getDiList"+responseSumCount.getDiList().toString());
        return 0;
    }

    @Override
    public long[] sumUi(Person person) {
        return new long[0];
    }

    @Override
    public long[] sumUandV(Person person) {
        return new long[0];
    }
}
