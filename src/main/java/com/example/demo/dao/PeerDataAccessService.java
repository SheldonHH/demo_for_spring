package com.example.demo.dao;

import com.example.demo.model.*;
import com.example.demo.p4p.user.UserVector2;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgres1")
public class PeerDataAccessService implements PeerDao{
    private static List<Person> DB = new ArrayList<>();

    @Override
    public int insertViandProof(ViandProof viandProof)
    {
        return 0;
    }

    @Override
    public int insertVHashMatrix(VHashMatrix vHashMatrix) {

        return 0;
    }

    @Override
    public int hashVerifywithReceiveRquestRCVisTuple(RCVisTupleUser rcVisTupleUser) {
//        UUID userID = rcVisTupleUser.getUserid();

        return 0;
    }

    @Override
    public long sumVi(Person person) {
        return 0;
    }

}
