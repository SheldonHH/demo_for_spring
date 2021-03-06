package com.example.demo.dao;

import com.example.demo.model.*;
import com.example.demo.p4p.user.UserVector2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.UUID;

// dependency injection
public interface ServerDao {
    int insertUiandProof(UUID id, UiandProof uniandproof);
    default int insertUiandProof(UiandProof uniandproof){
        UUID data_id = UUID.randomUUID();
        return insertUiandProof(data_id, uniandproof);
    }

    byte[] addBoundforGauss(BoundforGauss boundForGauss);

    int cancelDS(UUID person_id);
    int insertVSum(VSum vSum);

    String checkSig(UUID personID);
    int distanceofReceiveRquestSumCount(ResponseSumCount responseSumCount);

//    default int insertVi(Person person){
//        UUID id = UUID.randomUUID();
//        return insertVi(id, person);
//    }
    // return zero or one depends on whether data is persisted

    ArrayList<Long> sumUi(UUID person_id);

    byte[] sumUandV(UUID person_id, ArrayList<Long>uSum, ArrayList<Long>vSum);

//    Optional<long[]> selectPeerVById(UUID id);

//    int deletePersonById(UUID id);
//
//    int updatePersonById(UUID id, Person person);


}
