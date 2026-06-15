FROM maven:3.9-eclipse-temurin-25

WORKDIR /workspace

# Copy Maven metadata first so dependency downloads are cached between source edits.
COPY pom.xml testng.xml ./

# Download project dependencies into the image so the later build can run offline.
RUN mvn -B dependency:go-offline
RUN mvn -B dependency:get -Dartifact=org.apache.maven.surefire:surefire-testng:3.5.5

COPY Config.properties sample_data.csv ./
COPY src ./src

# Compile the framework during image building, but do not run API tests here.
RUN mvn -B -o -DskipTests test

# Run the TestNG suite when the container starts.
CMD ["mvn", "-o", "test", "-Dsurefire.suiteXmlFiles=testng.xml"]
