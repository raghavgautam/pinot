/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pinot.segment.local.dedup;

import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pinot.spi.ingestion.dedup.LocalKeyValueStore;

public class ConcurrentHashMapKeyValueStore implements LocalKeyValueStore {

  private final Map<ByteArray, byte[]> _map;

  public ConcurrentHashMapKeyValueStore(byte[] id) {
    _map = new ConcurrentHashMap<>();
  }

  @Override
  public byte[] get(byte[] key) {
    return _map.get(new ByteArray(key));
  }

  @Override
  public void delete(byte[] key) {
    _map.remove(new ByteArray(key));
  }

  @Override
  public void put(byte[] key, byte[] value) {
    _map.put(new ByteArray(key), value);
  }

  @Override
  public void putBatch(List<Pair<byte[], byte[]>> keyValues) {
    keyValues.forEach(pair -> _map.put(new ByteArray(pair.getKey()), pair.getValue()));
  }

  @Override
  public long getKeyCount() {
    return _map.size();
  }

  @Override
  @VisibleForTesting
  public void compact() {
  }

  private static final class ByteArray {
    private final byte[] _bytes;

    public ByteArray(byte[] bytes) {
      _bytes = bytes;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ByteArray byteArray = (ByteArray) o;
      return Arrays.equals(_bytes, byteArray._bytes);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(_bytes);
    }
  }
}
