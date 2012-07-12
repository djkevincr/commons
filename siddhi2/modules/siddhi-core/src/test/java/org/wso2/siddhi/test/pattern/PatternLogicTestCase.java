/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.siddhi.test.pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.Callback;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.query.api.query.Query;
import org.wso2.siddhi.query.api.QueryFactory;
import org.wso2.siddhi.query.api.condition.Condition;
import org.wso2.siddhi.query.api.expression.Expression;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.stream.pattern.Pattern;
import org.wso2.siddhi.query.api.stream.pattern.element.LogicalElement;

public class PatternLogicTestCase {

    private int eventCount;
    private boolean eventArrived;

    @Before
    public void init() {
        eventCount = 0;
        eventArrived = false;
    }

    @Test
    public void testQuery1() throws InterruptedException {
        System.out.println("test1 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream1").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));
        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream2").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(
                QueryFactory.patternStream(
                        Pattern.followedBy(
                                QueryFactory.inputStream("e1", "Stream1").handler(
                                        Condition.compare(Expression.variable("price"),
                                                          Condition.Operator.GREATER_THAN,
                                                          Expression.value(20))),
                                Pattern.logical(
                                        QueryFactory.inputStream("e2", "Stream2").handler(
                                                Condition.compare(Expression.variable("price"),
                                                                  Condition.Operator.GREATER_THAN,
                                                                  Expression.variable("e1", "price"))),
                                        LogicalElement.Type.OR,
                                        QueryFactory.inputStream("e3", "Stream2").handler(
                                                Condition.compare(Expression.value("IBM"),
                                                                  Condition.Operator.EQUAL,
                                                                  Expression.variable("symbol")))))));

        query.insertInto("OutStream");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol1", Expression.variable("e1", "symbol")).
                        project("symbol2", Expression.variable("e2", "symbol"))

        );


        siddhiManager.addQuery(query);
        siddhiManager.addCallback("OutStream", new Callback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents,
                                Event[] faultEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents, faultEvents);
                Assert.assertArrayEquals(new Object[]{"WSO2", "GOOG"}, inEvents[0].getData());
                eventCount++;
                eventArrived = true;
            }
        });
        InputHandler stream1 = siddhiManager.getInputHandler("Stream1");
        InputHandler stream2 = siddhiManager.getInputHandler("Stream2");
        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(500);
        stream2.send(new Object[]{"GOOG", 59.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals("Number of success events", 1, eventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);
//        stream2.send(new Object[]{"IBM", 55.7f, 100});
//        Thread.sleep(500);

    }

    @Test
    public void testQuery2() throws InterruptedException {
        System.out.println("test2 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream1").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));
        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream2").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(
                QueryFactory.patternStream(
                        Pattern.followedBy(
                                QueryFactory.inputStream("e1", "Stream1").handler(
                                        Condition.compare(Expression.variable("price"),
                                                          Condition.Operator.GREATER_THAN,
                                                          Expression.value(20))),
//
//                                        QueryFactory.inputStream("e2", "Stream2").handler(
//                                                Condition.compare(Expression.variable("price"),
//                                                                  Condition.Operator.GREATER_THAN,
//                                                                  Expression.variable("e1", "price"))))));
                                Pattern.logical(
                                        QueryFactory.inputStream("e2", "Stream2").handler(
                                                Condition.compare(Expression.variable("price"),
                                                                  Condition.Operator.GREATER_THAN,
                                                                  Expression.variable("e1", "price"))),
                                        LogicalElement.Type.OR,
                                        QueryFactory.inputStream("e3", "Stream2").handler(
                                                Condition.compare(Expression.value("IBM"),
                                                                  Condition.Operator.EQUAL,
                                                                  Expression.variable("symbol")))))));

        query.insertInto("OutStream");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol1", Expression.variable("e1", "symbol")).
                        project("symbol2", Expression.variable("e2", "symbol"))

        );


        siddhiManager.addQuery(query);
        siddhiManager.addCallback("OutStream", new Callback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents,
                                Event[] faultEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents, faultEvents);
                Assert.assertArrayEquals(new Object[]{"WSO2", null}, inEvents[0].getData());
                eventCount++;
                eventArrived = true;
            }
        });
        InputHandler stream1 = siddhiManager.getInputHandler("Stream1");
        InputHandler stream2 = siddhiManager.getInputHandler("Stream2");
        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(500);
