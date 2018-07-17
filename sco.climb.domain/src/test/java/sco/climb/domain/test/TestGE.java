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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.gamification.RuleValidateDTO;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource("classpath:application.properties")
public class TestGE {
	
	@Autowired
	private GEngineUtils gengineUtils;
	
	private String gameId = "5b4c5d33e4b0b12fd6fe03cf";
	
	@Test
	public void testValidationConstants() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("const_zi_solo_bonus", "200");
		params.put("const_school_name", "Scuola Schmid 2A-B-C");
		params.put("const_number_of_teams", "4");
		params.put("const_NoCarDayClass_bonus", "5000");
		params.put("const_ZeroImpactDayClass_bonus", "10000");
		params.put("const_weekly_nominal_distance", "400000");
		params.put("final_destination", "Trento");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		PedibusItineraryLeg leg1 = new PedibusItineraryLeg();
		leg1.setName("Parco Adamello Brenta");
		leg1.setScore(70000);
		legs.add(leg1);

		PedibusItineraryLeg leg2 = new PedibusItineraryLeg();
		leg2.setName("Trento");
		leg2.setScore(3700000);
		legs.add(leg2);

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/constants_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

	@Test
	public void testValidationClassday() throws Exception {
		Map<String, String> params = new HashMap<>();
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/classday_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

}

