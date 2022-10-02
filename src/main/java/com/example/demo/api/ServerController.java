package com.example.demo.api;

import com.example.demo.model.*;
import com.example.demo.service.ServerService;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        System.out.print("server controller receive request to add Ui and Proof");
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