//        stream2.send(new Object[]{"GOOG", 59.6f, 100});
//        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 10.7f, 100});
        Thread.sleep(500);
        Assert.assertEquals("Number of success events", 1, eventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

    }

    @Test
    public void testQuery3() throws InterruptedException {
        System.out.println("test3 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream1").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));
        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream2").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(
                QueryFactory.patternStream(
                        Pattern.followedBy(
                                QueryFactory.inputStream("e1", "Stream1").handler(
                                        Condition.compare(Expression.variable("price"),
                                                          Condition.Operator.GREATER_THAN,
                                                          Expression.value(20))),
//
//                                        QueryFactory.inputStream("e2", "Stream2").handler(
//                                                Condition.compare(Expression.variable("price"),
//                                                                  Condition.Operator.GREATER_THAN,
//                                                                  Expression.variable("e1", "price"))))));
                                Pattern.logical(
                                        QueryFactory.inputStream("e2", "Stream2").handler(
                                                Condition.compare(Expression.variable("price"),
                                                                  Condition.Operator.GREATER_THAN,
                                                                  Expression.variable("e1", "price"))),
                                        LogicalElement.Type.OR,
                                        QueryFactory.inputStream("e3", "Stream2").handler(
                                                Condition.compare(Expression.value("IBM"),
                                                                  Condition.Operator.EQUAL,
                                                                  Expression.variable("symbol")))))));

        query.insertInto("OutStream");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol1", Expression.variable("e1", "symbol")).
                        project("symbol2", Expression.variable("e2", "price")).
                        project("symbol3", Expression.variable("e3", "price"))

        );


        siddhiManager.addQuery(query);
        siddhiManager.addCallback("OutStream", new Callback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents,
                                Event[] faultEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents, faultEvents);
                Assert.assertArrayEquals(new Object[]{"WSO2", null, 72.7f}, inEvents[0].getData());
                eventCount++;
                eventArrived = true;
            }
        });
        InputHandler stream1 = siddhiManager.getInputHandler("Stream1");
        InputHandler stream2 = siddhiManager.getInputHandler("Stream2");
        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(500);
//        stream2.send(new Object[]{"GOOG", 59.6f, 100});
//        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 72.7f, 100});
        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 75.7f, 100});
        Thread.sleep(500);
        Assert.assertEquals("Number of success events", 1, eventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

    }

    @Test
    public void testQuery4() throws InterruptedException {
        System.out.println("test4 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream1").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));
        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream2").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(
                QueryFactory.patternStream(
                        Pattern.followedBy(
                                QueryFactory.inputStream("e1", "Stream1").handler(
                                        Condition.compare(Expression.variable("price"),
                                                          Condition.Operator.GREATER_THAN,
                                                          Expression.value(20))),
//
//                                        QueryFactory.inputStream("e2", "Stream2").handler(
//                                                Condition.compare(Expression.variable("price"),
//                                                                  Condition.Operator.GREATER_THAN,
//                                                                  Expression.variable("e1", "price"))))));
                                Pattern.logical(
                                        QueryFactory.inputStream("e2", "Stream2").handler(
                                                Condition.compare(Expression.variable("price"),
                                                                  Condition.Operator.GREATER_THAN,
                                                                  Expression.variable("e1", "price"))),
                                        LogicalElement.Type.AND,
                                        QueryFactory.inputStream("e3", "Stream2").handler(
                                                Condition.compare(Expression.value("IBM"),
                                                                  Condition.Operator.EQUAL,
                                                                  Expression.variable("symbol")))))));

        query.insertInto("OutStream");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol1", Expression.variable("e1", "symbol")).
                        project("symbol2", Expression.variable("e2", "price")).
                        project("symbol3", Expression.variable("e3", "price"))

        );


        siddhiManager.addQuery(query);
        siddhiManager.addCallback("OutStream", new Callback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents,
                                Event[] faultEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents, faultEvents);
                Assert.assertArrayEquals(new Object[]{"WSO2", 72.7f, 4.7f}, inEvents[0].getData());
                eventCount++;
                eventArrived = true;
            }
        });
        InputHandler stream1 = siddhiManager.getInputHandler("Stream1");
        InputHandler stream2 = siddhiManager.getInputHandler("Stream2");
        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(500);
        stream2.send(new Object[]{"GOOG", 72.7f, 100});
        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 4.7f, 100});
        Thread.sleep(500);
        Assert.assertEquals("Number of success events", 1, eventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

    }

    @Test
    public void testQuery5() throws InterruptedException {
        System.out.println("test5 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream1").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));
        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream2").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(
                QueryFactory.patternStream(
                        Pattern.followedBy(
                                QueryFactory.inputStream("e1", "Stream1").handler(
                                        Condition.compare(Expression.variable("price"),
                                                          Condition.Operator.GREATER_THAN,
                                                          Expression.value(20))),
//
//                                        QueryFactory.inputStream("e2", "Stream2").handler(
//                                                Condition.compare(Expression.variable("price"),
//                                                                  Condition.Operator.GREATER_THAN,
//                                                                  Expression.variable("e1", "price"))))));
                                Pattern.logical(
                                        QueryFactory.inputStream("e2", "Stream2").handler(
                                                Condition.compare(Expression.variable("price"),
                                                                  Condition.Operator.GREATER_THAN,
                                                                  Expression.variable("e1", "price"))),
                                        LogicalElement.Type.AND,
                                        QueryFactory.inputStream("e3", "Stream2").handler(
                                                Condition.compare(Expression.value("IBM"),
                                                                  Condition.Operator.EQUAL,
                                                                  Expression.variable("symbol")))))));

        query.insertInto("OutStream");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol1", Expression.variable("e1", "symbol")).
                        project("symbol2", Expression.variable("e2", "price")).
                        project("symbol3", Expression.variable("e3", "price"))

        );


        siddhiManager.addQuery(query);
        siddhiManager.addCallback("OutStream", new Callback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents,
                                Event[] faultEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents, faultEvents);
                Assert.assertArrayEquals(new Object[]{"WSO2", 72.7f, 72.7f}, inEvents[0].getData());
                eventCount++;
                eventArrived = true;
            }
        });
        InputHandler stream1 = siddhiManager.getInputHandler("Stream1");
        InputHandler stream2 = siddhiManager.getInputHandler("Stream2");
        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(500);
