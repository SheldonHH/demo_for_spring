package com.example.demo.service;

import com.example.demo.dao.ServerDao;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ServerService {
    private final ServerDao serverDao;

    @Autowired
    public ServerService( @Qualifier("postgres0") ServerDao serverDao) {
        this.serverDao = serverDao;
    }

    public int cancelDS(UUID personID) { return serverDao.cancelDS(personID); }
    public String checkSig(UUID personID){return serverDao.checkSig(personID); }
    public int insertUiandProof(UiandProof uiandProof) {
        return serverDao.insertUiandProof(uiandProof);
    }

    public byte[] addBoundforGauss(BoundforGauss boundForGauss){
        return serverDao.addBoundforGauss(boundForGauss);
    }

    public int insertVSum(VSum vSum){
        return serverDao.insertVSum(vSum);
    }

    public int sendRquestSampleSumCount(ResponseSumCount responseSumCount){
        return serverDao.distanceofReceiveRquestSumCount(responseSumCount);
    }

}