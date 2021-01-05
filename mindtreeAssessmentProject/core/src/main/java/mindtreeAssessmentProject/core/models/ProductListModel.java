package mindtreeAssessmentProject.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import mindtreeAssessmentProject.core.services.ProductFeedServiceProvider;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductListModel {

	@ChildResource
	private Resource productIds;

	private List<String> productIdsList = new ArrayList<String>();

	@OSGiService
	ProductFeedServiceProvider productFeedServiceProvider;

	@PostConstruct
	protected void init() {

		if (Objects.nonNull(productIds)) {
			for (Iterator<Resource> iter = productIds.listChildren(); iter.hasNext();) {

				ValueMap productId = iter.next().getValueMap();

				productIdsList.add(productId.get("productId", String.class));
			}

			productFeedServiceProvider.getProductDetail(productIdsList);
		}
	}

	public List<String> getProductIdsList() {
		return productIdsList;
	}

}
