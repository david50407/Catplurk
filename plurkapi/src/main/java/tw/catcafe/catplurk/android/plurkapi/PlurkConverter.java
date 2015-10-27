package tw.catcafe.catplurk.android.plurkapi;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.DateTypeConverter;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import org.mariotaku.restfu.Converter;
import org.mariotaku.restfu.Utils;
import org.mariotaku.restfu.http.ContentType;
import org.mariotaku.restfu.http.RestHttpResponse;
import org.mariotaku.restfu.http.mime.TypedData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import tw.catcafe.catplurk.android.plurkapi.auth.OAuthToken;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.ProfileResponse;
import tw.catcafe.catplurk.android.plurkapi.model.ResponseCode;
import tw.catcafe.catplurk.android.plurkapi.model.TimelinePlurksResponse;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.plurkapi.model.Wrapper;
import tw.catcafe.catplurk.android.plurkapi.model.impl.ProfileResponseImpl;
import tw.catcafe.catplurk.android.plurkapi.model.impl.TimelinePlurksResponseImpl;

/**
 * @author Davy
 */
public class PlurkConverter implements Converter {
    private static final Map<Class<?>, Class<? extends Wrapper<?>>> wrapperMap = new HashMap<>();

    static {
        TypeConverterMapper.register(TimelinePlurksResponse.class, TimelinePlurksResponseImpl.class);
        TypeConverterMapper.register(ProfileResponse.class, ProfileResponseImpl.class);

        LoganSquare.registerTypeConverter(Date.class, new PlurkDateConverter());
        LoganSquare.registerTypeConverter(Plurk.Commentable.class, EnumIntegerConverter.get(Plurk.Commentable.class));
        LoganSquare.registerTypeConverter(Plurk.ReadState.class, EnumIntegerConverter.get(Plurk.ReadState.class));
        LoganSquare.registerTypeConverter(User.Privacy.class, EnumIntegerConverter.get(User.Privacy.class));
        LoganSquare.registerTypeConverter(User.BirthdayPrivacy.class, EnumIntegerConverter.get(User.BirthdayPrivacy.class));
        LoganSquare.registerTypeConverter(User.Gender.class, EnumIntegerConverter.get(User.Gender.class));
        LoganSquare.registerTypeConverter(User.Relationship.class, EnumConverter.get(User.Relationship.class));
    }

    @Override
    public Object convert(RestHttpResponse response, Type type) throws Exception {
        final TypedData body = response.getBody();
        if (!response.isSuccessful()) {
            throw parseOrThrow(response, body.stream(), PlurkException.class);
        }
        final ContentType contentType = body.contentType();
        final InputStream stream = body.stream();
        try {
            if (type instanceof Class<?>) {
                final Class<?> cls = (Class<?>) type;
                final Class<?> wrapperCls = wrapperMap.get(cls);
                if (wrapperCls != null) {
                    final Wrapper<?> wrapper = (Wrapper<?>) parseOrThrow(response, stream, wrapperCls);
                    return wrapper.getWrapped(null);
                } else if (OAuthToken.class.isAssignableFrom(cls)) {
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    body.writeTo(os);
                    Charset charset = contentType != null ? contentType.getCharset() : null;
                    if (charset == null) {
                        charset = Charset.defaultCharset();
                    }
                    try {
                        return new OAuthToken(os.toString(charset.name()), charset);
                    } catch (ParseException e) {
                        throw new IOException(e);
                    }
                } else if (ResponseCode.class.isAssignableFrom(cls)) {
                    return new ResponseCode(response);
                }
                return parseOrThrow(response, stream, cls);
            } else if (type instanceof ParameterizedType) {
                final Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType instanceof Class<?>) {
                    final Class<?> rawClass = (Class<?>) rawType;
                    final Class<?> wrapperCls = wrapperMap.get(rawClass);
                    if (wrapperCls != null) {
                        final Wrapper<?> wrapper = (Wrapper<?>) parseOrThrow(response, stream, wrapperCls);
                        return wrapper.getWrapped(((ParameterizedType) type).getActualTypeArguments());
                    }
                }
            }
            throw new UnsupportedTypeException(type);
        } finally {
            Utils.closeSilently(stream);
        }
    }

    private static <T> T parseOrThrow(RestHttpResponse resp, InputStream stream, Class<T> cls) throws IOException, PlurkException {
        try {
            final T parse = LoganSquare.parse(stream, cls);
            if (PlurkException.class.isAssignableFrom(cls) && parse == null) {
                throw new PlurkException();
            }
            return parse;
        } catch (JsonParseException e) {
            throw new PlurkException("Malformed JSON Data", resp);
        }
    }

    private static <T> List<T> parseListOrThrow(RestHttpResponse resp, InputStream stream, Class<T> elementCls)
            throws IOException, PlurkException {
        try {
            return LoganSquare.parseList(stream, elementCls);
        } catch (JsonParseException e) {
            throw new PlurkException("Malformed JSON Data", resp);
        }
    }

    private static <T> void registerWrapper(Class<T> cls, Class<? extends Wrapper<? extends T>> wrapperCls) {
        wrapperMap.put(cls, wrapperCls);
    }

    private static class EnumConverter<T extends Enum<T>> implements TypeConverter<T> {
        private final Class<T> cls;

        EnumConverter(Class<T> cls) {
            this.cls = cls;
        }

        @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
        @Override
        public T parse(JsonParser jsonParser) throws IOException {
            try {
                final Method method = cls.getMethod("parse", String.class);
                return (T) method.invoke(null, jsonParser.getValueAsString());
            } catch (NoSuchMethodException e) {
                return Enum.valueOf(cls, jsonParser.getValueAsString());
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void serialize(T object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) {
            throw new UnsupportedOperationException();
        }

        public static <T extends Enum<T>> EnumConverter<T> get(Class<T> cls) {
            return new EnumConverter<>(cls);
        }
    }

    private static class EnumIntegerConverter<T extends Enum<T>> implements TypeConverter<T> {
        private final Class<T> cls;

        EnumIntegerConverter(Class<T> cls) {
            this.cls = cls;
        }

        @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
        @Override
        public T parse(JsonParser jsonParser) throws IOException {
            try {
                final Method method = cls.getMethod("parse", Long.class);
                return (T) method.invoke(null, jsonParser.getValueAsLong());
            } catch (NoSuchMethodException e) {
                return cls.getEnumConstants()[jsonParser.getIntValue()];
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void serialize(T object, String fieldName, boolean writeFieldNameForObject,
                              JsonGenerator jsonGenerator) throws IOException  {
            jsonGenerator.writeNumberField(fieldName, object.ordinal());
        }

        public static <T extends Enum<T>> EnumIntegerConverter<T> get(Class<T> cls) {
            return new EnumIntegerConverter<>(cls);
        }
    }

    public static class PlurkDateConverter extends DateTypeConverter {
        // Fri, 05 Jun 2009 23:07:13 GMT
        final static DateFormat mDateFormat = new SimpleDateFormat("ccc, dd LLL yyyy HH:mm:ss 'GMT'", Locale.US);
        static {
            mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        public PlurkDateConverter() {

        }

        public DateFormat getDateFormat() {
            return mDateFormat;
        }
    }

    public static class UnsupportedTypeException extends UnsupportedOperationException {
        private static final long serialVersionUID = -7548468674021391028L;

        public UnsupportedTypeException(Type type) {
            super("Unsupported type " + type);
        }
    }
}
