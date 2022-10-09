package com.example.demo.api;

import com.example.demo.model.*;
import com.example.demo.service.ServerService;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.UUID;

import static com.example.demo.dao.ServerDataAccessService.userNameMap;

@RequestMapping("api/v1/server")
@RestController
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

//    @GetMapping("{uuidstr}")
//    public String checkSig(@PathVariable("uuidstr") String uuidstr){
//        System.out.println("uuidstr: "+uuidstr);
//
////        System.out.println(uuidstr.split("=")[1]);
//        HashMap<String, String> userNameHashMap =
//                (userNameMap instanceof HashMap)
//                        ? (HashMap) userNameMap
//                        : new HashMap<String, String>(userNameMap);
//        System.out.println(userNameHashMap.get(uuidstr.split("=")[1].toString()));
//        System.out.println("⬆️CheckSig⬆️");
//        UUID personID = UUID.fromString(uuidstr.split("=")[1]);
//        String batch_time = uuidstr.split("=")[2];
//        System.out.println(serverService.checkSig(new PersonCount(-1, personID, batch_time)));
//        return serverService.checkSig((new PersonCount(-1, personID, batch_time)));
////        return "";
//    }

    @PostMapping("/check_sig")
    public String checkSig(@Valid @NonNull @RequestBody PersonCount personCount){
//        System.out.println("uuidstr: "+personCount.getPerson_ID());
//
////        System.out.println(uuidstr.split("=")[1]);
//        HashMap<String, String> userNameHashMap =
//                (userNameMap instanceof HashMap)
//                        ? (HashMap) userNameMap
//                        : new HashMap<String, String>(userNameMap);
//        System.out.println(userNameHashMap.get(uuidstr.split("=")[1].toString()));
//        System.out.println("⬆️CheckSig⬆️");
//        UUID personID = UUID.fromString(uuidstr.split("=")[1]);
//        String batch_time = uuidstr.split("=")[2];
        System.out.println(serverService.checkSig(personCount));
        return serverService.checkSig(personCount);
//        return "";
    }


    @PostMapping("/cancel_ds")
    public void cancelDS(@Valid @NonNull @RequestBody PersonCount personCount){
        HashMap<String, String> userNameHashMap =
                (userNameMap instanceof HashMap)
                        ? (HashMap) userNameMap
                        : new HashMap<String, String>(userNameMap);
        System.out.println("cancelDS of "+userNameHashMap.get(personCount.getPerson_ID()));
        serverService.cancelDS(personCount);
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
