package it.smartcommunitylab.climb.domain.config;

import java.util.List;

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
import it.smartcommunitylab.climb.domain.model.MultimediaContentTags;
import it.smartcommunitylab.climb.domain.model.gamification.PedibusGameConfTemplate;
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
		MultimediaContentTags tags = storage.getMultimediaContentTags();
		if(tags == null) {
			try {
				Resource resource = new ClassPathResource("json/mctags.json", 
						this.getClass().getClassLoader());
				tags = Utils.readJSONFromInputStream(resource.getInputStream(), MultimediaContentTags.class);
				storage.saveMultimediaContentTags(tags);
			} catch (Exception e) {
				logger.warn("error in init mctags:{}", e.getMessage());
			}
		}
		List<PedibusGameConfTemplate> pedibusGameConfTemplates = storage.getPedibusGameConfTemplates();
		if(pedibusGameConfTemplates.size() == 0) {
			try {
				Resource resource = new ClassPathResource("game-template/confTemplate.json", 
						this.getClass().getClassLoader());
				pedibusGameConfTemplates = Utils.readJSONListFromInputStream(resource.getInputStream(), 
						PedibusGameConfTemplate.class);
				for(PedibusGameConfTemplate confTemplate : pedibusGameConfTemplates) {
					storage.savePedibusGameConfTemplate(confTemplate);
				}
			} catch (Exception e) {
				logger.warn("error in init pedibus game conf template:{}", e.getMessage());
			}
		}
  }
}
