/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.file.it;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.commons.io.FileUtils;

/**
 * Ensures that all test files are created with expected content before camel routes starts to consume them.
 */
public class NonFlakyFileTestResource implements QuarkusTestResourceLifecycleManager {

    static final Path TEST_FILES_FOLDER = Paths.get("target", "test-files");

    static final String FILTER_NON_SKIPPED_FILE_NAME = "non-skipped-file";
    static final String FILTER_NON_SKIPPED_FILE_CONTENT = FILTER_NON_SKIPPED_FILE_NAME + "-CONTENT";
    static final String IDEMPOTENT_FILE_NAME = "moved-back-read-once";
    static final String IDEMPOTENT_FILE_CONTENT = IDEMPOTENT_FILE_NAME + "-CONTENT";
    static final String POLL_ENRICH_FILE_NAME = "poll-enrich-file";
    static final String POLL_ENRICH_FILE_CONTENT = POLL_ENRICH_FILE_NAME + "-CONTENT";
    static final String QUARTZ_SCHEDULED_FILE_NAME = "quartz-schedule-file";
    static final String QUARTZ_SCHEDULED_FILE_CONTENT = QUARTZ_SCHEDULED_FILE_NAME + "-CONTENT";
    static final String FILE_CREATION_FOLDER = "file-creation";
    static final String FILE_CREATION_FILE_NAME = "should-be-created";
    static final String FILE_CREATION_FILE_CONTENT = FILE_CREATION_FILE_NAME + "-CONTENT";

    private final List<Path> createdTestFiles = new ArrayList<Path>();

    @Override
    public Map<String, String> start() {
        try {
            FileUtils.deleteDirectory(TEST_FILES_FOLDER.toFile());

            createTestFile("filter", FILTER_NON_SKIPPED_FILE_NAME);
            createTestFile("filter", "skipped_file");

            createTestFile("idempotent", IDEMPOTENT_FILE_NAME);

            createTestFile("poll-enrich", POLL_ENRICH_FILE_NAME);

            createTestFile("quartz-scheduled", QUARTZ_SCHEDULED_FILE_NAME);

            // Do not use  createTestFile("file-creation"... as it is already reserved by the createFileShouldSucceed test

            ensureAllTestFilesCreatedWithExpectedContent();
        } catch (Exception ex) {
            throw new RuntimeException("Problem while initializing test files", ex);
        }
        return null;
    }

    private void createTestFile(String testName, String testFileName) throws IOException {
        Path testFilePath = TEST_FILES_FOLDER.resolve(Paths.get(testName, testFileName));
        FileUtils.writeStringToFile(testFilePath.toFile(), testFileName + "-CONTENT", StandardCharsets.UTF_8);
        createdTestFiles.add(testFilePath);
    }

    private void ensureAllTestFilesCreatedWithExpectedContent() {
        for (Path path : createdTestFiles) {
            try {
                String content = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
                String expectedContent = path.toFile().getName() + "-CONTENT";

                if (!expectedContent.equals(content)) {
                    String format = "The NonFlakyFileTestResource failed to create the test file %s with expected content";
                    throw new RuntimeException(String.format(format, path));
                }
            } catch (IOException ioex) {
                String format = "The NonFlakyFileTestResource has exception while creating the test file %s with expected content";
                throw new RuntimeException(String.format(format, path), ioex);
            }
        }
    }

    @Override
    public void stop() {
    }

}
