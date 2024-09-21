/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.url;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class RsURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private static final String PREFIX = "sun.net.www.protocol";
    private final List<String> notContains = Arrays.asList("jar", "file", "http", "https");

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        for (String tempProtocol : notContains) {
            if (tempProtocol.equals(StrUtil.sub(protocol, 0, tempProtocol.length()))) {
                return null;
            }
        }
        if (ResourceFileSystem.URI_SCHEMA.getScheme().equals(protocol)) {
            return new RsURLStreamHandler();
        }
        try {
            Class.forName("org.apache.hadoop.fs.FsUrlStreamHandlerFactory");
        } catch (Throwable e) {
            return null;
        }
        String name = PREFIX + "." + protocol + ".Handler";
        try {
            @SuppressWarnings("deprecation")
            Object o = Class.forName(name).newInstance();
            return (URLStreamHandler) o;
        } catch (ClassNotFoundException x) {
            // ignore
        } catch (Exception e) {
            // For compatibility, all Exceptions are ignored.
            // any number of exceptions can get thrown here
        }
        return null;
    }
}
