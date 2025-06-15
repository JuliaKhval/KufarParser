package org.example.kufarparser.parser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.example.kufarparser.model.Apartment;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;

public class KufarParser {

    public static List<Apartment> parseApartments() {
        String baseUrl = "https://re.kufar.by/l/grodno/snyat/kvartiru/3k?cur=USD";
        open(baseUrl);

        List<Apartment> allApartments = new ArrayList<>();

        while (true) {
            $$(By.cssSelector("a.styles_wrapper__Q06m9")).shouldBe(sizeGreaterThan(0));
            List<Apartment> page = parseCurrentPage();
            allApartments.addAll(page);

            if ($("[data-testid='realty-pagination-next-link']").is(Condition.tagName("a"))) {
                $("[data-testid='realty-pagination-next-link']").click();
                Selenide.sleep(2000);
            } else if ($("[data-testid='realty-pagination-next-link']").is(Condition.tagName("span"))) {
                break;
            } else {
                break;
            }
        }

        return allApartments;
    }

    private static List<Apartment> parseCurrentPage() {
        List<Apartment> apartments = new ArrayList<>();

        $$(By.cssSelector("a.styles_wrapper__Q06m9")).forEach(el -> {
            try {
                String priceByr = el.$("span.styles_price__byr__lLSfd").exists()
                        ? el.$("span.styles_price__byr__lLSfd").text() : "не указано";

                String priceUsd = el.$("span.styles_price__usd__HpXMa").exists()
                        ? el.$("span.styles_price__usd__HpXMa").text() : "не указано";

                String pricePerMeter = el.$("span.styles_price__meter__37vhl").exists()
                        ? el.$("span.styles_price__meter__37vhl").text() : "не указано";

                String address = el.$("span.styles_address__l6Qe_").exists()
                        ? el.$("span.styles_address__l6Qe_").text() : "не указано";

                String description = el.$("p.styles_body__5BrnC").exists()
                        ? el.$("p.styles_body__5BrnC").text() : "не указано";

                String date = el.$("div.styles_date__ssUVP span").exists()
                        ? el.$("div.styles_date__ssUVP span").text() : "не указано";

                String parameters = el.$("div.styles_parameters__7zKlL").exists()
                        ? el.$("div.styles_parameters__7zKlL").text() : "";

                String[] parts = parameters.split(",");
                String rooms = parts.length > 0 ? (parts[0].trim().isEmpty() ? "не указано" : parts[0].trim()) : "не указано";
                String area = parts.length > 1 ? (parts[1].trim().isEmpty() ? "не указано" : parts[1].trim()) : "не указано";
                String floor = parts.length > 2 ? (parts[2].trim().isEmpty() ? "не указано" : parts[2].trim()) : "не указано";

                List<String> photos = el.$$(By.cssSelector(".swiper-slide img"))
                        .stream()
                        .map(img -> img.getAttribute("src"))
                        .filter(src -> src != null && !src.trim().isEmpty())
                        .toList();

                String url = el.getAttribute("href");

                apartments.add(new Apartment(
                        priceByr, priceUsd, pricePerMeter,
                        address, description, date,
                        rooms, area, floor, photos, url
                ));

            } catch (Exception e) {
                System.err.println("Ошибка при парсинге: " + e.getMessage());
            }
        });

        return apartments;
    }
}