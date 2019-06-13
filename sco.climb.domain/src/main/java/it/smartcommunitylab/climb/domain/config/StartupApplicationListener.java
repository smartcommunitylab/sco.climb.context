package it.smartcommunitylab.climb.domain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.ModalityMap;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Component
public class StartupApplicationListener {
	private static final transient Logger logger = LoggerFactory.getLogger(StartupApplicationListener.class);
	
	@Autowired
	RepositoryManager storage;
	
	@EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
		ModalityMap modalityMap = storage.getModalityMap();
		if(modalityMap == null) {
			try {
				Resource resource = new ClassPathResource("json/modalitymap.json", 
						this.getClass().getClassLoader());
				ModalityMap map = Utils.readJSONFromInputStream(resource.getInputStream(), 
						ModalityMap.class);
				storage.saveModalityMap(map);
			} catch (Exception e) {
				logger.warn("error in init madalitymap:{}", e.getMessage());
			}
		}
  }
}
