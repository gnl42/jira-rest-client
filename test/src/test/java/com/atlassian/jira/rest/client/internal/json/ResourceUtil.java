/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtil {
    public static JsonObject getJsonObjectFromResource(String resourcePath) {
        final String s = getStringFromResource(resourcePath);
        try {
            return JsonParser.GSON_PARSER.parse(s).getAsJsonObject();
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }

    }

    public static JsonArray getJsonArrayFromResource(String resourcePath) {
        final String s = getStringFromResource(resourcePath);
        try {
            return JsonParser.GSON_PARSER.parse(s).getAsJsonArray();
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getStringFromResource(String resourcePath) {
        final String s;
        try {
            final InputStream is = ResourceUtil.class.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new IOException("Cannot open resource [" + resourcePath + "]");
            }
            s = IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s;
    }
}
