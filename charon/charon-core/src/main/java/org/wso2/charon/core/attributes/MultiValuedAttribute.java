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
package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon.core.schema.SCIMSchemaDefinitions.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents Multi-Valued Attribute as defined in SCIM spec.
 * Values of this type of attribute can take the form of a complex attribute having several
 * properties for an attribute value.
 */
public class MultiValuedAttribute extends AbstractAttribute {

    /*Following are characteristics of an attribute VALUE if it is multi-valued attribute.*/

    /*//Type of the value of an multivalued attribute goes as another attribute.
    protected String multivaluedAttributeType;
    //whether this is the primary value
    protected String multiValuedAttributePrimary;
    //usually used in a PATCH operation of an attribute.
    protected String operation;*/

    //array of string values for a multi-valued attribute
    protected List<String> stringAttributeValues;

    //Multi valued attributes can also have VALUES as an array of complex or simple attributes.
    protected List<Attribute> attributeValues = new ArrayList<Attribute>();

    /**
     * Create the attribute with the given name. Attribute name can be set only when creating the
     * attribute.
     *
     * @param attributeName Name of the attribute
     */
    public MultiValuedAttribute(String attributeName) {
        super(attributeName);
    }

    public MultiValuedAttribute(String attributeName, List<Attribute> attributeValues) {
        this.attributeName = attributeName;
        this.attributeValues = attributeValues;
    }

