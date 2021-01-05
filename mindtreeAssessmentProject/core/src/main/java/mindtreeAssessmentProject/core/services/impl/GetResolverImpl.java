package mindtreeAssessmentProject.core.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mindtreeAssessmentProject.core.services.GetResolver;


@Component(service = GetResolver.class)
public class GetResolverImpl implements GetResolver {

	private final Logger log = LoggerFactory.getLogger(GetResolverImpl.class);
	
	private static final String SUB_SERVICE = "mindtreeaemuser";

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public ResourceResolver getResolver() throws Exception {
		
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE);
		
		ResourceResolver resourceResolver;
		
		try {
		
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
		
		} catch (Exception e) {
		
			log.error("Exception in resolving resourceresolver from system user {}" + e.getMessage());
			throw e;
		}

		return resourceResolver;
	}

}