//        stream2.send(new Object[]{"GOOG", 59.6f, 100});
//        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 72.7f, 100});
        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 75.7f, 100});
        Thread.sleep(500);
        Assert.assertEquals("Number of success events", 1, eventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

    }

    @Test
    public void testQuery6() throws InterruptedException {
        System.out.println("test6 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream1").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));
        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("Stream2").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(
                QueryFactory.patternStream(
                        Pattern.followedBy(
                                QueryFactory.inputStream("e1", "Stream1").handler(
                                        Condition.compare(Expression.variable("price"),
                                                          Condition.Operator.GREATER_THAN,
                                                          Expression.value(20))),
//
//                                        QueryFactory.inputStream("e2", "Stream2").handler(
//                                                Condition.compare(Expression.variable("price"),
//                                                                  Condition.Operator.GREATER_THAN,
//                                                                  Expression.variable("e1", "price"))))));
                                Pattern.logical(
                                        QueryFactory.inputStream("e2", "Stream2").handler(
                                                Condition.compare(Expression.variable("price"),
                                                                  Condition.Operator.GREATER_THAN,
                                                                  Expression.variable("e1", "price"))),
                                        LogicalElement.Type.AND,
                                        QueryFactory.inputStream("e3", "Stream1").handler(
                                                Condition.compare(Expression.value("IBM"),
                                                                  Condition.Operator.EQUAL,
                                                                  Expression.variable("symbol")))))));

        query.insertInto("OutStream");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol1", Expression.variable("e1", "symbol")).
                        project("symbol2", Expression.variable("e2", "price")).
                        project("symbol3", Expression.variable("e3", "price"))

        );


        siddhiManager.addQuery(query);
        siddhiManager.addCallback("OutStream", new Callback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents,
                                Event[] faultEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents, faultEvents);
                Assert.assertArrayEquals(new Object[]{"WSO2", 72.7f, 75.7f}, inEvents[0].getData());
                eventCount++;
                eventArrived = true;
            }
        });
        InputHandler stream1 = siddhiManager.getInputHandler("Stream1");
        InputHandler stream2 = siddhiManager.getInputHandler("Stream2");
        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(500);
//        stream2.send(new Object[]{"GOOG", 59.6f, 100});
//        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 72.7f, 100});
        Thread.sleep(500);
        stream1.send(new Object[]{"IBM", 75.7f, 100});
        Thread.sleep(500);
        Assert.assertEquals("Number of success events", 1, eventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

    }
}
