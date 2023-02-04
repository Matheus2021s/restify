package br.com.mariah.restapi.enums;

import lombok.Getter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
public enum DataType {


    INTEGER(Pattern.compile("^-?[0-9]+$"), Integer.class) {
        @Override
        public Integer cast(String value) {
            return Integer.valueOf(value);
        }
    },
    DOUBLE(Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$"), Double.class) {
        @Override
        public Double cast(String value) {
            return Double.valueOf(value);
        }
    },
    BOOLEAN(Pattern.compile("^(true|false)$"), Boolean.class) {
        @Override
        public Boolean cast(String value) {
            return Boolean.valueOf(value);
        }
    },
    CHARACTER(Pattern.compile("^'.'$"), Character.class) {
        @Override
        public Character cast(String value) {
            return value.charAt(1);
        }
    },
    BYTE(Pattern.compile("^-?[0-9]+$"), Byte.class) {
        @Override
        public Byte cast(String value) {
            return Byte.valueOf(value);
        }
    },
    SHORT(Pattern.compile("^-?[0-9]+$"), Short.class) {
        @Override
        public Short cast(String value) {
            return Short.valueOf(value);
        }
    },
    LONG(Pattern.compile("^-?[0-9]+L$"), Long.class) {
        @Override
        public Long cast(String value) {
            return Long.valueOf(value.substring(0, value.length() - 1));
        }
    },
    FLOAT(Pattern.compile("^-?[0-9]+(\\.[0-9]+)?f$"), Float.class) {
        @Override
        public Float cast(String value) {
            return Float.valueOf(value.substring(0, value.length() - 1));
        }
    },
    LOCALDATE(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"), java.time.LocalDate.class) {
        @Override
        public LocalDate cast(String value) {
            return java.time.LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    },
    LOCALTIME(Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?$"), java.time.LocalTime.class) {
        @Override
        public LocalTime cast(String value) {
            return java.time.LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"));
        }
    },
    LOCALDATETIME(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?$"), java.time.LocalDateTime.class) {
        @Override
        public LocalDateTime cast(String value) {
            return java.time.LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    },
    INSTANT(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?Z$"), java.time.Instant.class) {
        @Override
        public Instant cast(String value) {
            return java.time.Instant.parse(value);
        }
    },
    ZONEDDATETIME(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?[\\+|\\-][0-9]{2}:[0-9]{2}$"), java.time.ZonedDateTime.class) {
        @Override
        public ZonedDateTime cast(String value) {
            return java.time.ZonedDateTime.parse(value);
        }
    },
    DURATION(Pattern.compile("^PT[0-9]+[HMS]$"), java.time.Duration.class) {
        @Override
        public Duration cast(String value) {
            return java.time.Duration.parse(value);
        }
    },

    STRING(Pattern.compile(".*"), String.class) {
        @Override
        public String cast(String value) {
            return value;
        }
    };

    private final Pattern pattern;
    private final Class<?> clazz;

    DataType(Pattern pattern, Class<?> clazz) {
        this.pattern = pattern;
        this.clazz = clazz;
    }

    public abstract <Y extends Comparable> Y cast(String value);

    public static DataType resolveByClass(Class<?> dataType) {
        return Arrays.stream(DataType.values())
                .filter(searchDataType -> searchDataType.getClazz().equals(dataType))
                .findFirst().orElse(null);
    }


}