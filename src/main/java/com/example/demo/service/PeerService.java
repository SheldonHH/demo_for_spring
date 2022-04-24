package com.example.demo.service;

import com.example.demo.dao.PeerDao;
import com.example.demo.model.Person;
import com.example.demo.p4p.user.UserVector2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PeerService {
    private final PeerDao peerDao;

    @Autowired
    public PeerService(@Qualifier("postgres1") PeerDao peerDao){
        this.peerDao = peerDao;
    }
    public int insertViandProof(UUID id, long[] vi, UserVector2.L2NormBoundProof2 peerProof){
        return  peerDao.insertViandProof(id, vi, peerProof);
    }

    public int insertHashMatrix(UUID id, BigInteger[][] hash)  {
        return 1;
    }

//    public Optional<Person> getPersonById(UUID id){
//        return peerDao.selectPeerVById(id);
//    }



}