# ──────────────────────────────────────────────────────────────────────────────
# Karate Automation Framework — Docker Image
# Base : maven:3.9-eclipse-temurin-25 (Ubuntu Noble)
# Tests: JUnit 5 via karate-junit5:1.5.0
# UI   : Browser is NOT installed here — connect to an external Selenium Grid.
#        Use docker-compose.yml to start the Grid + this runner together.
# ──────────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-25

WORKDIR /workspace

# ── 1. Maven dependency cache ──────────────────────────────────────────────────
# pom.xml is copied alone so Docker reuses this layer on source-only changes.
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# ── 2. Project sources ─────────────────────────────────────────────────────────
COPY testng.xml Config.properties ./
COPY src ./src

# ── 3. Compile test sources ────────────────────────────────────────────────────
RUN mvn -B -o test-compile

# ── 4. Default command ─────────────────────────────────────────────────────────
# Runs all five runners against the dev API environment, local Chrome mode.
# docker-compose.yml overrides this with -Dwebdriver.remote=true so Karate
# connects to the Selenium Grid hub instead of looking for a local browser.
#
# Override examples:
#   docker run <image> mvn -o test -Dtest="practiceData.gorest.GoRestRunner"
#   docker run <image> mvn -o test -Dwebdriver.remote=true -Dgrid.url=http://host:4444
CMD ["mvn", "clean", "test", "-Dkarate.env=dev"]
