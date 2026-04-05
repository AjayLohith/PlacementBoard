package com.placementportal.config;

import com.placementportal.model.Article;
import com.placementportal.model.Company;
import com.placementportal.model.Experience;
import com.placementportal.model.JobPosting;
import com.placementportal.model.Round;
import com.placementportal.model.User;
import com.placementportal.repository.ArticleRepository;
import com.placementportal.repository.CompanyRepository;
import com.placementportal.repository.ExperienceRepository;
import com.placementportal.repository.JobPostingRepository;
import com.placementportal.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements ApplicationRunner {

    private static final String MARKER_EMAIL = "demo.priya@gmail.com";

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ExperienceRepository experienceRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (companyRepository.count() > 0) {
            log.info("Demo data skipped (companies collection is not empty)");
            return;
        }

        String demoPw = passwordEncoder.encode("DemoPass123!");

        User priya = userRepository.save(User.builder()
                .name("Priya Mehta")
                .email(MARKER_EMAIL)
                .password(demoPw)
                .build());
        User arjun = userRepository.save(User.builder()
                .name("Arjun Reddy")
                .email("demo.arjun@gmail.com")
                .password(demoPw)
                .build());
        User sneha = userRepository.save(User.builder()
                .name("Sneha Iyer")
                .email("demo.sneha@gmail.com")
                .password(demoPw)
                .build());

        Company google = companyRepository.save(Company.builder()
                .name("Google")
                .slug("google")
                .logo("https://placehold.co/80x80/e8f0fe/174ea6?text=G")
                .build());
        Company microsoft = companyRepository.save(Company.builder()
                .name("Microsoft")
                .slug("microsoft")
                .logo("https://placehold.co/80x80/e8f0fe/0078d4?text=M")
                .build());
        Company amazon = companyRepository.save(Company.builder()
                .name("Amazon")
                .slug("amazon")
                .logo("https://placehold.co/80x80/ffe8e0/ff9900?text=A")
                .build());
        Company goldman = companyRepository.save(Company.builder()
                .name("Goldman Sachs")
                .slug("goldman-sachs")
                .logo("https://placehold.co/80x80/f0f0f0/7399c6?text=GS")
                .build());

        experienceRepository.save(Experience.builder()
                .userId(priya.getId())
                .companyId(google.getId())
                .postTitle("SWE intern - summer pipeline, Bangalore")
                .interviewDate(d(2024, 6, 18))
                .interviewRounds(List.of(
                        new Round("Online assessment", "90 minutes", "HackerRank", "DS, graphs, strings",
                                "Two medium LC-style problems; third was a variant on interval merging.",
                                "Time felt tight; practice typing under pressure."),
                        new Round("Technical phone", "45 minutes", "Google Meet, shared doc",
                                "Data structures, complexity",
                                "Implement LRU with O(1) get/put; then discuss eviction and concurrency.",
                                "Clarified requirements slowly; interviewer helped narrow scope."),
                        new Round("Virtual onsite - coding", "50 minutes", "Google Meet",
                                "Problem solving",
                                "Grid BFS with weighted edges; had to explain pruning.",
                                "Drew examples on paper first; helped avoid silly bugs.")))
                .suggestions("Do timed practice on graphs and heaps. Read aloud while you code.")
                .additionalInfo("Recruiter was responsive; ~3 weeks between rounds.")
                .closingNote("Offer accepted - happy to compare notes.")
                .isApproved(true)
                .rejected(false)
                .build());

        experienceRepository.save(Experience.builder()
                .userId(arjun.getId())
                .companyId(microsoft.getId())
                .postTitle("Explore intern - Azure networking")
                .interviewDate(d(2024, 7, 3))
                .interviewRounds(List.of(
                        new Round("Screening", "60 minutes", "Codility", "C# / .NET basics",
                                "Debugging a small REST client; fix async deadlock.",
                                "Forgot ConfigureAwait in one branch."),
                        new Round("System design lite", "45 minutes", "Teams",
                                "API design for rate limiting",
                                "Token bucket vs sliding window; where to store counters in distributed cache.",
                                "Kept diagrams simple; interviewer wanted trade-offs, not buzzwords.")))
                .suggestions("Brush up on async/await in .NET and basic caching patterns.")
                .additionalInfo(null)
                .closingNote(null)
                .isApproved(true)
                .rejected(false)
                .build());

        experienceRepository.save(Experience.builder()
                .userId(sneha.getId())
                .companyId(amazon.getId())
                .postTitle("SDE-1 (new grad) - retail tech")
                .interviewDate(d(2024, 5, 22))
                .interviewRounds(List.of(
                        new Round("Bar raiser prep", "30 minutes", "Chime", "Leadership principles",
                                "Tell me about a time you disagreed with a teammate.",
                                "Used STAR; they dug into metrics."),
                        new Round("Coding", "70 minutes", "IDE + whiteboard",
                                "Arrays, design",
                                "Two-pointer on sorted array; then design a tiny inventory reservation service.",
                                "Second part was vague on purpose - ask clarifying questions early.")))
                .suggestions("Prepare 8-10 STAR stories. Amazon loves deep follow-ups.")
                .additionalInfo("Loop was 5 hours with breaks; lunch was informal chat.")
                .closingNote(null)
                .isApproved(true)
                .rejected(false)
                .build());

        experienceRepository.save(Experience.builder()
                .userId(priya.getId())
                .companyId(goldman.getId())
                .postTitle("Engineering campus - strats desk")
                .interviewDate(d(2024, 4, 10))
                .interviewRounds(List.of(
                        new Round("Quant-ish screening", "75 minutes", "Zoom",
                                "Probability, brain teasers",
                                "Expected value with biased coin; conditional probability puzzle.",
                                "Show intermediate steps even if unsure."),
                        new Round("Coding", "60 minutes", "CoderPad", "Java",
                                "Stream large CSV, aggregate by key with limited memory.",
                                "Used iterator pattern; discussed spill-to-disk briefly.")))
                .suggestions("Review discrete probability and clean Java streams.")
                .closingNote("Different from typical product companies - expect fast mental math.")
                .isApproved(true)
                .rejected(false)
                .build());

        experienceRepository.save(Experience.builder()
                .userId(arjun.getId())
                .companyId(google.getId())
                .postTitle("STEP intern - short screening write-up")
                .interviewDate(d(2025, 1, 8))
                .interviewRounds(List.of(
                        new Round("Kickoff call", "20 minutes", "Meet",
                                "Resume deep dive",
                                "Why distributed systems; one project in detail.",
                                "Nothing technical yet.")))
                .suggestions(null)
                .additionalInfo("Awaiting full loop - posting so others see early-stage format.")
                .closingNote(null)
                .isApproved(false)
                .rejected(false)
                .build());

        Instant now = Instant.now();
        jobPostingRepository.save(JobPosting.builder()
                .title("Backend intern - summer")
                .companyName("PlacementPedia Labs")
                .description("Build APIs with Spring Boot and MongoDB. Remote-friendly.")
                .applyLink("https://example.com/apply")
                .location("Remote / India")
                .jobType("intern")
                .qualificationMajor("B.Tech / B.E.")
                .qualificationBranch("CSE")
                .qualificationYear("2025")
                .experienceText("Fresher")
                .audienceTag("FRESHERS")
                .passoutYear("2025")
                .active(true)
                .build());
        jobPostingRepository.save(JobPosting.builder()
                .title("Campus ambassador")
                .companyName("PlacementPedia")
                .description("Help peers document interview experiences and run resume clinics.")
                .location("On-campus")
                .jobType("part-time")
                .qualificationMajor("Any")
                .qualificationBranch("Open")
                .qualificationYear("2024–2026")
                .experienceText("Fresher")
                .audienceTag("FRESHERS")
                .active(true)
                .build());

        articleRepository.save(Article.builder()
                .title("How interview reviews get published")
                .slug("how-interviews-are-reviewed")
                .excerpt("A short guide to moderation, quality, and what we remove.")
                .body("Submissions are reviewed by admins before they appear on company pages. "
                        + "We look for specific round details and redact personal or confidential info. "
                        + "Rejections stay out of the public queue; authors can submit a revised version.")
                .published(true)
                .publishedAt(now)
                .build());
        articleRepository.save(Article.builder()
                .title("Draft: resume checklist (admin only until published)")
                .slug("resume-checklist-draft")
                .excerpt("Internal draft - toggle publish from Admin when ready.")
                .body("1) One page for early career. 2) Impact bullets with metrics. 3) Projects with links.")
                .published(false)
                .publishedAt(null)
                .build());

        log.info("Seeded demo users (password: DemoPass123!), companies, experiences, jobs, and articles");
    }

    private static Date d(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant());
    }
}
