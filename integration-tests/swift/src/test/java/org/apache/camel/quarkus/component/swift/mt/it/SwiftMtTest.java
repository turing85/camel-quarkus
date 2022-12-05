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
package org.apache.camel.quarkus.component.swift.mt.it;

import java.nio.file.Files;
import java.nio.file.Paths;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class SwiftMtTest {

    @ParameterizedTest
    @ValueSource(strings = { "", "dsl" })
    void testUnmarshal(String mode) throws Exception {
        RestAssured.given() //
                .contentType(ContentType.TEXT)
                .body(Files.readAllBytes(Paths.get("src/test/resources/mt/message1.txt")))
                .post(String.format("/swift-mt/unmarshal%s", mode)) //
                .then()
                .body(equalTo("515"))
                .statusCode(200);
    }

    @Test
    void testMarshal() throws Exception {
        RestAssured.given() //
                .contentType(ContentType.TEXT)
                .body(Files.readAllBytes(Paths.get("src/test/resources/mt/message2.txt")))
                .post("/swift-mt/marshal") //
                .then()
                .body(equalTo("103"))
                .statusCode(200);
    }

    @Test
    void testMarshalJson() throws Exception {
        RestAssured.given() //
                .contentType(ContentType.TEXT)
                .body(Files.readAllBytes(Paths.get("src/test/resources/mt/message2.txt")))
                .post("/swift-mt/marshalJson") //
                .then()
                .body(equalTo(Files.readString(Paths.get("src/test/resources/mt/message2.json")).trim()))
                .statusCode(200);
    }

}