    /**
     * Create the attribute with given name and schema name.
     *
     * @param attributeName Name of the attribute
     * @param schema        schema in which the attribute is defined.
     */
    public MultiValuedAttribute(String attributeName, String schema) {
        super(attributeName, schema);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Add complex type value to multi-valued attribute given the properties of the value.
     *
     * @param multiValueAttributeProperties
     */
    public void setComplexValue(Map<String, Object> multiValueAttributeProperties)
            throws CharonException {
        //attribute value as a complex attribute.
        ComplexAttribute attributeValue = new ComplexAttribute();
        for (Map.Entry<String, Object> entry : multiValueAttributeProperties.entrySet()) {
            if ((SCIMConstants.CommonSchemaConstants.TYPE).equals(entry.getKey())) {
                SimpleAttribute typeAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE, entry.getValue());
                typeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.TYPE, typeAttribute);
                attributeValue.setSubAttribute(typeAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.VALUE).equals(entry.getKey())) {
                SimpleAttribute typeAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE, entry.getValue());
                typeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.VALUE, typeAttribute);
                attributeValue.setSubAttribute(typeAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.DISPLAY).equals(entry.getKey())) {
                SimpleAttribute typeAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.DISPLAY, entry.getValue());
                typeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.DISPLAY, typeAttribute);
                attributeValue.setSubAttribute(typeAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.PRIMARY).equals(entry.getKey())) {
                SimpleAttribute typeAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.PRIMARY, entry.getValue());
                typeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.PRIMARY, typeAttribute);
                attributeValue.setSubAttribute(typeAttribute);
            } else if ((SCIMConstants.CommonSchemaConstants.OPERATION).equals(entry.getKey())) {
                SimpleAttribute typeAttribute =
                        new SimpleAttribute(SCIMConstants.CommonSchemaConstants.OPERATION, entry.getValue());
                typeAttribute = (SimpleAttribute) DefaultAttributeFactory.createAttribute(
                        SCIMSchemaDefinitions.OPERATION, typeAttribute);
                attributeValue.setSubAttribute(typeAttribute);
            }
        }
        if (attributeValue.getSubAttributes() != null && !attributeValue.getSubAttributes().isEmpty()) {
            this.attributeValues.add(attributeValue);
        }
    }

    /**
     * Create attribute with given name, schema name,whether it is readOnly and required.
     *
     * @param attributeName Name of the attribute
     * @param schema        schema in which the attribute is defined
     * @param readOnly      whether attribute is readOnly
     * @param optional      whether attribute is required
     *//*
    public MultiValuedAttribute(String attributeName, String schema, boolean readOnly,
                                boolean optional) {
        super(attributeName, schema, readOnly, optional);
    }

    *//**
     * Create multi valued attribute by providing values as a list of attributes (Complex or Simple)
     * along with other info about the attribute.
     *
     * @param attributeName
     * @param schema
     * @param values
     * @param readOnly
     * @param optional
     *//*
    public MultiValuedAttribute(String attributeName, String schema, List<Attribute> values,
                                boolean readOnly, boolean optional) {
        super(attributeName, schema, readOnly, optional);
        attributeValues = values;

    }*/

    /**
     * Get the attribute values as sub attributes.
     *
     * @return
     */
    public List<Attribute> getValuesAsSubAttributes() {
        return attributeValues;
    }

    /**
     * Set the values as set of sub attributes.
     *
     * @param subAttributes
     */
    public void setValuesAsSubAttributes(List<Attribute> subAttributes) {
        this.attributeValues = subAttributes;
    }

    /**
     * There can be multivalued attributes whose value is an array of strings. eg: schemas attribute.
     * Set the attribute values in such a multivalued attribute.
     *
     * @return
     */
    public List<String> getValuesAsStrings() {
        return stringAttributeValues;
    }

    /**
     * There can be multivalued attributes whose value is an array of strings. eg: schemas attribute.
     * Set the attribute values in such a multivalued attribute.
     *
     * @param attributeValues
     */
    public void setValuesAsStrings(List<String> attributeValues) {
        this.stringAttributeValues = attributeValues;
    }

    /**
     * Get the type of the given attribute value.
     *
     * @param value
     * @return
     */
    public String getAttributeTypeOfValue(Object value) throws NotFoundException, CharonException {
        for (Attribute attributeValue : attributeValues) {
            //get the 'value' attribute
            Attribute valueAttribute = ((ComplexAttribute) attributeValue).getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.VALUE);
            //get the 'type' attribute
            Attribute typeAttribute = ((ComplexAttribute) attributeValue).getSubAttribute(
                    SCIMConstants.CommonSchemaConstants.TYPE);
            //get the value of 'value' attribute
            String attrValue = ((SimpleAttribute) (valueAttribute)).getStringValue();
            //compare
            if (value.equals(attrValue)) {
                return ((SimpleAttribute) typeAttribute).getStringValue();
            }
        }
        throw new NotFoundException();
    }

    /*public Object[] getMultiValuedAttributeValues() {
        return multiValuedAttributeValues;
    }

    public void setMultiValuedAttributeValues(Object[] multiValuedAttributeValues) {
        this.multiValuedAttributeValues = multiValuedAttributeValues;
    }*/

    /**
     * Set one value on the MultiValuedAttribute. Value goes as a complex attribute consisting of
     * sub attributes corresponding to the given characteristics of the value which are passed as
     * parameters to the method.
     *
     * @param type            - type of the value, i.e 'work', 'home'
     * @param primary         - whether this value is the primary value
     * @param display         - human readable format of the attribute - read only
     * @param value           - actual value
     * @param dataTypeOfValue - data type of the value, i.e Sting, boolean
     */
    public void setAttributeValue(String type, boolean primary, String display, Object value,
                                  DataType dataTypeOfValue)
            throws CharonException {
        //one value of the multi-valued attribute that goes as a complex attribute.
        ComplexAttribute attributeValue = new ComplexAttribute(null);
        //'type' of the value
        SimpleAttribute typeValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.TYPE, null, type,
                DataType.STRING);
        attributeValue.setSubAttribute(typeValue);
        //set primary
        SimpleAttribute primaryValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.PRIMARY, null, primary,
                DataType.BOOLEAN);
        attributeValue.setSubAttribute(primaryValue);
        //set display
        SimpleAttribute displayValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.DISPLAY, null, display,
                DataType.STRING, true, true);
        attributeValue.setSubAttribute(displayValue);
        //actual value with provided data type
        SimpleAttribute actualValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.VALUE, null, value, dataTypeOfValue);
        attributeValue.setSubAttribute(actualValue);
        //canonicalize value before adding to the multi valued attribute
        canonicalizeAttributeValue(attributeValue);
        //add value to the multi valued attribute
        attributeValues.add(actualValue);
    }

    /**
     * Set the value as a simple attribute value.
     *
     * @param value
     * @param dataType
     * @throws CharonException
     */
    public void setSimpleAttributeValue(Object value, DataType dataType)
            throws CharonException {
        SimpleAttribute simpleAttributeValue = new SimpleAttribute(
                SCIMConstants.CommonSchemaConstants.VALUE, null, value, dataType);
        //canonicalizeAttributeValue(simpleAttributeValue);
        attributeValues.add(simpleAttributeValue);
    }

    /**
     * Get the primary value of multi valued attribute
     *
     * @return
     */
    public Object getPrimaryValue() throws NotFoundException, CharonException {
        for (Attribute attributeValue : attributeValues) {
            if (attributeValue instanceof ComplexAttribute) {
                SimpleAttribute primaryValue = (SimpleAttribute) (
                        (ComplexAttribute) attributeValue).getSubAttribute(
                        SCIMConstants.CommonSchemaConstants.PRIMARY);
                boolean primary = primaryValue.getBooleanValue();
                if (primary) {
                    SimpleAttribute value = (SimpleAttribute) (
                            (ComplexAttribute) attributeValue).getSubAttribute(
                            SCIMConstants.CommonSchemaConstants.VALUE);
                    return value.getValue();
                }
            }
        }
        return new NotFoundException();
    }

    /**
     * Get a value of multi valued attribute by specifying its type.
     *
     * @param type
     * @return
     * @throws NotFoundException
     * @throws CharonException
     */
    public Object getAttrbuteValueByType(String type)
            throws NotFoundException, CharonException {
        for (Attribute attributeValue : attributeValues) {
            if (attributeValue instanceof ComplexAttribute) {
                SimpleAttribute typeValue = (SimpleAttribute) (
                        (ComplexAttribute) attributeValue).getSubAttribute(
                        SCIMConstants.CommonSchemaConstants.TYPE);
                String typeAttrVal = typeValue.getStringValue();
                if (type.equals(typeAttrVal)) {
                    SimpleAttribute value = (SimpleAttribute) (
                            (ComplexAttribute) attributeValue).getSubAttribute(
                            SCIMConstants.CommonSchemaConstants.VALUE);
                    return value.getValue();
                }
            }
        }
        return new NotFoundException();
    }

    public void canonicalizeAttributeValue(Attribute attribute) {
        //iterate through attribute values.
        //compare type and value - in one case, with the given attribute. If same, print error and
        //do not add the given attribute to the values.

    }

    /**
     * Validate whether the attribute adheres to the SCIM schema.
     *
     * @param attribute
     * @return
     */
    public boolean validate(Attribute attribute) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
