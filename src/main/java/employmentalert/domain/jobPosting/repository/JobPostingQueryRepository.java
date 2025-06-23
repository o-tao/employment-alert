package employmentalert.domain.jobPosting.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static employmentalert.domain.jobPosting.QJobPosting.jobPosting;

@Repository
@RequiredArgsConstructor
public class JobPostingQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Set<String> existingUrls(List<String> urls) {
        return urls.isEmpty()
                ? Set.of()
                : new HashSet<>(
                jpaQueryFactory
                        .select(jobPosting.url)
                        .from(jobPosting)
                        .where(jobPosting.url.in(urls))
                        .fetch()
        );
    }
}
