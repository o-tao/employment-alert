package employmentalert.application.crawler;

import employmentalert.application.crawler.dto.JobKoreaPostingInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JobKoreaCrawler {

    public List<JobKoreaPostingInfo> getJobLinks() {
        List<JobKoreaPostingInfo> jobList = new ArrayList<>();

        try {
            String url = "https://www.jobkorea.co.kr/Search/?ord=RegDtDesc&tabType=recruit&stext=java";
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            Elements jobCards = document.select("div[data-sentry-component=CardCommon]");

            for (Element card : jobCards) {
                // card 내 <a> 태그
                Elements anchors = card.select("a");
                if (anchors.size() < 2) {
                    log.warn("공고 제목 a 태그가 부족합니다.");
                    continue;
                }

                // 첫 번째 <a> 태그 안의 <span> → 회사명
                Element firstAnchorSpan = anchors.getFirst().selectFirst("span");
                if (firstAnchorSpan == null) {
                    log.warn("회사명 span 태그를 찾을 수 없습니다.");
                    continue;
                }
                String company = firstAnchorSpan.text().trim();

                // 두 번째 <a> 태그 안의 <span> → 공고 제목
                Element secondAnchorSpan = anchors.get(1).selectFirst("span");
                if (secondAnchorSpan == null) {
                    log.warn("공고 제목 span 태그를 찾을 수 없습니다.");
                    continue;
                }
                String title = secondAnchorSpan.text().trim();

                // 두번째 <a> 태그 안의 공고 url
                String href = anchors.get(1).absUrl("href");

                // 부가정보 추출: 경력 / 고용형태 / 지역 / 마감일 등
                Elements infoSpans = card.select("div[class*=Flex_gap_space16] > span");
                String career = null, education = null, employmentType = null, region = null, deadline = null;

                if (infoSpans.size() >= 5) {
                    career = infoSpans.get(0).text().trim();          // 경력사항
                    education = infoSpans.get(1).text().trim();       // 학력사항
                    employmentType = infoSpans.get(2).text().trim();  // 고용형태
                    region = infoSpans.get(3).text().trim();          // 지역
                    deadline = infoSpans.get(4).text().trim();        // 마감일
                }

                jobList.add(new JobKoreaPostingInfo(company, title, href, career, education, employmentType, region, deadline));
            }

        } catch (Exception e) {
            log.error("getJobLinks 크롤링 실패", e);

        } finally {
            log.info("찾은 공고 수 : {}", jobList.size());
        }

        return jobList;
    }

}
