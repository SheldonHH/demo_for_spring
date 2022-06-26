package com.example.demo.api;

import com.example.demo.model.*;
import com.example.demo.p4p.crypto.BitCommitment;
import com.example.demo.p4p.crypto.SquareCommitment;
import com.example.demo.p4p.crypto.ThreeWayCommitment;
import com.example.demo.p4p.user.UserVector2;
import com.example.demo.service.ServerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/server")
@RestController
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping("{uuidstr}")
    public String checkSig(@PathVariable("uuidstr") String uuidstr){
        System.out.println(uuidstr.split("=")[1]);
        System.out.println("Hi");
        UUID personID = UUID.fromString(uuidstr.split("=")[1]);
        System.out.println(serverService.checkSig(personID));
        return serverService.checkSig(personID);
//        return "";
    }

    @PostMapping("/cancel_ds")
    public void cancelDS(@Valid @NonNull @RequestBody UUID person_id){
        System.out.println("I am here");
        serverService.cancelDS(person_id);
    }



    @PostMapping("/addGaussParamsSampleRange")
    public void addGaussParamsSampleRange(@Valid @NonNull @RequestBody BoundforGauss boundforGauss){
        serverService.addBoundforGauss(boundforGauss);
    }

    @PostMapping("/uiandproof")
    public void addUiandProof(@Valid @NonNull @RequestBody UiandProof uiandProof){
        serverService.insertUiandProof(uiandProof);
    }


    @PostMapping("/tcProof")
    public void tcandProof(@Valid @NonNull @RequestBody ThreeWayCommitment.ThreeWayCommitmentProof tcProof){
        System.out.println("tcProofs");
    }

    @PostMapping("/samplesumcount")
    public void sendRquestSampleSumCount (@Valid @NonNull @RequestBody ResponseSumCount responseSumCount){
        serverService.sendRquestSampleSumCount(responseSumCount);
    }

    @PostMapping("/addvsum") // peerID
    public void addVSum(@Valid @NonNull @RequestBody VSum vSum) {
        System.out.println("addVSum");
        serverService.insertVSum(vSum);
    }

}
