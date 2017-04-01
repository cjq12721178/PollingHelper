package com.example.kat.pollinghelper.protocol;

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

        public Map<SensorDataType, Map<Byte, String>> getMeasureNameMap() {
            return measureNameMap;
        }

        @Override
        public void startDocument() throws SAXException {
            dataTypeMap = new HashMap<>();
            measureNameMap = new HashMap<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals(DATA_TYPE)) {
                dataType = new SensorDataType();
            } else if (localName.equals(DIRECTIONS)) {
                directionMap = new HashMap<>();
            } else if (localName.equals(PARAPHRASES)) {
                paraphrases = new HashMap<>();
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
                case SIGNED:dataType.signed = Boolean.parseBoolean(builder.toString());break;
                //case DECIMAL:dataType.decimal = Integer.parseInt(builder.toString());break;
                case DECIMAL:dataType.interpreter = FloatInterpreter.build(Integer.parseInt(builder.toString()));break;
                case UNIT:dataType.unit = builder.toString();break;
                case COEFFICIENT:dataType.coefficient = Double.parseDouble(builder.toString());break;
                case ON:on = builder.toString();break;
                case OFF:off = builder.toString();break;
                case STATUS:dataType.interpreter = new StatusInterpreter(on, off);break;
                case DATA_TYPE: {
                    if (dataType.interpreter == null) {
                        dataType.interpreter = DefaultInterpreter.getInstance();
                    }
                    dataTypeMap.put(dataType.getValue(), dataType);
                    if (directionMap != null) {
                        measureNameMap.put(dataType, directionMap);
                        directionMap = null;
                    }
                } break;
                case INDEX:index = Byte.valueOf(builder.toString());break;
                case INTRODUCTION:introduction = builder.toString();break;
                case DIRECTION:directionMap.put(index, introduction);break;
                case NUMBER:number = Double.parseDouble(builder.toString());break;
                case TEXT:text = builder.toString();break;
                case PARAPHRASE:paraphrases.put(number, text);break;
                case PARAPHRASES:dataType.interpreter = new ParaphraseInterpreter(paraphrases);break;
                case CALENDAR:dataType.interpreter = CalendarInterpreter.from(builder.toString());break;
                default:break;
            }
        }

        private static final String VALUE = "value";
        private static final String NAME = "name";
        private static final String PATTERN = "type";
        private static final String SIGNED = "signed";
        private static final String DECIMAL = "decimal";
        private static final String UNIT = "unit";
        private static final String COEFFICIENT = "coefficient";
        private static final String DIRECTION = "direction";
        private static final String DIRECTIONS = "directions";
        private static final String INDEX = "index";
        private static final String INTRODUCTION = "introduction";
        private static final String ON = "on";
        private static final String OFF = "off";
        private static final String DATA_TYPE = "DataType";
        private static final String PARAPHRASES = "paraphrases";
        private static final String PARAPHRASE = "paraphrase";
        private static final String NUMBER = "number";
        private static final String TEXT = "text";
        private static final String STATUS = "status";
        private static final String CALENDAR = "calendar";
        private Map<Byte, SensorDataType> dataTypeMap;
        private Map<SensorDataType, Map<Byte, String>> measureNameMap;
        private Map<Byte, String> directionMap;
        private Byte index;
        private String introduction;
        private SensorDataType dataType;
        private StringBuilder builder;
        private Map<Double, String> paraphrases;
        private Double number;
        private String text;
        private String on;
        private String off;
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

    public static SensorDataType getEmptyType(byte value) {
        SensorDataType nullType = new SensorDataType();
        nullType.value = value;
        nullType.pattern = Pattern.DT_ANALOG;
        nullType.interpreter = FloatInterpreter.build(3);
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

    public String getUnit() {
        return unit;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public boolean isSigned() {
        return signed;
    }

    public String getSignificantValue(double rawValue) {
        return interpreter.interpret(rawValue);
    }

    public String getSignificantValueWithUnit(double rawValue) {
        return unit != "" ? getSignificantValue(rawValue) + unit : getSignificantValue(rawValue);
    }

    private byte value;
    private String name;
    private Pattern pattern;
    private boolean signed;
    private String unit = "";
    private double coefficient;
    private ValueInterpreter interpreter;
}
