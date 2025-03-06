package cu.redcuba.helper;

public class CastHelper {

    public static <T> T cast(Object value, Class<T> clazz) {
        if (value == null) {
            return null;
        }

        //Only must apply when the required type is String
        if (clazz == String.class) {
            return clazz.cast(value);
        }

        //When the required type is Character
        if (clazz == Character.class) {
            return clazz.cast(value.toString().charAt(0));
        }

        //When the required type is Integer
        if (clazz == Integer.class) {
            return clazz.cast(Integer.parseInt(value.toString()));
        }

        //When the required type is Byte
        if (clazz == Short.class) {
            return clazz.cast(Short.parseShort(value.toString()));
        }

        //When the required type is Long
        if (clazz == Long.class) {
            return clazz.cast(Long.parseLong(value.toString()));
        }

        return null;
    }

}
