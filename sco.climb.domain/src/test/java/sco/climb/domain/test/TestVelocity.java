package sco.climb.domain.test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Test;

import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;

public class TestVelocity {

	@Test
	public void testVelocityConstants() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		    
		Template t = velocityEngine.getTemplate("game-template/constants.vm");
		
		Map<String, String> params = new HashMap<>();
		params.put("const_zi_solo_bonus", "1000.0");
		params.put("final_destination", "FineViaggio");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		PedibusItineraryLeg leg = new PedibusItineraryLeg();
		leg.setName("Tappa 1");
		leg.setScore(1000);
		legs.add(leg);
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		System.out.println(writer.toString());
	}
	
	@Test
	public void testVelocityCalendarTrips() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		    
		Template t = velocityEngine.getTemplate("game-template/calendartrips.vm");
		
		Map<String, String> params = new HashMap<>();
		params.put("const_pandr_distance", "100");
		params.put("const_bus_distance", "100");
		params.put("const_zeroimpact_distance", "100");
		params.put("const_cloudy_bonus", "100");
		params.put("const_rain_bonus", "100");
		params.put("const_snow_bonus", "100");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		System.out.println(writer.toString());
	}
	
	@Test
	public void testVelocityLegsBadges() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		    
		Template t = velocityEngine.getTemplate("game-template/legsbadges.vm");
		
		Map<String, String> params = new HashMap<>();
		params.put("const_zi_solo_bonus", "1000.0");
		params.put("final_destination", "Fine Viaggio");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		PedibusItineraryLeg leg = new PedibusItineraryLeg();
		leg.setName("Tappa 1");
		leg.setScore(1000);
		legs.add(leg);
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		System.out.println(writer.toString());
	}
	
}
