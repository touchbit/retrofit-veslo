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

import okhttp3.MediaType;

import java.nio.charset.Charset;
import java.util.Objects;

public class ContentType {

    private final String type;
    private final String subtype;
    private final String charset;

    public ContentType() {
        this(null);
    }

    public ContentType(MediaType mediaType) {
        if (mediaType == null) {
            this.type = null;
            this.subtype = null;
            this.charset = null;
        } else {
            this.type = mediaType.type().toLowerCase();
            this.subtype = mediaType.subtype().toLowerCase();
            final Charset charset = mediaType.charset();
            if (charset == null) {
                this.charset = null;
            } else {
                this.charset = charset.toString().toLowerCase();
            }
        }
    }

    public ContentType(String type, String subtype) {
        this(type, subtype, null);
    }

    public ContentType(String type, String subtype, String charset) {
        this.type = type == null ? null : type.toLowerCase();
        this.subtype = subtype == null ? null : subtype.toLowerCase();
        this.charset = charset == null ? null : charset.toLowerCase();
    }

    public MediaType getMediaType() {
        if (type == null || subtype == null) {
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

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        String charset = (getCharset() == null ? "" : "; charset=" + getCharset());
        return (getType() + "/" + getSubtype() + charset);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return this.getType() == null && this.getSubtype() == null && this.getCharset() == null;
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
