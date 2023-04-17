package com.kevinmv.controller;

import com.kevinmv.model.Person;
import org.eclipse.microprofile.faulttolerance.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {
    List<Person> personList = new ArrayList<Person>();
    Logger LOGGER = Logger.getLogger("DemoLogger");

    @GET
    @Timeout(value = 5000L)
    @Retry(maxRetries = 3)
    @CircuitBreaker(failureRatio = 0.1, delay = 15000L)
    @Bulkhead(value = 1)
    @Fallback(fallbackMethod = "getPersonFallbackList")
    public List<Person> getPersonList() {
        doFail();
        //doWait();
        return personList;
    }

    public List<Person> getPersonFallbackList(){
        var person = new Person(-1L, "Kevin", "kevinvr@hotmail.es");
        return List.of(person);
    }

    public void doWait(){
        var random = new Random();
        try{
            LOGGER.warning("Haciendo un sleep");
            Thread.sleep(random.nextInt(10)+1*1000L);
        }catch (Exception e){

        }
    }
    public void doFail(){
        var random = new Random();
        if(random.nextBoolean()){
            LOGGER.warning("Falla producida");
            throw new RuntimeException("Haciendo qu ela implementación falle");
        }
    }
}

