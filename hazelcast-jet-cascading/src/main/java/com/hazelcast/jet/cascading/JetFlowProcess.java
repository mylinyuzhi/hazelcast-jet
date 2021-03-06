/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.cascading;

import cascading.flow.FlowProcess;
import cascading.tap.Tap;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.JetInstance;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class JetFlowProcess extends FlowProcess<JetConfig> {

    private final JetConfig config;
    private final JetInstance instance;
    private int sliceNum;

    public JetFlowProcess() {
        this(new JetConfig(), null);
    }

    public JetFlowProcess(JetConfig config, JetInstance instance) {
        this.instance = instance;
        this.config = config;
    }

    @Override
    public FlowProcess<JetConfig> copyWith(JetConfig jetConfig) {
        return new JetFlowProcess(jetConfig, instance);
    }

    @Override
    public int getNumProcessSlices() {
        return 0;
    }

    @Override
    public int getCurrentSliceNum() {
        return sliceNum;
    }

    public void setCurrentSliceNum(int num) {
        sliceNum = num;
    }

    @Override
    public Object getProperty(String key) {
        return config.getProperties().getProperty(key);
    }

    @Override
    public Collection<String> getPropertyKeys() {
        return config.getProperties().stringPropertyNames();
    }

    @Override
    public Object newInstance(String className) {
        return null;
    }

    @Override
    public void keepAlive() {

    }

    @Override
    public void increment(Enum counter, long amount) {

    }

    @Override
    public void increment(String group, String counter, long amount) {

    }

    @Override
    public long getCounterValue(Enum counter) {
        return 0;
    }

    @Override
    public long getCounterValue(String group, String counter) {
        return 0;
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public boolean isCounterStatusInitialized() {
        return false;
    }

    @Override
    public TupleEntryIterator openTapForRead(Tap tap) throws IOException {
        return tap.openForRead(this);
    }

    @Override
    public TupleEntryCollector openTapForWrite(Tap tap) throws IOException {
        // do not honor sinkmode as this may be opened across tasks
        return tap.openForWrite(this, null);
    }

    @Override
    public TupleEntryCollector openTrapForWrite(Tap tap) throws IOException {
        // do not honor sinkmode as this may be opened across tasks
        return tap.openForWrite(this, null);
    }

    @Override
    public TupleEntryCollector openSystemIntermediateForWrite() throws IOException {
        return null;
    }

    @Override
    public JetConfig getConfig() {
        return config;
    }

    @Override
    public JetConfig getConfigCopy() {
        return config;
    }

    @Override
    public <C> C copyConfig(C config) {
        return config;
    }

    @Override
    public <C> Map<String, String> diffConfigIntoMap(C defaultConfig, C updatedConfig) {
        return null;
    }

    @Override
    public JetConfig mergeMapIntoConfig(JetConfig defaultConfig, Map<String, String> map) {
        if (map != null) {
            defaultConfig.getProperties().putAll(map);
        }
        return defaultConfig;
    }

    public JetInstance getJetInstance() {
        return instance;
    }
}
