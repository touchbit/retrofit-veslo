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

package veslo.client.header;

import okhttp3.MediaType;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.util.Objects;

public class ContentType {

    public static final ContentType NULL = new ContentType(null);

    private final String type;
    private final String subtype;
    private final String charset;

    public ContentType(String type, String subtype) {
        this(type, subtype, null);
    }

    public ContentType(String type, String subtype, String charset) {
        if ((type != null && subtype == null) || (type == null && subtype != null)) {
            throw new IllegalArgumentException("Type and subtype can only be null at the same time");
        }
        if (type != null && type.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter 'type' cannot be blank.");
        }
        if (subtype != null && subtype.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter 'subtype' cannot be blank.");
        }
        if (charset != null && charset.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter 'charset' cannot be blank.");
        }
        if (type == null) {
            this.type = null;
            this.subtype = null;
            this.charset = null;
        } else {
            this.type = type.toLowerCase();
            this.subtype = subtype.toLowerCase();
            this.charset = charset == null ? null : charset.toLowerCase();
        }
    }

    public ContentType(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            this.type = null;
            this.subtype = null;
            this.charset = null;
        } else {
            this.type = mediaType.type().toLowerCase();
            this.subtype = mediaType.subtype().toLowerCase();
            final Charset mediaTypeCharset = mediaType.charset();
            if (mediaTypeCharset == null) {
                this.charset = null;
            } else {
                this.charset = mediaTypeCharset.toString().toLowerCase();
            }
        }
    }

    @Nullable
    public MediaType getMediaType() {
        if (isNull()) {
            return null;
        }
        return MediaType.get(toString());
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isNull() {
        return type == null;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        if (isNull()) {
            return "null";
        }
        String charsetPostfix = (getCharset() == null ? "" : "; charset=" + getCharset());
        return (getType() + "/" + getSubtype() + charsetPostfix);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return isNull();
        }
        if (obj instanceof ContentType) {
            ContentType contentType = (ContentType) obj;
            return Objects.equals(this.getType(), contentType.getType()) &&
                    Objects.equals(this.getSubtype(), contentType.getSubtype()) &&
                    Objects.equals(this.getCharset(), contentType.getCharset());
        }
        return false;
    }

}
