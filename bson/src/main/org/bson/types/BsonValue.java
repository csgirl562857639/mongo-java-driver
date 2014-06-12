/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bson.types;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonTimestamp;
import org.bson.BsonType;

/**
 * Base class for any BSON type.
 *
 * @since 3.0
 */
public abstract class BsonValue {
    /**
     * Construct a new instance.  This is package-protected so that the BSON type system is closed.
     */
    protected BsonValue() {
    }

    /**
     * Gets the BSON type of this value.
     *
     * @return the BSON type, which may not be null (but may be BSONType.NULL)
     */
    public abstract BsonType getBsonType();

    /**
     * Gets this value as a BsonDocument if it is one, otherwise throws exception
     *
     * @return a BsonDocument
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonDocument asDocument() {
        throwIfInvalidType(BsonType.DOCUMENT);
        return (BsonDocument) this;
    }

    /**
     * Gets this value as a BsonArray if it is one, otherwise throws exception
     *
     * @return a BsonArray
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonArray asArray() {
        throwIfInvalidType(BsonType.ARRAY);
        return (BsonArray) this;
    }

    /**
     * Gets this value as a BsonString if it is one, otherwise throws exception
     *
     * @return a BsonArray
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonString asString() {
        throwIfInvalidType(BsonType.STRING);
        return (BsonString) this;
    }

    /**
     * Gets this value as a BsonNumber if it is one, otherwise throws exception
     *
     * @return a BsonArray
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonNumber asNumber() {
        if (getBsonType() != BsonType.INT32 && getBsonType() != BsonType.INT64 && getBsonType() != BsonType.DOUBLE) {
            throw new BsonInvalidOperationException(String.format("Value expected to be of a numerical BSON type is of unexpected type %s",
                                                                  getBsonType()));
        }
        return (BsonNumber) this;
    }

    /**
     * Gets this value as a BsonString if it is one, otherwise throws exception
     *
     * @return a BsonArray
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonInt32 asInt32() {
        throwIfInvalidType(BsonType.INT32);
        return (BsonInt32) this;
    }

    /**
     * Gets this value as a BsonString if it is one, otherwise throws exception
     *
     * @return a BsonArray
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonInt64 asInt64() {
        throwIfInvalidType(BsonType.INT64);
        return (BsonInt64) this;
    }

    /**
     * Gets this value as a BsonString if it is one, otherwise throws exception
     *
     * @return a BsonArray
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonDouble asDouble() {
        throwIfInvalidType(BsonType.DOUBLE);
        return (BsonDouble) this;
    }

    /**
     * Gets this value as a BsonString if it is one, otherwise throws exception
     *
     * @return a BsonArray
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonBoolean asBoolean() {
        throwIfInvalidType(BsonType.BOOLEAN);
        return (BsonBoolean) this;
    }

    /**
     * Gets this value as an BsonObjectId if it is one, otherwise throws exception
     *
     * @return an BsonObjectId
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonObjectId asObjectId() {
        throwIfInvalidType(BsonType.OBJECT_ID);
        return (BsonObjectId) this;
    }

    /**
     * Gets this value as a DBPointer if it is one, otherwise throws exception
     *
     * @return an DBPointer
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonDbPointer asDBPointer() {
        throwIfInvalidType(BsonType.DB_POINTER);
        return (BsonDbPointer) this;
    }

    /**
     * Gets this value as a Timestamp if it is one, otherwise throws exception
     *
     * @return an Timestamp
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonTimestamp asTimestamp() {
        throwIfInvalidType(BsonType.TIMESTAMP);
        return (BsonTimestamp) this;
    }

    /**
     * Gets this value as a Binary if it is one, otherwise throws exception
     *
     * @return an Binary
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonBinary asBinary() {
        throwIfInvalidType(BsonType.BINARY);
        return (BsonBinary) this;
    }

    /**
     * Gets this value as a BsonDateTime if it is one, otherwise throws exception
     *
     * @return an BsonDateTime
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonDateTime asDateTime() {
        throwIfInvalidType(BsonType.DATE_TIME);
        return (BsonDateTime) this;
    }

    /**
     * Gets this value as a Symbol if it is one, otherwise throws exception
     *
     * @return an Symbol
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonSymbol asSymbol() {
        throwIfInvalidType(BsonType.SYMBOL);
        return (BsonSymbol) this;
    }

    /**
     * Gets this value as a RegularExpression if it is one, otherwise throws exception
     *
     * @return an ObjectId
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonRegularExpression asRegularExpression() {
        throwIfInvalidType(BsonType.REGULAR_EXPRESSION);
        return (BsonRegularExpression) this;
    }

    /**
     * Gets this value as a {@code BsonJavaScript} if it is one, otherwise throws exception
     *
     * @return a Code
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonJavaScript asJavaScript() {
        throwIfInvalidType(BsonType.JAVASCRIPT);
        return (BsonJavaScript) this;
    }

    /**
     * Gets this value as a CodeWithScope if it is one, otherwise throws exception
     *
     * @return a CodeWithScope
     * @throws org.bson.BsonInvalidOperationException if this value is not of the expected type
     */
    public BsonJavaScriptWithScope asJavaScriptWithScope() {
        throwIfInvalidType(BsonType.JAVASCRIPT_WITH_SCOPE);
        return (BsonJavaScriptWithScope) this;
    }


