package org.example.kufarparser.parser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kufarparser.City;
import org.example.kufarparser.model.Apartment;
import org.example.kufarparser.repository.ApartmentRepository;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class KufarParser {

    public static String buildUrl(String city, String type) {
        String baseUrl = "https://re.kufar.by/l/%s/%s/kvartiru?cur=USD";

        if ("купить".equalsIgnoreCase(type)) {
            return String.format("https://re.kufar.by/l/%s/kupit/kvartiru?cur=USD", city);
        } else {
            return String.format(baseUrl, city, "snyat");
        }
    }
//    private static String buildUrl(String type) {
//        if ("снять".equalsIgnoreCase(type)) {
//            return "https://re.kufar.by/l/grodno/snyat/kvartiru?cur=USD&prc=r%3A0%2C200&rms=v.or%3A3%2C2&size=30";
//        } else {
//            return "https://re.kufar.by/l/grodno/kupit-kvartiru-deshevo/3k?size=30";
//        }
//    }
    public static List<Apartment> parseAndSave(String city,String type,ApartmentRepository repo) {
        String StartUrl = buildUrl(City.fromDisplayName(city),type);//метод чтобы по названию возвращал url города


        Selenide.open(StartUrl);

        List<Apartment> apartments = new ArrayList<>();
        AtomicBoolean stopParsing = new AtomicBoolean(false);
        while (true) {
            $$(By.cssSelector("a.styles_wrapper__Q06m9")).shouldBe(sizeGreaterThan(0));

            int count = $$(By.cssSelector("a.styles_wrapper__Q06m9")).size();
            System.out.println("Найдено квартир на странице: " + count);

            List<SelenideElement> elements = $$(By.cssSelector("a.styles_wrapper__Q06m9"))
                    .stream()
                    .collect(Collectors.toList());

            for (SelenideElement el : elements) {
                if (stopParsing.get()) break;

                try {
                    String url = el.getAttribute("href");
                    String id = extractIdFromUrl(url);

                    if (repo.existsById(id)) {
                        System.out.println("Найдено старое объявление");
                        stopParsing.set(true);
                        continue;
                    }

                    String priceByr = safeText(el, "span.styles_price__byr__lLSfd", "не указана");
                    String priceUsd = safeText(el, "span.styles_price__usd__HpXMa", "не указана");
                    String pricePerMeter = safeText(el, "span.styles_price__meter__37vhl", "не указано");

                    String address = safeText(el, "span.styles_address__l6Qe_", "не указан");
                    String description = safeText(el, "p.styles_body__5BrnC", "не указано");
                    String date = safeText(el, "div.styles_date__ssUVP span", "не указано");
                    String parameters = safeText(el, "div.styles_parameters__7zKlL", "");

                    String[] parts = parameters.split(",");
                    String rooms = parts.length > 0 ? (parts[0].trim().isEmpty() ? "не указано" : parts[0].trim()) : "не указано";
                    String area = parts.length > 1 ? (parts[1].trim().isEmpty() ? "не указано" : parts[1].trim()) : "не указано";
                    String floor = parts.length > 2 ? (parts[2].trim().isEmpty() ? "не указано" : parts[2].trim()) : "не указано";

                    List<String> photos = el.$$(By.cssSelector(".swiper-slide img"))
                            .stream()
                            .map(img -> img.getAttribute("src"))
                            .filter(src -> src != null && !src.isEmpty())
                            .toList();

                    Apartment apartment = new Apartment(
                            id,
                            priceByr,
                            priceUsd,
                            pricePerMeter,
                            address,
                            description,
                            date,
                            rooms,
                            area,
                            floor,
                            photos.toString(),
                            url
                    );

                    apartments.add(apartment);
                    repo.save(apartment);

                } catch (Exception e) {
                    System.err.println("Ошибка при парсинге элемента: " + e.getMessage());
                }
            }

            if ($("[data-testid='realty-pagination-next-link']").is(Condition.tagName("a"))) {
                $("[data-testid='realty-pagination-next-link']").click();
                Selenide.sleep(2000);
            } else {
                break;
            }
        }

        return apartments;
    }

    private static String extractIdFromUrl(String url) {
        if (url == null || !url.contains("/")) return "unknown";

        var pattern = java.util.regex.Pattern.compile("/(\\d+)\\?");
        var matcher = pattern.matcher(url);
        return matcher.find() ? matcher.group(1) : "unknown";
    }

    private static String safeText(SelenideElement el, String selector, String def) {
        try {
            return el.$(selector).exists() ? el.$(selector).text().trim() : def;
        } catch (Exception ex) {
            return def;
        }
    }
}