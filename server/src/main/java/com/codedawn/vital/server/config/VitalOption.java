package com.codedawn.vital.server.config;

import java.util.Objects;

/**
 *
 * 配置项类
 *
 * @author codedawn
 * @date 2021-08-02 9:30
 */
public class VitalOption <T>{
    private final String name;
    private  T value;


    public VitalOption(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public T value() {
        return value;
    }

    public VitalOption<T> setValue(T value) {
        this.value = value;
        return this;
    }


    //    public static <T> VitalOption<T> valueOf(String name) {
//        return new VitalOption<T>(name, null);
//    }

    public static <T> VitalOption<T> valueOf(String name, T value) {
        return new VitalOption<T>(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VitalOption<?> that = (VitalOption<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
