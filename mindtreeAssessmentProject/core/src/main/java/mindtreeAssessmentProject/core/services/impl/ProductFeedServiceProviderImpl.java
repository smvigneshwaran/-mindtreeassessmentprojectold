package mindtreeAssessmentProject.core.services.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mindtreeAssessmentProject.core.services.ProductFeedServiceProvider;
import mindtreeAssessmentProject.core.services.ProductPageService;

@Component(service = ProductFeedServiceProvider.class, immediate = true)
@Designate(ocd = ProductFeedServiceProviderImpl.Config.class)
public class ProductFeedServiceProviderImpl implements ProductFeedServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(ProductFeedServiceProviderImpl.class);

	private static int timeout = 100;

	private String productAPIEndPoint = "";

	@Reference
	private ProductPageService productPageService;

	@Activate
	@Modified
	protected void activate(final Config config) {

		LOG.debug("Into ProductFeedServiceProviderImpl service Start");

		this.productAPIEndPoint = String.valueOf(config.productFeedEndpointUrl());

		LOG.debug("ProductFeedServiceProviderImpl Configs : {} ", productAPIEndPoint);

		LOG.debug("Into ProductFeedServiceProviderImpl service END");

	}

	@Override
	public void getProductDetail(List<String> ctnList) {

		CloseableHttpClient client = null;

		CloseableHttpResponse response = null;

		int statusCode;

		try {

			if (Objects.nonNull(productAPIEndPoint) && !productAPIEndPoint.equals(StringUtils.EMPTY)) {

				for (String ctn : ctnList) {

					String apiEndPoint = productAPIEndPoint.replace("{CTN}", ctn);

					HttpGet request = new HttpGet(apiEndPoint);

					request.setHeader("Content-Type", "application/json");

					RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
							.setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();

					client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

					response = client.execute(request);

					statusCode = response.getStatusLine().getStatusCode();

					LOG.debug("statusCode {}", statusCode);

					if (!this.isError(statusCode)) {

						String responseString = EntityUtils.toString(response.getEntity(),
								StandardCharsets.UTF_8.name());
						if (Objects.nonNull(responseString) && responseString != StringUtils.EMPTY) {

							JsonObject productJson = new JsonParser().parse(responseString).getAsJsonObject();

							productPageService.createProductPage(productJson);

						}
						LOG.debug("ProductFeedServiceProviderImpl responseString {}", responseString);

					} else {

						LOG.error("Recieved response with error status: {}", statusCode);
					}
				}
			}

		} catch (UnsupportedEncodingException e) {

			LOG.error("UnsupportedEncodingException Occured in Product service {} ", e);

		} catch (ClientProtocolException e) {

			LOG.error("ClientProtocolException Occured in Product service {} ", e);

		} catch (IOException e) {

			LOG.error("IOException Occured in Product service {} ", e);

		} finally {

			try {

				if (Objects.nonNull(client)) {

					client.close();
				}

				if (Objects.nonNull(response)) {

					response.close();
				}

			} catch (IOException e) {

				LOG.error("IOException Occured in Product service {} ", e);
			}
		}

	}

	public boolean isError(int status) {
		if (status == 200 || status == 201 || status == 204 || status == 400 || status == 401) {
			return false;
		} else {
			return true;
		}
	}

	@ObjectClassDefinition(name = "Product Feed Endpoint Configuration", description = "This service contains "
			+ "product Feed Endpoint Configuration")
	public @interface Config {

		@AttributeDefinition(name = "Product Feed Endpoint", type = AttributeType.STRING)
		String productFeedEndpointUrl() default "https://www.philips.com/prx/product/B2C/en_GB/CONSUMER/products/{CTN}.summary"; // StringUtils.EMPTY;

	}

}
