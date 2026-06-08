package com.mcqportal.config;

import com.mcqportal.entity.Question;
import com.mcqportal.entity.Role;
import com.mcqportal.entity.User;
import com.mcqportal.repository.QuestionRepository;
import com.mcqportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final String ADMIN_EMAIL = "info@forgeindiaconnect.com";
    private static final String ADMIN_PASSWORD = "Sandeep@09";
    private static final String SEED_MARKER = "A Spring MVC profile page renders a user-controlled display name. The safest Thymeleaf output is";
    private static final String[] ANSWER_SHUFFLE = {
            "C", "A", "D", "B", "B", "D", "A", "C", "D", "A",
            "C", "B", "D", "C", "A", "B", "C", "D", "B", "A",
            "D", "C", "A", "B", "A", "D", "B", "C", "B", "D",
            "A", "C", "D", "B", "C", "A", "B", "D", "C", "A",
            "D", "B", "A", "C", "B", "A", "D", "C", "A", "B"
    };
    private static final List<String> TOPICS = List.of("Cybersecurity", "Web Development", "UI/UX", "Java");

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, QuestionRepository questionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        ensureAdminAccount();
        replaceOldQuestionBank();
    }

    private void ensureAdminAccount() {
        User admin = userRepository.findByEmail(ADMIN_EMAIL)
                .or(() -> userRepository.findByEmail("admin@fic.com"))
                .orElseGet(User::new);
        admin.setFullname("FIC Administrator");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(Role.ROLE_ADMIN);
        userRepository.save(admin);
    }

    private void replaceOldQuestionBank() {
        boolean hasFullCurrentBank = TOPICS.stream().allMatch(topic -> questionRepository.countByCategoryIgnoreCase(topic) >= 50)
                && questionRepository.findByQuestionContainingIgnoreCase(SEED_MARKER, org.springframework.data.domain.PageRequest.of(0, 1)).hasContent();
        if (hasFullCurrentBank) {
            return;
        }
        seedCybersecurity();
        seedWebDevelopment();
        seedUiUx();
        seedJava();
    }

    private void seedCybersecurity() {
        seed("Cybersecurity", new String[][]{
                {SEED_MARKER, "th:text with server-side validation", "th:utext for all profile fields", "innerHTML after page load", "Raw string concatenation in script", "A"},
                {"A password reset token must survive restarts without exposing the raw token. The server should store", "The plain token in a text column", "A salted hash with expiry and single-use state", "The user's old password", "A reversible copy in a cookie", "B"},
                {"An API receives a webhook signed with HMAC. The verification code should compare signatures using", "Case-insensitive string equality", "The first eight characters only", "Constant-time byte comparison", "A browser checksum", "C"},
                {"A browser-based OAuth client needs protection against authorization-code interception. The recommended flow is", "Implicit flow", "Password grant", "Client credentials in JavaScript", "Authorization code with PKCE", "D"},
                {"A JWT remains valid after logout until expiry. A stronger session design adds", "Short-lived access tokens with refresh-token rotation", "Longer access-token lifetime", "Passwords inside the JWT", "Unsigned JWT payload checks only", "A"},
                {"A query builds SQL by appending form input into a WHERE clause. The safest replacement is", "Escaping only single quotes manually", "Parameterized queries or repository bind parameters", "Hiding the database table name", "Changing POST to GET", "B"},
                {"An authenticated cookie is used across the portal. A strong flag set is", "Readable, public, no expiry", "SameSite=None without Secure", "HttpOnly, Secure, SameSite=Lax or Strict", "Stored only in localStorage", "C"},
                {"A threat model for certificate downloads should identify", "CSS colors and border radius only", "Keyboard shortcuts only", "Server uptime only", "Assets, actors, trust boundaries, abuse cases, and mitigations", "D"},
                {"Login failures need audit value without leaking secrets. A safe log entry contains", "User id/email hash, timestamp, IP/device signal, and outcome", "Raw submitted password", "Session cookie value", "Full reset token", "A"},
                {"A build pipeline needs to reduce dependency risk. Add", "Manual jar downloads from random sites", "Dependency pinning, CVE scanning, and timely upgrades", "A CSS minifier only", "No lockfile and no scan", "B"},
                {"A user can download another user's certificate by changing the id in the URL. The missing control is", "A darker button", "A longer certificate id", "Ownership or admin authorization before returning the file", "A larger PDF page", "C"},
                {"A credential-stuffing defense should limit abuse while avoiding mass lockout. A practical control is", "Unlimited login attempts", "Plaintext password reminders", "Shared admin login for everyone", "Adaptive rate limiting with anomaly detection", "D"},
                {"Client-side validation already checks question forms. The API must still apply", "Server-side allow-list validation and authorization", "Only HTML required attributes", "Console warnings", "No validation", "A"},
                {"Production secrets in a Spring Boot app should be kept in", "JavaScript bundles", "Environment variables or a secret manager", "Committed source files", "Public templates", "B"},
                {"Different error messages for unknown email and wrong password create", "A UI grid issue", "A cache miss only", "Username enumeration risk", "A PDF rendering issue", "C"},
                {"A file upload accepts images for profile photos. Strong validation includes", "Trusting only the extension", "Storing under templates", "Skipping size checks", "MIME/signature checks, size limits, safe paths, and scanning", "D"},
                {"A download endpoint joins a folder path with user input. Path traversal is controlled by", "Normalizing and confirming the final path stays inside the allowed folder", "Adding a Bootstrap class", "Using a longer filename", "Returning a 200 for all files", "A"},
                {"The code Runtime.exec(command + userInput) creates", "A layout shift", "Command injection risk", "An immutable string", "A CSRF token", "B"},
                {"A service accepts third-party JWTs. Validation must include", "Only the email claim", "Only token length", "Signature, issuer, audience, expiry, and accepted algorithm", "Only the number of dots", "C"},
                {"New password hashing should avoid", "Argon2id", "bcrypt", "PBKDF2 with strong parameters", "MD5 or unsalted fast hashes", "D"},
                {"Certificate pinning mainly reduces", "Trust in unexpected or compromised certificate authorities", "Need for HTTPS", "Need for authorization", "Need for logs", "A"},
                {"A Java dependency check in CI is an example of", "Contrast testing", "Software composition analysis", "Image optimization", "Session fixation", "B"},
                {"A CSRF token proves that a state-changing request came from", "Any public website", "A search engine crawler", "A page/session issued by the server", "A CSS file", "C"},
                {"A session cookie never needs cross-site navigation. The strictest SameSite value is", "None", "Public", "Off", "Strict", "D"},
                {"A database service account has admin rights across every schema. This violates", "Least privilege", "Content negotiation", "Responsive design", "Lazy loading", "A"},
                {"A secret was committed to source control. The first operational response is", "Rename the variable", "Rotate the secret and remove exposure where possible", "Add a comment", "Hide the repository tab", "B"},
                {"A site must force HTTPS after the first secure visit. The relevant header is", "ETag", "Vary", "Strict-Transport-Security", "Accept", "C"},
                {"An authenticated API configures CORS. The safe policy allows", "Every origin with credentials", "Null origin always", "Any method from any site", "Only trusted origins, methods, and headers", "D"},
                {"An XML parser resolves external entities from user input. The vulnerability class is", "XXE", "IDOR only", "Open redirect only", "Clickjacking only", "A"},
                {"XML entity attacks are reduced by", "Enabling remote DTD loading", "Disabling external entities and DTD processing", "Parsing XML as raw HTML", "Increasing file size limits", "B"},
                {"A redirect endpoint accepts any next parameter. A safe design uses", "Longer URLs", "Random query names", "An allow-list of local or trusted targets", "Client-side checks only", "C"},
                {"Frame embedding of sensitive pages should be restricted using", "Content-Length", "X-Powered-By", "Server header", "CSP frame-ancestors", "D"},
                {"Two requests pass a balance check before either writes. This is", "A race condition", "A color contrast defect", "A cache hit", "A font loading issue", "A"},
                {"Duplicate certificate issuance is best prevented by", "Client-side button disabling only", "Atomic check-and-create plus a unique constraint", "A longer animation", "Manual sorting", "B"},
                {"Sensitive stored fields should use", "Plain text for easy support", "CSS masking only", "Encryption or tokenization based on risk", "URL encoding", "C"},
                {"Account takeover detection is helped by logging", "Button hover events", "Logo size", "CSS imports", "New device/location login signals", "D"},
                {"A private database should normally sit in", "A private subnet reachable only by trusted application services", "A public bucket", "The static image folder", "A client browser", "A"},
                {"Container image scanning should detect", "Button labels", "Known vulnerable packages and risky image configuration", "Certificate font sizes", "HTML comments only", "B"},
                {"A Kubernetes workload should reduce container privileges using", "privileged true", "hostNetwork everywhere", "runAsNonRoot and dropped capabilities", "Docker socket mount", "C"},
                {"A private certificate bucket is publicly readable. The defect is", "Typography mismatch", "A slow query", "A cache policy", "Broken access control", "D"},
                {"AES-GCM becomes unsafe when code reuses", "The same nonce with the same key", "Random nonces", "Authenticated ciphertext", "Strong keys", "A"},
                {"Key rotation helps", "Make keys public", "Limit blast radius after key exposure", "Avoid authentication", "Skip encryption", "B"},
                {"Before wiping an affected server, incident response should", "Delete every log", "Post credentials in chat", "Preserve evidence and isolate the host", "Disable monitoring permanently", "C"},
                {"Missing ownership checks on user records are categorized as", "Cache optimization", "Cryptographic agility", "Progressive rendering", "Broken access control", "D"},
                {"Leaked tokens cause less damage when they are", "Short-lived and scoped", "Never expiring", "Stored in URLs", "Shared across users", "A"},
                {"mTLS is used for", "CSS optimization", "Mutual certificate-based client and server authentication", "Password reset emails", "Image compression", "B"},
                {"Server-side request forgery risk appears when a server", "Renders escaped text", "Uses CSRF tokens", "Fetches arbitrary user-supplied URLs", "Hashes passwords", "C"},
                {"SSRF mitigation should include", "Checking only that the URL starts with http", "Logging and fetching anyway", "Increasing timeout", "Destination allow-lists and blocking private metadata ranges", "D"},
                {"After a password change, active sessions should be", "Invalidated or forced to re-authenticate", "Kept forever", "Printed once", "Copied into logs", "A"},
                {"An API key appears in a public JavaScript file. The practical fix is", "Make the file darker", "Move privileged calls server-side and rotate the key", "Rename the key", "Minify the file only", "B"}
        });
    }

    private void seedWebDevelopment() {
        seed("Web Development", new String[][]{
                {"A mostly static public page needs faster first contentful paint. A strong rendering strategy is", "Server-side rendering or pre-rendering", "Waiting for all API calls before HTML", "Rendering after a timeout", "Loading CSS after interaction", "A"},
                {"A create endpoint stores a new question successfully. The response status should commonly be", "200 for every case", "201 Created", "404 Not Found", "500 Internal Server Error", "B"},
                {"Images should lazy-load as they enter the viewport. The browser API suited to this is", "MutationObserver", "ResizeObserver only", "IntersectionObserver", "WebSocket", "C"},
                {"ETag-based HTTP caching improves performance by", "Replacing authorization", "Preventing every XSS issue", "Turning POST into GET", "Allowing revalidation and avoiding unnecessary downloads", "D"},
                {"A responsive image card must avoid layout shift. Reserve space with", "aspect-ratio or explicit dimensions", "Negative margins", "z-index", "Hidden overflow only", "A"},
                {"A full resource replacement endpoint should use", "GET", "PUT", "TRACE", "CONNECT", "B"},
                {"Progressive enhancement means", "Using only newest browser APIs", "Disabling forms without JavaScript", "Building a usable baseline before richer behavior", "Rendering everything in canvas", "C"},
                {"Heavy client-side calculations are freezing scroll. Move the work to", "A larger CSS file", "An alert loop", "A synchronous click handler", "A Web Worker or smaller scheduled tasks", "D"},
                {"Localized cached responses need the cache to respect Accept-Language. Add", "Vary", "Server", "Connection", "Date", "A"},
                {"An input error message needs to be announced with the field. Use", "data-id only", "aria-describedby", "role=button", "tabindex=-9", "B"},
                {"A page shows N+1 queries for rows and related data. Fix with", "One query per cell", "Browser slicing", "Joins, fetch plans, or entity graphs", "Random ordering", "C"},
                {"Generated build output should not be edited directly in this project. Avoid editing", "src/main/java", "src/main/resources/templates", "pom.xml", "target/classes", "D"},
                {"Spring Security POST forms should include", "A CSRF token", "A CSS token", "A favicon token", "A font token", "A"},
                {"Bootstrap dropdown clicks need", "bootstrap.css only", "bootstrap.bundle.min.js", "schema.sql", "pom.xml", "B"},
                {"Untrusted text in Thymeleaf should normally render with", "th:utext", "script concatenation", "th:text", "innerHTML", "C"},
                {"The JavaScript code [1,2,3].map(n => n * 2).join('-') returns", "1-2-3", "6", "NaN", "2-4-6", "D"},
                {"A shallow copy of an array named items can be created with", "[...items]", "[items]", "items.copy()", "copy items", "A"},
                {"A scroll listener that never calls preventDefault should use", "blocking true", "passive true", "sync true", "capture false only", "B"},
                {"The selector .card > .title matches", "Any nested title inside card", "Cards inside title", "Direct title children of card", "Only ids named title", "C"},
                {"Responsive equal-width columns can be created with", "position absolute", "float left", "z-index 999", "grid-template-columns: repeat(auto-fit, minmax(220px, 1fr))", "D"},
                {"Submitted JSON fails validation. A suitable status is", "400 Bad Request", "204 No Content", "301 Moved Permanently", "101 Switching Protocols", "A"},
                {"An authenticated user lacks permission for an admin route. Return", "401 for every auth case", "403 Forbidden", "200 with hidden content", "302 always", "B"},
                {"Production API errors should expose", "Full stack traces", "Raw SQL", "Structured safe messages while logging details server-side", "Database passwords", "C"},
                {"A query parameter in Spring MVC is read with", "@Entity", "@Table", "@Column", "@RequestParam", "D"},
                {"A route segment like /results/{id} is bound with", "@PathVariable", "@GeneratedValue", "@Bean", "@Service", "A"},
                {"A Thymeleaf static CSS link uses", "${/css/style.css}", "@{/css/style.css}", "#{/css/style.css}", "*{/css/style.css}", "B"},
                {"Browser storage automatically sent with matching HTTP requests is", "localStorage", "IndexedDB", "Cookie", "sessionStorage", "C"},
                {"Promise-based HTTP requests in modern JavaScript are commonly made with", "alert", "prompt", "document.write", "fetch", "D"},
                {"Inside an async function, await", "Pauses that function until the promise settles", "Deletes the promise", "Blocks every browser tab forever", "Makes CSS synchronous", "A"},
                {"User text should be inserted into a DOM node using", "innerHTML", "textContent", "document.write", "eval", "B"},
                {"Rarely used features should not bloat initial JS. Use", "Global scripts everywhere", "Duplicate bundles", "Code splitting with lazy loading", "Blocking unused libraries", "C"},
                {"Photographic web images are often best served as", "BMP", "TIFF", "ICO", "AVIF or WebP when supported", "D"},
                {"Meaningful images need alternate text through", "alt", "srcset", "loading", "width", "A"},
                {"Responsive images choose file sizes using", "onclick", "srcset", "charset", "defer", "B"},
                {"A script that waits for parsing and preserves order uses", "async", "hidden", "defer", "poster", "C"},
                {"A script that can run as soon as it downloads uses", "defer", "required", "selected", "async", "D"},
                {"Paginated admin tables should use", "Pageable repository queries", "Load all rows and slice in browser", "One query per cell", "CSS storage", "A"},
                {"The unique main content of a page belongs in", "span", "main", "b", "small", "B"},
                {"A state-changing delete action in this Spring app should use", "GET from an image", "HEAD", "POST with CSRF protection", "OPTIONS only", "C"},
                {"The media type of a response is declared by", "Set-Cookie", "Origin", "Referer", "Content-Type", "D"},
                {"Broken flows across pages are best caught by", "End-to-end browser tests", "Line count checks", "Favicon tests", "Only color checks", "A"},
                {"Spacing relative to the root font size uses", "px", "rem", "dpi", "vh only", "B"},
                {"Stacking order for positioned elements is controlled by", "line-height", "font-weight", "z-index", "border-radius", "C"},
                {"Existing event handlers are preserved when adding behavior with", "innerHTML", "setAttribute onclick", "document.write", "addEventListener", "D"},
                {"Reading a property from null in JavaScript throws", "TypeError", "SyntaxError", "RangeError only", "ReferenceError only", "A"},
                {"A file download endpoint should set", "Only page title", "Content-Type and Content-Disposition attachment", "404 on success", "Credentials in filename", "B"},
                {"Mobile navigation should provide", "Desktop hover only", "Hidden navigation", "Accessible collapse/toggler and adequate tap targets", "8px targets", "C"},
                {"Duplicate form submissions are best handled by", "Trusting button disable only", "Using GET for all writes", "Ignoring duplicates", "Server-side idempotency or redirect-after-post", "D"},
                {"Email search performance improves with", "An index or unique constraint on email", "A CSS class named email", "A longer VARCHAR only", "A refresh button", "A"},
                {"Static images and JS in Spring Boot are served from", "src/main/java/static", "src/main/resources/static", "target/logs", "pom/resources", "B"}
        });
    }

    private void seedUiUx() {
        seed("UI/UX", new String[][]{
                {"A team needs to learn where students abandon a multi-step quiz. The best method is", "Observed usability testing", "Server logs only", "Changing fonts", "Adding animation", "A"},
                {"A primary action button in a form should state", "A random brand word", "The next action and outcome", "Only an unexplained icon", "A hidden shortcut", "B"},
                {"Task efficiency is measured most directly with", "CSS file count", "Server port", "Task completion rate and time on task", "Logo size only", "C"},
                {"Destructive admin actions should provide", "Extra delay for every task", "Hidden labels", "No feedback", "Confirmation or undo for irreversible changes", "D"},
                {"An admin table becomes easier to scan with", "Clear labels, alignment, spacing, and visible actions", "All text centered randomly", "Low contrast text", "Unlabeled icons only", "A"},
                {"A dropdown is accessible when it has", "No focus outline", "Keyboard support, labels, focus states, and semantic markup", "Hover-only behavior", "A div without roles", "B"},
                {"Normal body text under WCAG AA commonly targets contrast of", "1.2 to 1", "3 to 1", "4.5 to 1", "9 to 1 for every logo", "C"},
                {"A design token is", "A login password", "A database key only", "A PDF number", "A reusable named value for color, spacing, or typography", "D"},
                {"After adding a question successfully, show", "A clear success message near the workflow", "No visible response", "A random redirect", "Only a console log", "A"},
                {"Dark mode should use", "Bright saturated cards everywhere", "A coordinated neutral palette with readable contrast", "Hidden tables", "No borders", "B"},
                {"Selecting one quiz topic from four choices works well as", "Four unrelated text boxes", "A file upload", "A select or compact topic tile group", "A date picker", "C"},
                {"Visual hierarchy helps users understand", "Only database schema", "Only server logs", "Only password strength", "Priority, grouping, and next action", "D"},
                {"An authentic certificate layout benefits from", "Balanced border, seal area, metadata, and consistent type", "Misaligned text", "Hidden certificate id", "Random line colors", "A"},
                {"Duplicate controls around one action should be replaced by", "More nearby dropdowns", "One clear control for the workflow", "Repeated buttons everywhere", "Hidden labels", "B"},
                {"Button text overflowing on mobile causes", "Better security", "Faster queries", "Reduced readability and accidental actions", "Higher conversion always", "C"},
                {"A square responsive thumbnail is maintained with", "height auto only", "z-index 1", "font-weight bold", "aspect-ratio: 1 / 1 and object-fit: cover", "D"},
                {"An open dropdown button should expose", "aria-expanded true", "aria-hidden true", "aria-random open", "aria-color blue", "A"},
                {"A modal dialog should", "Lose focus", "Move focus inside and trap it until closed", "Make Tab close the browser", "Delete user data on Escape", "B"},
                {"A long-running submit action should show", "No change", "A hidden spinner only", "Loading/disabled state with clear feedback", "Random color flashes", "C"},
                {"A maintainable token name looks like", "blue1maybe", "bigThing", "random-color-now", "color.action.primary", "D"},
                {"Touch icon buttons should have", "Min dimensions near 44px", "4px font", "Negative padding", "display none", "A"},
                {"A delete button should use clear microcopy such as", "Do magic", "Delete question", "Click here", "Continue maybe", "B"},
                {"Category pass rates are compared clearly using", "An unlabeled pie with many slices", "A decorative image", "A bar chart with labeled axes", "Random animation", "C"},
                {"A selected MCQ option should have", "Hover color only", "Hidden input only", "Page title changes only", "Persistent selected state with checked radio and contrast", "D"},
                {"Steps, pain points, and emotions across a workflow are captured in", "A journey map", "A stack trace", "A database index", "A CSS reset", "A"},
                {"A neutral usability prompt is", "Click the green certificate button now", "Show me how you would find your certificate", "This is easy, right", "Use the correct dropdown", "B"},
                {"Keyboard accessibility is tested by", "Logo inspection only", "Database refresh only", "Tab, Shift+Tab, Enter, Space, and Escape navigation", "Image format checks only", "C"},
                {"Error messages should rely on", "Red color only", "Low contrast text", "Hover-only text", "Text/icon plus color", "D"},
                {"An empty question table should show", "A helpful message and action to add a question", "A blank page", "Only a 500 error", "A decorative banner only", "A"},
                {"Dense admin tables on mobile should use", "6px text", "Horizontal scroll or stacked rows with labels", "Overlapping controls", "Hidden data with no access", "B"},
                {"Visible keyboard focus can be improved with", "outline none always", "cursor none", "focus-visible outline styling", "display none on hover", "C"},
                {"Four quiz categories are best shown as", "Two duplicate dropdowns", "A hidden route only", "A modal per topic", "A small topic tile grid or one labeled selector", "D"},
                {"Certificate verification needs visible", "Student name, date, certificate id, score/status", "Only a color", "No metadata", "Only first name", "A"},
                {"Grouping dashboard stats uses", "Obfuscation", "Similarity and proximity", "Compression only", "Random placement", "B"},
                {"Quiz question readability improves through", "All caps paragraphs", "Tiny text", "Moderate line length and clear hierarchy", "Five fonts per question", "C"},
                {"A dashboard loading state should", "Jump after every value", "Stay blank", "Use blocking alerts", "Keep layout stable with concise loading or skeleton states", "D"},
                {"Repeated menu openings and backtracking indicate", "Selector confusion", "Maven speed", "Database password length", "Image count", "A"},
                {"Clear topic-selection copy says", "Thing", "Choose quiz topic", "Data option", "Click random", "B"},
                {"After validation errors in Save Question, the form should", "Clear everything", "Log out the admin", "Keep entered data and show field-level errors", "Download a PDF", "C"},
                {"Keyboard operation belongs to the WCAG principle", "Encrypted", "Compiled", "Normalized", "Operable", "D"},
                {"Long option text can be kept inside cards with", "overflow-wrap: anywhere", "nowrap everywhere", "position fixed", "opacity 0", "A"},
                {"Clickable tiles need", "Disabled styling", "Pointer cursor, hover/focus state, and link semantics", "Hidden labels", "No contrast change", "B"},
                {"The highest-priority design review issue is", "A subjective shade preference", "A minor icon difference", "Text overlapping controls and blocking completion", "A 1px spacing preference", "C"},
                {"Question entry is easier when fields are", "Scattered randomly", "Labels hidden", "Placeholders only", "Grouped as category, question, options, and answer", "D"},
                {"A passing result badge should read", "PASS", "Good maybe", "Green", "Done-ish", "A"},
                {"Current navigation state can be exposed with", "aria-hidden true", "aria-current page", "aria-live off", "aria-busy false", "B"},
                {"After certificate download starts, the interface should", "Redirect blank", "Delete context", "Keep context and show/download through browser behavior", "Show unrelated quiz options", "C"},
                {"A severe dark-mode problem is", "Neutral dark backgrounds", "Visible borders", "Readable table text", "Bright saturated cards with low readability", "D"},
                {"Admin answer keys are useful because", "Admins can verify correctness before publishing", "They decorate the page", "They replace auth", "They hide categories", "A"},
                {"Spacing that scales from the root font size uses", "ms", "rem", "dpi", "hz", "B"}
        });
    }

    private void seedJava() {
        seed("Java", new String[][]{
                {"A custom resource must close automatically in try-with-resources. It should implement", "AutoCloseable", "Serializable", "Runnable", "Comparator", "A"},
                {"A service may return no user for an email lookup. A clear return type is", "String only", "Optional<User>", "int", "Thread", "B"},
                {"Unique elements must preserve insertion order. Use", "HashSet", "ArrayList", "LinkedHashSet", "PriorityQueue", "C"},
                {"A flag written by one thread must be visible to another. Use", "static", "final only", "transient", "volatile", "D"},
                {"In a stream pipeline, map is", "Lazy until a terminal operation runs", "A terminal operation", "Always parallel", "A database command", "A"},
                {"A Spring business component is commonly marked with", "@Table", "@Service", "@Column", "@Id", "B"},
                {"Required dependencies are clearest with", "Field injection everywhere", "Global static state", "Constructor injection", "Random lookup", "C"},
                {"Recoverable checked conditions should use", "OutOfMemoryError", "StackOverflowError", "NoClassDefFoundError", "A checked exception extending Exception", "D"},
                {"A JPA primary key field uses", "@Id", "@Controller", "@Bean", "@RequestParam", "A"},
                {"equals and hashCode must stay consistent because", "They change CSS", "Hash-based collections rely on the contract", "They encrypt values", "They start Spring", "B"},
                {"A thread-safe date-only value type is", "Date", "Calendar", "LocalDate", "SimpleDateFormat", "C"},
                {"A service method annotated @Transactional runs", "Without database access", "In a new browser tab", "Only as CSS", "Inside a transaction boundary", "D"},
                {"Visibility limited to the same class uses", "private", "protected", "public", "default package plus all subclasses", "A"},
                {"Compilation without tests can be run with", "java pom.xml", "mvn -DskipTests compile", "mvn clean css", "spring html", "B"},
                {"Java records are mainly", "Mutable entities by default", "Servlet filters", "Concise immutable data carriers", "SQL triggers", "C"},
                {"The code List.of(1,2,3).stream().filter(n -> n > 1).count() returns", "1", "3", "Compilation error", "2", "D"},
                {"An immutable list can be created with", "List.of(\"A\", \"B\")", "new ArrayList<>() only", "Arrays.asList always immutable", "Collections.emptyMap()", "A"},
                {"Optional.empty().orElseGet(() -> \"x\") returns", "An empty Optional", "x", "null", "It never compiles", "B"},
                {"Stream elements can be grouped by key using", "Collectors.joiningBy", "Collectors.mapBy", "Collectors.groupingBy", "Collectors.sortingBy", "C"},
                {"The lambda s -> s.trim() can be written as", "String.trim()", "s::String", "trim::String", "String::trim", "D"},
                {"A class that cannot be subclassed is declared", "final", "abstract", "public only", "static import", "A"},
                {"A subclass calls a parent implementation with", "this", "super", "parent", "base()", "B"},
                {"Integer.valueOf(10).equals(10) evaluates to", "false", "Compilation error", "true", "NullPointerException", "C"},
                {"Java String values are", "Always mutable", "Invalid map keys", "ASCII only", "Immutable", "D"},
                {"Repeated string concatenation in loops should use", "StringBuilder", "Scanner", "Random", "String + only", "A"},
                {"An asynchronous computation can be represented by", "LocalDate", "CompletableFuture", "BigDecimal", "PathMatcher", "B"},
                {"A high-read concurrent map should use", "HashMap", "TreeMap", "ConcurrentHashMap", "LinkedHashMap", "C"},
                {"synchronized protects", "All database rows", "Every JVM", "CSS rendering", "A monitor-owned critical section", "D"},
                {"A controlled worker thread pool is managed by", "ExecutorService", "System.gc", "ClassLoader", "Formatter", "A"},
                {"Exact currency arithmetic should use", "double", "BigDecimal", "float", "Random", "B"},
                {"An unchecked exception example is", "IOException", "SQLException", "IllegalArgumentException", "FileNotFoundException", "C"},
                {"Many answers linked to one question use", "@OneToOne", "@ElementCollection", "@Transient", "@ManyToOne", "D"},
                {"A non-persisted JPA field uses", "@Transient", "@Id", "@Column", "@Table", "A"},
                {"Spring property values can be injected with", "@Entity", "@Value", "@PathVariable", "@GeneratedValue", "B"},
                {"Persistence components commonly use", "@Controller", "@Table", "@Repository", "@Id", "C"},
                {"A string field must reject null, empty, and spaces-only values. Use", "@NotNull only", "@Size only", "@Email only", "@NotBlank", "D"},
                {"Email format validation uses", "@Email", "@Address", "@MailOnly", "@PatternEmail", "A"},
                {"Form submission in Spring MVC is commonly handled with", "@GetMapping only", "@PostMapping", "@Entity", "@Column", "B"},
                {"Mutable internal lists are safely exposed by returning", "The internal list", "A public field", "List.copyOf(internalList)", "A static global", "C"},
                {"A stream operation that can short-circuit on a match is", "collect", "forEach", "sorted", "anyMatch", "D"},
                {"A stream map operation", "Transforms each element into another value", "Only reads first element", "Only catches exceptions", "Only starts threads", "A"},
                {"Nested streams can be flattened with", "map only", "flatMap", "peek only", "sorted", "B"},
                {"Date and time without timezone is represented by", "LocalDate", "Instant only", "LocalDateTime", "YearMonth only", "C"},
                {"A machine timestamp on the UTC timeline is", "LocalDate", "MonthDay", "Period", "Instant", "D"},
                {"A valid try-with-resources form is", "try (var in = Files.newInputStream(path)) { }", "try var in = path { }", "try (String s = \"x\") { }", "try resource Files { }", "A"},
                {"Generics provide", "Runtime classes for every type", "Compile-time type safety with type erasure", "Primitive-only typing", "Automatic tests", "B"},
                {"A nullable numeric JPA field should use", "int", "byte", "Integer", "char", "C"},
                {"A null-safe not-blank check is", "!s.isBlank()", "s.length() > 0", "s.trim() != null", "s != null && !s.isBlank()", "D"},
                {"A Spring Boot entry class uses", "@SpringBootApplication", "@Main", "@RunJava", "@ApplicationOnly", "A"},
                {"CRUD and paging repository methods come from", "Runnable", "JpaRepository", "Serializable", "Comparator", "B"}
        });
    }

    private void seed(String category, String[][] rows) {
        List<Question> existingQuestions = questionRepository.findTop50ByCategoryIgnoreCaseOrderByIdAsc(category);
        for (int index = 0; index < rows.length; index++) {
            String[] row = rows[index];
            Question question = index < existingQuestions.size() ? existingQuestions.get(index) : new Question();
            saveQuestion(question, category, row, shuffledAnswerFor(category, index));
        }
    }

    private String shuffledAnswerFor(String category, int index) {
        int offset = Math.floorMod(category.hashCode(), ANSWER_SHUFFLE.length);
        return ANSWER_SHUFFLE[(index + offset) % ANSWER_SHUFFLE.length];
    }

    private void saveQuestion(Question question, String category, String[] row, String targetAnswer) {
        String[] options = {row[1], row[2], row[3], row[4]};
        int originalCorrectIndex = "ABCD".indexOf(row[5]);
        int targetCorrectIndex = "ABCD".indexOf(targetAnswer);
        String correctOption = options[originalCorrectIndex];
        options[originalCorrectIndex] = options[targetCorrectIndex];
        options[targetCorrectIndex] = correctOption;

        question.setCategory(category);
        question.setQuestion(row[0]);
        question.setOptionA(options[0]);
        question.setOptionB(options[1]);
        question.setOptionC(options[2]);
        question.setOptionD(options[3]);
        question.setCorrectAnswer(targetAnswer);
        questionRepository.save(question);
    }
}
