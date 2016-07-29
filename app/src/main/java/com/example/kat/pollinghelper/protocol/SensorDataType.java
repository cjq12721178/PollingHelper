package com.example.kat.pollinghelper.protocol;

import com.example.kat.pollinghelper.utility.SimpleFormatter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KAT on 2016/7/26.
 */
public class SensorDataType {

    public static class Handler extends DefaultHandler {

        public Map<Byte, SensorDataType> getDataTypeMap() {
            return dataTypeMap;
        }

        @Override
        public void startDocument() throws SAXException {
            dataTypeMap = new HashMap<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals(DATA_TYPE)) {
                dataType = new SensorDataType();
            }
            builder.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (localName) {
                case VALUE:dataType.value = Integer.decode(builder.toString()).byteValue();break;
                case NAME:dataType.name = builder.toString();break;
                case PATTERN:dataType.pattern = Pattern.from(Integer.parseInt(builder.toString()));break;
                case DECIMAL:dataType.decimal = Integer.parseInt(builder.toString());break;
                case UNIT:dataType.unit = builder.toString();break;
                case COEFFICIENT:dataType.coefficient = Double.parseDouble(builder.toString());break;
                case TRUE:dataType.labelOn = builder.toString();break;
                case FALSE:dataType.labelOff = builder.toString();break;
                case DATA_TYPE:dataTypeMap.put(dataType.getValue(), dataType);break;
            }
        }

        private static final String VALUE = "value";
        private static final String NAME = "name";
        private static final String PATTERN = "type";
        private static final String DECIMAL = "decimal";
        private static final String UNIT = "unit";
        private static final String COEFFICIENT = "coefficient";
        private static final String TRUE = "true";
        private static final String FALSE = "false";
        private static final String DATA_TYPE = "DataType";
        private Map<Byte, SensorDataType> dataTypeMap;
        private SensorDataType dataType;
        private StringBuilder builder;
    }

    public enum Pattern {
        DT_ANALOG,
        DT_STATUS,
        DT_COUNT;

        public static Pattern from(int patternOrder) {
            switch (patternOrder) {
                case 0:return Pattern.DT_ANALOG;
                case 1:return Pattern.DT_STATUS;
                case 2:return Pattern.DT_COUNT;
                default:return Pattern.DT_ANALOG;
            }
        }
    }

    public static Handler getHandler() {
        return new Handler();
    }

    public static SensorDataType getNullType(byte value) {
        SensorDataType nullType = new SensorDataType();
        nullType.value = value;
        nullType.pattern = Pattern.DT_ANALOG;
        nullType.decimal = 3;
        nullType.coefficient = 1.0;
        return nullType;
    }

    public byte getValue() {
        return value;
    }

    public String getName() {
        return name != null ? name : String.valueOf(value);
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getDecimal() {
        return decimal;
    }

    public String getUnit() {
        return unit;
    }

    public String getLabelOn() {
        return labelOn;
    }

    public String getLabelOff() {
        return labelOff;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public String getSignificantValue(double rawValue) {
        switch (pattern) {
            case DT_ANALOG:
            case DT_COUNT:return SimpleFormatter.keepDecimal(rawValue, decimal);
            case DT_STATUS:return rawValue == 1 ? labelOn : labelOff;
            default:return String.valueOf(rawValue);
        }
    }

    public String getSignificantValueWithUnit(double rawValue) {
        return getSignificantValue(rawValue) + unit;
    }

    //private static SensorDataType nullType;
    private byte value;
    private String name;
    private Pattern pattern;
    private int decimal;
    private String unit = "";
    private double coefficient;
    private String labelOn;
    private String labelOff;
}
