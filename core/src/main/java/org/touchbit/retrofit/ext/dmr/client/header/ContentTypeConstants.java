/*
 * Copyright 2021 Shaburov Oleg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.touchbit.retrofit.ext.dmr.client.header;

import org.touchbit.retrofit.ext.dmr.exception.UtilityClassException;

public class ContentTypeConstants {

    public static final String UTF8 = "utf-8";
    public static final String APP_TYPE = "application";
    public static final String TXT_TYPE = "text";
    public static final String JSON_SUBTYPE = "json";
    public static final String XML_SUBTYPE = "xml";
    public static final String PLAIN_SUBTYPE = "plain";
    public static final String HTML_SUBTYPE = "html";
    public static final String OCTET_SUBTYPE = "octet-stream";
    public static final String FORM_URLENCODED_SUBTYPE = "x-www-form-urlencoded";
    public static final ContentType APP_JSON = new ContentType(APP_TYPE, JSON_SUBTYPE);
    public static final ContentType APP_JSON_UTF8 = new ContentType(APP_TYPE, JSON_SUBTYPE, UTF8);
    public static final ContentType APP_XML = new ContentType(APP_TYPE, XML_SUBTYPE);
    public static final ContentType APP_XML_UTF8 = new ContentType(APP_TYPE, XML_SUBTYPE, UTF8);
    public static final ContentType APP_FORM_URLENCODED = new ContentType(APP_TYPE, FORM_URLENCODED_SUBTYPE);
    public static final ContentType APP_FORM_URLENCODED_UTF8 = new ContentType(APP_TYPE, FORM_URLENCODED_SUBTYPE, UTF8);
    public static final ContentType APP_OCTET_STREAM = new ContentType(APP_TYPE, OCTET_SUBTYPE);
    public static final ContentType TEXT_JSON = new ContentType(TXT_TYPE, JSON_SUBTYPE);
    public static final ContentType TEXT_JSON_UTF8 = new ContentType(TXT_TYPE, JSON_SUBTYPE, UTF8);
    public static final ContentType TEXT_XML = new ContentType(TXT_TYPE, XML_SUBTYPE);
    public static final ContentType TEXT_XML_UTF8 = new ContentType(TXT_TYPE, XML_SUBTYPE, UTF8);
    public static final ContentType TEXT_PLAIN = new ContentType(TXT_TYPE, PLAIN_SUBTYPE);
    public static final ContentType TEXT_PLAIN_UTF8 = new ContentType(TXT_TYPE, PLAIN_SUBTYPE, UTF8);
    public static final ContentType TEXT_HTML = new ContentType(TXT_TYPE, HTML_SUBTYPE);
    public static final ContentType TEXT_HTML_UTF8 = new ContentType(TXT_TYPE, HTML_SUBTYPE, UTF8);
    public static final ContentType NULL = new ContentType(null, null);

    /**
     * Utility class
     */
    private ContentTypeConstants() {
        throw new UtilityClassException();
    }

}
