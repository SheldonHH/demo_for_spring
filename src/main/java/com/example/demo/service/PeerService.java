package com.example.demo.service;

import com.example.demo.dao.PeerDao;
import com.example.demo.dao.PersonDao;
import com.example.demo.model.Person;
import com.example.demo.model.RCVisTuple;
import com.example.demo.model.RowColHash;
import com.example.demo.p4p.user.UserVector2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PeerService {
    private final PeerDao peerDao;
    private final PersonDao personDao;

    @Autowired
    public PeerService(PeerDao peerDao, @Qualifier("postgres1") PersonDao personDao){
        this.peerDao = peerDao;
        this.personDao = personDao;
    }
    public int addViandProof(UUID userid, UUID id, long[] vi, UserVector2.L2NormBoundProof2 peerProof){
        return  peerDao.insertViandProof(userid, id, vi, peerProof);
    }

    public int addVHashMatrix(UUID userid, long[][] v_matrix, RowColHash rowColHash){
         return  peerDao.insertVHashMatrix(userid, v_matrix, rowColHash);
    }

    public int hashVerifywithReceiveRquestRCVisTuple(UUID userid, RCVisTuple rcVisTuple){
        return  peerDao.hashVerifywithReceiveRquestRCVisTuple(userid, rcVisTuple);
    }


    public List<Person> getAllPeople() {
        return personDao.selectAllPeople();
    }

    public Optional<Person> getPersonById(UUID user_id){
        return personDao.selectPersonById(user_id);
    }


}