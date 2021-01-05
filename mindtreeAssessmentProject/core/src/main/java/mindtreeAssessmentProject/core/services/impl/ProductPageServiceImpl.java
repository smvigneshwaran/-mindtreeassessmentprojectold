package mindtreeAssessmentProject.core.services.impl;

import java.util.Objects;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.gson.JsonObject;

import mindtreeAssessmentProject.core.services.GetResolver;
import mindtreeAssessmentProject.core.services.ProductPageService;

@Component(service = ProductPageService.class, immediate = true)
@Designate(ocd = ProductPageServiceImpl.Config.class)
public class ProductPageServiceImpl implements ProductPageService {

	private static final Logger LOG = LoggerFactory.getLogger(ProductPageServiceImpl.class);

	@Reference
	private GetResolver getResolver;

	private String rootPagePath = StringUtils.EMPTY;

	private String template = StringUtils.EMPTY;;

	private String renderer = StringUtils.EMPTY;;

	@Activate
	@Modified
	protected void activate(final Config config) {

		LOG.debug("Into ProductPageServiceImpl service Start");

		this.rootPagePath = String.valueOf(config.rootPagePath());

		this.template = String.valueOf(config.template());

		this.renderer = String.valueOf(config.rendererComponent());

		LOG.debug("Into ProductPageServiceImpl service END");
	}

	@Override
	public void createProductPage(JsonObject productJson) {

		try (final ResourceResolver resourceResolver = getResolver.getResolver()) {

			Page productPage = null;

			Session session = resourceResolver.adaptTo(Session.class);

			// Create Page

			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

			if (session != null && productJson != null) {

				JsonObject dataJson = productJson.getAsJsonObject("data");

				if (dataJson != null) {

					String ctn = dataJson.get("ctn").getAsString().replace("/", "_");

					String productPagePath = rootPagePath + "/" + ctn;

					Resource productPageRes = resourceResolver.getResource(productPagePath);

					if (Objects.nonNull(productPageRes)) {

						productPage = productPageRes.adaptTo(Page.class);
					}

					else {

						productPage = pageManager.create(rootPagePath, ctn, template,
								dataJson.get("productTitle").getAsString());
					}

					Node pageNode = productPage.adaptTo(Node.class);

					Node jcrNode = null;

					if (productPage.hasContent()) {

						jcrNode = productPage.getContentResource().adaptTo(Node.class);

					} else {

						jcrNode = pageNode.addNode("jcr:content", "cq:PageContent");
					}

					jcrNode.setProperty("sling:resourceType", renderer);

					if (Objects.nonNull(dataJson.get("imageURL")))
						jcrNode.setProperty("productImageURL", dataJson.get("imageURL").getAsString());

					if (Objects.nonNull(dataJson.get("marketingTextHeader")))
						jcrNode.setProperty("productDescription", dataJson.get("marketingTextHeader").getAsString());

					Node parNode = this.getNode(jcrNode, "root");

					parNode.setProperty("sling:resourceType", "wcm/foundation/components/responsivegrid");

					Node responsivegrid = this.getNode(parNode, "responsivegrid");

					responsivegrid.setProperty("sling:resourceType", "wcm/foundation/components/responsivegrid");

					Node textNode = this.getNode(responsivegrid, "productlistcomponent");

					textNode.setProperty("sling:resourceType",
							"/apps/mindtreeAssessmentProject/components/content/productdetailcomponent");

					textNode.setProperty("productId", ctn);
					
					textNode.setProperty("productTitle", dataJson.get("productTitle").getAsString());

					textNode.setProperty("productDescription", dataJson.get("marketingTextHeader").getAsString());

					textNode.setProperty("productImage", dataJson.get("imageURL").getAsString());

					textNode.setProperty("subcategoryName", dataJson.get("subcategoryName").getAsString());

					JsonObject priceObj = dataJson.get("price").getAsJsonObject();

					if (Objects.nonNull(priceObj)) {

						textNode.setProperty("productPrice", priceObj.get("formattedDisplayPrice").getAsString());

					}
					resourceResolver.commit();

					session.save();

					session.refresh(true);
				}
			}
		} catch (WCMException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (ItemExistsException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (PathNotFoundException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (NoSuchNodeTypeException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (LockException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (VersionException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (ConstraintViolationException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (RepositoryException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (LoginException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (PersistenceException e) {
			LOG.error("Exception occurred while creating page {} ", e);
		} catch (Exception e) {
			LOG.error("Exception occurred while creating page {} ", e);
		}

	}

	private Node getNode(Node parentNode, String relNode) throws PathNotFoundException, ItemExistsException,
			VersionException, ConstraintViolationException, LockException, RepositoryException {
		Node target = null;
		if (parentNode.hasNode(relNode)) {
			target = parentNode.getNode(relNode);
		} else {
			target = parentNode.addNode(relNode);
		}
		return target;
	}

	@ObjectClassDefinition(name = "Product page creation service Configuration", description = "This service contains "
			+ "all Product page creation service Configuration")
	public @interface Config {

		@AttributeDefinition(name = "Product pages root path", type = AttributeType.STRING)
		String rootPagePath() default "/content/mindtreeAssessmentProject/en/productPages"; // StringUtils.EMPTY;

		@AttributeDefinition(name = "Product Page template type", type = AttributeType.STRING)
		String template() default "/conf/mindtreeAssessmentProject/settings/wcm/templates/content-page"; // StringUtils.EMPTY;

		@AttributeDefinition(name = "Product page component", type = AttributeType.STRING)
		String rendererComponent() default "mindtreeAssessmentProject/components/structure/page"; // StringUtils.EMPTY;
	}

}
