package com.scc.enci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.scc.enci.config.ServiceConfig;
import com.scc.enci.model.Club;
import com.scc.enci.repository.ClubRepository;
import com.scc.enci.template.ClubObject;
import com.scc.enci.template.ResponseObjectList;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ClubService {

    private static final Logger logger = LoggerFactory.getLogger(ClubService.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    ServiceConfig config;

    @HystrixCommand(fallbackMethod = "buildFallbackClubList",
            threadPoolKey = "getClubsThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="30"),
                     @HystrixProperty(name="maxQueueSize", value="10")},
            commandProperties={
                     @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                     @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                     @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
                     @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                     @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")}
    )
    public ResponseObjectList<ClubObject> getClubs(){

        Span newSpan = tracer.createSpan("getClubs");
        logger.debug("In the clubService.getClubs() call, trace id: {}", tracer.getCurrentSpan().traceIdString());
        try {
        	
        	List<Club> list = new ArrayList<Club>(); 
        	list = clubRepository.findAll();
        		
        	List<ClubObject> results = new ArrayList<ClubObject>();
	    	
	    	for (Club _club : list) {
	    		
	    		ClubObject result = new ClubObject();

		    	// Construction de la réponse
	    		result.withId(_club.getId() )
	    			.withName( _club.getName() )
	    			.withAddress( _club.getAddress())
	    			.withCity( _club.getCity() )
	    			.withZipCode( _club.getZipCode() )
	    			.withInscriptionEmail( _club.getInscriptionEmail() )
	    			.withInfoEmail( _club.getInfoEmail() )
	    			.withTelephone( _club.getTelephone() )
	    		;
	    		
	    		results.add(result);
	    	}
	    	return new ResponseObjectList<ClubObject>(results.size(),results);
        }
	    finally{
	    	newSpan.tag("peer.service", "postgres");
	        newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
	        tracer.close(newSpan);
	    }
    }

    private ResponseObjectList<ClubObject> buildFallbackClubList(){
    	
    	List<ClubObject> list = new ArrayList<ClubObject>(); 
    	list.add(new ClubObject()
                .withId(0))
    	;
        return new ResponseObjectList<ClubObject>(list.size(),list);
    }
    
}
