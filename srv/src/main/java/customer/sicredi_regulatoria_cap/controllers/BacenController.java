package customer.sicredi_regulatoria_cap.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import customer.sicredi_regulatoria_cap.services.BacenService;


@RequestMapping("/bacen")
@RestController
public class BacenController {

    @Autowired
    private BacenService bacenService;

    @GetMapping
    public String getMethodName() throws IOException {
        // bacenService.getBacen();
        
        return new String();
    }
    
}
