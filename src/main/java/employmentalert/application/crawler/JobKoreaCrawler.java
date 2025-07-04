package employmentalert.application.crawler;

import employmentalert.application.crawler.dto.JobKoreaPostingInfo;
import employmentalert.global.exception.EmploymentAlertException;
import employmentalert.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JobKoreaCrawler {

    /**
     * 채용 공고 목록 크롤링하여 반환
     */
    public List<JobKoreaPostingInfo> getJobLinks() {
        List<JobKoreaPostingInfo> jobList = new ArrayList<>();

        try {
            Document document = fetchDocument();
            // 채용공고 카드 요소 선택
            Elements jobCards = document.select("div[data-sentry-component=CardCommon]");

            for (Element card : jobCards) {
                // 각 카드에서 공고 정보를 추출
                JobKoreaPostingInfo info = extractJobInfo(card);

                // 정보 추출에 실패하지 않았으면 리스트에 추가
                if (info != null) jobList.add(info);
            }

        } catch (Exception exception) {
            throw new EmploymentAlertException(ErrorCode.CRAWLER_ERROR);

        } finally {
            log.info("찾은 공고 수 : {}", jobList.size());
        }

        return jobList;
    }

    /**
     * 크롤링할 페이지의 Document를 Jsoup으로 로드
     */
    private Document fetchDocument() throws IOException {
        return Jsoup.connect("https://www.jobkorea.co.kr/Search/?ord=RegDtDesc&tabType=recruit&stext=java")
                .userAgent("Mozilla/5.0")
                .get();
    }

    /**
     * 공고 카드 Element에서 채용 공고 상세 정보를 추출
     * - 필요한 태그나 정보가 없으면 null 반환
     */
    private JobKoreaPostingInfo extractJobInfo(Element card) {
        Elements anchors = card.select("a");

        if (anchors.size() < 2) {
            log.warn("공고 제목 a 태그가 부족합니다.");
            return null;
        }

        // 첫 번째 <a> 태그 내 회사명 <span> 추출
        Element firstAnchorSpan = anchors.getFirst().selectFirst("span");
        if (firstAnchorSpan == null) {
            log.warn("회사명 span 태그를 찾을 수 없습니다.");
            return null;
        }
        String company = firstAnchorSpan.text().trim();

        // 두 번째 <a> 태그 내 공고 제목 <span> 추출
        Element secondAnchorSpan = anchors.get(1).selectFirst("span");
        if (secondAnchorSpan == null) {
            log.warn("공고 제목 span 태그를 찾을 수 없습니다.");
            return null;
        }
        String title = secondAnchorSpan.text().trim();

        // 공고 URL
        String href = anchors.get(1).absUrl("href");

        // 부가정보(경력, 학력, 고용형태, 지역, 마감일) 추출
        Elements infoSpans = card.select("div[class*=Flex_gap_space16] > span");
        String career = null, education = null, employmentType = null, region = null, deadline = null;

        if (infoSpans.size() >= 5) {
            career = infoSpans.get(0).text().trim();
            education = infoSpans.get(1).text().trim();
            employmentType = infoSpans.get(2).text().trim();
            region = infoSpans.get(3).text().trim();
            deadline = infoSpans.get(4).text().trim();
        }

        return new JobKoreaPostingInfo(company, title, href, career, education, employmentType, region, deadline);
    }

}
