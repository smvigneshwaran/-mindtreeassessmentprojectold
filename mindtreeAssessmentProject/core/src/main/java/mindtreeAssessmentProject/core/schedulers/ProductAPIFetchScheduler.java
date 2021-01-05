package mindtreeAssessmentProject.core.schedulers;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mindtreeAssessmentProject.core.models.ProductListModel;
import mindtreeAssessmentProject.core.services.GetResolver;
import mindtreeAssessmentProject.core.services.ProductFeedServiceProvider;

@Designate(ocd = ProductAPIFetchScheduler.Config.class)
@Component(service = Runnable.class, immediate = true)
public class ProductAPIFetchScheduler implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductAPIFetchScheduler.class);

	private String productListPage="";

	@Reference
	private GetResolver getResolver;

	@Reference
	private ProductFeedServiceProvider productFeedServiceProvider;

	@ObjectClassDefinition(name = "Product Fetch Scheduler", description = "A Scheduler to fetch product api daily")
	public @interface Config {
		@AttributeDefinition(name = "Cron-job expression", description = "Expression stands for sec min hour monthDay month weekday year")
		String scheduler_expression() default "0 0 0 1/1 * ? *";

		@AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
		boolean scheduler_concurrent() default false;

		@AttributeDefinition(name = "Product List Config Page", description = "Product List Config Page")
		String productListPage() default "/content/mindtreeAssessmentProject/en/productListConfig";
	}
	@Activate
	protected void activate(final Config config) {
		productListPage = config.productListPage();
	}
	@Override
	public void run() {
		String productListComponentPath = productListPage + "/jcr:content/root/responsivegrid/productlistcomponent";
		try (final ResourceResolver resourceResolver = getResolver.getResolver()) {
			Resource productListComponentResource = resourceResolver.getResource(productListComponentPath);
			productListComponentResource.adaptTo(ProductListModel.class);
		} catch (Exception e) {
			LOGGER.error("Exception occurred {}", e.getMessage());
		}
	}

}
