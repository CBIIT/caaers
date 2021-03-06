/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package gov.nih.nci.cabig.caaers.utils.el;

import junit.framework.TestCase;

public class ELTest extends TestCase {

    private EL el = new EL();

    public void testMath() {
        assertEquals("2", el.evaluate("${1 + 1}"));
        assertEquals("5", el.evaluate("${10 - 5}"));
        assertEquals("true", el.evaluate("${true && (false || true)}"));
        assertEquals("true", el.evaluate("${true && (false || (true && true && (true || false)))}"));
        assertEquals("false", el.evaluate("${true && (false || (true && true && (false || false)))}"));
        assertEquals("true", el.evaluate("${true || false}"));
        assertEquals("true", el.evaluate("${true}"));
    }
    
    public void testString(){
    	assertEquals("testString",el.evaluate("testString"));
    }
    
    public void testBooleanLiteral(){
    	assertEquals("false",el.evaluate("false"));
    }
    
    public void testExpression(){
    	assertEquals("9.0",el.evaluate("${2*5 + 12/3 -5}"));
    	assertEquals("true",el.evaluate("${4.0 >= 3}"));
    	assertEquals("12001.4",el.evaluate("${1.2E4 + 1.4}"));
    	assertEquals("1.0",el.evaluate("${1.0 mod 3}"));
    }
    
    public void testIncorrectInput(){
    	try {
    		assertEquals("9.0",el.evaluate("${ABoi23*344/167uio98}"));
    		fail("Test should have failed");
    	} catch (Exception ex){
    		
    	}
    }
}