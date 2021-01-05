package mindtreeAssessmentProject.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductDetailModel {

	
	@ValueMapValue(name = "productId")
	@Default(values = "No Id")
	private String productId;
	
	@ValueMapValue(name = "productTitle")
	@Default(values = "No Title")
	private String productTitle;

	@ValueMapValue(name = "productImage")
	@Default(values = "No Image")
	private String productImage;

	@ValueMapValue(name = "productDescription")
	@Default(values = "No Description")
	private String productDescription;

	@ValueMapValue(name = "productPrice")
	@Default(values = "No Price")
	private String productPrice;
	
	@ValueMapValue(name = "subcategoryName")
	@Default(values = "No subcategoryName")
	private String subcategoryName;
	

	public String getProductTitle() {
		return productTitle;
	}

	public String getProductImage() {
		return productImage;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public String getProductId() {
		return productId;
	}

	public String getSubcategoryName() {
		return subcategoryName;
	}

}
