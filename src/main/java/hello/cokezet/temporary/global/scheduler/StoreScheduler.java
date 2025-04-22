package hello.cokezet.temporary.global.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.cokezet.temporary.domain.product.entity.Product;
import hello.cokezet.temporary.domain.product.repository.ProductRepository;
import hello.cokezet.temporary.domain.store.entity.Store;
import hello.cokezet.temporary.domain.store.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class StoreScheduler {

	private final RestClient restClient;
	private final ObjectMapper objectMapper;
	private final ProductRepository productRepository;
	private final StoreRepository storeRepository;

	public StoreScheduler(RestClient restClient, ObjectMapper objectMapper, ProductRepository productRepository, StoreRepository storeRepository) {
		this.objectMapper = objectMapper;
		this.restClient = restClient;
		this.productRepository = productRepository;
		this.storeRepository = storeRepository;
	}

	@Scheduled(cron = "0 0 12,23 * * *")
//	@Scheduled(cron = "0 * * * * *")
	public void storeScheduler() {
		Long systemTime = System.currentTimeMillis();

		String uri = String.format(
				"https://apis.11st.co.kr/search/api/tab/total-search/more/common?kwd=제로콜라&tabId=TOTAL_SEARCH&_=%d&commonPrdTotCnt=5453&pageNo=1"
						+ "&prdMoreStartShowCnt=1000", systemTime);

		log.info("uri: {}", uri);

		JsonNode storeResponse = restClient.get().uri(uri).retrieve().body(JsonNode.class);

		JsonNode items = Objects.requireNonNull(storeResponse).get("items");

		// ✅ "items" 배열에서 개별 아이템 추출 후 필터링
		List<JsonNode> filteredItems = objectMapper.convertValue(items, new TypeReference<>() { });
		Store store = storeRepository.findByName("11번가");

		filteredItems.stream().filter(this::itemsValidator).filter(item -> item.has("unitPrcInfo") && item.get("unitPrcInfo").has("unitPrc"))
				.filter(item -> !item.get("title").asText().contains("&")).forEach(item -> {
					log.info("item: {}", item);

					if (productRepository.existsByStoreProductId(item.get("id").asLong())) {
						return;
					}

					log.info("item: {}", item.get("unitPrcInfo").asText());

					String size = getSize(item.get("title").asText());
					if (size == null) {
						return;
					}

					String brand = getBrand(item.get("title").asText());
					if (brand == null) {
						return;
					}

					Integer count = getCount(item.get("title").asText());
					if (count == null) {
						return;
					}

					String taste = getTaste(item.get("title").asText());
					if (taste == null) {
						taste = "original";
					}

					productRepository.save(
							new Product(
									item.get("id").asLong(),
									store.getId(),
									item.get("finalPrc").asInt(),
									item.get("unitPrcInfo").get("unitPrc").asInt(),
									item.get("discountRate").asInt(),
									size,
									brand,
									count,
									taste
							)
					);
				});
	}


	// ✅ title이 null이면 빈 문자열("")을 반환하여 NPE 방지
	private boolean itemsValidator(JsonNode items) {
		String title = items.has("title") ? items.get("title").asText("") : "";
		return title.contains("코카콜라 제로") || title.contains("펩시콜라제로") || title.contains("펩시 제로슈거") || title.contains("코크제로") || title.contains("콜라")
				|| title.contains("펩시콜라 제로") || title.contains("펩시콜라 제로슈거") || title.contains("제로펩시 라임") || title.contains("제로 콜라") || title.contains("코카콜라제로");
	}

	private String getSize(String originalTitle) {
		// 정규 표현식 패턴: 100 이상의 숫자 + ml
		Pattern pattern = Pattern.compile("(\\d{3,})");
		Matcher matcher = pattern.matcher(originalTitle);

		// 매칭된 값이 있으면 그 값을 반환
		if (matcher.find()) {
			return matcher.group(1); // 첫 번째 캡처 그룹 (즉, 100 이상의 숫자와 ml)
		}

		return null; // 100 이상의 ml 값이 없으면 null 반환
	}


	private String getBrand(String originalTitle) {
		// 브랜드 이름을 대소문자 구분 없이 찾기
		if (originalTitle.contains("코카콜라") || originalTitle.contains("코크")) {
			return "코카콜라";
		} else if (originalTitle.contains("펩시")) {
			return "펩시";
		}

		return null; // 브랜드가 없는 경우
	}

	private Integer getCount(String originalTitle) {
		Pattern pattern = Pattern.compile("\\b\\d{1,3}\\b"); // 최대 3자리 숫자 추출
		Matcher matcher = pattern.matcher(originalTitle);

		while (matcher.find()) {
			int num = Integer.parseInt(matcher.group());
			if (num <= 100) {
				return num;
			}
		}
		return null;
	}

	private String getTaste(String originalTitle) {
		if (originalTitle.contains("라임")) {
			return "라임";
		} else if (originalTitle.contains("레몬")) {
			return "레몬";
		}

		return null;
	}
}

