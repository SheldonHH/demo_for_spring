package com.example.demo.dao;

import com.example.demo.model.Person;
import com.example.demo.p4p.user.UserVector2;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgres")
public class PeerDataAccessService implements PeerDao{
    private static List<Person> DB = new ArrayList<>();

    @Override
    public int insertViandProof(UUID id, long[] vi, UserVector2.L2NormBoundProof2 peerProof) {
        return 0;
    }

    @Override
    public int insertHashMatrix(UUID id, BigInteger[][] hash) {
        return 0;
    }

    @Override
    public long sumViforPerson(Person person) {
        return 0;
    }

}
