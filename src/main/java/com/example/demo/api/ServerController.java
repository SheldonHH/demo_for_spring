package com.example.demo.api;

import com.example.demo.dao.ServerDataAccessService;
import com.example.demo.model.*;
import com.example.demo.p4p.user.UserVector2;
import com.example.demo.service.PeerService;
import com.example.demo.service.ServerService;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping("api/v1/server")
@RestController
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostMapping("/addGaussParamsSampleRange")
    public void addGaussParamsSampleRange(@Valid @NonNull @RequestBody BoundforGauss boundforGauss){
        serverService.addBoundforGauss(boundforGauss);
    }

    @PostMapping("/uiandproof")
    public void addUiandProof(@Valid @NonNull @RequestBody UiandProof uiandProof){
        serverService.insertUiandProof(uiandProof);
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