    /**
     * Returns true if this is a BsonNull, false otherwise.
     *
     * @return true if this is a BsonNull, false otherwise
     */
    public boolean isNull() {
        return this instanceof BsonNull;
    }

    /**
     * Returns true if this is a BsonDocument, false otherwise.
     *
     * @return true if this is a BsonDocument, false otherwise
     */
    public boolean isDocument() {
        return this instanceof BsonDocument;
    }

    /**
     * Returns true if this is a BsonArray, false otherwise.
     *
     * @return true if this is a BsonArray, false otherwise
     */
    public boolean isArray() {
        return this instanceof BsonArray;
    }

    /**
     * Returns true if this is a BsonString, false otherwise.
     *
     * @return true if this is a BsonString, false otherwise
     */
    public boolean isString() {
        return this instanceof BsonString;
    }

    /**
     * Returns true if this is a BsonNumber, false otherwise.
     *
     * @return true if this is a BsonNumber, false otherwise
     */
    public boolean isNumber() {
        return isInt32() || isInt64() || isDouble();
    }

    /**
     * Returns true if this is a BsonInt32, false otherwise.
     *
     * @return true if this is a BsonInt32, false otherwise
     */
    public boolean isInt32() {
        return this instanceof BsonInt32;
    }

    /**
     * Returns true if this is a BsonInt64, false otherwise.
     *
     * @return true if this is a BsonInt64, false otherwise
     */
    public boolean isInt64() {
        return this instanceof BsonInt64;
    }

    /**
     * Returns true if this is a BsonDouble, false otherwise.
     *
     * @return true if this is a BsonDouble, false otherwise
     */
    public boolean isDouble() {
        return this instanceof BsonDouble;

    }

    /**
     * Returns true if this is a , false otherwise.
     *
     * @return true if this is a , false otherwise
     */
    public boolean isBoolean() {
        return this instanceof BsonBoolean;

    }

    /**
     * Returns true if this is an ObjectId, false otherwise.
     *
     * @return true if this is an ObjectId, false otherwise
     */
    public boolean isObjectId() {
        return this instanceof BsonObjectId;
    }

    /**
     * Returns true if this is a DBPointer, false otherwise.
     *
     * @return true if this is a DBPointer, false otherwise
     */
    public boolean isDBPointer() {
        return this instanceof BsonDbPointer;
    }

    /**
     * Returns true if this is a Timestamp, false otherwise.
     *
     * @return true if this is a Timestamp, false otherwise
     */
    public boolean isTimestamp() {
        return this instanceof BsonTimestamp;
    }

    /**
     * Returns true if this is a Binary, false otherwise.
     *
     * @return true if this is a Binary, false otherwise
     */
    public boolean isBinary() {
        return this instanceof BsonBinary;
    }

    /**
     * Returns true if this is a BsonDateTime, false otherwise.
     *
     * @return true if this is a BsonDateTime, false otherwise
     */
    public boolean isDateTime() {
        return this instanceof BsonDateTime;
    }

    /**
     * Returns true if this is a Symbol, false otherwise.
     *
     * @return true if this is a Symbol, false otherwise
     */
    public boolean isSymbol() {
        return this instanceof BsonSymbol;
    }

    /**
     * Returns true if this is a RegularExpression, false otherwise.
     *
     * @return true if this is a RegularExpression, false otherwise
     */
    public boolean isRegularExpression() {
        return this instanceof BsonRegularExpression;
    }

    /**
     * Returns true if this is a Code, false otherwise.
     *
     * @return true if this is a Code, false otherwise
     */
    public boolean isJavaScript() {
        return this instanceof BsonJavaScript;
    }

    /**
     * Returns true if this is a CodeWithScope, false otherwise.
     *
     * @return true if this is a CodeWithScope, false otherwise
     */
    public boolean isJavaScriptWithScope() {
        return this instanceof BsonJavaScriptWithScope;
    }

    private void throwIfInvalidType(final BsonType expectedType) {
        if (getBsonType() != expectedType) {
            throw new BsonInvalidOperationException(String.format("Value expected to be of type %s is of unexpected type %s",
                                                                  expectedType, getBsonType()));
        }
    }
}