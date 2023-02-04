package br.com.mariah.restify.enums;

import lombok.Getter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
public enum DataType {


    INTEGER(Pattern.compile("^-?[0-9]+$"), Integer.class, "1", "2") {
        @Override
        public Integer cast(String value) {
            return Integer.valueOf(value);
        }
    },
    DOUBLE(Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$"), Double.class, "1D", "2D") {
        @Override
        public Double cast(String value) {
            return Double.valueOf(value);
        }
    },
    BOOLEAN(Pattern.compile("^(true|false)$"), Boolean.class, "true", "false") {
        @Override
        public Boolean cast(String value) {
            return Boolean.valueOf(value);
        }
    },
    CHARACTER(Pattern.compile("^'.'$"), Character.class, "\"a\".charAt(1)", "\"b\".charAt(1)") {
        @Override
        public Character cast(String value) {
            return value.charAt(1);
        }
    },
    BYTE(Pattern.compile("^-?[0-9]+$"), Byte.class, "Byte.parseByte(\"s\")", "Byte.parseByte(\"n\")") {
        @Override
        public Byte cast(String value) {
            return Byte.valueOf(value);
        }
    },
    SHORT(Pattern.compile("^-?[0-9]+$"), Short.class, "Short.valueOf(\"s\")", "Short.valueOf(\"n\")") {
        @Override
        public Short cast(String value) {
            return Short.valueOf(value);
        }
    },
    LONG(Pattern.compile("^-?[0-9]+L$"), Long.class, "1L", "2L") {
        @Override
        public Long cast(String value) {
            return Long.valueOf(value.substring(0, value.length() - 1));
        }
    },
    FLOAT(Pattern.compile("^-?[0-9]+(\\.[0-9]+)?f$"), Float.class, "1F", "2F") {
        @Override
        public Float cast(String value) {
            return Float.valueOf(value.substring(0, value.length() - 1));
        }
    },
    LOCALDATE(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"), LocalDate.class, "LocalDate.now()", "LocalDate.now().plus(1,ChronoUnit.DAYS)") {
        @Override
        public LocalDate cast(String value) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    },
    LOCALTIME(Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?$"), LocalTime.class, "LocalTime.now()", "LocalTime.now().plus(1,ChronoUnit.MINUTES)") {
        @Override
        public LocalTime cast(String value) {
            return LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"));
        }
    },
    LOCALDATETIME(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?$"), LocalDateTime.class, "LocalDateTime.now()", "LocalDateTime.now().plus(1,ChronoUnit.DAYS)") {
        @Override
        public LocalDateTime cast(String value) {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    },
    INSTANT(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?Z$"), Instant.class, "Instant.now()", "Instant.now().plus(1,ChronoUnit.DAYS)") {
        @Override
        public Instant cast(String value) {
            return Instant.parse(value);
        }
    },
    ZONEDDATETIME(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?[\\+|\\-][0-9]{2}:[0-9]{2}$"), ZonedDateTime.class, "ZonedDateTime.now()", "ZonedDateTime.now().plus(1,ChronoUnit.DAYS)") {
        @Override
        public ZonedDateTime cast(String value) {
            return ZonedDateTime.parse(value);
        }
    },
    DURATION(Pattern.compile("^PT[0-9]+[HMS]$"), Duration.class, "Duration.ofSeconds(1)", "Duration.ofSeconds(2)") {
        @Override
        public Duration cast(String value) {
            return Duration.parse(value);
        }
    },

    STRING(Pattern.compile(".*"), String.class, "\"String\"", "\"String updated\"") {
        @Override
        public String cast(String value) {
            return value;
        }
    };

    private final Pattern pattern;
    private final Class<?> clazz;

    private final String defalutValue;

    private final String updatedDefaultValue;

    DataType(Pattern pattern, Class<?> clazz, String defalutValue, String updatedDefaultValue) {
        this.pattern = pattern;
        this.clazz = clazz;
        this.defalutValue = defalutValue;
        this.updatedDefaultValue = updatedDefaultValue;
    }

    public abstract <Y extends Comparable> Y cast(String value);

    public static DataType resolveByClass(Class<?> dataType) {
        return Arrays.stream(DataType.values())
                .filter(searchDataType -> searchDataType.getClazz().equals(dataType))
                .findFirst().orElse(null);
    }


}