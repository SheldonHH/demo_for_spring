package com.example.demo.dao;

import com.example.demo.model.*;
import com.example.demo.p4p.user.UserVector2;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository("postgres0")
public class ServerDataAccessService implements ServerDao{
    private static List<Person> DB = new ArrayList<>();

    @Override
    public int insertUiandProof(UiandProof uiandProof) {
        return 0;
    }

    @Override
    public int insertGaussParamsandSampleRange(GaussParamsandSampleRange gaussian_params) {
        return 0;
    }

    @Override
    public int insertVSum(VSum vSum) {
        return 0;
    }


    @Override
    public int distanceofReceiveRquestSumCount(SampleSumandCount sampleSumandCount) {
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
