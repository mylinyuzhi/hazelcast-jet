/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.jet.impl.container;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.jet.dag.Vertex;
import com.hazelcast.jet.data.tuple.JetTupleFactory;
import com.hazelcast.jet.impl.job.JobContext;
import com.hazelcast.jet.impl.statemachine.StateMachine;
import com.hazelcast.jet.impl.statemachine.StateMachineFactory;
import com.hazelcast.spi.NodeEngine;

public abstract class AbstractContainer
        <SI extends ContainerEvent,
                SS extends ContainerState,
                SO extends ContainerResponse>
        implements Container<SI, SS, SO> {
    private final int id;

    private final NodeEngine nodeEngine;
    private final ContainerContext containerContext;
    private final JobContext jobContext;
    private final StateMachine<SI, SS, SO> stateMachine;

    public AbstractContainer(StateMachineFactory<SI, StateMachine<SI, SS, SO>> stateMachineFactory,
                             NodeEngine nodeEngine,
                             JobContext jobContext,
                             JetTupleFactory tupleFactory) {
        this(null, stateMachineFactory, nodeEngine, jobContext, tupleFactory);
    }

    public AbstractContainer(Vertex vertex,
                             StateMachineFactory<SI, StateMachine<SI, SS, SO>> stateMachineFactory,
                             NodeEngine nodeEngine,
                             JobContext jobContext,
                             JetTupleFactory tupleFactory) {
        this.nodeEngine = nodeEngine;
        String name = vertex == null ? jobContext.getName() : vertex.getName();
        this.stateMachine = stateMachineFactory.newStateMachine(name, this, nodeEngine, jobContext);
        this.jobContext = jobContext;
        this.id = jobContext.getContainerIDGenerator().incrementAndGet();
        this.containerContext = new ContainerContext(nodeEngine, jobContext, this.id, vertex, tupleFactory);
    }

    @Override
    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public StateMachine<SI, SS, SO> getStateMachine() {
        return this.stateMachine;
    }

    @Override
    public <P> ICompletableFuture<SO> handleContainerRequest(ContainerRequest<SI, P> request) {
        try {
            return this.stateMachine.handleRequest(request);
        } finally {
            wakeUpExecutor();
        }
    }

    protected abstract void wakeUpExecutor();

    public JobContext getJobContext() {
        return jobContext;
    }

    @Override
    public ContainerContext getContainerContext() {
        return containerContext;
    }

    public String getApplicationName() {
        return jobContext.getName();
    }

    @Override
    public int getID() {
        return id;
    }
}
