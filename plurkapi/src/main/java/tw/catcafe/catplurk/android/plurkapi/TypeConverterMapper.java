package tw.catcafe.catplurk.android.plurkapi;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Davy
 */
public class TypeConverterMapper<T> implements TypeConverter<T> {

    private final Class<? extends T> cls;

    TypeConverterMapper(Class<? extends T> cls) {
        this.cls = cls;
    }

    @Override
    public T parse(JsonParser jsonParser) throws IOException {
        return LoganSquare.mapperFor(cls).parse(jsonParser);
    }

    @Override
    public void serialize(T object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings({"TryWithIdenticalCatches"})
    public static <T> void register(Class<T> cls, Class<? extends T> impl) {
        LoganSquare.registerTypeConverter(cls, new TypeConverterMapper<>(impl));
        try {
            //noinspection unchecked
            register(cls, impl, (JsonMapper) Class.forName(impl.getName() + "$$JsonObjectMapper").newInstance());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings({"TryWithIdenticalCatches"})
    public static <T> void register(Class<T> cls, Class<? extends T> impl, JsonMapper<? extends T> mapper) {
        LoganSquare.registerTypeConverter(cls, new TypeConverterMapper<>(impl));
        try {
            //noinspection unchecked
            final Field objectMappersField = LoganSquare.class.getDeclaredField("OBJECT_MAPPERS");
            objectMappersField.setAccessible(true);
            //noinspection unchecked
            final Map<Class, JsonMapper> mappers = (Map<Class, JsonMapper>) objectMappersField.get(null);
            mappers.put(cls, mapper);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}

